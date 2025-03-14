/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.service.workflow.service.impl.message;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.primitives.Longs;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import im.turms.common.model.dto.notification.TurmsNotification;
import im.turms.common.model.dto.request.TurmsRequest;
import im.turms.common.util.Validator;
import im.turms.server.common.bo.common.DateRange;
import im.turms.server.common.cluster.node.Node;
import im.turms.server.common.cluster.service.idgen.ServiceType;
import im.turms.server.common.constant.TurmsStatusCode;
import im.turms.server.common.dao.util.OperationResultUtil;
import im.turms.server.common.exception.TurmsBusinessException;
import im.turms.server.common.logging.core.logger.Logger;
import im.turms.server.common.logging.core.logger.LoggerFactory;
import im.turms.server.common.mongo.DomainFieldName;
import im.turms.server.common.mongo.IMongoCollectionInitializer;
import im.turms.server.common.mongo.TurmsMongoClient;
import im.turms.server.common.mongo.operation.option.Filter;
import im.turms.server.common.mongo.operation.option.QueryOptions;
import im.turms.server.common.mongo.operation.option.Update;
import im.turms.server.common.plugin.PluginManager;
import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.property.constant.TimeType;
import im.turms.server.common.property.env.service.business.message.MessageProperties;
import im.turms.server.common.property.env.service.business.message.SequenceIdProperties;
import im.turms.server.common.redis.TurmsRedisClientManager;
import im.turms.server.common.task.TrivialTaskManager;
import im.turms.server.common.util.AssertUtil;
import im.turms.server.common.util.BitUtil;
import im.turms.server.common.util.CollectionUtil;
import im.turms.server.common.util.CollectorUtil;
import im.turms.server.common.util.DateUtil;
import im.turms.service.bo.ServicePermission;
import im.turms.service.constant.OperationResultConstant;
import im.turms.service.plugin.extension.ExpiredMessageDeletionNotifier;
import im.turms.service.proto.ProtoModelConvertor;
import im.turms.service.workflow.dao.domain.message.Message;
import im.turms.service.workflow.service.impl.conversation.ConversationService;
import im.turms.service.workflow.service.impl.group.GroupMemberService;
import im.turms.service.workflow.service.impl.statistics.MetricsService;
import im.turms.service.workflow.service.impl.user.UserService;
import io.micrometer.core.instrument.Counter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static im.turms.server.common.constant.BusinessConstant.ADMIN_REQUESTER_ID;
import static im.turms.server.common.constant.BusinessConstant.ADMIN_REQUEST_ID;
import static im.turms.server.common.constant.TurmsStatusCode.ILLEGAL_ARGUMENT;
import static im.turms.server.common.constant.TurmsStatusCode.MESSAGE_RECALL_TIMEOUT;
import static im.turms.server.common.constant.TurmsStatusCode.NOT_SENDER_TO_UPDATE_MESSAGE;
import static im.turms.server.common.constant.TurmsStatusCode.OK;
import static im.turms.server.common.constant.TurmsStatusCode.RECALLING_MESSAGE_IS_DISABLED;
import static im.turms.server.common.constant.TurmsStatusCode.RECALL_NON_EXISTING_MESSAGE;
import static im.turms.server.common.constant.TurmsStatusCode.UPDATING_MESSAGE_BY_SENDER_IS_DISABLED;
import static im.turms.service.constant.MetricsConstant.SENT_MESSAGES_COUNTER_NAME;

/**
 * @author James Chen
 */
@Service
@DependsOn(IMongoCollectionInitializer.BEAN_NAME)
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private static final byte[] GROUP_CONVERSATION_SEQUENCE_ID_PREFIX = {'g', 'i'};
    private static final byte[] PRIVATE_CONVERSATION_SEQUENCE_ID_PREFIX = {'p', 'i'};

    private final TurmsMongoClient mongoClient;
    @Nullable
    private final TurmsRedisClientManager redisClientManager;
    private final Node node;
    private final ConversationService conversationService;
    private final OutboundMessageService outboundMessageService;
    private final GroupMemberService groupMemberService;
    private final UserService userService;
    private final PluginManager pluginManager;

    private final boolean useConversationId;

    private final boolean useSequenceIdForGroupConversation;
    private final boolean useSequenceIdForPrivateConversation;

    @Getter
    private TimeType timeType;
    private final Cache<Long, Message> sentMessageCache;

    private final Counter sentMessageCounter;

    @Autowired
    public MessageService(
            TurmsMongoClient messageMongoClient,
            @Nullable @Autowired(required = false) @Qualifier("sequenceIdRedisClientManager")
                    TurmsRedisClientManager sequenceIdRedisClientManager,

            Node node,

            TurmsPropertiesManager turmsPropertiesManager,
            ConversationService conversationService,
            GroupMemberService groupMemberService,
            UserService userService,
            OutboundMessageService outboundMessageService,
            MetricsService metricsService,

            PluginManager pluginManager,
            TrivialTaskManager taskManager) {
        this.mongoClient = messageMongoClient;
        this.redisClientManager = sequenceIdRedisClientManager;
        this.node = node;
        this.conversationService = conversationService;
        this.groupMemberService = groupMemberService;
        this.userService = userService;
        this.outboundMessageService = outboundMessageService;
        this.pluginManager = pluginManager;

        MessageProperties messageProperties = node.getSharedProperties().getService().getMessage();
        useConversationId = messageProperties.isUseConversationId();
        SequenceIdProperties sequenceIdProperties = messageProperties.getSequenceId();
        useSequenceIdForGroupConversation = sequenceIdProperties.isUseSequenceIdForGroupConversation();
        useSequenceIdForPrivateConversation = sequenceIdProperties.isUseSequenceIdForPrivateConversation();
        timeType = messageProperties.getTimeType();
        int relayedMessageCacheMaxSize = turmsPropertiesManager.getLocalProperties().getService().getMessage().getSentMessageCacheMaxSize();
        if (relayedMessageCacheMaxSize > 0 && turmsPropertiesManager.getLocalProperties().getService().getMessage().isMessagePersistent()) {
            this.sentMessageCache = Caffeine
                    .newBuilder()
                    .maximumSize(relayedMessageCacheMaxSize)
                    .expireAfterWrite(Duration.ofSeconds(
                            turmsPropertiesManager.getLocalProperties().getService().getMessage().getSentMessageExpireAfter()))
                    .build();
        } else {
            sentMessageCache = null;
        }
        sentMessageCounter = metricsService.getRegistry().counter(SENT_MESSAGES_COUNTER_NAME);
        node.addPropertiesChangeListener(properties -> timeType = properties.getService().getMessage().getTimeType());
        // Set up the checker for expired messages join requests
        taskManager.reschedule(
                "expiredMessagesCleanup",
                turmsPropertiesManager.getLocalProperties().getService().getMessage().getExpiredMessagesCleanupCron(),
                () -> {
                    if (node.isLocalNodeLeader()) {
                        int expireAfterHours = node.getSharedProperties()
                                .getService()
                                .getMessage()
                                .getMessageExpireAfterHours();
                        if (expireAfterHours > 0) {
                            deleteExpiredMessages(expireAfterHours)
                                    .subscribe(null, t -> LOGGER.error("Caught an error while deleting expired messages", t));
                        }
                    }
                });
    }

    public Mono<Boolean> isMessageSentByUser(@NotNull Long messageId, @NotNull Long senderId) {
        try {
            AssertUtil.notNull(messageId, "messageId");
            AssertUtil.notNull(senderId, "senderId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (sentMessageCache != null) {
            Message message = sentMessageCache.getIfPresent(messageId);
            if (message != null) {
                return Mono.just(message.getSenderId().equals(senderId));
            }
        }
        Filter filter = Filter.newBuilder(2)
                .eq(DomainFieldName.ID, messageId)
                .eq(Message.Fields.SENDER_ID, senderId);
        return mongoClient.exists(Message.class, filter);
    }

    public Mono<Boolean> isMessageRecipient(@NotNull Long messageId, @NotNull Long recipientId) {
        try {
            AssertUtil.notNull(messageId, "messageId");
            AssertUtil.notNull(recipientId, "recipientId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (sentMessageCache != null) {
            Message message = sentMessageCache.getIfPresent(messageId);
            if (message != null && !message.getIsGroupMessage()) {
                return Mono.just(message.getTargetId().equals(recipientId));
            }
        }
        Filter filter = Filter.newBuilder(3)
                .eq(DomainFieldName.ID, messageId)
                .eq(Message.Fields.TARGET_ID, recipientId)
                .eq(Message.Fields.IS_GROUP_MESSAGE, false);
        return mongoClient.exists(Message.class, filter);
    }

    public Mono<Boolean> isMessageRecipientOrSender(@NotNull Long messageId, @NotNull Long userId) {
        return isMessageRecipient(messageId, userId)
                .flatMap(isSentToUser -> isSentToUser
                        ? Mono.just(true)
                        : isMessageSentByUser(messageId, userId));
    }

    public Mono<ServicePermission> isMessageRecallable(@NotNull Long messageId) {
        try {
            AssertUtil.notNull(messageId, "messageId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Mono<Message> messageMono = null;
        if (sentMessageCache != null) {
            Message message = sentMessageCache.getIfPresent(messageId);
            if (message != null) {
                messageMono = Mono.just(message);
            }
        }
        if (messageMono == null) {
            Filter filter = Filter.newBuilder(1)
                    .eq(DomainFieldName.ID, messageId);
            QueryOptions options = QueryOptions.newBuilder(2)
                    .include(Message.Fields.DELIVERY_DATE);
            messageMono = mongoClient.findOne(Message.class, filter, options);
        }
        return messageMono
                .map(message -> {
                    Date deliveryDate = message.getDeliveryDate();
                    long elapsedTime = (deliveryDate.getTime() - System.currentTimeMillis()) / 1000;
                    boolean isRecallable = elapsedTime < node.getSharedProperties()
                            .getService()
                            .getMessage()
                            .getAvailableRecallDurationSeconds();
                    return isRecallable
                            ? ServicePermission.OK
                            : ServicePermission.get(MESSAGE_RECALL_TIMEOUT);
                })
                .defaultIfEmpty(ServicePermission.get(RECALL_NON_EXISTING_MESSAGE));
    }

    public Flux<Message> authAndQueryCompleteMessages(
            boolean closeToDate,
            @Nullable Collection<Long> messageIds,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages,
            @Nullable Long senderId,
            @Nullable Long targetId,
            @Nullable DateRange deliveryDateRange,
            @Nullable DateRange deletionDateRange,
            @Nullable Integer page,
            @Nullable Integer size) {
        return queryMessages(
                closeToDate,
                messageIds,
                areGroupMessages,
                areSystemMessages,
                senderId == null ? null : Set.of(senderId),
                targetId == null ? null : Set.of(targetId),
                deliveryDateRange,
                deletionDateRange,
                page,
                size);
    }

    public Mono<Message> queryMessage(@NotNull Long messageId) {
        try {
            AssertUtil.notNull(messageId, "messageId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .eq(DomainFieldName.ID, messageId);
        return mongoClient.findOne(Message.class, filter);
    }

    public Flux<Message> queryMessages(
            boolean closeToDate,
            @Nullable Collection<Long> messageIds,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages,
            @Nullable Set<Long> senderIds,
            @Nullable Set<Long> targetIds,
            @Nullable DateRange deliveryDateRange,
            @Nullable DateRange deletionDateRange,
            @Nullable Integer page,
            @Nullable Integer size) {
        boolean enableConversationId = useConversationId && areGroupMessages != null;
        Filter filter = Filter.newBuilder(enableConversationId ? 9 : 8)
                .eqIfNotNull(Message.Fields.IS_GROUP_MESSAGE, areGroupMessages)
                .eqIfNotNull(Message.Fields.IS_SYSTEM_MESSAGE, areSystemMessages)
                .inIfNotNull(Message.Fields.SENDER_ID, senderIds)
                .inIfNotNull(Message.Fields.TARGET_ID, targetIds)
                .addBetweenIfNotNull(Message.Fields.DELIVERY_DATE, deliveryDateRange)
                .addBetweenIfNotNull(Message.Fields.DELETION_DATE, deletionDateRange);
        if (enableConversationId) {
            int conversationIdSize = CollectionUtil.getSize(senderIds) * CollectionUtil.getSize(targetIds);
            // fast path
            if (conversationIdSize == 1) {
                filter.eq(Message.Fields.CONVERSATION_ID,
                        getConversationId0(senderIds.iterator().next(), targetIds.iterator().next(), areGroupMessages));
            } else if (conversationIdSize > 0) {
                // slow path
                List<byte[]> conversationIds = new ArrayList<>(conversationIdSize);
                for (long senderId : senderIds) {
                    for (long targetId : targetIds) {
                        conversationIds.add(getConversationId0(senderId, targetId, areGroupMessages));
                    }
                }
                filter.in(Message.Fields.CONVERSATION_ID, conversationIds);
            }
        }
        QueryOptions options = QueryOptions.newBuilder(closeToDate ? 3 : 2)
                .paginateIfNotNull(page, size);
        if (closeToDate) {
            boolean isAsc = deliveryDateRange != null && deliveryDateRange.start() != null;
            options.sort(isAsc, Message.Fields.DELIVERY_DATE);
        }
        filter.inIfNotNull(DomainFieldName.ID, messageIds);
        return mongoClient.findMany(Message.class, filter, options);
    }

    public Mono<Message> saveMessage(
            @Nullable Long messageId,
            @NotNull Long senderId,
            @NotNull Long targetId,
            @NotNull Boolean isGroupMessage,
            @NotNull Boolean isSystemMessage,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable @Min(0) Integer burnAfter,
            @Nullable @PastOrPresent Date deliveryDate,
            @Nullable @PastOrPresent Date recallDate,
            @Nullable Long referenceId,
            @Nullable Long preMessageId) {
        MessageProperties messageProperties = node.getSharedProperties().getService().getMessage();
        try {
            AssertUtil.notNull(senderId, "senderId");
            AssertUtil.notNull(targetId, "targetId");
            AssertUtil.notNull(isGroupMessage, "isGroupMessage");
            AssertUtil.notNull(isSystemMessage, "isSystemMessage");
            AssertUtil.maxLength(text, "text", messageProperties.getMaxTextLimit());
            validRecordsLength(records);
            AssertUtil.min(burnAfter, "burnAfter", 0);
            AssertUtil.pastOrPresent(deliveryDate, "deliveryDate");
            AssertUtil.pastOrPresent(recallDate, "recallDate");
            AssertUtil.before(deliveryDate, recallDate, "deliveryDate", "recallDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (timeType == TimeType.LOCAL_SERVER_TIME || deliveryDate == null) {
            deliveryDate = new Date();
        }
        if (messageId == null) {
            messageId = node.nextLargeGapId(ServiceType.MESSAGE);
        }
        if (!messageProperties.isRecordsPersistent()) {
            records = null;
        }
        if (!messageProperties.isPreMessageIdPersistent()) {
            preMessageId = null;
        }
        Mono<Long> sequenceId = null;
        if (isGroupMessage) {
            if (useSequenceIdForGroupConversation) {
                sequenceId = fetchSequenceId(true, targetId);
            }
        } else if (useSequenceIdForPrivateConversation) {
            sequenceId = fetchSequenceId(false, targetId);
        }
        byte[] conversationId = getConversationId0(senderId, targetId, isGroupMessage);
        Mono<Message> saveMessage;
        if (sequenceId == null) {
            Message message = new Message(
                    messageId,
                    conversationId,
                    isGroupMessage,
                    isSystemMessage,
                    deliveryDate,
                    null,
                    null,
                    null,
                    text,
                    senderId,
                    targetId,
                    records,
                    burnAfter,
                    referenceId,
                    null,
                    preMessageId);
            saveMessage = mongoClient.insert(message)
                    .thenReturn(message);
        } else {
            Long finalMessageId = messageId;
            Date finalDeliveryDate = deliveryDate;
            List<byte[]> finalRecords = records;
            Long finalPreMessageId = preMessageId;
            saveMessage = sequenceId
                    .flatMap(seqId -> {
                        Message message = new Message(
                                finalMessageId,
                                conversationId,
                                isGroupMessage,
                                isSystemMessage,
                                finalDeliveryDate,
                                null,
                                null,
                                null,
                                text,
                                senderId,
                                targetId,
                                finalRecords,
                                burnAfter,
                                referenceId,
                                seqId.intValue(),
                                finalPreMessageId);
                        return mongoClient.insert(message)
                                .thenReturn(message);
                    });
        }
        if (node.getSharedProperties().getService().getConversation().getReadReceipt().isUpdateReadDateAfterMessageSent()) {
            Mono<Void> upsertConversation = isGroupMessage
                    ? conversationService.upsertGroupConversationReadDate(targetId, senderId, deliveryDate)
                    : conversationService.upsertPrivateConversationReadDate(senderId, targetId, deliveryDate);
            return saveMessage
                    .doOnNext(ignored -> upsertConversation
                            .subscribe(null, t -> LOGGER.error("Caught an error while upserting the {} conversation: {}",
                                    isGroupMessage ? "group" : "private",
                                    targetId,
                                    t)));
        }
        return saveMessage;
    }

    public Flux<Long> queryExpiredMessageIds(@NotNull Integer timeToLiveHours) {
        try {
            AssertUtil.notNull(timeToLiveHours, "timeToLiveHours");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Date beforeDate = DateUtil.addHours(System.currentTimeMillis(), -timeToLiveHours);
        Filter filter = Filter.newBuilder(1)
                .lt(Message.Fields.DELIVERY_DATE, beforeDate);
        QueryOptions options = QueryOptions.newBuilder(1)
                .include(DomainFieldName.ID);
        return mongoClient.findMany(Message.class, filter, options)
                .map(Message::getId);
    }

    public Mono<Void> deleteExpiredMessages(@NotNull Integer timeToLiveHours) {
        return queryExpiredMessageIds(timeToLiveHours)
                .collectList()
                .flatMap(expiredMessageIds -> {
                    if (expiredMessageIds.isEmpty()) {
                        return Mono.empty();
                    }
                    Mono<List<Long>> messageIdsToDeleteMono = Mono.just(expiredMessageIds);
                    if (pluginManager.hasRunningExtensions(ExpiredMessageDeletionNotifier.class)) {
                        Filter messagesFilter = Filter.newBuilder(1)
                                .in(DomainFieldName.ID, expiredMessageIds);
                        messageIdsToDeleteMono = mongoClient.findMany(Message.class, messagesFilter)
                                .collectList()
                                .flatMap(messages -> pluginManager.invokeExtensionPointsSequentially(
                                        ExpiredMessageDeletionNotifier.class,
                                        "getMessagesToDelete",
                                        messages,
                                        (notifier, pre) -> pre.flatMap(notifier::getMessagesToDelete)))
                                .map(messages -> {
                                    List<Long> messageIds = new ArrayList<>(messages.size());
                                    for (Message message : messages) {
                                        messageIds.add(message.getId());
                                    }
                                    return messageIds;
                                });
                    }
                    return messageIdsToDeleteMono
                            .flatMap(messageIds -> {
                                Filter messagesFilter = Filter.newBuilder(1)
                                        .in(DomainFieldName.ID, messageIds);
                                return mongoClient.deleteMany(Message.class, messagesFilter).then();
                            });
                });
    }

    public Mono<DeleteResult> deleteMessages(
            @Nullable Set<Long> messageIds,
            @Nullable Boolean deleteLogically) {
        Filter filterMessage = Filter.newBuilder(1)
                .inIfNotNull(DomainFieldName.ID, messageIds);
        if (deleteLogically == null) {
            deleteLogically = node.getSharedProperties()
                    .getService().getMessage()
                    .isDeleteMessageLogicallyByDefault();
        }
        if (deleteLogically) {
            Update update = Update.newBuilder(1)
                    .set(Message.Fields.DELETION_DATE, new Date());
            return mongoClient.updateMany(Message.class, filterMessage, update)
                    .map(OperationResultUtil::update2delete);
        }
        return mongoClient.deleteMany(Message.class, filterMessage);
    }

    public Mono<UpdateResult> updateMessages(
            @NotEmpty Set<Long> messageIds,
            @Nullable Boolean isSystemMessage,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable @Min(0) Integer burnAfter,
            @Nullable @PastOrPresent Date recallDate,
            @Nullable ClientSession session) {
        try {
            AssertUtil.notEmpty(messageIds, "messageIds");
            AssertUtil.maxLength(text, "text", node.getSharedProperties().getService().getMessage().getMaxTextLimit());
            AssertUtil.min(burnAfter, "burnAfter", 0);
            AssertUtil.pastOrPresent(recallDate, "recallDate");
            validRecordsLength(records);
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (Validator.areAllNull(isSystemMessage, text, records, burnAfter, recallDate)) {
            return Mono.just(OperationResultConstant.ACKNOWLEDGED_UPDATE_RESULT);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, messageIds);
        Update update = Update.newBuilder(6)
                .setIfNotNull(Message.Fields.MODIFICATION_DATE, new Date())
                .setIfNotNull(Message.Fields.TEXT, text)
                .setIfNotNull(Message.Fields.RECORDS, records)
                .setIfNotNull(Message.Fields.IS_SYSTEM_MESSAGE, isSystemMessage)
                .setIfNotNull(Message.Fields.BURN_AFTER, burnAfter)
                .setIfNotNull(Message.Fields.RECALL_DATE, recallDate);
        if (recallDate == null) {
            return mongoClient.updateMany(session, Message.class, filter, update);
        }
        return mongoClient.findMany(Message.class, filter)
                .map(message -> {
                    byte[] messageType = {BuiltinSystemMessageType.RECALL_MESSAGE};
                    byte[] messageId = Longs.toByteArray(message.getId());
                    return authAndSaveAndSendMessage(true,
                            null,
                            message.getIsGroupMessage(),
                            true,
                            null,
                            List.of(messageType, messageId),
                            null,
                            message.getTargetId(),
                            null,
                            null,
                            null);
                })
                .collect(CollectorUtil.toList(messageIds.size()))
                .flatMap(messageMonos -> {
                    int size = messageMonos.size();
                    return Mono.whenDelayError(messageMonos)
                            .thenReturn(UpdateResult.acknowledged(size, (long) size, null));
                });
    }

    public Mono<UpdateResult> updateMessage(
            @NotNull Long messageId,
            @Nullable Boolean isSystemMessage,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable @Min(0) Integer burnAfter,
            @Nullable @PastOrPresent Date recallDate,
            @Nullable ClientSession session) {
        try {
            AssertUtil.notNull(messageId, "messageId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return updateMessages(Set.of(messageId), isSystemMessage, text, records, burnAfter, recallDate, session);
    }

    public Mono<Long> countMessages(
            @Nullable Set<Long> messageIds,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages,
            @Nullable Set<Long> senderIds,
            @Nullable Set<Long> targetIds,
            @Nullable DateRange deliveryDateRange,
            @Nullable DateRange deletionDateRange) {
        Filter filter = Filter.newBuilder(9)
                .eqIfNotNull(Message.Fields.IS_GROUP_MESSAGE, areGroupMessages)
                .eqIfNotNull(Message.Fields.IS_SYSTEM_MESSAGE, areSystemMessages)
                .inIfNotNull(Message.Fields.SENDER_ID, senderIds)
                .inIfNotNull(Message.Fields.TARGET_ID, targetIds)
                .addBetweenIfNotNull(Message.Fields.DELIVERY_DATE, deliveryDateRange)
                .addBetweenIfNotNull(Message.Fields.DELETION_DATE, deletionDateRange)
                .inIfNotNull(DomainFieldName.ID, messageIds);
        return mongoClient.count(Message.class, filter);
    }

    public Mono<Long> countUsersWhoSentMessage(
            @Nullable DateRange dateRange,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages) {
        Filter filter = Filter.newBuilder(4)
                .addBetweenIfNotNull(Message.Fields.DELIVERY_DATE, dateRange)
                .eqIfNotNull(Message.Fields.IS_GROUP_MESSAGE, areGroupMessages)
                .eqIfNotNull(Message.Fields.IS_SYSTEM_MESSAGE, areSystemMessages);
        return mongoClient.countDistinct(
                Message.class,
                filter,
                Message.Fields.SENDER_ID);
    }

    public Mono<Long> countGroupsThatSentMessages(@Nullable DateRange dateRange) {
        Filter filter = Filter.newBuilder(3)
                .addBetweenIfNotNull(Message.Fields.DELIVERY_DATE, dateRange)
                .eq(Message.Fields.IS_GROUP_MESSAGE, true);
        return mongoClient.countDistinct(
                Message.class,
                filter,
                Message.Fields.TARGET_ID);
    }

//    public Mono<Long> countUsersWhoAcknowledgedMessage(
//            @Nullable DateRange dateRange,
//            @Nullable Boolean areGroupMessage) {
//        Criteria criteria = QueryBuilder.newBuilder()
//                .addBetweenIfNotNull(MessageStatus.Fields.RECEPTION_DATE, dateRange)
//                .buildCriteria();
//        if (areGroupMessage != null) {
//            if (areGroupMessage) {
//                criteria.and(MessageStatus.Fields.GROUP_ID).ne(null);
//            } else {
//                criteria.and(MessageStatus.Fields.GROUP_ID).is(null);
//            }
//        }
//        return AggregationUtil.countDistinct(
//                mongoTemplate,
//                criteria,
//                MessageStatus.Fields.ID_RECIPIENT_ID,
//                MessageStatus.class);
//    }

    public Mono<Long> countSentMessages(
            @Nullable DateRange dateRange,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages) {
        Filter filter = Filter.newBuilder(4)
                .addBetweenIfNotNull(Message.Fields.DELIVERY_DATE, dateRange)
                .eqIfNotNull(Message.Fields.IS_GROUP_MESSAGE, areGroupMessages)
                .eqIfNotNull(Message.Fields.IS_SYSTEM_MESSAGE, areSystemMessages);
        return mongoClient.count(Message.class, filter);
    }

    public Mono<Long> countSentMessagesOnAverage(
            @Nullable DateRange dateRange,
            @Nullable Boolean areGroupMessages,
            @Nullable Boolean areSystemMessages) {
        return countSentMessages(dateRange, areGroupMessages, areSystemMessages)
                .flatMap(totalDeliveredMessages -> {
                    if (totalDeliveredMessages == 0) {
                        return Mono.just(0L);
                    }
                    return countUsersWhoSentMessage(dateRange, areGroupMessages, areSystemMessages)
                            .map(totalUsers -> totalUsers == 0
                                    ? Long.MAX_VALUE
                                    : totalDeliveredMessages / totalUsers);
                });
    }

//    public Mono<Long> countAcknowledgedMessages(
//            @Nullable DateRange dateRange,
//            @Nullable Boolean areGroupMessages,
//            @Nullable Boolean areSystemMessages) {
//        Query query = QueryBuilder.newBuilder()
//                .addBetweenIfNotNull(MessageStatus.Fields.RECEPTION_DATE, dateRange)
//                .addIsIfNotNull(MessageStatus.Fields.IS_SYSTEM_MESSAGE, areSystemMessages)
//                .buildQuery();
//        if (areGroupMessages != null) {
//            if (areGroupMessages) {
//                query.addCriteria(Criteria.where(MessageStatus.Fields.GROUP_ID).ne(null));
//            } else {
//                query.addCriteria(Criteria.where(MessageStatus.Fields.GROUP_ID).is(null));
//            }
//        }
//        return mongoTemplate.count(query, MessageStatus.class, MessageStatus.COLLECTION_NAME);
//    }

//    public Mono<Long> countAcknowledgedMessagesOnAverage(
//            @Nullable DateRange dateRange,
//            @Nullable Boolean areGroupMessages,
//            @Nullable Boolean areSystemMessages) {
//        return countAcknowledgedMessages(dateRange, areGroupMessages, areSystemMessages)
//                .flatMap(totalAcknowledgedMessages -> {
//                    if (totalAcknowledgedMessages == 0) {
//                        return Mono.just(0L);
//                    } else {
//                        return countUsersWhoAcknowledgedMessage(dateRange, areGroupMessages)
//                                .map(totalUsers -> totalUsers == 0
//                                        ? Long.MAX_VALUE
//                                        : totalAcknowledgedMessages / totalUsers);
//                    }
//                });
//    }

    public Mono<UpdateResult> authAndUpdateMessage(
            @NotNull Long requesterId,
            @NotNull Long messageId,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable @PastOrPresent Date recallDate) {
        boolean updateMessageContent = text != null || !CollectionUtils.isEmpty(records);
        if (!updateMessageContent && recallDate == null) {
            return Mono.empty();
        }
        if (recallDate != null && !node.getSharedProperties()
                .getService()
                .getMessage()
                .isAllowRecallMessage()) {
            return Mono.error(TurmsBusinessException.get(RECALLING_MESSAGE_IS_DISABLED));
        }
        if (updateMessageContent && !node.getSharedProperties()
                .getService()
                .getMessage().isAllowEditMessageBySender()) {
            return Mono.error(TurmsBusinessException.get(UPDATING_MESSAGE_BY_SENDER_IS_DISABLED));
        }
        return isMessageSentByUser(messageId, requesterId)
                .flatMap(isSentByUser -> {
                    if (!isSentByUser) {
                        return Mono.error(TurmsBusinessException.get(NOT_SENDER_TO_UPDATE_MESSAGE));
                    }
                    return recallDate == null
                            ? updateMessage(messageId, null, text, records, null, null, null)
                            : updateMessageRecallDate(messageId, text, records, recallDate);
                });
    }

    public Flux<Long> queryMessageRecipients(@NotNull Long messageId) {
        try {
            AssertUtil.notNull(messageId, "messageId");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .eq(DomainFieldName.ID, messageId);
        QueryOptions options = QueryOptions.newBuilder(2)
                .include(Message.Fields.TARGET_ID, Message.Fields.IS_GROUP_MESSAGE);
        return mongoClient.findOne(Message.class, filter, options)
                .flatMapMany(message -> message.getIsGroupMessage()
                        ? groupMemberService.queryGroupMemberIds(message.groupId())
                        : Mono.just(message.getTargetId()));
    }

    // message - recipientIds
    public Mono<Pair<Message, Set<Long>>> authAndSaveMessage(
            @Nullable Long messageId,
            @NotNull Long senderId,
            @NotNull Long targetId,
            @NotNull Boolean isGroupMessage,
            @NotNull Boolean isSystemMessage,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable @Min(0) Integer burnAfter,
            @Nullable @PastOrPresent Date deliveryDate,
            @Nullable Long referenceId,
            @Nullable Long preMessageId) {
        try {
            AssertUtil.maxLength(text, "text", node.getSharedProperties().getService().getMessage().getMaxTextLimit());
            validRecordsLength(records);
            AssertUtil.pastOrPresent(deliveryDate, "deliveryDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return userService.isAllowedToSendMessageToTarget(isGroupMessage, isSystemMessage, senderId, targetId)
                .flatMap(permission -> {
                    TurmsStatusCode code = permission.code();
                    if (code != OK) {
                        return Mono.error(TurmsBusinessException.get(code, permission.reason()));
                    }
                    Mono<Set<Long>> recipientIdsMono = isGroupMessage
                            ? groupMemberService.getMemberIdsByGroupId(targetId).collect(Collectors.toSet())
                            : Mono.just(Set.of(targetId));
                    return recipientIdsMono.flatMap(recipientIds -> {
                        if (!node.getSharedProperties().getService().getMessage().isMessagePersistent()) {
                            return recipientIds.isEmpty()
                                    ? Mono.empty()
                                    : Mono.just(Pair.of(null, recipientIds));
                        }
                        Mono<Message> saveMono = saveMessage(messageId, senderId, targetId, isGroupMessage,
                                isSystemMessage, text, records, burnAfter, deliveryDate, null, referenceId, preMessageId);
                        return saveMono.map(message -> {
                            if (message.getId() != null && sentMessageCache != null) {
                                cacheSentMessage(message);
                            }
                            return Pair.of(message, recipientIds);
                        });
                    });
                });
    }

    /**
     * clone a new message rather than using its ID as a reference
     */
    public Mono<Pair<Message, Set<Long>>> authAndCloneAndSaveMessage(
            @NotNull Long requesterId,
            @NotNull Long referenceId,
            @NotNull Boolean isGroupMessage,
            @NotNull Boolean isSystemMessage,
            @NotNull Long targetId) {
        return queryMessage(referenceId)
                .flatMap(message -> authAndSaveMessage(
                        node.nextLargeGapId(ServiceType.MESSAGE),
                        requesterId,
                        targetId,
                        isGroupMessage,
                        isSystemMessage,
                        message.getText(),
                        message.getRecords(),
                        message.getBurnAfter(),
                        message.getDeliveryDate(),
                        referenceId,
                        null));
    }

    public Mono<Void> authAndSaveAndSendMessage(
            boolean send,
            @Nullable Long messageId,
            @NotNull Boolean isGroupMessage,
            @NotNull Boolean isSystemMessage,
            @Nullable String text,
            @Nullable List<byte[]> records,
            @Nullable Long senderId,
            @NotNull Long targetId,
            @Nullable @Min(0) Integer burnAfter,
            @Nullable Long referenceId,
            @Nullable Long preMessageId) {
        try {
            AssertUtil.notNull(isGroupMessage, "isGroupMessage");
            AssertUtil.notNull(isSystemMessage, "isSystemMessage");
            AssertUtil.notNull(targetId, "targetId");
            AssertUtil.min(burnAfter, "burnAfter", 0);
            AssertUtil.throwIfAllFalsy("text and records cannot be both null", text, records);
            AssertUtil.maxLength(text, "text", node.getSharedProperties().getService().getMessage().getMaxTextLimit());
            validRecordsLength(records);
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (senderId == null) {
            if (isSystemMessage) {
                senderId = ADMIN_REQUESTER_ID;
            } else {
                return Mono.error(TurmsBusinessException.get(ILLEGAL_ARGUMENT, "senderId must not be null for user messages"));
            }
        }
        Date deliveryDate = new Date();
        Mono<Pair<Message, Set<Long>>> saveMono = referenceId == null
                ? authAndSaveMessage(messageId, senderId, targetId, isGroupMessage, isSystemMessage,
                text, records, burnAfter, deliveryDate, null, preMessageId)
                : authAndCloneAndSaveMessage(senderId, referenceId, isGroupMessage, isSystemMessage, targetId);
        return saveMono
                .doOnNext(pair -> {
                    Message message = pair.getLeft();
                    sentMessageCounter.increment();
                    if (message != null && message.getId() != null && sentMessageCache != null) {
                        cacheSentMessage(message);
                    }
                    if (send) {
                        // No need to let the client wait to send notifications to recipients
                        sendMessage(message, pair.getRight())
                                .subscribe(null, t -> LOGGER.error("Failed to send message", t));
                    }
                })
                .then();
    }

    private Mono<Boolean> sendMessage(@NotNull Message message, @NotNull Set<Long> recipientIds) {
        TurmsRequest request = TurmsRequest
                .newBuilder()
                .setCreateMessageRequest(ProtoModelConvertor.message2createMessageRequest(message))
                .build();
        TurmsNotification notification = TurmsNotification
                .newBuilder()
                .setRelayedRequest(request)
                .setRequestId(ADMIN_REQUEST_ID)
                .build();
        if (node.getSharedProperties().getService().getMessage().isSendMessageToOtherSenderOnlineDevices()) {
            recipientIds = CollectionUtil.add(recipientIds, message.getSenderId());
        }
        return outboundMessageService.forwardNotification(
                notification,
                recipientIds);
    }

    private void cacheSentMessage(@NotNull Message message) {
        sentMessageCache.put(message.getId(), new Message(
                message.getId(),
                null,
                message.getIsGroupMessage(),
                message.getIsSystemMessage(),
                message.getDeliveryDate(),
                null,
                null,
                null,
                null,
                message.getSenderId(),
                message.getTargetId(),
                null,
                null,
                null,
                null,
                null));
    }

    private Mono<UpdateResult> updateMessageRecallDate(@NotNull Long messageId,
                                                       String text,
                                                       List<byte[]> records,
                                                       @PastOrPresent Date recallDate) {
        return isMessageRecallable(messageId)
                .flatMap(permission -> {
                    TurmsStatusCode code = permission.code();
                    if (code != OK) {
                        return Mono.error(TurmsBusinessException.get(code, permission.reason()));
                    }
                    return updateMessage(messageId, null, text, records, null, recallDate, null);
                });
    }

    private void validRecordsLength(List<byte[]> records) {
        if (records != null) {
            int maxRecordsSize = node.getSharedProperties()
                    .getService()
                    .getMessage()
                    .getMaxRecordsSizeBytes();
            if (maxRecordsSize > -1) {
                int count = 0;
                for (byte[] record : records) {
                    count += record.length;
                }
                if (count > maxRecordsSize) {
                    throw TurmsBusinessException
                            .get(ILLEGAL_ARGUMENT, "The total size of records must be less than or equal to " + maxRecordsSize);
                }
            }
        }
    }

    // Sequence ID

    public Mono<Void> deleteSequenceIds(boolean isGroupConversation, Set<Long> targetIds) {
        if (redisClientManager == null) {
            return Mono.empty();
        }
        return redisClientManager.execute(targetIds, (client, keyList) -> {
            List<ByteBuf> keys = new ArrayList<>(keyList.size());
            for (Long targetId : keyList) {
                byte[] prefix = isGroupConversation
                        ? GROUP_CONVERSATION_SEQUENCE_ID_PREFIX
                        : PRIVATE_CONVERSATION_SEQUENCE_ID_PREFIX;
                ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(prefix.length + Long.BYTES)
                        .writeBytes(prefix)
                        .writeLong(targetId);
                keys.add(buffer);
            }
            return client.del(keys);
        });
    }

    private Mono<Long> fetchSequenceId(boolean isGroupConversation, Long targetId) {
        if (redisClientManager == null) {
            return Mono.empty();
        }
        byte[] prefix = isGroupConversation
                ? GROUP_CONVERSATION_SEQUENCE_ID_PREFIX
                : PRIVATE_CONVERSATION_SEQUENCE_ID_PREFIX;
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(prefix.length + Long.BYTES)
                .writeBytes(prefix)
                .writeLong(targetId);
        return redisClientManager.incr(targetId, buffer);
    }

    // conversation ID

    public static byte[] getConversationId(long id1, long id2, boolean isGroupMessage) {
        // ID is always positive, meaning that the most significant bit of ID is always 0,
        // so we can use the bit to distinguish group messages and private messages
        // in 16 bytes without adding a new byte to avoid the collision of conversation ID
        byte b = (byte) (id2 >> 56);
        if (isGroupMessage) {
            b = BitUtil.setBit(b, 7);
        }
        return new byte[]{
                (byte) (id1 >> 56),
                (byte) (id1 >> 48),
                (byte) (id1 >> 40),
                (byte) (id1 >> 32),
                (byte) (id1 >> 24),
                (byte) (id1 >> 16),
                (byte) (id1 >> 8),
                (byte) id1,

                b,
                (byte) (id2 >> 48),
                (byte) (id2 >> 40),
                (byte) (id2 >> 32),
                (byte) (id2 >> 24),
                (byte) (id2 >> 16),
                (byte) (id2 >> 8),
                (byte) id2
        };
    }

    @Nullable
    private byte[] getConversationId0(long id1, long id2, boolean isGroupMessage) {
        if (!useConversationId) {
            return null;
        }
        if (id1 < id2) {
            return getConversationId(id1, id2, isGroupMessage);
        } else {
            return getConversationId(id2, id1, isGroupMessage);
        }
    }

    private static class BuiltinSystemMessageType {
        /**
         * NORMAL is only used as a placeholder and won't be set for normal messages
         * because the client implementations consider a system message as a normal message
         * if no message type specified
         */
        private static final int NORMAL = 0;
        private static final int RECALL_MESSAGE = 1;
    }

}
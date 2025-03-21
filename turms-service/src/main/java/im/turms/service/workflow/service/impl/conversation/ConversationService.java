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

package im.turms.service.workflow.service.impl.conversation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import im.turms.server.common.cluster.node.Node;
import im.turms.server.common.constant.TurmsStatusCode;
import im.turms.server.common.exception.TurmsBusinessException;
import im.turms.server.common.mongo.DomainFieldName;
import im.turms.server.common.mongo.IMongoCollectionInitializer;
import im.turms.server.common.mongo.TurmsMongoClient;
import im.turms.server.common.mongo.exception.DuplicateKeyException;
import im.turms.server.common.mongo.operation.option.Filter;
import im.turms.server.common.mongo.operation.option.Update;
import im.turms.server.common.property.env.service.business.conversation.ReadReceiptProperties;
import im.turms.server.common.util.AssertUtil;
import im.turms.server.common.util.CollectionUtil;
import im.turms.service.workflow.dao.domain.conversation.GroupConversation;
import im.turms.service.workflow.dao.domain.conversation.PrivateConversation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author James Chen
 */
@Service
@DependsOn(IMongoCollectionInitializer.BEAN_NAME)
public class ConversationService {

    private final Node node;
    private final TurmsMongoClient mongoClient;

    public ConversationService(
            Node node,
            @Qualifier("conversationMongoClient") TurmsMongoClient mongoClient) {
        this.node = node;
        this.mongoClient = mongoClient;
    }

    // TODO: authenticate
    public Mono<Void> authAndUpsertGroupConversationReadDate(@NotNull Long groupId,
                                                             @NotNull Long memberId,
                                                             @Nullable @PastOrPresent Date readDate) {
        ReadReceiptProperties properties = node.getSharedProperties().getService().getConversation().getReadReceipt();
        if (!properties.isEnabled()) {
            return Mono.error(TurmsBusinessException.get(TurmsStatusCode.UPDATING_READ_DATE_IS_DISABLED));
        }
        if (properties.isUseServerTime()) {
            readDate = new Date();
        }
        return upsertGroupConversationReadDate(groupId, memberId, readDate);
    }

    // TODO: authenticate
    public Mono<Void> authAndUpsertPrivateConversationReadDate(@NotNull Long ownerId,
                                                               @NotNull Long targetId,
                                                               @Nullable @PastOrPresent Date readDate) {
        ReadReceiptProperties properties = node.getSharedProperties().getService().getConversation().getReadReceipt();
        if (!properties.isEnabled()) {
            return Mono.error(TurmsBusinessException.get(TurmsStatusCode.UPDATING_READ_DATE_IS_DISABLED));
        }
        if (properties.isUseServerTime()) {
            readDate = new Date();
        }
        return upsertPrivateConversationReadDate(ownerId, targetId, readDate);
    }

    public Mono<Void> upsertGroupConversationReadDate(@NotNull Long groupId,
                                                      @NotNull Long memberId,
                                                      @Nullable @PastOrPresent Date readDate) {
        try {
            AssertUtil.notNull(groupId, "groupId");
            AssertUtil.notNull(memberId, "memberId");
            AssertUtil.pastOrPresent(readDate, "readDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Date finalReadDate = readDate == null ? new Date() : readDate;
        String fieldKey = GroupConversation.Fields.MEMBER_ID_AND_READ_DATE + "." + memberId;
        boolean allowMoveReadDateForward =
                node.getSharedProperties().getService().getConversation().getReadReceipt().isAllowMoveReadDateForward();
        Filter filter = Filter.newBuilder(allowMoveReadDateForward ? 1 : 2)
                .eq(DomainFieldName.ID, groupId);
        if (!allowMoveReadDateForward) {
            // Only update if no existing date or the existing date is before readDate
            filter.ltOrNull(fieldKey, finalReadDate);
        }
        Update update = Update.newBuilder(1)
                .set(fieldKey, finalReadDate);
        return mongoClient.upsert(GroupConversation.class, filter, update)
                .onErrorResume(DuplicateKeyException.class,
                        e -> readDate == null
                                ? Mono.empty()
                                : Mono.error(TurmsBusinessException.get(TurmsStatusCode.MOVING_READ_DATE_FORWARD_IS_DISABLED)));
    }

    public Mono<Void> upsertGroupConversationsReadDate(@NotNull Set<GroupConversation.GroupConversionMemberKey> keys,
                                                       @Nullable @PastOrPresent Date readDate) {
        try {
            AssertUtil.notNull(keys, "keys");
            AssertUtil.pastOrPresent(readDate, "readDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (keys.isEmpty()) {
            return Mono.empty();
        }
        if (readDate == null) {
            readDate = new Date();
        }
        Multimap<Long, Long> multimap = ArrayListMultimap.create(1, keys.size());
        for (GroupConversation.GroupConversionMemberKey key : keys) {
            multimap.put(key.getGroupId(), key.getMemberId());
        }
        Set<Map.Entry<Long, Collection<Long>>> entries = multimap.asMap().entrySet();
        List<Mono<Void>> upsertMonos = new ArrayList<>(entries.size());
        for (Map.Entry<Long, Collection<Long>> entry : entries) {
            Long groupId = entry.getKey();
            Collection<Long> memberIds = entry.getValue();
            Filter filter = Filter.newBuilder(1)
                    .eq(DomainFieldName.ID, groupId);
            Update update = Update.newBuilder(memberIds.size());
            for (Long memberId : memberIds) {
                String fieldKey = GroupConversation.Fields.MEMBER_ID_AND_READ_DATE + "." + memberId;
                // Ignore isAllowMoveReadDateForward()
                update.set(fieldKey, readDate);
            }
            upsertMonos.add(mongoClient.upsert(GroupConversation.class, filter, update));
        }
        return Mono.whenDelayError(upsertMonos);
    }

    public Mono<Void> upsertPrivateConversationReadDate(@NotNull Long ownerId,
                                                        @NotNull Long targetId,
                                                        @Nullable @PastOrPresent Date readDate) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(targetId, "targetId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return upsertPrivateConversationsReadDate(Set.of(new PrivateConversation.Key(ownerId, targetId)), readDate);
    }

    /**
     * @throws com.mongodb.DuplicateKeyException if {@code readDate} isn't after the read date in DB
     *                                           when "isAllowMoveReadDateForward" is enabled
     */
    public Mono<Void> upsertPrivateConversationsReadDate(@NotNull Set<PrivateConversation.Key> keys,
                                                         @Nullable @PastOrPresent Date readDate) {
        try {
            AssertUtil.notNull(keys, "keys");
            AssertUtil.pastOrPresent(readDate, "readDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Date finalReadDate = readDate == null ? new Date() : readDate;
        boolean allowMoveReadDateForward =
                node.getSharedProperties().getService().getConversation().getReadReceipt().isAllowMoveReadDateForward();
        Filter filter = Filter.newBuilder(allowMoveReadDateForward ? 1 : 2)
                .in(DomainFieldName.ID, keys);
        if (!allowMoveReadDateForward) {
            // Only update if no existing date or the existing date is before readDate
            filter.ltOrNull(PrivateConversation.Fields.READ_DATE, finalReadDate);
        }
        Update update = Update.newBuilder(1)
                .set(PrivateConversation.Fields.READ_DATE, finalReadDate);
        return mongoClient.upsert(PrivateConversation.class, filter, update)
                .onErrorResume(DuplicateKeyException.class,
                        e -> readDate == null
                                ? Mono.empty()
                                : Mono.error(TurmsBusinessException.get(TurmsStatusCode.MOVING_READ_DATE_FORWARD_IS_DISABLED)));
    }

    public Flux<GroupConversation> queryGroupConversations(@NotNull Collection<Long> groupIds) {
        try {
            AssertUtil.notNull(groupIds, "groupIds");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        if (groupIds.isEmpty()) {
            return Flux.empty();
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, groupIds);
        return mongoClient.findMany(GroupConversation.class, filter);
    }

    public Flux<PrivateConversation> queryPrivateConversationsByOwnerIds(
            @NotNull Set<Long> ownerIds) {
        try {
            AssertUtil.notNull(ownerIds, "ownerIds");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        if (ownerIds.isEmpty()) {
            return Flux.empty();
        }
        Filter filter = Filter.newBuilder(1)
                .in(PrivateConversation.Fields.ID_OWNER_ID, ownerIds);
        return mongoClient.findMany(PrivateConversation.class, filter);
    }

    public Flux<PrivateConversation> queryPrivateConversations(
            @NotNull Long ownerId,
            @NotNull Collection<Long> targetIds) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(targetIds, "targetIds");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        int size = targetIds.size();
        if (size == 0) {
            return Flux.empty();
        }
        Set<PrivateConversation.Key> keys = CollectionUtil.newSetWithExpectedSize(size);
        for (Long targetId : targetIds) {
            keys.add(new PrivateConversation.Key(ownerId, targetId));
        }
        return queryPrivateConversations(keys);
    }

    public Flux<PrivateConversation> queryPrivateConversations(@NotNull Set<PrivateConversation.Key> keys) {
        try {
            AssertUtil.notNull(keys, "keys");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, keys);
        return mongoClient.findMany(PrivateConversation.class, filter);
    }

    public Mono<DeleteResult> deletePrivateConversations(@NotNull Set<PrivateConversation.Key> keys) {
        try {
            AssertUtil.notNull(keys, "keys");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, keys);
        return mongoClient.deleteMany(PrivateConversation.class, filter);
    }

    public Mono<DeleteResult> deletePrivateConversations(@NotNull Set<Long> userIds, @Nullable ClientSession session) {
        try {
            AssertUtil.notNull(userIds, "userIds");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(PrivateConversation.Fields.ID_OWNER_ID, userIds);
        return mongoClient.deleteMany(session, PrivateConversation.class, filter);
    }

    public Mono<DeleteResult> deleteGroupConversations(@NotNull Set<Long> groupIds, @Nullable ClientSession session) {
        try {
            AssertUtil.notNull(groupIds, "groupIds");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, groupIds);
        return mongoClient.deleteMany(session, GroupConversation.class, filter);
    }

}
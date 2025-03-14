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

package im.turms.service.workflow.service.impl.user.relationship;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import im.turms.common.model.bo.user.UserRelationshipGroupsWithVersion;
import im.turms.common.util.RandomUtil;
import im.turms.server.common.bo.common.DateRange;
import im.turms.server.common.constant.TurmsStatusCode;
import im.turms.server.common.exception.TurmsBusinessException;
import im.turms.server.common.logging.core.logger.Logger;
import im.turms.server.common.logging.core.logger.LoggerFactory;
import im.turms.server.common.mongo.DomainFieldName;
import im.turms.server.common.mongo.IMongoCollectionInitializer;
import im.turms.server.common.mongo.TurmsMongoClient;
import im.turms.server.common.mongo.exception.DuplicateKeyException;
import im.turms.server.common.mongo.operation.option.Filter;
import im.turms.server.common.mongo.operation.option.QueryOptions;
import im.turms.server.common.mongo.operation.option.Update;
import im.turms.server.common.util.AssertUtil;
import im.turms.server.common.util.CollectionUtil;
import im.turms.server.common.util.CollectorUtil;
import im.turms.server.common.util.DateUtil;
import im.turms.service.constant.OperationResultConstant;
import im.turms.service.constraint.ValidUserRelationshipGroupKey;
import im.turms.service.constraint.ValidUserRelationshipKey;
import im.turms.service.proto.ProtoModelConvertor;
import im.turms.service.workflow.dao.domain.user.UserRelationship;
import im.turms.service.workflow.dao.domain.user.UserRelationshipGroup;
import im.turms.service.workflow.dao.domain.user.UserRelationshipGroupMember;
import im.turms.service.workflow.service.documentation.UsesNonIndexedData;
import im.turms.service.workflow.service.impl.user.UserVersionService;
import im.turms.service.workflow.service.util.DomainConstraintUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static im.turms.server.common.constant.BusinessConstant.DEFAULT_RELATIONSHIP_GROUP_INDEX;

/**
 * @author James Chen
 */
@Service
@DependsOn(IMongoCollectionInitializer.BEAN_NAME)
public class UserRelationshipGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRelationshipGroupService.class);

    private final TurmsMongoClient mongoClient;
    private final UserVersionService userVersionService;
    private final UserRelationshipService userRelationshipService;

    /**
     * @param userRelationshipService is lazy because: UserRelationshipService -> UserRelationshipGroupService -> UserRelationshipService
     */
    public UserRelationshipGroupService(
            @Qualifier("userMongoClient") TurmsMongoClient mongoClient,
            UserVersionService userVersionService,
            @Lazy UserRelationshipService userRelationshipService) {
        this.mongoClient = mongoClient;
        this.userVersionService = userVersionService;
        this.userRelationshipService = userRelationshipService;
    }

    public Mono<UserRelationshipGroup> createRelationshipGroup(
            @NotNull Long ownerId,
            @Nullable Integer groupIndex,
            @NotNull String groupName,
            @Nullable @PastOrPresent Date creationDate,
            @Nullable ClientSession session) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(groupName, "groupName");
            AssertUtil.pastOrPresent(creationDate, "creationDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Integer finalGroupIndex = groupIndex == null
                ? RandomUtil.nextPositiveInt()
                : groupIndex;
        if (creationDate == null) {
            creationDate = new Date();
        }
        UserRelationshipGroup group = new UserRelationshipGroup(
                ownerId,
                finalGroupIndex,
                groupName,
                creationDate);
        Mono<UserRelationshipGroup> result = mongoClient.insert(session, group)
                .thenReturn(group);
        // If groupIndex is null but session isn't null and DuplicateKeyException occurs,
        // it's a bug of server because we cannot "resume" the session.
        // Luckily, we don't have the case now.
        if (groupIndex == null && session == null) {
            Date finalCreationDate = creationDate;
            return result
                    .onErrorResume(DuplicateKeyException.class, t ->
                            createRelationshipGroup(ownerId, null, groupName, finalCreationDate, null));
        }
        return result;
    }

    public Flux<UserRelationshipGroup> queryRelationshipGroupsInfos(@NotNull Long ownerId) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .eq(UserRelationshipGroup.Fields.ID_OWNER_ID, ownerId);
        return mongoClient.findMany(UserRelationshipGroup.class, filter);
    }

    public Mono<UserRelationshipGroupsWithVersion> queryRelationshipGroupsInfosWithVersion(
            @NotNull Long ownerId,
            @Nullable Date lastUpdatedDate) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return userVersionService.queryRelationshipGroupsLastUpdatedDate(ownerId)
                .flatMap(date -> {
                    if (DateUtil.isAfterOrSame(lastUpdatedDate, date)) {
                        return Mono.error(TurmsBusinessException.get(TurmsStatusCode.ALREADY_UP_TO_DATE));
                    }
                    UserRelationshipGroupsWithVersion.Builder builder = UserRelationshipGroupsWithVersion.newBuilder()
                            .setLastUpdatedDate(date.getTime());
                    return queryRelationshipGroupsInfos(ownerId)
                            .collect(CollectorUtil.toList())
                            .map(groups -> {
                                for (UserRelationshipGroup group : groups) {
                                    builder.addUserRelationshipGroups(ProtoModelConvertor.relationshipGroup2proto(group));
                                }
                                return builder.build();
                            });
                })
                .switchIfEmpty(Mono.error(TurmsBusinessException.get(TurmsStatusCode.ALREADY_UP_TO_DATE)));
    }

    @UsesNonIndexedData
    public Flux<Integer> queryGroupIndexes(
            @NotNull Long ownerId,
            @NotNull Long relatedUserId) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(relatedUserId, "relatedUserId");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Filter filter = Filter.newBuilder(2)
                .eq(UserRelationshipGroupMember.Fields.ID_OWNER_ID, ownerId)
                .eq(UserRelationshipGroupMember.Fields.ID_RELATED_USER_ID, relatedUserId);
        QueryOptions options = QueryOptions.newBuilder(1)
                .include(UserRelationshipGroupMember.Fields.ID_GROUP_INDEX);
        return mongoClient.findMany(UserRelationshipGroupMember.class, filter, options)
                .map(member -> member.getKey().getGroupIndex());
    }

    public Flux<Long> queryRelationshipGroupMemberIds(
            @NotNull Long ownerId,
            @NotNull Integer groupIndex) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(groupIndex, "groupIndex");
        } catch (TurmsBusinessException e) {
            return Flux.error(e);
        }
        Filter filter = Filter.newBuilder(2)
                .eq(UserRelationshipGroupMember.Fields.ID_OWNER_ID, ownerId)
                .eq(UserRelationshipGroupMember.Fields.ID_GROUP_INDEX, groupIndex);
        QueryOptions options = QueryOptions.newBuilder(1)
                .include(UserRelationshipGroupMember.Fields.ID_RELATED_USER_ID);
        return mongoClient.findMany(UserRelationshipGroupMember.class, filter, options)
                .map(member -> member.getKey().getRelatedUserId());
    }

    public Flux<Long> queryRelationshipGroupMemberIds(
            @Nullable Set<Long> ownerIds,
            @Nullable Set<Integer> groupIndexes,
            @Nullable Integer page,
            @Nullable Integer size) {
        Filter filter = Filter.newBuilder(2)
                .inIfNotNull(UserRelationshipGroupMember.Fields.ID_OWNER_ID, ownerIds)
                .inIfNotNull(UserRelationshipGroupMember.Fields.ID_GROUP_INDEX, groupIndexes);
        QueryOptions options = QueryOptions.newBuilder(3)
                .paginateIfNotNull(page, size)
                .include(UserRelationshipGroupMember.Fields.ID_RELATED_USER_ID);
        return mongoClient.findMany(UserRelationshipGroupMember.class, filter, options)
                .map(member -> member.getKey().getRelatedUserId());
    }

    public Mono<UpdateResult> updateRelationshipGroupName(
            @NotNull Long ownerId,
            @NotNull Integer groupIndex,
            @NotNull String newGroupName) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(groupIndex, "groupIndex");
            AssertUtil.notNull(newGroupName, "newGroupName");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        UserRelationshipGroup.Key key = new UserRelationshipGroup.Key(ownerId, groupIndex);
        Filter filter = Filter.newBuilder(1)
                .eq(DomainFieldName.ID, key);
        Update update = Update.newBuilder(1)
                .set(UserRelationshipGroup.Fields.NAME, newGroupName);
        return mongoClient.updateOne(UserRelationshipGroup.class, filter, update)
                .flatMap(result -> userVersionService.updateRelationshipGroupsVersion(ownerId)
                        .onErrorResume(t -> {
                            LOGGER.error("Caught an error while updating the relationship groups version of the owner {} after updating a relationship group name",
                                    ownerId, t);
                            return Mono.empty();
                        })
                        .thenReturn(result));
    }

    public Mono<UpdateResult> updateRelationshipGroups(
            @NotEmpty Set<UserRelationshipGroup.@ValidUserRelationshipGroupKey Key> keys,
            @Nullable String name,
            @Nullable @PastOrPresent Date creationDate) {
        try {
            AssertUtil.notEmpty(keys, "keys");
            for (UserRelationshipGroup.Key key : keys) {
                DomainConstraintUtil.validRelationshipGroupKey(key);
            }
            AssertUtil.pastOrPresent(creationDate, "creationDate");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (name == null && creationDate == null) {
            return Mono.just(OperationResultConstant.ACKNOWLEDGED_UPDATE_RESULT);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, keys);
        Update update = Update
                .newBuilder(2)
                .setIfNotNull(UserRelationshipGroup.Fields.NAME, name)
                .setIfNotNull(UserRelationshipGroup.Fields.CREATION_DATE, creationDate);
        return mongoClient.updateMany(UserRelationshipGroup.class, filter, update);
    }

    public Mono<UserRelationshipGroupMember> addRelatedUserToRelationshipGroups(
            @NotNull Long ownerId,
            @NotNull Integer groupIndex,
            @NotNull Long relatedUserId,
            @Nullable ClientSession session) {
        try {
            AssertUtil.notNull(groupIndex, "groupIndex");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return userRelationshipService.hasOneSidedRelationship(ownerId, relatedUserId)
                .flatMap(hasRelationship -> {
                    if (!hasRelationship) {
                        return Mono.error(TurmsBusinessException.get(TurmsStatusCode.ADD_NOT_RELATED_USER_TO_GROUP));
                    }
                    UserRelationshipGroupMember member = new UserRelationshipGroupMember(
                            ownerId, groupIndex, relatedUserId, new Date());
                    return mongoClient.upsert(session, member)
                            .flatMap(groupMember -> userVersionService.updateRelationshipGroupsVersion(ownerId)
                                    .onErrorResume(t -> {
                                        LOGGER.error("Caught an error while updating the relationship groups version of the owner {} after adding a user to the groups",
                                                ownerId, t);
                                        return Mono.empty();
                                    }))
                            .thenReturn(member);
                });
    }

    public Mono<Void> deleteRelationshipGroupAndMoveMembers(
            @NotNull Long ownerId,
            @NotNull Integer deleteGroupIndex,
            @NotNull Integer newGroupIndex) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(deleteGroupIndex, "deleteGroupIndex");
            AssertUtil.notNull(newGroupIndex, "newGroupIndex");
            AssertUtil.state(!deleteGroupIndex.equals(DEFAULT_RELATIONSHIP_GROUP_INDEX),
                    "The default relationship group cannot be deleted");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (deleteGroupIndex.equals(newGroupIndex)) {
            return Mono.empty();
        }
        Filter filterMember = Filter.newBuilder(2)
                .eq(UserRelationshipGroupMember.Fields.ID_OWNER_ID, ownerId)
                .eq(UserRelationshipGroupMember.Fields.ID_GROUP_INDEX, deleteGroupIndex);
        UserRelationshipGroup.Key key = new UserRelationshipGroup.Key(ownerId, deleteGroupIndex);
        Filter filterGroup = Filter.newBuilder(1)
                .eq(DomainFieldName.ID, key);
        // Don't use transaction for better performance
        return mongoClient.findMany(UserRelationshipGroupMember.class, filterMember)
                .collectList()
                .flatMap(members -> {
                    if (members.isEmpty()) {
                        return Mono.empty();
                    }
                    List<UserRelationshipGroupMember> newMembers = new ArrayList<>(members.size());
                    Date now = new Date();
                    for (UserRelationshipGroupMember member : members) {
                        UserRelationshipGroupMember.Key memberKey = member.getKey();
                        UserRelationshipGroupMember.Key newKey = new UserRelationshipGroupMember
                                .Key(memberKey.getOwnerId(), newGroupIndex, memberKey.getRelatedUserId());
                        newMembers.add(new UserRelationshipGroupMember(newKey, now));
                    }
                    return mongoClient.insertAllOfSameType(newMembers)
                            .onErrorResume(DuplicateKeyException.class, e -> Mono.empty());
                })
                .then(mongoClient.deleteOne(UserRelationshipGroup.class, filterGroup))
                .then(userVersionService.updateRelationshipGroupsVersion(ownerId)
                        .onErrorResume(t -> {
                            LOGGER.error("Caught an error while updating the relationship groups version of the owner {} after deleting relationships",
                                    ownerId, t);
                            return Mono.empty();
                        }))
                .then();
    }

    public Mono<DeleteResult> deleteAllRelationshipGroups(
            @NotEmpty Set<Long> ownerIds,
            @Nullable ClientSession session,
            boolean updateRelationshipGroupsVersion) {
        try {
            AssertUtil.notEmpty(ownerIds, "ownerIds");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(2)
                .in(UserRelationshipGroup.Fields.ID_OWNER_ID, ownerIds)
                .ne(UserRelationshipGroup.Fields.ID_GROUP_INDEX, 0);
        if (updateRelationshipGroupsVersion) {
            return mongoClient.deleteMany(session, UserRelationshipGroup.class, filter)
                    .flatMap(result -> userVersionService.updateRelationshipGroupsVersion(ownerIds)
                            .onErrorResume(t -> {
                                LOGGER.error("Caught an error while updating the relationship groups version of the owners {} after deleting all groups",
                                        ownerIds, t);
                                return Mono.empty();
                            })
                            .thenReturn(result));
        }
        return mongoClient.deleteMany(session, UserRelationshipGroup.class, filter);
    }

    public Mono<DeleteResult> deleteRelatedUserFromAllRelationshipGroups(
            @NotNull Long ownerId,
            @NotNull Long relatedUserId,
            @Nullable ClientSession session,
            boolean updateRelationshipGroupsMembersVersion) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(relatedUserId, "relatedUserId");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        return deleteRelatedUsersFromAllRelationshipGroups(Set.of(new UserRelationship.Key(ownerId, relatedUserId)), session,
                updateRelationshipGroupsMembersVersion);
    }

    public Mono<DeleteResult> deleteRelatedUsersFromAllRelationshipGroups(
            @NotEmpty Set<UserRelationship.@ValidUserRelationshipKey Key> keys,
            @Nullable ClientSession session,
            boolean updateRelationshipGroupsMembersVersion) {
        try {
            AssertUtil.notEmpty(keys, "keys");
            for (UserRelationship.Key key : keys) {
                DomainConstraintUtil.validRelationshipKey(key);
            }
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, keys);
        if (updateRelationshipGroupsMembersVersion) {
            return mongoClient.deleteMany(session, UserRelationshipGroupMember.class, filter)
                    .flatMap(result -> {
                        Set<Long> ownerIds = CollectionUtil.newSetWithExpectedSize(keys.size());
                        for (UserRelationship.Key key : keys) {
                            ownerIds.add(key.getOwnerId());
                        }
                        return userVersionService.updateRelationshipGroupsVersion(ownerIds)
                                .onErrorResume(t -> {
                                    LOGGER.error("Caught an error while updating the relationship groups version of the owners {} after deleting users from all groups",
                                            ownerIds, t);
                                    return Mono.empty();
                                })
                                .thenReturn(result);
                    });
        }
        return mongoClient.deleteMany(session, UserRelationshipGroupMember.class, filter);
    }

    public Mono<Void> moveRelatedUserToNewGroup(
            @NotNull Long ownerId,
            @NotNull Long relatedUserId,
            @NotNull Integer currentGroupIndex,
            @NotNull Integer targetGroupIndex) {
        try {
            AssertUtil.notNull(ownerId, "ownerId");
            AssertUtil.notNull(relatedUserId, "relatedUserId");
            AssertUtil.notNull(currentGroupIndex, "currentGroupIndex");
            AssertUtil.notNull(targetGroupIndex, "targetGroupIndex");
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        if (currentGroupIndex.equals(targetGroupIndex)) {
            return Mono.empty();
        }
        UserRelationshipGroupMember.Key key = new UserRelationshipGroupMember.Key(ownerId, currentGroupIndex, relatedUserId);
        Filter filter = Filter.newBuilder(1)
                .eq(DomainFieldName.ID, key);
        UserRelationshipGroupMember.Key newKey = new UserRelationshipGroupMember
                .Key(ownerId, targetGroupIndex, relatedUserId);
        // Don't use transaction for better performance
        return mongoClient.insert(new UserRelationshipGroupMember(newKey, new Date()))
                .then(mongoClient.deleteOne(UserRelationshipGroupMember.class, filter))
                .then(userVersionService.updateRelationshipGroupsVersion(ownerId)
                        .onErrorResume(t -> {
                            LOGGER.error("Caught an error while updating the relationship groups version of the owner {} after moving a user to a new group",
                                    ownerId, t);
                            return Mono.empty();
                        }))
                .then();
    }

    public Mono<DeleteResult> deleteRelationshipGroups() {
        return mongoClient.deleteAll(UserRelationshipGroup.class);
    }

    public Mono<DeleteResult> deleteRelationshipGroups(@NotEmpty Set<UserRelationshipGroup.@ValidUserRelationshipGroupKey Key> keys) {
        try {
            AssertUtil.notEmpty(keys, "keys");
            for (UserRelationshipGroup.Key key : keys) {
                DomainConstraintUtil.validRelationshipGroupKey(key);
            }
        } catch (TurmsBusinessException e) {
            return Mono.error(e);
        }
        Filter filter = Filter.newBuilder(1)
                .in(DomainFieldName.ID, keys);
        return mongoClient.deleteMany(UserRelationshipGroup.class, filter);
    }

    public Flux<UserRelationshipGroup> queryRelationshipGroups(
            @Nullable Set<Long> ownerIds,
            @Nullable Set<Integer> indexes,
            @Nullable Set<String> names,
            @Nullable DateRange creationDateRange,
            @Nullable Integer page,
            @Nullable Integer size) {
        Filter filter = Filter.newBuilder(5)
                .inIfNotNull(UserRelationshipGroup.Fields.ID_OWNER_ID, ownerIds)
                .inIfNotNull(UserRelationshipGroup.Fields.ID_GROUP_INDEX, indexes)
                .inIfNotNull(UserRelationshipGroup.Fields.NAME, names)
                .addBetweenIfNotNull(UserRelationshipGroup.Fields.CREATION_DATE, creationDateRange);
        QueryOptions options = QueryOptions.newBuilder(2)
                .paginateIfNotNull(page, size);
        return mongoClient.findMany(UserRelationshipGroup.class, filter, options);
    }

    public Mono<Long> countRelationshipGroups(
            @Nullable Set<Long> ownerIds,
            @Nullable Set<Integer> indexes,
            @Nullable Set<String> names,
            @Nullable DateRange creationDateRange) {
        Filter filter = Filter.newBuilder(5)
                .inIfNotNull(UserRelationshipGroup.Fields.ID_OWNER_ID, ownerIds)
                .inIfNotNull(UserRelationshipGroup.Fields.ID_GROUP_INDEX, indexes)
                .inIfNotNull(UserRelationshipGroup.Fields.NAME, names)
                .addBetweenIfNotNull(UserRelationshipGroup.Fields.CREATION_DATE, creationDateRange);
        return mongoClient.count(UserRelationshipGroup.class, filter);
    }

}
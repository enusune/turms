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

package im.turms.service.proto;

import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import im.turms.common.constant.DeviceType;
import im.turms.common.constant.GroupMemberRole;
import im.turms.common.constant.ProfileAccessStrategy;
import im.turms.common.constant.RequestStatus;
import im.turms.common.constant.UserStatus;
import im.turms.common.model.bo.conversation.GroupConversation;
import im.turms.common.model.bo.conversation.PrivateConversation;
import im.turms.common.model.bo.group.Group;
import im.turms.common.model.bo.group.GroupInvitation;
import im.turms.common.model.bo.group.GroupJoinQuestion;
import im.turms.common.model.bo.group.GroupJoinRequest;
import im.turms.common.model.bo.group.GroupMember;
import im.turms.common.model.bo.user.NearbyUser;
import im.turms.common.model.bo.user.UserFriendRequest;
import im.turms.common.model.bo.user.UserInfo;
import im.turms.common.model.bo.user.UserLocation;
import im.turms.common.model.bo.user.UserRelationship;
import im.turms.common.model.bo.user.UserRelationshipGroup;
import im.turms.common.model.bo.user.UserStatusDetail;
import im.turms.common.model.dto.request.message.CreateMessageRequest;
import im.turms.server.common.bo.session.UserSessionId;
import im.turms.server.common.bo.session.UserSessionsStatus;
import im.turms.server.common.dao.domain.User;
import im.turms.service.workflow.dao.domain.message.Message;
import io.lettuce.core.GeoCoordinates;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author James Chen
 */
public final class ProtoModelConvertor {

    private ProtoModelConvertor() {
    }

    // Transformation

    public static im.turms.common.model.bo.message.Message.Builder message2proto(Message message) {
        var builder = im.turms.common.model.bo.message.Message.newBuilder();
        Long messageId = message.getId();
        Boolean isSystemMessage = message.getIsSystemMessage();
        Date deliveryDate = message.getDeliveryDate();
        Date modificationDate = message.getModificationDate();
        String text = message.getText();
        List<byte[]> records = message.getRecords();
        Long senderId = message.getSenderId();
        Long targetId = message.getTargetId();
        Long groupId = message.groupId();
        Integer sequenceId = message.getSequenceId();
        Long preMessageId = message.getPreMessageId();
        if (messageId != null) {
            builder.setId(messageId);
        }
        if (isSystemMessage != null) {
            builder.setIsSystemMessage(isSystemMessage);
        }
        if (deliveryDate != null) {
            builder.setDeliveryDate(deliveryDate.getTime());
        }
        if (modificationDate != null) {
            builder.setModificationDate(modificationDate.getTime());
        }
        if (text != null) {
            builder.setText(text);
        }
        if (senderId != null) {
            builder.setSenderId(senderId);
        }
        if (targetId != null) {
            builder.setRecipientId(targetId);
        }
        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (records != null && !records.isEmpty()) {
            for (byte[] record : records) {
                builder.addRecords(ByteString.copyFrom(record));
            }
        }
        if (sequenceId != null) {
            builder.setSequenceId(sequenceId);
        }
        if (preMessageId != null) {
            builder.setPreMessageId(preMessageId);
        }
        return builder;
    }

    public static UserInfo.Builder userProfile2proto(@NotNull User user) {
        UserInfo.Builder builder = UserInfo.newBuilder();
        Long userId = user.getId();
        String name = user.getName();
        String intro = user.getIntro();
        Date registrationDate = user.getRegistrationDate();
        Boolean active = user.getIsActive();
        ProfileAccessStrategy profileAccess = user.getProfileAccess();
        if (userId != null) {
            builder.setId(userId);
        }
        if (name != null) {
            builder.setName(name);
        }
        if (intro != null) {
            builder.setIntro(intro);
        }
        if (registrationDate != null) {
            builder.setRegistrationDate(registrationDate.getTime());
        }
        if (active != null) {
            builder.setActive(active);
        }
        if (profileAccess != null) {
            builder.setProfileAccessStrategy(profileAccess);
        }
        return builder;
    }

    public static UserStatusDetail.Builder userOnlineInfo2userStatus(
            @NotNull Long userId,
            @Nullable UserSessionsStatus userSessionsStatus,
            boolean convertInvisibleToOffline) {
        UserStatusDetail.Builder builder = UserStatusDetail.newBuilder()
                .setUserId(userId);
        if (userSessionsStatus == null) {
            builder.setUserStatus(UserStatus.OFFLINE);
        } else {
            builder.setUserStatus(userSessionsStatus.getUserStatus(convertInvisibleToOffline));
            builder.addAllUsingDeviceTypes(userSessionsStatus.getLoggedInDeviceTypes());
        }
        return builder;
    }

    public static GroupMember.Builder userOnlineInfo2groupMember(
            @NotNull Long userId,
            @Nullable UserSessionsStatus userSessionsStatus,
            boolean convertInvisibleToOffline) {
        GroupMember.Builder builder = GroupMember
                .newBuilder()
                .setUserId(userId);
        if (userSessionsStatus == null) {
            builder.setUserStatus(UserStatus.OFFLINE);
        } else {
            builder.setUserStatus(userSessionsStatus.getUserStatus(convertInvisibleToOffline));
            builder.addAllUsingDeviceTypes(userSessionsStatus.getLoggedInDeviceTypes());
        }
        return builder;
    }

    public static NearbyUser.Builder nearbyUser2proto(@NotNull im.turms.server.common.bo.location.NearbyUser nearbyUser) {
        NearbyUser.Builder builder = NearbyUser
                .newBuilder();
        UserSessionId sessionId = nearbyUser.sessionId();
        Long userId = sessionId.userId();
        DeviceType deviceType = sessionId.deviceType();
        GeoCoordinates coordinates = nearbyUser.coordinates();
        Integer distance = nearbyUser.distance();
        User info = nearbyUser.info();
        if (userId != null) {
            builder.setUserId(userId);
        }
        if (deviceType != null) {
            builder.setDeviceType(deviceType);
        }
        if (coordinates != null) {
            builder.setLocation(UserLocation.newBuilder()
                    .setLongitude(coordinates.getX().floatValue())
                    .setLatitude(coordinates.getY().floatValue())
                    .build());
        }
        if (distance != null) {
            builder.setDistance(distance);
        }
        if (info != null) {
            builder.setInfo(userProfile2proto(info));
        }
        return builder;
    }

    public static UserFriendRequest.Builder friendRequest2proto(
            @NotNull im.turms.service.workflow.dao.domain.user.UserFriendRequest userFriendRequest,
            int expireAfterSeconds) {
        UserFriendRequest.Builder builder = UserFriendRequest.newBuilder();
        Long requestId = userFriendRequest.getId();
        Date creationDate = userFriendRequest.getCreationDate();
        String content = userFriendRequest.getContent();
        RequestStatus status = userFriendRequest.getStatus();
        String reason = userFriendRequest.getReason();
        Long requesterId = userFriendRequest.getRequesterId();
        Long recipientId = userFriendRequest.getRecipientId();
        if (requestId != null) {
            builder.setId(requestId);
        }
        if (creationDate != null) {
            builder.setCreationDate(creationDate.getTime());
            if (expireAfterSeconds > 0) {
                builder.setExpirationDate(creationDate.getTime() + expireAfterSeconds * 1000L);
            }
        }
        if (content != null) {
            builder.setContent(content);
        }
        if (status != null) {
            builder.setRequestStatus(status);
        }
        if (reason != null) {
            builder.setReason(reason);
        }
        if (requesterId != null) {
            builder.setRequesterId(requesterId);
        }
        if (recipientId != null) {
            builder.setRecipientId(recipientId);
        }
        return builder;
    }

    public static UserRelationship.Builder relationship2proto(
            @NotNull im.turms.service.workflow.dao.domain.user.UserRelationship relationship) {
        UserRelationship.Builder builder = UserRelationship.newBuilder();
        im.turms.service.workflow.dao.domain.user.UserRelationship.Key key = relationship.getKey();
        Date establishmentDate = relationship.getEstablishmentDate();
        Date blockDate = relationship.getBlockDate();
        if (key != null) {
            Long ownerId = key.getOwnerId();
            Long relatedUserId = key.getRelatedUserId();
            if (ownerId != null) {
                builder.setOwnerId(ownerId);
            }
            if (relatedUserId != null) {
                builder.setRelatedUserId(relatedUserId);
            }
        }
        if (blockDate != null) {
            builder.setBlockDate(blockDate.getTime());
        }
        if (establishmentDate != null) {
            builder.setEstablishmentDate(establishmentDate.getTime());
        }
        return builder;
    }

    public static UserRelationshipGroup.Builder relationshipGroup2proto(
            @NotNull im.turms.service.workflow.dao.domain.user.UserRelationshipGroup relationshipGroup) {
        UserRelationshipGroup.Builder builder = UserRelationshipGroup.newBuilder();
        im.turms.service.workflow.dao.domain.user.UserRelationshipGroup.Key key = relationshipGroup.getKey();
        if (key != null) {
            Integer index = key.getGroupIndex();
            if (index != null) {
                builder.setIndex(index);
            }
        }
        String name = relationshipGroup.getName();
        if (name != null) {
            builder.setName(name);
        }
        return builder;
    }

    public static Group.Builder group2proto(@NotNull im.turms.service.workflow.dao.domain.group.Group group) {
        Group.Builder builder = Group.newBuilder();
        Long groupId = group.getId();
        Long typeId = group.getTypeId();
        Long creatorId = group.getCreatorId();
        Long ownerId = group.getOwnerId();
        String name = group.getName();
        String intro = group.getIntro();
        String announcement = group.getAnnouncement();
        Date creationDate = group.getCreationDate();
        Date muteEndDate = group.getMuteEndDate();
        Boolean active = group.getIsActive();
        if (groupId != null) {
            builder.setId(groupId);
        }
        if (typeId != null) {
            builder.setTypeId(typeId);
        }
        if (creatorId != null) {
            builder.setCreatorId(creatorId);
        }
        if (ownerId != null) {
            builder.setOwnerId(ownerId);
        }
        if (name != null) {
            builder.setName(name);
        }
        if (intro != null) {
            builder.setIntro(intro);
        }
        if (announcement != null) {
            builder.setAnnouncement(announcement);
        }
        if (creationDate != null) {
            builder.setCreationDate(creationDate.getTime());
        }
        if (muteEndDate != null) {
            builder.setMuteEndDate(muteEndDate.getTime());
        }
        if (active != null) {
            builder.setActive(active);
        }
        return builder;
    }

    public static GroupInvitation.Builder groupInvitation2proto(
            @NotNull im.turms.service.workflow.dao.domain.group.GroupInvitation invitation,
            int expireAfterSeconds) {
        GroupInvitation.Builder builder = GroupInvitation.newBuilder();
        Long invitationId = invitation.getId();
        Date creationDate = invitation.getCreationDate();
        String content = invitation.getContent();
        RequestStatus status = invitation.getStatus();
        Long groupId = invitation.getGroupId();
        Long inviterId = invitation.getInviterId();
        Long inviteeId = invitation.getInviteeId();
        if (invitationId != null) {
            builder.setId(invitationId);
        }
        if (creationDate != null) {
            builder.setCreationDate(creationDate.getTime());
            if (expireAfterSeconds > 0) {
                builder.setExpirationDate(creationDate.getTime() + expireAfterSeconds * 1000L);
            }
        }
        if (content != null) {
            builder.setContent(content);
        }
        if (status != null) {
            builder.setStatusValue(status.getNumber());
        }
        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (inviterId != null) {
            builder.setInviterId(inviterId);
        }
        if (inviteeId != null) {
            builder.setInviteeId(inviteeId);
        }
        return builder;
    }

    public static GroupJoinRequest.Builder groupJoinRequest2proto(
            @NotNull im.turms.service.workflow.dao.domain.group.GroupJoinRequest groupJoinRequest,
            int expireAfterSeconds) {
        GroupJoinRequest.Builder builder = GroupJoinRequest.newBuilder();
        Long requestId = groupJoinRequest.getId();
        Date creationDate = groupJoinRequest.getCreationDate();
        String content = groupJoinRequest.getContent();
        RequestStatus status = groupJoinRequest.getStatus();
        Long groupId = groupJoinRequest.getGroupId();
        Long requesterId = groupJoinRequest.getRequesterId();
        Long responderId = groupJoinRequest.getResponderId();
        if (requestId != null) {
            builder.setId(requestId);
        }
        if (creationDate != null) {
            builder.setCreationDate(creationDate.getTime());
            if (expireAfterSeconds > 0) {
                builder.setExpirationDate(creationDate.getTime() + expireAfterSeconds * 1000L);
            }
        }
        if (content != null) {
            builder.setContent(content);
        }
        if (status != null) {
            builder.setStatus(status);
        }
        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (requesterId != null) {
            builder.setRequesterId(requesterId);
        }
        if (responderId != null) {
            builder.setResponderId(responderId);
        }
        return builder;
    }

    public static GroupJoinQuestion.Builder groupJoinQuestion2proto(
            @NotNull im.turms.service.workflow.dao.domain.group.GroupJoinQuestion question) {
        GroupJoinQuestion.Builder builder = GroupJoinQuestion.newBuilder();
        Long questionId = question.getId();
        Long groupId = question.getGroupId();
        String content = question.getQuestion();
        if (questionId != null) {
            builder.setId(questionId);
        }
        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (content != null) {
            builder.setQuestion(content);
        }
        if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
            builder.addAllAnswers(question.getAnswers());
        }
        return builder;
    }

    public static GroupMember.Builder groupMember2proto(@NotNull im.turms.service.workflow.dao.domain.group.GroupMember groupMember) {
        GroupMember.Builder builder = GroupMember.newBuilder();
        im.turms.service.workflow.dao.domain.group.GroupMember.Key key = groupMember.getKey();
        if (key != null) {
            Long groupId = key.getGroupId();
            Long userId = key.getUserId();
            if (groupId != null) {
                builder.setGroupId(groupId);
            }
            if (userId != null) {
                builder.setUserId(userId);
            }
        }
        String name = groupMember.getName();
        GroupMemberRole role = groupMember.getRole();
        Date joinDate = groupMember.getJoinDate();
        Date muteEndDate = groupMember.getMuteEndDate();
        if (name != null) {
            builder.setName(name);
        }
        if (role != null) {
            builder.setRole(role);
        }
        if (joinDate != null) {
            builder.setJoinDate(joinDate.getTime());
        }
        if (muteEndDate != null) {
            builder.setMuteEndDate(muteEndDate.getTime());
        }
        return builder;
    }

    // Conversation

    public static PrivateConversation.Builder privateConversation2proto(
            im.turms.service.workflow.dao.domain.conversation.PrivateConversation privateConversation) {
        PrivateConversation.Builder builder = PrivateConversation.newBuilder();
        im.turms.service.workflow.dao.domain.conversation.PrivateConversation.Key key = privateConversation.getKey();
        if (key != null) {
            Long ownerId = key.getOwnerId();
            Long targetId = key.getTargetId();
            if (ownerId != null) {
                builder.setOwnerId(ownerId);
            }
            if (targetId != null) {
                builder.setTargetId(targetId);
            }
        }
        Date readDate = privateConversation.getReadDate();
        if (readDate != null) {
            builder.setReadDate(readDate.getTime());
        }
        return builder;
    }

    public static GroupConversation.Builder groupConversations2proto(
            im.turms.service.workflow.dao.domain.conversation.GroupConversation groupConversation) {
        GroupConversation.Builder builder = GroupConversation.newBuilder();
        Long groupId = groupConversation.getGroupId();
        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        Map<Long, Date> memberIdAndReadDate = groupConversation.getMemberIdAndReadDate();
        if (memberIdAndReadDate != null) {
            Map<Long, Long> map = Maps.transformValues(memberIdAndReadDate, Date::getTime);
            builder.putAllMemberIdAndReadDate(map);
        }
        return builder;
    }

    public static CreateMessageRequest.Builder message2createMessageRequest(Message message) {
        CreateMessageRequest.Builder builder = CreateMessageRequest.newBuilder();
        Long messageId = message.getId();
        Boolean isGroupMessage = message.getIsGroupMessage();
        Boolean isSystemMessage = message.getIsSystemMessage();
        Date deliveryDate = message.getDeliveryDate();
        String text = message.getText();
        // message.getSenderId() is just requesterId
        Long targetId = message.getTargetId();
        List<byte[]> records = message.getRecords();
        Integer burnAfter = message.getBurnAfter();
        if (messageId != null) {
            builder.setMessageId(messageId);
        }
        if (isGroupMessage) {
            builder.setGroupId(targetId);
        } else {
            builder.setRecipientId(targetId);
        }
        if (isSystemMessage != null) {
            builder.setIsSystemMessage(isSystemMessage);
        }
        if (deliveryDate != null) {
            builder.setDeliveryDate(deliveryDate.getTime());
        }
        if (text != null) {
            builder.setText(text);
        }
        if (records != null && !records.isEmpty()) {
            for (byte[] record : records) {
                ByteString byteString = ByteString.copyFrom(record);
                builder.addRecords(byteString);
            }
        }
        if (burnAfter != null) {
            builder.setBurnAfter(burnAfter);
        }
        return builder;
    }

    public static CreateMessageRequest.Builder cloneAndFillMessageRequest(
            @NotNull CreateMessageRequest request,
            @NotNull Message message) {
        CreateMessageRequest.Builder builder = request.toBuilder();
        Long messageId = message.getId();
        Boolean isSystemMessage = message.getIsSystemMessage();
        Date deliveryDate = message.getDeliveryDate();
        String text = message.getText();
        List<byte[]> records = message.getRecords();
        Integer burnAfter = message.getBurnAfter();

        if (messageId != null) {
            builder.setMessageId(messageId);
        } else {
            builder.clearMessageId();
        }
        builder.setIsSystemMessage(isSystemMessage);
        builder.setDeliveryDate(deliveryDate.getTime());
        if (text != null) {
            builder.setText(text);
        }
        if (records != null) {
            for (byte[] record : records) {
                builder.addRecords(ByteString.copyFrom(record));
            }
        }
        if (burnAfter != null) {
            builder.setBurnAfter(burnAfter);
        }
        return builder;
    }

}

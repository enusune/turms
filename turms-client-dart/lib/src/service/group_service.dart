import 'package:fixnum/fixnum.dart' show Int64;

import '../../turms_client.dart';
import '../extension/date_time_extensions.dart';
import '../extension/int_extensions.dart';
import '../extension/iterable_extensions.dart';
import '../extension/notification_extensions.dart';
import '../model/constant/group_member_role.pbenum.dart';
import '../model/group_with_version.dart';
import '../model/model/common/int64_values_with_version.pb.dart';
import '../model/model/group/group_invitations_with_version.pb.dart';
import '../model/model/group/group_join_questions_answer_result.pb.dart';
import '../model/model/group/group_join_questions_with_version.pb.dart';
import '../model/model/group/group_join_requests_with_version.pb.dart';
import '../model/model/group/group_members_with_version.pb.dart';
import '../model/model/group/groups_with_version.pb.dart';
import '../model/model/user/users_infos_with_version.pb.dart';
import '../model/request/group/blocklist/create_group_blocked_user_request.pb.dart';
import '../model/request/group/blocklist/delete_group_blocked_user_request.pb.dart';
import '../model/request/group/blocklist/query_group_blocked_user_ids_request.pb.dart';
import '../model/request/group/blocklist/query_group_blocked_user_infos_request.pb.dart';
import '../model/request/group/create_group_request.pb.dart';
import '../model/request/group/delete_group_request.pb.dart';
import '../model/request/group/enrollment/check_group_join_questions_answers_request.pb.dart';
import '../model/request/group/enrollment/create_group_invitation_request.pb.dart';
import '../model/request/group/enrollment/create_group_join_question_request.pb.dart';
import '../model/request/group/enrollment/create_group_join_request_request.pb.dart';
import '../model/request/group/enrollment/delete_group_invitation_request.pb.dart';
import '../model/request/group/enrollment/delete_group_join_question_request.pb.dart';
import '../model/request/group/enrollment/delete_group_join_request_request.pb.dart';
import '../model/request/group/enrollment/query_group_invitations_request.pb.dart';
import '../model/request/group/enrollment/query_group_join_questions_request.pb.dart';
import '../model/request/group/enrollment/query_group_join_requests_request.pb.dart';
import '../model/request/group/enrollment/update_group_join_question_request.pb.dart';
import '../model/request/group/member/create_group_member_request.pb.dart';
import '../model/request/group/member/delete_group_member_request.pb.dart';
import '../model/request/group/member/query_group_members_request.pb.dart';
import '../model/request/group/member/update_group_member_request.pb.dart';
import '../model/request/group/query_group_request.pb.dart';
import '../model/request/group/query_joined_group_ids_request.pb.dart';
import '../model/request/group/query_joined_group_infos_request.pb.dart';
import '../model/request/group/update_group_request.pb.dart';
import '../model/turms_business_exception.dart';
import '../model/turms_status_code.dart';

class GroupService {
  final TurmsClient _turmsClient;

  GroupService(this._turmsClient);

  Future<Int64> createGroup(String name,
      {String? intro,
      String? announcement,
      int? minimumScore,
      DateTime? muteEndDate,
      Int64? groupTypeId}) async {
    final n = await _turmsClient.driver.send(CreateGroupRequest(
        name: name,
        intro: intro,
        announcement: announcement,
        minimumScore: minimumScore,
        muteEndDate: muteEndDate?.toInt64(),
        groupTypeId: groupTypeId));
    return n.getFirstIdOrThrow();
  }

  Future<void> deleteGroup(Int64 groupId) async {
    await _turmsClient.driver.send(DeleteGroupRequest(groupId: groupId));
  }

  Future<void> updateGroup(Int64 groupId,
      {String? groupName,
      String? intro,
      String? announcement,
      int? minimumScore,
      Int64? groupTypeId,
      DateTime? muteEndDate,
      Int64? successorId,
      bool? quitAfterTransfer}) async {
    if ([
      groupName,
      intro,
      announcement,
      minimumScore,
      groupTypeId,
      muteEndDate,
      successorId
    ].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateGroupRequest(
        groupId: groupId,
        groupName: groupName,
        intro: intro,
        announcement: announcement,
        muteEndDate: muteEndDate?.toInt64(),
        minimumScore: minimumScore,
        groupTypeId: groupTypeId,
        successorId: successorId,
        quitAfterTransfer: quitAfterTransfer));
  }

  Future<void> transferOwnership(Int64 groupId, Int64 successorId,
          {bool quitAfterTransfer = false}) =>
      updateGroup(groupId,
          successorId: successorId, quitAfterTransfer: quitAfterTransfer);

  Future<void> muteGroup(Int64 groupId, DateTime muteEndDate) =>
      updateGroup(groupId, muteEndDate: muteEndDate);

  Future<void> unmuteGroup(Int64 groupId) => muteGroup(groupId, DateTime(0));

  Future<GroupWithVersion?> queryGroup(Int64 groupId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupRequest(
        groupId: groupId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    final groupsWithVersion = n.data.groupsWithVersion;
    if (groupsWithVersion.groups.isEmpty) {
      return null;
    }
    final date = groupsWithVersion.hasLastUpdatedDate()
        ? groupsWithVersion.lastUpdatedDate.toDateTime()
        : null;
    return GroupWithVersion(groupsWithVersion.groups[0], date);
  }

  Future<Int64ValuesWithVersion?> queryJoinedGroupIds(
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryJoinedGroupIdsRequest(
        lastUpdatedDate: lastUpdatedDate == null
            ? null
            : Int64(lastUpdatedDate.millisecondsSinceEpoch)));
    return n.data.hasIdsWithVersion() ? n.data.idsWithVersion : null;
  }

  Future<GroupsWithVersion?> queryJoinedGroupInfos(
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryJoinedGroupInfosRequest(
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupsWithVersion() ? n.data.groupsWithVersion : null;
  }

  Future<Int64> addGroupJoinQuestion(
      Int64 groupId, String question, Set<String> answers, int score) async {
    if (answers.isEmpty) {
      throw TurmsBusinessException(
          TurmsStatusCode.illegalArgument, 'answers must not be empty');
    }
    final n = await _turmsClient.driver.send(CreateGroupJoinQuestionRequest(
        groupId: groupId, question: question, answers: answers, score: score));
    return n.getFirstIdOrThrow();
  }

  Future<void> deleteGroupJoinQuestion(Int64 questionId) async {
    await _turmsClient.driver
        .send(DeleteGroupJoinQuestionRequest(questionId: questionId));
  }

  Future<void> updateGroupJoinQuestion(Int64 questionId,
      {String? question, Set<String>? answers, int? score}) async {
    if ([question, answers, score].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateGroupJoinQuestionRequest(
        questionId: questionId,
        question: question,
        answers: answers,
        score: score));
  }

  // Group Blocklist
  Future<void> blockUser(Int64 groupId, Int64 userId) async {
    await _turmsClient.driver
        .send(CreateGroupBlockedUserRequest(userId: userId, groupId: groupId));
  }

  Future<void> unblockUser(Int64 groupId, Int64 userId) async {
    await _turmsClient.driver
        .send(DeleteGroupBlockedUserRequest(groupId: groupId, userId: userId));
  }

  Future<Int64ValuesWithVersion?> queryBlockedUserIds(Int64 groupId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupBlockedUserIdsRequest(
        groupId: groupId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasIdsWithVersion() ? n.data.idsWithVersion : null;
  }

  Future<UsersInfosWithVersion?> queryBlockedUserInfos(Int64 groupId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupBlockedUserInfosRequest(
        groupId: groupId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasUsersInfosWithVersion()
        ? n.data.usersInfosWithVersion
        : null;
  }

  // Group Enrollment
  Future<Int64> createInvitation(
      Int64 groupId, Int64 inviteeId, String content) async {
    final n = await _turmsClient.driver.send(CreateGroupInvitationRequest(
        groupId: groupId, inviteeId: inviteeId, content: content));
    return n.getFirstIdOrThrow();
  }

  Future<void> deleteInvitation(Int64 invitationId) async {
    await _turmsClient.driver
        .send(DeleteGroupInvitationRequest(invitationId: invitationId));
  }

  Future<GroupInvitationsWithVersion?> queryInvitationsByGroupId(Int64 groupId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupInvitationsRequest(
        groupId: groupId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupInvitationsWithVersion()
        ? n.data.groupInvitationsWithVersion
        : null;
  }

  Future<GroupInvitationsWithVersion?> queryInvitations(bool areSentByMe,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupInvitationsRequest(
        areSentByMe: areSentByMe, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupInvitationsWithVersion()
        ? n.data.groupInvitationsWithVersion
        : null;
  }

  Future<Int64> createJoinRequest(Int64 groupId, String content) async {
    final n = await _turmsClient.driver.send(
        CreateGroupJoinRequestRequest(groupId: groupId, content: content));
    return n.getFirstIdOrThrow();
  }

  Future<void> deleteJoinRequest(Int64 requestId) async {
    await _turmsClient.driver
        .send(DeleteGroupJoinRequestRequest(requestId: requestId));
  }

  Future<GroupJoinRequestsWithVersion?> queryJoinRequests(Int64 groupId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupJoinRequestsRequest(
        groupId: groupId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupJoinRequestsWithVersion()
        ? n.data.groupJoinRequestsWithVersion
        : null;
  }

  Future<GroupJoinRequestsWithVersion?> querySentJoinRequests(
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupJoinRequestsRequest(
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupJoinRequestsWithVersion()
        ? n.data.groupJoinRequestsWithVersion
        : null;
  }

  /// Note: Only the owner and managers have the right to fetch questions with answers
  Future<GroupJoinQuestionsWithVersion?> queryGroupJoinQuestionsRequest(
      Int64 groupId,
      {bool withAnswers = false,
      DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupJoinQuestionsRequest(
        groupId: groupId,
        withAnswers: withAnswers,
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasGroupJoinQuestionsWithVersion()
        ? n.data.groupJoinQuestionsWithVersion
        : null;
  }

  Future<GroupJoinQuestionsAnswerResult?> answerGroupQuestions(
      Map<Int64, String> questionIdsAndAnswers) async {
    if (questionIdsAndAnswers.isEmpty) {
      throw TurmsBusinessException(TurmsStatusCode.illegalArgument,
          'questionIdsAndAnswers must not be empty');
    }
    final n = await _turmsClient.driver.send(
        CheckGroupJoinQuestionsAnswersRequest(
            questionIdAndAnswer: questionIdsAndAnswers));
    return n.data.hasGroupJoinQuestionAnswerResult()
        ? n.data.groupJoinQuestionAnswerResult
        : throw TurmsBusinessException.fromCode(
            TurmsStatusCode.invalidResponse);
  }

  // Group Member

  Future<void> addGroupMember(Int64 groupId, Int64 userId,
      {String? name, GroupMemberRole? role, DateTime? muteEndDate}) async {
    await _turmsClient.driver.send(CreateGroupMemberRequest(
        groupId: groupId,
        userId: userId,
        name: name,
        role: role,
        muteEndDate: muteEndDate?.toInt64()));
  }

  Future<void> quitGroup(Int64 groupId,
      {Int64? successorId, bool? quitAfterTransfer}) async {
    await _turmsClient.driver.send(DeleteGroupMemberRequest(
        groupId: groupId,
        memberId: _turmsClient.userService.userInfo?.userId,
        successorId: successorId,
        quitAfterTransfer: quitAfterTransfer));
  }

  Future<void> removeGroupMember(Int64 groupId, Int64 memberId) async {
    await _turmsClient.driver
        .send(DeleteGroupMemberRequest(groupId: groupId, memberId: memberId));
  }

  Future<void> updateGroupMemberInfo(Int64 groupId, Int64 memberId,
      {String? name, GroupMemberRole? role, DateTime? muteEndDate}) async {
    if ([name, role, muteEndDate].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateGroupMemberRequest(
        groupId: groupId,
        memberId: memberId,
        name: name,
        role: role,
        muteEndDate: muteEndDate?.toInt64()));
  }

  Future<void> muteGroupMember(
          Int64 groupId, Int64 memberId, DateTime muteEndDate) =>
      updateGroupMemberInfo(groupId, memberId, muteEndDate: muteEndDate);

  Future<void> unmuteGroupMember(Int64 groupId, Int64 memberId) =>
      muteGroupMember(
          groupId, memberId, DateTime.fromMillisecondsSinceEpoch(0));

  Future<GroupMembersWithVersion?> queryGroupMembers(Int64 groupId,
      {bool withStatus = false, DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryGroupMembersRequest(
        groupId: groupId,
        lastUpdatedDate: lastUpdatedDate?.toInt64(),
        withStatus: withStatus));
    return n.data.hasGroupMembersWithVersion()
        ? n.data.groupMembersWithVersion
        : null;
  }

  Future<GroupMembersWithVersion?> queryGroupMembersByMemberIds(
      Int64 groupId, List<Int64> memberIds,
      {bool withStatus = false}) async {
    final n = await _turmsClient.driver.send(QueryGroupMembersRequest(
        groupId: groupId, memberIds: memberIds, withStatus: withStatus));
    return n.data.hasGroupMembersWithVersion()
        ? n.data.groupMembersWithVersion
        : null;
  }
}

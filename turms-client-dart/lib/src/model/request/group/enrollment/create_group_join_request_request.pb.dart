///
//  Generated code. Do not modify.
//  source: request/group/enrollment/create_group_join_request_request.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

import 'dart:core' as $core;

import 'package:fixnum/fixnum.dart' as $fixnum;
import 'package:protobuf/protobuf.dart' as $pb;

class CreateGroupJoinRequestRequest extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'CreateGroupJoinRequestRequest', package: const $pb.PackageName(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'im.turms.proto'), createEmptyInstance: create)
    ..aInt64(1, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'groupId')
    ..aOS(2, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'content')
    ..hasRequiredFields = false
  ;

  CreateGroupJoinRequestRequest._() : super();
  factory CreateGroupJoinRequestRequest({
    $fixnum.Int64? groupId,
    $core.String? content,
  }) {
    final _result = create();
    if (groupId != null) {
      _result.groupId = groupId;
    }
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory CreateGroupJoinRequestRequest.fromBuffer($core.List<$core.int> i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromBuffer(i, r);
  factory CreateGroupJoinRequestRequest.fromJson($core.String i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromJson(i, r);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
  'Will be removed in next major version')
  CreateGroupJoinRequestRequest clone() => CreateGroupJoinRequestRequest()..mergeFromMessage(this);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
  'Will be removed in next major version')
  CreateGroupJoinRequestRequest copyWith(void Function(CreateGroupJoinRequestRequest) updates) => super.copyWith((message) => updates(message as CreateGroupJoinRequestRequest)) as CreateGroupJoinRequestRequest; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static CreateGroupJoinRequestRequest create() => CreateGroupJoinRequestRequest._();
  CreateGroupJoinRequestRequest createEmptyInstance() => create();
  static $pb.PbList<CreateGroupJoinRequestRequest> createRepeated() => $pb.PbList<CreateGroupJoinRequestRequest>();
  @$core.pragma('dart2js:noInline')
  static CreateGroupJoinRequestRequest getDefault() => _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<CreateGroupJoinRequestRequest>(create);
  static CreateGroupJoinRequestRequest? _defaultInstance;

  @$pb.TagNumber(1)
  $fixnum.Int64 get groupId => $_getI64(0);
  @$pb.TagNumber(1)
  set groupId($fixnum.Int64 v) { $_setInt64(0, v); }
  @$pb.TagNumber(1)
  $core.bool hasGroupId() => $_has(0);
  @$pb.TagNumber(1)
  void clearGroupId() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get content => $_getSZ(1);
  @$pb.TagNumber(2)
  set content($core.String v) { $_setString(1, v); }
  @$pb.TagNumber(2)
  $core.bool hasContent() => $_has(1);
  @$pb.TagNumber(2)
  void clearContent() => clearField(2);
}


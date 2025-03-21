///
//  Generated code. Do not modify.
//  source: request/user/relationship/query_related_user_ids_request.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

import 'dart:core' as $core;

import 'package:fixnum/fixnum.dart' as $fixnum;
import 'package:protobuf/protobuf.dart' as $pb;

class QueryRelatedUserIdsRequest extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'QueryRelatedUserIdsRequest', package: const $pb.PackageName(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'im.turms.proto'), createEmptyInstance: create)
    ..aOB(1, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'blocked')
    ..a<$core.int>(2, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'groupIndex', $pb.PbFieldType.O3)
    ..aInt64(3, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'lastUpdatedDate')
    ..hasRequiredFields = false
  ;

  QueryRelatedUserIdsRequest._() : super();
  factory QueryRelatedUserIdsRequest({
    $core.bool? blocked,
    $core.int? groupIndex,
    $fixnum.Int64? lastUpdatedDate,
  }) {
    final _result = create();
    if (blocked != null) {
      _result.blocked = blocked;
    }
    if (groupIndex != null) {
      _result.groupIndex = groupIndex;
    }
    if (lastUpdatedDate != null) {
      _result.lastUpdatedDate = lastUpdatedDate;
    }
    return _result;
  }
  factory QueryRelatedUserIdsRequest.fromBuffer($core.List<$core.int> i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromBuffer(i, r);
  factory QueryRelatedUserIdsRequest.fromJson($core.String i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromJson(i, r);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
  'Will be removed in next major version')
  QueryRelatedUserIdsRequest clone() => QueryRelatedUserIdsRequest()..mergeFromMessage(this);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
  'Will be removed in next major version')
  QueryRelatedUserIdsRequest copyWith(void Function(QueryRelatedUserIdsRequest) updates) => super.copyWith((message) => updates(message as QueryRelatedUserIdsRequest)) as QueryRelatedUserIdsRequest; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static QueryRelatedUserIdsRequest create() => QueryRelatedUserIdsRequest._();
  QueryRelatedUserIdsRequest createEmptyInstance() => create();
  static $pb.PbList<QueryRelatedUserIdsRequest> createRepeated() => $pb.PbList<QueryRelatedUserIdsRequest>();
  @$core.pragma('dart2js:noInline')
  static QueryRelatedUserIdsRequest getDefault() => _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<QueryRelatedUserIdsRequest>(create);
  static QueryRelatedUserIdsRequest? _defaultInstance;

  @$pb.TagNumber(1)
  $core.bool get blocked => $_getBF(0);
  @$pb.TagNumber(1)
  set blocked($core.bool v) { $_setBool(0, v); }
  @$pb.TagNumber(1)
  $core.bool hasBlocked() => $_has(0);
  @$pb.TagNumber(1)
  void clearBlocked() => clearField(1);

  @$pb.TagNumber(2)
  $core.int get groupIndex => $_getIZ(1);
  @$pb.TagNumber(2)
  set groupIndex($core.int v) { $_setSignedInt32(1, v); }
  @$pb.TagNumber(2)
  $core.bool hasGroupIndex() => $_has(1);
  @$pb.TagNumber(2)
  void clearGroupIndex() => clearField(2);

  @$pb.TagNumber(3)
  $fixnum.Int64 get lastUpdatedDate => $_getI64(2);
  @$pb.TagNumber(3)
  set lastUpdatedDate($fixnum.Int64 v) { $_setInt64(2, v); }
  @$pb.TagNumber(3)
  $core.bool hasLastUpdatedDate() => $_has(2);
  @$pb.TagNumber(3)
  void clearLastUpdatedDate() => clearField(3);
}


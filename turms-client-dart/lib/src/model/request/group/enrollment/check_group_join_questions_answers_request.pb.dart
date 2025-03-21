///
//  Generated code. Do not modify.
//  source: request/group/enrollment/check_group_join_questions_answers_request.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

import 'dart:core' as $core;

import 'package:fixnum/fixnum.dart' as $fixnum;
import 'package:protobuf/protobuf.dart' as $pb;

class CheckGroupJoinQuestionsAnswersRequest extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'CheckGroupJoinQuestionsAnswersRequest', package: const $pb.PackageName(const $core.bool.fromEnvironment('protobuf.omit_message_names') ? '' : 'im.turms.proto'), createEmptyInstance: create)
    ..m<$fixnum.Int64, $core.String>(1, const $core.bool.fromEnvironment('protobuf.omit_field_names') ? '' : 'questionIdAndAnswer', entryClassName: 'CheckGroupJoinQuestionsAnswersRequest.QuestionIdAndAnswerEntry', keyFieldType: $pb.PbFieldType.O6, valueFieldType: $pb.PbFieldType.OS, packageName: const $pb.PackageName('im.turms.proto'))
    ..hasRequiredFields = false
  ;

  CheckGroupJoinQuestionsAnswersRequest._() : super();
  factory CheckGroupJoinQuestionsAnswersRequest({
    $core.Map<$fixnum.Int64, $core.String>? questionIdAndAnswer,
  }) {
    final _result = create();
    if (questionIdAndAnswer != null) {
      _result.questionIdAndAnswer.addAll(questionIdAndAnswer);
    }
    return _result;
  }
  factory CheckGroupJoinQuestionsAnswersRequest.fromBuffer($core.List<$core.int> i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromBuffer(i, r);
  factory CheckGroupJoinQuestionsAnswersRequest.fromJson($core.String i, [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) => create()..mergeFromJson(i, r);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
  'Will be removed in next major version')
  CheckGroupJoinQuestionsAnswersRequest clone() => CheckGroupJoinQuestionsAnswersRequest()..mergeFromMessage(this);
  @$core.Deprecated(
  'Using this can add significant overhead to your binary. '
  'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
  'Will be removed in next major version')
  CheckGroupJoinQuestionsAnswersRequest copyWith(void Function(CheckGroupJoinQuestionsAnswersRequest) updates) => super.copyWith((message) => updates(message as CheckGroupJoinQuestionsAnswersRequest)) as CheckGroupJoinQuestionsAnswersRequest; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static CheckGroupJoinQuestionsAnswersRequest create() => CheckGroupJoinQuestionsAnswersRequest._();
  CheckGroupJoinQuestionsAnswersRequest createEmptyInstance() => create();
  static $pb.PbList<CheckGroupJoinQuestionsAnswersRequest> createRepeated() => $pb.PbList<CheckGroupJoinQuestionsAnswersRequest>();
  @$core.pragma('dart2js:noInline')
  static CheckGroupJoinQuestionsAnswersRequest getDefault() => _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<CheckGroupJoinQuestionsAnswersRequest>(create);
  static CheckGroupJoinQuestionsAnswersRequest? _defaultInstance;

  @$pb.TagNumber(1)
  $core.Map<$fixnum.Int64, $core.String> get questionIdAndAnswer => $_getMap(0);
}


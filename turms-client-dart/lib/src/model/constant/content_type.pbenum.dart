///
//  Generated code. Do not modify.
//  source: constant/content_type.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

// ignore_for_file: UNDEFINED_SHOWN_NAME
import 'dart:core' as $core;
import 'package:protobuf/protobuf.dart' as $pb;

class ContentType extends $pb.ProtobufEnum {
  static const ContentType PROFILE = ContentType._(0, const $core.bool.fromEnvironment('protobuf.omit_enum_names') ? '' : 'PROFILE');
  static const ContentType GROUP_PROFILE = ContentType._(1, const $core.bool.fromEnvironment('protobuf.omit_enum_names') ? '' : 'GROUP_PROFILE');
  static const ContentType ATTACHMENT = ContentType._(2, const $core.bool.fromEnvironment('protobuf.omit_enum_names') ? '' : 'ATTACHMENT');

  static const $core.List<ContentType> values = <ContentType> [
    PROFILE,
    GROUP_PROFILE,
    ATTACHMENT,
  ];

  static final $core.Map<$core.int, ContentType> _byValue = $pb.ProtobufEnum.initByValue(values);
  static ContentType? valueOf($core.int value) => _byValue[value];

  const ContentType._($core.int v, $core.String n) : super(v, n);
}


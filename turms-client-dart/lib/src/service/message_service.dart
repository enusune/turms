import 'dart:typed_data';

import 'package:fixnum/fixnum.dart' show Int64;

import '../../turms_client.dart';
import '../extension/date_time_extensions.dart';
import '../extension/iterable_extensions.dart';
import '../extension/notification_extensions.dart';
import '../model/builtin_system_message_type.dart';
import '../model/message_addition.dart';
import '../model/model/file/audio_file.pb.dart';
import '../model/model/file/file.pb.dart';
import '../model/model/file/image_file.pb.dart';
import '../model/model/file/video_file.pb.dart';
import '../model/model/message/message.pb.dart';
import '../model/model/message/messages_with_total.pb.dart';
import '../model/model/user/user_location.pb.dart';
import '../model/request/message/create_message_request.pb.dart';
import '../model/request/message/query_messages_request.pb.dart';
import '../model/request/message/update_message_request.pb.dart';
import '../model/turms_business_exception.dart';
import '../model/turms_status_code.dart';

typedef MentionedUserIdsParser = Set<Int64> Function(Message message);
typedef MessageListener = void Function(
    Message message, MessageAddition addition);

class MessageService {
  /// Format: "@{userId}"
  /// Example: "@{123}", "I need to talk with @{123} and @{321}"
  static final RegExp _defaultMentionedUserIdsParserRegex =
      RegExp(r'@{(\d+?)}', multiLine: true);

  Set<Int64> _defaultMentionedUserIdsParser(Message message) {
    if (message.hasText()) {
      final userIds = <Int64>{};
      for (final matches
          in _defaultMentionedUserIdsParserRegex.allMatches(message.text)) {
        userIds.add(Int64.parseInt(matches.group(1)!));
      }
      return userIds;
    }
    return {};
  }

  final TurmsClient _turmsClient;
  MentionedUserIdsParser? _mentionedUserIdsParser;
  final List<MessageListener> _messageListeners = [];

  MessageService(this._turmsClient) {
    _turmsClient.driver.addNotificationListener((notification) {
      if (_messageListeners.isNotEmpty &&
          notification.hasRelayedRequest() &&
          notification.relayedRequest.hasCreateMessageRequest()) {
        final request = notification.relayedRequest.createMessageRequest;
        final message =
            _createMessageRequest2Message(notification.requesterId, request);
        final addition = _parseMessageAddition(message);
        for (final listener in _messageListeners) {
          listener.call(message, addition);
        }
      }
    });
  }

  void addMessageListener(MessageListener listener) =>
      _messageListeners.add(listener);

  void removeMessageListener(MessageListener listener) =>
      _messageListeners.remove(listener);

  Future<Int64> sendMessage(bool isGroupMessage, Int64 targetId,
      {DateTime? deliveryDate,
      String? text,
      List<Uint8List>? records,
      int? burnAfter,
      Int64? preMessageId}) async {
    if (text == null && (records?.isEmpty ?? true)) {
      throw TurmsBusinessException(TurmsStatusCode.illegalArgument,
          'text and records must not all be null');
    }
    deliveryDate ??= DateTime.now();
    final n = await _turmsClient.driver.send(CreateMessageRequest(
        groupId: isGroupMessage ? targetId : null,
        recipientId: !isGroupMessage ? targetId : null,
        deliveryDate: deliveryDate.toInt64(),
        text: text,
        records: records,
        burnAfter: burnAfter,
        preMessageId: preMessageId));
    return n.getFirstIdOrThrow();
  }

  Future<Int64> forwardMessage(
      Int64 messageId, bool isGroupMessage, Int64 targetId) async {
    final n = await _turmsClient.driver.send(CreateMessageRequest(
        messageId: messageId,
        groupId: isGroupMessage ? targetId : null,
        recipientId: !isGroupMessage ? targetId : null));
    return n.getFirstIdOrThrow();
  }

  Future<void> updateSentMessage(Int64 messageId,
      {String? text, List<Uint8List>? records}) async {
    if ([text, records].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateMessageRequest(
        messageId: messageId, text: text, records: records));
  }

  Future<List<Message>> queryMessages(
      {Set<Int64>? ids,
      bool? areGroupMessages,
      bool? areSystemMessages,
      Int64? fromId,
      DateTime? deliveryDateAfter,
      DateTime? deliveryDateBefore,
      int size = 50}) async {
    final n = await _turmsClient.driver.send(QueryMessagesRequest(
        ids: ids,
        areGroupMessages: areGroupMessages,
        areSystemMessages: areSystemMessages,
        fromId: fromId,
        deliveryDateAfter: deliveryDateAfter?.toInt64(),
        deliveryDateBefore: deliveryDateBefore?.toInt64(),
        size: size,
        withTotal: false));
    return n.data.messages.messages;
  }

  Future<List<MessagesWithTotal>> queryMessagesWithTotal(
      {Set<Int64>? ids,
      bool? areGroupMessages,
      bool? areSystemMessages,
      Int64? fromId,
      DateTime? deliveryDateAfter,
      DateTime? deliveryDateBefore,
      int size = 1}) async {
    final n = await _turmsClient.driver.send(QueryMessagesRequest(
        ids: ids,
        areGroupMessages: areGroupMessages,
        areSystemMessages: areSystemMessages,
        fromId: fromId,
        deliveryDateAfter: deliveryDateAfter?.toInt64(),
        deliveryDateBefore: deliveryDateBefore?.toInt64(),
        size: size,
        withTotal: true));
    return n.data.messagesWithTotalList.messagesWithTotalList;
  }

  Future<void> recallMessage(Int64 messageId, {DateTime? recallDate}) =>
      _turmsClient.driver.send(UpdateMessageRequest(
          messageId: messageId,
          recallDate:
              Int64((recallDate ?? DateTime.now()).millisecondsSinceEpoch)));

  bool isMentionEnabled() => _mentionedUserIdsParser != null;

  void enableMention({MentionedUserIdsParser? mentionedUserIdsParser}) {
    _mentionedUserIdsParser = mentionedUserIdsParser ??
        _mentionedUserIdsParser ??
        _defaultMentionedUserIdsParser;
  }

  static Uint8List generateLocationRecord(double latitude, double longitude,
          {String? locationName, String? address}) =>
      UserLocation(
              latitude: latitude,
              longitude: longitude,
              name: locationName,
              address: address)
          .writeToBuffer();

  static Uint8List generateAudioRecordByDescription(String url,
          {int? duration, String? format, int? size}) =>
      AudioFile(
          description: AudioFile_Description(
        url: url,
        duration: duration,
        format: format,
        size: size,
      )).writeToBuffer();

  static Uint8List generateAudioRecordByData(Uint8List data) =>
      AudioFile(data: data).writeToBuffer();

  static Uint8List generateVideoRecordByDescription(String url,
          {int? duration, String? format, int? size}) =>
      VideoFile(
          description: VideoFile_Description(
        url: url,
        duration: duration,
        format: format,
        size: size,
      )).writeToBuffer();

  static Uint8List generateVideoRecordByData(Uint8List data) =>
      VideoFile(data: data).writeToBuffer();

  static Uint8List generateImageRecordByData(Uint8List data) =>
      ImageFile(data: data).writeToBuffer();

  static Uint8List generateImageRecordByDescription(String url,
          {int? fileSize, int? imageSize, bool? original}) =>
      ImageFile(
              description: ImageFile_Description(
                  url: url,
                  fileSize: fileSize,
                  imageSize: imageSize,
                  original: original))
          .writeToBuffer();

  static Uint8List generateFileRecordByDate(Uint8List data) =>
      File(data: data).writeToBuffer();

  static Uint8List generateFileRecordByDescription(String url,
          {String? format, int? size}) =>
      File(description: File_Description(url: url, format: format, size: size))
          .writeToBuffer();

  MessageAddition _parseMessageAddition(Message message) {
    final mentionedUserIds = _mentionedUserIdsParser?.call(message) ?? {};
    final isMentioned =
        mentionedUserIds.contains(_turmsClient.userService.userInfo?.userId);
    final records = message.records;
    var systemMessageType = BuiltinSystemMessageType.normal;
    final recalledMessageIds = <Int64>{};
    if (message.isSystemMessage && records.isNotEmpty) {
      final bytes = records[0];
      if (bytes.isNotEmpty) {
        systemMessageType = BuiltinSystemMessageType.values
            .firstWhere((element) => element.index == bytes[0]);
        if (systemMessageType == BuiltinSystemMessageType.recallMessage) {
          final size = records.length;
          for (var i = 1; i < size; i++) {
            // Note that it will be truncated into the range of
            // -2^53 and 2^53 in JavaScript.
            final id = ByteData.view(Uint8List.fromList(records[i]).buffer)
                .getInt64(0);
            recalledMessageIds.add(Int64(id));
          }
        }
      }
    }
    return MessageAddition(isMentioned, mentionedUserIds, recalledMessageIds);
  }

  Message _createMessageRequest2Message(
          Int64 requesterId, CreateMessageRequest request) =>
      Message(
          id: request.messageId,
          isSystemMessage: request.isSystemMessage,
          deliveryDate: request.deliveryDate,
          text: request.text,
          records: request.records,
          senderId: requesterId,
          groupId: request.groupId,
          recipientId: request.recipientId);
}

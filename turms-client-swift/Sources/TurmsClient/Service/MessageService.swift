import Foundation
import PromiseKit

public class MessageService {
    /**
     * Format: "@{userId}"
     * Example: "@{123}", "I need to talk with @{123} and @{321}"
     */
    private static let DEFAULT_MENTIONED_USER_IDS_REGEX = try! NSRegularExpression(pattern: "@\\{(\\d+?)\\}", options: [])
    private static let DEFAULT_MENTIONED_USER_IDS_PARSER: (_ message: Message) -> [Int64] = {
        if $0.hasText {
            let text = $0.text
            let results = DEFAULT_MENTIONED_USER_IDS_REGEX.matches(in: text, range: NSRange(text.startIndex..., in: text))
            return results.map {
                Int64(String(text[Range($0.range(at: 1), in: text)!]))!
            }
        }
        return []
    }

    private weak var turmsClient: TurmsClient!
    private var mentionedUserIdsParser: ((Message) -> [Int64])?
    public var messageListeners: [(Message, MessageAddition) -> Void] = []

    init(_ turmsClient: TurmsClient) {
        self.turmsClient = turmsClient
        self.turmsClient.driver
            .addNotificationListener {
                if !self.messageListeners.isEmpty, $0.hasRelayedRequest, case let .createMessageRequest(request) = $0.relayedRequest.kind {
                    let message = MessageService.createMessage2Message($0.requesterID, request)
                    let addition = self.parseMessageAddition(message)
                    self.messageListeners.forEach { listener in listener(message, addition) }
                }
            }
    }

    func addMessageListener(_ listener: @escaping (Message, MessageAddition) -> Void) {
        messageListeners.append(listener)
    }

    public func sendMessage(
        isGroupMessage: Bool,
        targetId: Int64,
        deliveryDate: Date? = nil,
        text: String? = nil,
        records: [Data]? = nil,
        burnAfter: Int32? = nil,
        preMessageId: Int64? = nil
    ) -> Promise<Int64> {
        if Validator.areAllNil(text, records) {
            return Promise(error: TurmsBusinessError(TurmsStatusCode.illegalArgument, "text and records must not all be null"))
        }
        return turmsClient.driver
            .send {
                $0.createMessageRequest = .with {
                    if isGroupMessage {
                        $0.groupID = targetId
                    } else {
                        $0.recipientID = targetId
                    }
                    if let v = deliveryDate {
                        $0.deliveryDate = v.toMillis()
                    }
                    if let v = text {
                        $0.text = v
                    }
                    if let v = records {
                        $0.records = v
                    }
                    if let v = burnAfter {
                        $0.burnAfter = v
                    }
                    if let v = preMessageId {
                        $0.preMessageID = v
                    }
                }
            }
            .map {
                try $0.getFirstId()
            }
    }

    public func forwardMessage(
        messageId: Int64,
        isGroupMessage: Bool,
        targetId: Int64
    ) -> Promise<Int64> {
        return turmsClient.driver
            .send {
                $0.createMessageRequest = .with {
                    $0.messageID = messageId
                    if isGroupMessage {
                        $0.groupID = targetId
                    } else {
                        $0.recipientID = targetId
                    }
                }
            }
            .map {
                try $0.getFirstId()
            }
    }

    public func updateSentMessage(
        messageId: Int64,
        text: String? = nil,
        records: [Data]? = nil
    ) -> Promise<Void> {
        if Validator.areAllNil(text, records) {
            return Promise.value(())
        }
        return turmsClient.driver
            .send {
                $0.updateMessageRequest = .with {
                    $0.messageID = messageId
                    if let v = text {
                        $0.text = v
                    }
                    if let v = records {
                        $0.records = v
                    }
                }
            }
            .asVoid()
    }

    public func queryMessages(
        ids: [Int64]? = nil,
        areGroupMessages: Bool? = nil,
        areSystemMessages: Bool? = nil,
        fromId: Int64? = nil,
        deliveryDateAfter: Date? = nil,
        deliveryDateBefore: Date? = nil,
        size: Int32 = 50
    ) -> Promise<[Message]> {
        return turmsClient.driver
            .send {
                $0.queryMessagesRequest = .with {
                    if let v = ids {
                        $0.ids = v
                    }
                    if let v = areGroupMessages {
                        $0.areGroupMessages = v
                    }
                    if let v = areSystemMessages {
                        $0.areSystemMessages = v
                    }
                    if let v = fromId {
                        $0.fromID = v
                    }
                    if let v = deliveryDateAfter {
                        $0.deliveryDateAfter = v.toMillis()
                    }
                    if let v = deliveryDateBefore {
                        $0.deliveryDateBefore = v.toMillis()
                    }
                    $0.size = size
                    $0.withTotal = false
                }
            }
            .map {
                $0.data.messages.messages
            }
    }

    public func queryMessagesWithTotal(
        ids: [Int64]? = nil,
        areGroupMessages: Bool? = nil,
        areSystemMessages: Bool? = nil,
        fromId: Int64? = nil,
        deliveryDateAfter: Date? = nil,
        deliveryDateBefore: Date? = nil,
        size: Int32 = 1
    ) -> Promise<[MessagesWithTotal]> {
        return turmsClient.driver
            .send {
                $0.queryMessagesRequest = .with {
                    if let v = ids {
                        $0.ids = v
                    }
                    if let v = areGroupMessages {
                        $0.areGroupMessages = v
                    }
                    if let v = areSystemMessages {
                        $0.areSystemMessages = v
                    }
                    if let v = fromId {
                        $0.fromID = v
                    }
                    if let v = deliveryDateAfter {
                        $0.deliveryDateAfter = v.toMillis()
                    }
                    if let v = deliveryDateBefore {
                        $0.deliveryDateBefore = v.toMillis()
                    }
                    $0.size = size
                    $0.withTotal = true
                }
            }
            .map {
                $0.data.messagesWithTotalList.messagesWithTotalList
            }
    }

    public func recallMessage(messageId: Int64, recallDate: Date = Date()) -> Promise<Void> {
        return turmsClient.driver
            .send {
                $0.updateMessageRequest = .with {
                    $0.messageID = messageId
                    $0.recallDate = recallDate.toMillis()
                }
            }
            .asVoid()
    }

    public func isMentionEnabled() -> Bool {
        return mentionedUserIdsParser != nil
    }

    public func enableMention(mentionedUserIdsParser: ((Message) -> [Int64])?) {
        if mentionedUserIdsParser != nil {
            self.mentionedUserIdsParser = mentionedUserIdsParser
        } else if self.mentionedUserIdsParser == nil {
            self.mentionedUserIdsParser = MessageService.DEFAULT_MENTIONED_USER_IDS_PARSER
        }
    }

    public static func generateLocationRecord(latitude: Float, longitude: Float, locationName: String? = nil, address: String? = nil) -> Data {
        return try! UserLocation.with {
            $0.latitude = latitude
            $0.longitude = longitude
            if locationName != nil {
                $0.name = locationName!
            }
            if address != nil {
                $0.address = address!
            }
        }.serializedData()
    }

    public static func generateAudioRecordByDescription(url: String, duration: Int32? = nil, format: String? = nil, size: Int32? = nil) -> Data {
        return try! AudioFile.with {
            $0.description_p.url = url
            if duration != nil {
                $0.description_p.duration = duration!
            }
            if format != nil {
                $0.description_p.format = format!
            }
            if size != nil {
                $0.description_p.size = size!
            }
        }.serializedData()
    }

    public static func generateAudioRecordByData(_ data: Data) -> Data {
        return try! AudioFile.with {
            $0.data = data
        }.serializedData()
    }

    public static func generateVideoRecordByDescription(url: String, duration: Int32? = nil, format: String? = nil, size: Int32? = nil) -> Data {
        return try! VideoFile.with {
            $0.description_p.url = url
            if duration != nil {
                $0.description_p.duration = duration!
            }
            if format != nil {
                $0.description_p.format = format!
            }
            if size != nil {
                $0.description_p.size = size!
            }
        }.serializedData()
    }

    public static func generateVideoRecordByData(_ data: Data) -> Data {
        return try! VideoFile.with {
            $0.data = data
        }.serializedData()
    }

    public static func generateImageRecordByData(_ data: Data) -> Data {
        return try! ImageFile.with {
            $0.data = data
        }.serializedData()
    }

    public static func generateImageRecordByDescription(url: String, fileSize: Int32? = nil, imageSize: Int32? = nil, original: Bool? = nil) -> Data {
        return try! ImageFile.with {
            $0.description_p.url = url
            if fileSize != nil {
                $0.description_p.fileSize = fileSize!
            }
            if imageSize != nil {
                $0.description_p.imageSize = imageSize!
            }
            if original != nil {
                $0.description_p.original = original!
            }
        }.serializedData()
    }

    public static func generateFileRecordByDate(_ data: Data) -> Data {
        return try! File.with {
            $0.data = data
        }.serializedData()
    }

    public static func generateFileRecordByDescription(url: String, format: String? = nil, size: Int32? = nil) -> Data {
        return try! File.with {
            $0.description_p.url = url
            if format != nil {
                $0.description_p.format = format!
            }
            if size != nil {
                $0.description_p.size = size!
            }
        }.serializedData()
    }

    private func parseMessageAddition(_ message: Message) -> MessageAddition {
        let mentionedUserIds = mentionedUserIdsParser?(message) ?? []
        let userId = turmsClient.userService.userInfo?.userId
        let isMentioned = userId != nil ? mentionedUserIds.contains(userId!) : false
        let records = message.records
        var systemMessageType: BuiltinSystemMessageType?
        if message.isSystemMessage, !records.isEmpty {
            let data = records[0]
            if !data.isEmpty {
                systemMessageType = BuiltinSystemMessageType(rawValue: Int(data[0]))
            }
        }
        var recalledMessageIds: [Int64] = []
        if systemMessageType == BuiltinSystemMessageType.recallMessage {
            let size = records.count
            for i in 1 ... (size - 1) {
                let id = records[i].withUnsafeBytes {
                    $0.load(as: Int64.self)
                }
                recalledMessageIds.append(id)
            }
        }
        return MessageAddition(isMentioned: isMentioned, mentionedUserIds: mentionedUserIds, recalledMessageIds: recalledMessageIds)
    }

    private static func createMessage2Message(_ requesterId: Int64, _ request: CreateMessageRequest) -> Message {
        return Message.with {
            if request.hasMessageID {
                $0.id = request.messageID
            }
            $0.isSystemMessage = request.isSystemMessage
            $0.deliveryDate = request.deliveryDate
            if request.hasText {
                $0.text = request.text
            }
            if request.records.count > 0 {
                $0.records = request.records
            }
            $0.senderID = requesterId
            if request.hasGroupID {
                $0.groupID = request.groupID
            }
            if request.hasRecipientID {
                $0.recipientID = request.recipientID
            }
        }
    }
}

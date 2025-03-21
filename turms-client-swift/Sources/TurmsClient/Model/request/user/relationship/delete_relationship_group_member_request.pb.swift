// DO NOT EDIT.
// swift-format-ignore-file
//
// Generated by the Swift generator plugin for the protocol buffer compiler.
// Source: request/user/relationship/delete_relationship_group_member_request.proto
//
// For information on using the generated types, please see the documentation:
//   https://github.com/apple/swift-protobuf/

import Foundation
import SwiftProtobuf

// If the compiler emits an error on this type, it is because this file
// was generated by a version of the `protoc` Swift plug-in that is
// incompatible with the version of SwiftProtobuf to which you are linking.
// Please ensure that you are building against the same version of the API
// that was used to generate this file.
private struct _GeneratedWithProtocGenSwiftVersion: SwiftProtobuf.ProtobufAPIVersionCheck {
    struct _2: SwiftProtobuf.ProtobufAPIVersion_2 {}
    typealias Version = _2
}

public struct DeleteRelationshipGroupMemberRequest {
    // SwiftProtobuf.Message conformance is added in an extension below. See the
    // `Message` and `Message+*Additions` files in the SwiftProtobuf library for
    // methods supported on all messages.

    public var userID: Int64 = 0

    public var groupIndex: Int32 = 0

    public var targetGroupIndex: Int32 {
        get { return _targetGroupIndex ?? 0 }
        set { _targetGroupIndex = newValue }
    }

    /// Returns true if `targetGroupIndex` has been explicitly set.
    public var hasTargetGroupIndex: Bool { return _targetGroupIndex != nil }
    /// Clears the value of `targetGroupIndex`. Subsequent reads from it will return its default value.
    public mutating func clearTargetGroupIndex() { _targetGroupIndex = nil }

    public var unknownFields = SwiftProtobuf.UnknownStorage()

    public init() {}

    fileprivate var _targetGroupIndex: Int32?
}

// MARK: - Code below here is support for the SwiftProtobuf runtime.

private let _protobuf_package = "im.turms.proto"

extension DeleteRelationshipGroupMemberRequest: SwiftProtobuf.Message, SwiftProtobuf._MessageImplementationBase, SwiftProtobuf._ProtoNameProviding {
    public static let protoMessageName: String = _protobuf_package + ".DeleteRelationshipGroupMemberRequest"
    public static let _protobuf_nameMap: SwiftProtobuf._NameMap = [
        1: .standard(proto: "user_id"),
        2: .standard(proto: "group_index"),
        3: .standard(proto: "target_group_index"),
    ]

    public mutating func decodeMessage<D: SwiftProtobuf.Decoder>(decoder: inout D) throws {
        while let fieldNumber = try decoder.nextFieldNumber() {
            // The use of inline closures is to circumvent an issue where the compiler
            // allocates stack space for every case branch when no optimizations are
            // enabled. https://github.com/apple/swift-protobuf/issues/1034
            switch fieldNumber {
            case 1: try try decoder.decodeSingularInt64Field(value: &userID)
            case 2: try try decoder.decodeSingularInt32Field(value: &groupIndex)
            case 3: try try decoder.decodeSingularInt32Field(value: &_targetGroupIndex)
            default: break
            }
        }
    }

    public func traverse<V: SwiftProtobuf.Visitor>(visitor: inout V) throws {
        if userID != 0 {
            try visitor.visitSingularInt64Field(value: userID, fieldNumber: 1)
        }
        if groupIndex != 0 {
            try visitor.visitSingularInt32Field(value: groupIndex, fieldNumber: 2)
        }
        try { if let v = self._targetGroupIndex {
            try visitor.visitSingularInt32Field(value: v, fieldNumber: 3)
        } }()
        try unknownFields.traverse(visitor: &visitor)
    }

    public static func == (lhs: DeleteRelationshipGroupMemberRequest, rhs: DeleteRelationshipGroupMemberRequest) -> Bool {
        if lhs.userID != rhs.userID { return false }
        if lhs.groupIndex != rhs.groupIndex { return false }
        if lhs._targetGroupIndex != rhs._targetGroupIndex { return false }
        if lhs.unknownFields != rhs.unknownFields { return false }
        return true
    }
}

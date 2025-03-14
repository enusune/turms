/* eslint-disable */
import Long from "long";
import _m0 from "protobufjs/minimal";
import { RequestStatus } from "../../constant/request_status";

export const protobufPackage = "im.turms.proto";

export interface GroupInvitation {
  id?: string | undefined;
  creationDate?: string | undefined;
  content?: string | undefined;
  status?: RequestStatus | undefined;
  expirationDate?: string | undefined;
  groupId?: string | undefined;
  inviterId?: string | undefined;
  inviteeId?: string | undefined;
}

function createBaseGroupInvitation(): GroupInvitation {
  return {
    id: undefined,
    creationDate: undefined,
    content: undefined,
    status: undefined,
    expirationDate: undefined,
    groupId: undefined,
    inviterId: undefined,
    inviteeId: undefined,
  };
}

export const GroupInvitation = {
  encode(
    message: GroupInvitation,
    writer: _m0.Writer = _m0.Writer.create()
  ): _m0.Writer {
    if (message.id !== undefined) {
      writer.uint32(8).int64(message.id);
    }
    if (message.creationDate !== undefined) {
      writer.uint32(16).int64(message.creationDate);
    }
    if (message.content !== undefined) {
      writer.uint32(26).string(message.content);
    }
    if (message.status !== undefined) {
      writer.uint32(32).int32(message.status);
    }
    if (message.expirationDate !== undefined) {
      writer.uint32(40).int64(message.expirationDate);
    }
    if (message.groupId !== undefined) {
      writer.uint32(48).int64(message.groupId);
    }
    if (message.inviterId !== undefined) {
      writer.uint32(56).int64(message.inviterId);
    }
    if (message.inviteeId !== undefined) {
      writer.uint32(64).int64(message.inviteeId);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): GroupInvitation {
    const reader = input instanceof _m0.Reader ? input : new _m0.Reader(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseGroupInvitation();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.id = longToString(reader.int64() as Long);
          break;
        case 2:
          message.creationDate = longToString(reader.int64() as Long);
          break;
        case 3:
          message.content = reader.string();
          break;
        case 4:
          message.status = reader.int32() as any;
          break;
        case 5:
          message.expirationDate = longToString(reader.int64() as Long);
          break;
        case 6:
          message.groupId = longToString(reader.int64() as Long);
          break;
        case 7:
          message.inviterId = longToString(reader.int64() as Long);
          break;
        case 8:
          message.inviteeId = longToString(reader.int64() as Long);
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
};

function longToString(long: Long) {
  return long.toString();
}

if (_m0.util.Long !== Long) {
  _m0.util.Long = Long as any;
  _m0.configure();
}

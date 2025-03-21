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

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: model/group/group_join_request.proto

package im.turms.common.model.bo.group;

/**
 * Protobuf type {@code im.turms.proto.GroupJoinRequest}
 */
public final class GroupJoinRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:im.turms.proto.GroupJoinRequest)
    GroupJoinRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GroupJoinRequest.newBuilder() to construct.
  private GroupJoinRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GroupJoinRequest() {
    content_ = "";
    status_ = 0;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GroupJoinRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GroupJoinRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            bitField0_ |= 0x00000001;
            id_ = input.readInt64();
            break;
          }
          case 16: {
            bitField0_ |= 0x00000002;
            creationDate_ = input.readInt64();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000004;
            content_ = s;
            break;
          }
          case 32: {
            int rawValue = input.readEnum();
            bitField0_ |= 0x00000008;
            status_ = rawValue;
            break;
          }
          case 40: {
            bitField0_ |= 0x00000010;
            expirationDate_ = input.readInt64();
            break;
          }
          case 48: {
            bitField0_ |= 0x00000020;
            groupId_ = input.readInt64();
            break;
          }
          case 56: {
            bitField0_ |= 0x00000040;
            requesterId_ = input.readInt64();
            break;
          }
          case 64: {
            bitField0_ |= 0x00000080;
            responderId_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return im.turms.common.model.bo.group.GroupJoinRequestOuterClass.internal_static_im_turms_proto_GroupJoinRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return im.turms.common.model.bo.group.GroupJoinRequestOuterClass.internal_static_im_turms_proto_GroupJoinRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            im.turms.common.model.bo.group.GroupJoinRequest.class, im.turms.common.model.bo.group.GroupJoinRequest.Builder.class);
  }

  private int bitField0_;
  public static final int ID_FIELD_NUMBER = 1;
  private long id_;
  /**
   * <code>optional int64 id = 1;</code>
   * @return Whether the id field is set.
   */
  @java.lang.Override
  public boolean hasId() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional int64 id = 1;</code>
   * @return The id.
   */
  @java.lang.Override
  public long getId() {
    return id_;
  }

  public static final int CREATION_DATE_FIELD_NUMBER = 2;
  private long creationDate_;
  /**
   * <code>optional int64 creation_date = 2;</code>
   * @return Whether the creationDate field is set.
   */
  @java.lang.Override
  public boolean hasCreationDate() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional int64 creation_date = 2;</code>
   * @return The creationDate.
   */
  @java.lang.Override
  public long getCreationDate() {
    return creationDate_;
  }

  public static final int CONTENT_FIELD_NUMBER = 3;
  private volatile java.lang.Object content_;
  /**
   * <code>optional string content = 3;</code>
   * @return Whether the content field is set.
   */
  @java.lang.Override
  public boolean hasContent() {
    return ((bitField0_ & 0x00000004) != 0);
  }
  /**
   * <code>optional string content = 3;</code>
   * @return The content.
   */
  @java.lang.Override
  public java.lang.String getContent() {
    java.lang.Object ref = content_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      content_ = s;
      return s;
    }
  }
  /**
   * <code>optional string content = 3;</code>
   * @return The bytes for content.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getContentBytes() {
    java.lang.Object ref = content_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      content_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int STATUS_FIELD_NUMBER = 4;
  private int status_;
  /**
   * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
   * @return Whether the status field is set.
   */
  @java.lang.Override public boolean hasStatus() {
    return ((bitField0_ & 0x00000008) != 0);
  }
  /**
   * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
   * @return The enum numeric value on the wire for status.
   */
  @java.lang.Override public int getStatusValue() {
    return status_;
  }
  /**
   * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
   * @return The status.
   */
  @java.lang.Override public im.turms.common.constant.RequestStatus getStatus() {
    @SuppressWarnings("deprecation")
    im.turms.common.constant.RequestStatus result = im.turms.common.constant.RequestStatus.valueOf(status_);
    return result == null ? im.turms.common.constant.RequestStatus.UNRECOGNIZED : result;
  }

  public static final int EXPIRATION_DATE_FIELD_NUMBER = 5;
  private long expirationDate_;
  /**
   * <code>optional int64 expiration_date = 5;</code>
   * @return Whether the expirationDate field is set.
   */
  @java.lang.Override
  public boolean hasExpirationDate() {
    return ((bitField0_ & 0x00000010) != 0);
  }
  /**
   * <code>optional int64 expiration_date = 5;</code>
   * @return The expirationDate.
   */
  @java.lang.Override
  public long getExpirationDate() {
    return expirationDate_;
  }

  public static final int GROUP_ID_FIELD_NUMBER = 6;
  private long groupId_;
  /**
   * <code>optional int64 group_id = 6;</code>
   * @return Whether the groupId field is set.
   */
  @java.lang.Override
  public boolean hasGroupId() {
    return ((bitField0_ & 0x00000020) != 0);
  }
  /**
   * <code>optional int64 group_id = 6;</code>
   * @return The groupId.
   */
  @java.lang.Override
  public long getGroupId() {
    return groupId_;
  }

  public static final int REQUESTER_ID_FIELD_NUMBER = 7;
  private long requesterId_;
  /**
   * <code>optional int64 requester_id = 7;</code>
   * @return Whether the requesterId field is set.
   */
  @java.lang.Override
  public boolean hasRequesterId() {
    return ((bitField0_ & 0x00000040) != 0);
  }
  /**
   * <code>optional int64 requester_id = 7;</code>
   * @return The requesterId.
   */
  @java.lang.Override
  public long getRequesterId() {
    return requesterId_;
  }

  public static final int RESPONDER_ID_FIELD_NUMBER = 8;
  private long responderId_;
  /**
   * <code>optional int64 responder_id = 8;</code>
   * @return Whether the responderId field is set.
   */
  @java.lang.Override
  public boolean hasResponderId() {
    return ((bitField0_ & 0x00000080) != 0);
  }
  /**
   * <code>optional int64 responder_id = 8;</code>
   * @return The responderId.
   */
  @java.lang.Override
  public long getResponderId() {
    return responderId_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeInt64(1, id_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeInt64(2, creationDate_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, content_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      output.writeEnum(4, status_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      output.writeInt64(5, expirationDate_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      output.writeInt64(6, groupId_);
    }
    if (((bitField0_ & 0x00000040) != 0)) {
      output.writeInt64(7, requesterId_);
    }
    if (((bitField0_ & 0x00000080) != 0)) {
      output.writeInt64(8, responderId_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, id_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, creationDate_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, content_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(4, status_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(5, expirationDate_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(6, groupId_);
    }
    if (((bitField0_ & 0x00000040) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(7, requesterId_);
    }
    if (((bitField0_ & 0x00000080) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(8, responderId_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof im.turms.common.model.bo.group.GroupJoinRequest)) {
      return super.equals(obj);
    }
    im.turms.common.model.bo.group.GroupJoinRequest other = (im.turms.common.model.bo.group.GroupJoinRequest) obj;

    if (hasId() != other.hasId()) return false;
    if (hasId()) {
      if (getId()
          != other.getId()) return false;
    }
    if (hasCreationDate() != other.hasCreationDate()) return false;
    if (hasCreationDate()) {
      if (getCreationDate()
          != other.getCreationDate()) return false;
    }
    if (hasContent() != other.hasContent()) return false;
    if (hasContent()) {
      if (!getContent()
          .equals(other.getContent())) return false;
    }
    if (hasStatus() != other.hasStatus()) return false;
    if (hasStatus()) {
      if (status_ != other.status_) return false;
    }
    if (hasExpirationDate() != other.hasExpirationDate()) return false;
    if (hasExpirationDate()) {
      if (getExpirationDate()
          != other.getExpirationDate()) return false;
    }
    if (hasGroupId() != other.hasGroupId()) return false;
    if (hasGroupId()) {
      if (getGroupId()
          != other.getGroupId()) return false;
    }
    if (hasRequesterId() != other.hasRequesterId()) return false;
    if (hasRequesterId()) {
      if (getRequesterId()
          != other.getRequesterId()) return false;
    }
    if (hasResponderId() != other.hasResponderId()) return false;
    if (hasResponderId()) {
      if (getResponderId()
          != other.getResponderId()) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasId()) {
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
    }
    if (hasCreationDate()) {
      hash = (37 * hash) + CREATION_DATE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getCreationDate());
    }
    if (hasContent()) {
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
    }
    if (hasStatus()) {
      hash = (37 * hash) + STATUS_FIELD_NUMBER;
      hash = (53 * hash) + status_;
    }
    if (hasExpirationDate()) {
      hash = (37 * hash) + EXPIRATION_DATE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getExpirationDate());
    }
    if (hasGroupId()) {
      hash = (37 * hash) + GROUP_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getGroupId());
    }
    if (hasRequesterId()) {
      hash = (37 * hash) + REQUESTER_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getRequesterId());
    }
    if (hasResponderId()) {
      hash = (37 * hash) + RESPONDER_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getResponderId());
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.group.GroupJoinRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(im.turms.common.model.bo.group.GroupJoinRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code im.turms.proto.GroupJoinRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:im.turms.proto.GroupJoinRequest)
      im.turms.common.model.bo.group.GroupJoinRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return im.turms.common.model.bo.group.GroupJoinRequestOuterClass.internal_static_im_turms_proto_GroupJoinRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return im.turms.common.model.bo.group.GroupJoinRequestOuterClass.internal_static_im_turms_proto_GroupJoinRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              im.turms.common.model.bo.group.GroupJoinRequest.class, im.turms.common.model.bo.group.GroupJoinRequest.Builder.class);
    }

    // Construct using im.turms.common.model.bo.group.GroupJoinRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      id_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000001);
      creationDate_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000002);
      content_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      status_ = 0;
      bitField0_ = (bitField0_ & ~0x00000008);
      expirationDate_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000010);
      groupId_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000020);
      requesterId_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000040);
      responderId_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000080);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return im.turms.common.model.bo.group.GroupJoinRequestOuterClass.internal_static_im_turms_proto_GroupJoinRequest_descriptor;
    }

    @java.lang.Override
    public im.turms.common.model.bo.group.GroupJoinRequest getDefaultInstanceForType() {
      return im.turms.common.model.bo.group.GroupJoinRequest.getDefaultInstance();
    }

    @java.lang.Override
    public im.turms.common.model.bo.group.GroupJoinRequest build() {
      im.turms.common.model.bo.group.GroupJoinRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public im.turms.common.model.bo.group.GroupJoinRequest buildPartial() {
      im.turms.common.model.bo.group.GroupJoinRequest result = new im.turms.common.model.bo.group.GroupJoinRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.id_ = id_;
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.creationDate_ = creationDate_;
        to_bitField0_ |= 0x00000002;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        to_bitField0_ |= 0x00000004;
      }
      result.content_ = content_;
      if (((from_bitField0_ & 0x00000008) != 0)) {
        to_bitField0_ |= 0x00000008;
      }
      result.status_ = status_;
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.expirationDate_ = expirationDate_;
        to_bitField0_ |= 0x00000010;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.groupId_ = groupId_;
        to_bitField0_ |= 0x00000020;
      }
      if (((from_bitField0_ & 0x00000040) != 0)) {
        result.requesterId_ = requesterId_;
        to_bitField0_ |= 0x00000040;
      }
      if (((from_bitField0_ & 0x00000080) != 0)) {
        result.responderId_ = responderId_;
        to_bitField0_ |= 0x00000080;
      }
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof im.turms.common.model.bo.group.GroupJoinRequest) {
        return mergeFrom((im.turms.common.model.bo.group.GroupJoinRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(im.turms.common.model.bo.group.GroupJoinRequest other) {
      if (other == im.turms.common.model.bo.group.GroupJoinRequest.getDefaultInstance()) return this;
      if (other.hasId()) {
        setId(other.getId());
      }
      if (other.hasCreationDate()) {
        setCreationDate(other.getCreationDate());
      }
      if (other.hasContent()) {
        bitField0_ |= 0x00000004;
        content_ = other.content_;
        onChanged();
      }
      if (other.hasStatus()) {
        setStatus(other.getStatus());
      }
      if (other.hasExpirationDate()) {
        setExpirationDate(other.getExpirationDate());
      }
      if (other.hasGroupId()) {
        setGroupId(other.getGroupId());
      }
      if (other.hasRequesterId()) {
        setRequesterId(other.getRequesterId());
      }
      if (other.hasResponderId()) {
        setResponderId(other.getResponderId());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      im.turms.common.model.bo.group.GroupJoinRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (im.turms.common.model.bo.group.GroupJoinRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private long id_ ;
    /**
     * <code>optional int64 id = 1;</code>
     * @return Whether the id field is set.
     */
    @java.lang.Override
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional int64 id = 1;</code>
     * @return The id.
     */
    @java.lang.Override
    public long getId() {
      return id_;
    }
    /**
     * <code>optional int64 id = 1;</code>
     * @param value The id to set.
     * @return This builder for chaining.
     */
    public Builder setId(long value) {
      bitField0_ |= 0x00000001;
      id_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      id_ = 0L;
      onChanged();
      return this;
    }

    private long creationDate_ ;
    /**
     * <code>optional int64 creation_date = 2;</code>
     * @return Whether the creationDate field is set.
     */
    @java.lang.Override
    public boolean hasCreationDate() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional int64 creation_date = 2;</code>
     * @return The creationDate.
     */
    @java.lang.Override
    public long getCreationDate() {
      return creationDate_;
    }
    /**
     * <code>optional int64 creation_date = 2;</code>
     * @param value The creationDate to set.
     * @return This builder for chaining.
     */
    public Builder setCreationDate(long value) {
      bitField0_ |= 0x00000002;
      creationDate_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 creation_date = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearCreationDate() {
      bitField0_ = (bitField0_ & ~0x00000002);
      creationDate_ = 0L;
      onChanged();
      return this;
    }

    private java.lang.Object content_ = "";
    /**
     * <code>optional string content = 3;</code>
     * @return Whether the content field is set.
     */
    public boolean hasContent() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string content = 3;</code>
     * @return The content.
     */
    public java.lang.String getContent() {
      java.lang.Object ref = content_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        content_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string content = 3;</code>
     * @return The bytes for content.
     */
    public com.google.protobuf.ByteString
        getContentBytes() {
      java.lang.Object ref = content_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string content = 3;</code>
     * @param value The content to set.
     * @return This builder for chaining.
     */
    public Builder setContent(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      content_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string content = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearContent() {
      bitField0_ = (bitField0_ & ~0x00000004);
      content_ = getDefaultInstance().getContent();
      onChanged();
      return this;
    }
    /**
     * <code>optional string content = 3;</code>
     * @param value The bytes for content to set.
     * @return This builder for chaining.
     */
    public Builder setContentBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000004;
      content_ = value;
      onChanged();
      return this;
    }

    private int status_ = 0;
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @return Whether the status field is set.
     */
    @java.lang.Override public boolean hasStatus() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @return The enum numeric value on the wire for status.
     */
    @java.lang.Override public int getStatusValue() {
      return status_;
    }
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @param value The enum numeric value on the wire for status to set.
     * @return This builder for chaining.
     */
    public Builder setStatusValue(int value) {
      bitField0_ |= 0x00000008;
      status_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @return The status.
     */
    @java.lang.Override
    public im.turms.common.constant.RequestStatus getStatus() {
      @SuppressWarnings("deprecation")
      im.turms.common.constant.RequestStatus result = im.turms.common.constant.RequestStatus.valueOf(status_);
      return result == null ? im.turms.common.constant.RequestStatus.UNRECOGNIZED : result;
    }
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @param value The status to set.
     * @return This builder for chaining.
     */
    public Builder setStatus(im.turms.common.constant.RequestStatus value) {
      if (value == null) {
        throw new NullPointerException();
      }
      bitField0_ |= 0x00000008;
      status_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>optional .im.turms.proto.RequestStatus status = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearStatus() {
      bitField0_ = (bitField0_ & ~0x00000008);
      status_ = 0;
      onChanged();
      return this;
    }

    private long expirationDate_ ;
    /**
     * <code>optional int64 expiration_date = 5;</code>
     * @return Whether the expirationDate field is set.
     */
    @java.lang.Override
    public boolean hasExpirationDate() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional int64 expiration_date = 5;</code>
     * @return The expirationDate.
     */
    @java.lang.Override
    public long getExpirationDate() {
      return expirationDate_;
    }
    /**
     * <code>optional int64 expiration_date = 5;</code>
     * @param value The expirationDate to set.
     * @return This builder for chaining.
     */
    public Builder setExpirationDate(long value) {
      bitField0_ |= 0x00000010;
      expirationDate_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 expiration_date = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearExpirationDate() {
      bitField0_ = (bitField0_ & ~0x00000010);
      expirationDate_ = 0L;
      onChanged();
      return this;
    }

    private long groupId_ ;
    /**
     * <code>optional int64 group_id = 6;</code>
     * @return Whether the groupId field is set.
     */
    @java.lang.Override
    public boolean hasGroupId() {
      return ((bitField0_ & 0x00000020) != 0);
    }
    /**
     * <code>optional int64 group_id = 6;</code>
     * @return The groupId.
     */
    @java.lang.Override
    public long getGroupId() {
      return groupId_;
    }
    /**
     * <code>optional int64 group_id = 6;</code>
     * @param value The groupId to set.
     * @return This builder for chaining.
     */
    public Builder setGroupId(long value) {
      bitField0_ |= 0x00000020;
      groupId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 group_id = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearGroupId() {
      bitField0_ = (bitField0_ & ~0x00000020);
      groupId_ = 0L;
      onChanged();
      return this;
    }

    private long requesterId_ ;
    /**
     * <code>optional int64 requester_id = 7;</code>
     * @return Whether the requesterId field is set.
     */
    @java.lang.Override
    public boolean hasRequesterId() {
      return ((bitField0_ & 0x00000040) != 0);
    }
    /**
     * <code>optional int64 requester_id = 7;</code>
     * @return The requesterId.
     */
    @java.lang.Override
    public long getRequesterId() {
      return requesterId_;
    }
    /**
     * <code>optional int64 requester_id = 7;</code>
     * @param value The requesterId to set.
     * @return This builder for chaining.
     */
    public Builder setRequesterId(long value) {
      bitField0_ |= 0x00000040;
      requesterId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 requester_id = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearRequesterId() {
      bitField0_ = (bitField0_ & ~0x00000040);
      requesterId_ = 0L;
      onChanged();
      return this;
    }

    private long responderId_ ;
    /**
     * <code>optional int64 responder_id = 8;</code>
     * @return Whether the responderId field is set.
     */
    @java.lang.Override
    public boolean hasResponderId() {
      return ((bitField0_ & 0x00000080) != 0);
    }
    /**
     * <code>optional int64 responder_id = 8;</code>
     * @return The responderId.
     */
    @java.lang.Override
    public long getResponderId() {
      return responderId_;
    }
    /**
     * <code>optional int64 responder_id = 8;</code>
     * @param value The responderId to set.
     * @return This builder for chaining.
     */
    public Builder setResponderId(long value) {
      bitField0_ |= 0x00000080;
      responderId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 responder_id = 8;</code>
     * @return This builder for chaining.
     */
    public Builder clearResponderId() {
      bitField0_ = (bitField0_ & ~0x00000080);
      responderId_ = 0L;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:im.turms.proto.GroupJoinRequest)
  }

  // @@protoc_insertion_point(class_scope:im.turms.proto.GroupJoinRequest)
  private static final im.turms.common.model.bo.group.GroupJoinRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new im.turms.common.model.bo.group.GroupJoinRequest();
  }

  public static im.turms.common.model.bo.group.GroupJoinRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GroupJoinRequest>
      PARSER = new com.google.protobuf.AbstractParser<GroupJoinRequest>() {
    @java.lang.Override
    public GroupJoinRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GroupJoinRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GroupJoinRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GroupJoinRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public im.turms.common.model.bo.group.GroupJoinRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


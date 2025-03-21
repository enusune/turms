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
// source: request/storage/delete_resource_request.proto

package im.turms.common.model.dto.request.storage;

/**
 * Protobuf type {@code im.turms.proto.DeleteResourceRequest}
 */
public final class DeleteResourceRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:im.turms.proto.DeleteResourceRequest)
    DeleteResourceRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DeleteResourceRequest.newBuilder() to construct.
  private DeleteResourceRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DeleteResourceRequest() {
    contentType_ = 0;
    keyStr_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new DeleteResourceRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DeleteResourceRequest(
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
            int rawValue = input.readEnum();

            contentType_ = rawValue;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000001;
            keyStr_ = s;
            break;
          }
          case 24: {
            bitField0_ |= 0x00000002;
            keyNum_ = input.readInt64();
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
    return im.turms.common.model.dto.request.storage.DeleteResourceRequestOuterClass.internal_static_im_turms_proto_DeleteResourceRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return im.turms.common.model.dto.request.storage.DeleteResourceRequestOuterClass.internal_static_im_turms_proto_DeleteResourceRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            im.turms.common.model.dto.request.storage.DeleteResourceRequest.class, im.turms.common.model.dto.request.storage.DeleteResourceRequest.Builder.class);
  }

  private int bitField0_;
  public static final int CONTENT_TYPE_FIELD_NUMBER = 1;
  private int contentType_;
  /**
   * <code>.im.turms.proto.ContentType content_type = 1;</code>
   * @return The enum numeric value on the wire for contentType.
   */
  @java.lang.Override public int getContentTypeValue() {
    return contentType_;
  }
  /**
   * <code>.im.turms.proto.ContentType content_type = 1;</code>
   * @return The contentType.
   */
  @java.lang.Override public im.turms.common.constant.ContentType getContentType() {
    @SuppressWarnings("deprecation")
    im.turms.common.constant.ContentType result = im.turms.common.constant.ContentType.valueOf(contentType_);
    return result == null ? im.turms.common.constant.ContentType.UNRECOGNIZED : result;
  }

  public static final int KEY_STR_FIELD_NUMBER = 2;
  private volatile java.lang.Object keyStr_;
  /**
   * <code>optional string key_str = 2;</code>
   * @return Whether the keyStr field is set.
   */
  @java.lang.Override
  public boolean hasKeyStr() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional string key_str = 2;</code>
   * @return The keyStr.
   */
  @java.lang.Override
  public java.lang.String getKeyStr() {
    java.lang.Object ref = keyStr_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      keyStr_ = s;
      return s;
    }
  }
  /**
   * <code>optional string key_str = 2;</code>
   * @return The bytes for keyStr.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getKeyStrBytes() {
    java.lang.Object ref = keyStr_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      keyStr_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int KEY_NUM_FIELD_NUMBER = 3;
  private long keyNum_;
  /**
   * <code>optional int64 key_num = 3;</code>
   * @return Whether the keyNum field is set.
   */
  @java.lang.Override
  public boolean hasKeyNum() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional int64 key_num = 3;</code>
   * @return The keyNum.
   */
  @java.lang.Override
  public long getKeyNum() {
    return keyNum_;
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
    if (contentType_ != im.turms.common.constant.ContentType.PROFILE.getNumber()) {
      output.writeEnum(1, contentType_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, keyStr_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeInt64(3, keyNum_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (contentType_ != im.turms.common.constant.ContentType.PROFILE.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(1, contentType_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, keyStr_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, keyNum_);
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
    if (!(obj instanceof im.turms.common.model.dto.request.storage.DeleteResourceRequest)) {
      return super.equals(obj);
    }
    im.turms.common.model.dto.request.storage.DeleteResourceRequest other = (im.turms.common.model.dto.request.storage.DeleteResourceRequest) obj;

    if (contentType_ != other.contentType_) return false;
    if (hasKeyStr() != other.hasKeyStr()) return false;
    if (hasKeyStr()) {
      if (!getKeyStr()
          .equals(other.getKeyStr())) return false;
    }
    if (hasKeyNum() != other.hasKeyNum()) return false;
    if (hasKeyNum()) {
      if (getKeyNum()
          != other.getKeyNum()) return false;
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
    hash = (37 * hash) + CONTENT_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + contentType_;
    if (hasKeyStr()) {
      hash = (37 * hash) + KEY_STR_FIELD_NUMBER;
      hash = (53 * hash) + getKeyStr().hashCode();
    }
    if (hasKeyNum()) {
      hash = (37 * hash) + KEY_NUM_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getKeyNum());
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest parseFrom(
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
  public static Builder newBuilder(im.turms.common.model.dto.request.storage.DeleteResourceRequest prototype) {
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
   * Protobuf type {@code im.turms.proto.DeleteResourceRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:im.turms.proto.DeleteResourceRequest)
      im.turms.common.model.dto.request.storage.DeleteResourceRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return im.turms.common.model.dto.request.storage.DeleteResourceRequestOuterClass.internal_static_im_turms_proto_DeleteResourceRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return im.turms.common.model.dto.request.storage.DeleteResourceRequestOuterClass.internal_static_im_turms_proto_DeleteResourceRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              im.turms.common.model.dto.request.storage.DeleteResourceRequest.class, im.turms.common.model.dto.request.storage.DeleteResourceRequest.Builder.class);
    }

    // Construct using im.turms.common.model.dto.request.storage.DeleteResourceRequest.newBuilder()
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
      contentType_ = 0;

      keyStr_ = "";
      bitField0_ = (bitField0_ & ~0x00000001);
      keyNum_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return im.turms.common.model.dto.request.storage.DeleteResourceRequestOuterClass.internal_static_im_turms_proto_DeleteResourceRequest_descriptor;
    }

    @java.lang.Override
    public im.turms.common.model.dto.request.storage.DeleteResourceRequest getDefaultInstanceForType() {
      return im.turms.common.model.dto.request.storage.DeleteResourceRequest.getDefaultInstance();
    }

    @java.lang.Override
    public im.turms.common.model.dto.request.storage.DeleteResourceRequest build() {
      im.turms.common.model.dto.request.storage.DeleteResourceRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public im.turms.common.model.dto.request.storage.DeleteResourceRequest buildPartial() {
      im.turms.common.model.dto.request.storage.DeleteResourceRequest result = new im.turms.common.model.dto.request.storage.DeleteResourceRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.contentType_ = contentType_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.keyStr_ = keyStr_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.keyNum_ = keyNum_;
        to_bitField0_ |= 0x00000002;
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
      if (other instanceof im.turms.common.model.dto.request.storage.DeleteResourceRequest) {
        return mergeFrom((im.turms.common.model.dto.request.storage.DeleteResourceRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(im.turms.common.model.dto.request.storage.DeleteResourceRequest other) {
      if (other == im.turms.common.model.dto.request.storage.DeleteResourceRequest.getDefaultInstance()) return this;
      if (other.contentType_ != 0) {
        setContentTypeValue(other.getContentTypeValue());
      }
      if (other.hasKeyStr()) {
        bitField0_ |= 0x00000001;
        keyStr_ = other.keyStr_;
        onChanged();
      }
      if (other.hasKeyNum()) {
        setKeyNum(other.getKeyNum());
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
      im.turms.common.model.dto.request.storage.DeleteResourceRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (im.turms.common.model.dto.request.storage.DeleteResourceRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int contentType_ = 0;
    /**
     * <code>.im.turms.proto.ContentType content_type = 1;</code>
     * @return The enum numeric value on the wire for contentType.
     */
    @java.lang.Override public int getContentTypeValue() {
      return contentType_;
    }
    /**
     * <code>.im.turms.proto.ContentType content_type = 1;</code>
     * @param value The enum numeric value on the wire for contentType to set.
     * @return This builder for chaining.
     */
    public Builder setContentTypeValue(int value) {
      
      contentType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.im.turms.proto.ContentType content_type = 1;</code>
     * @return The contentType.
     */
    @java.lang.Override
    public im.turms.common.constant.ContentType getContentType() {
      @SuppressWarnings("deprecation")
      im.turms.common.constant.ContentType result = im.turms.common.constant.ContentType.valueOf(contentType_);
      return result == null ? im.turms.common.constant.ContentType.UNRECOGNIZED : result;
    }
    /**
     * <code>.im.turms.proto.ContentType content_type = 1;</code>
     * @param value The contentType to set.
     * @return This builder for chaining.
     */
    public Builder setContentType(im.turms.common.constant.ContentType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      contentType_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.im.turms.proto.ContentType content_type = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearContentType() {
      
      contentType_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object keyStr_ = "";
    /**
     * <code>optional string key_str = 2;</code>
     * @return Whether the keyStr field is set.
     */
    public boolean hasKeyStr() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string key_str = 2;</code>
     * @return The keyStr.
     */
    public java.lang.String getKeyStr() {
      java.lang.Object ref = keyStr_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        keyStr_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string key_str = 2;</code>
     * @return The bytes for keyStr.
     */
    public com.google.protobuf.ByteString
        getKeyStrBytes() {
      java.lang.Object ref = keyStr_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        keyStr_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string key_str = 2;</code>
     * @param value The keyStr to set.
     * @return This builder for chaining.
     */
    public Builder setKeyStr(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      keyStr_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string key_str = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearKeyStr() {
      bitField0_ = (bitField0_ & ~0x00000001);
      keyStr_ = getDefaultInstance().getKeyStr();
      onChanged();
      return this;
    }
    /**
     * <code>optional string key_str = 2;</code>
     * @param value The bytes for keyStr to set.
     * @return This builder for chaining.
     */
    public Builder setKeyStrBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000001;
      keyStr_ = value;
      onChanged();
      return this;
    }

    private long keyNum_ ;
    /**
     * <code>optional int64 key_num = 3;</code>
     * @return Whether the keyNum field is set.
     */
    @java.lang.Override
    public boolean hasKeyNum() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional int64 key_num = 3;</code>
     * @return The keyNum.
     */
    @java.lang.Override
    public long getKeyNum() {
      return keyNum_;
    }
    /**
     * <code>optional int64 key_num = 3;</code>
     * @param value The keyNum to set.
     * @return This builder for chaining.
     */
    public Builder setKeyNum(long value) {
      bitField0_ |= 0x00000002;
      keyNum_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 key_num = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearKeyNum() {
      bitField0_ = (bitField0_ & ~0x00000002);
      keyNum_ = 0L;
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


    // @@protoc_insertion_point(builder_scope:im.turms.proto.DeleteResourceRequest)
  }

  // @@protoc_insertion_point(class_scope:im.turms.proto.DeleteResourceRequest)
  private static final im.turms.common.model.dto.request.storage.DeleteResourceRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new im.turms.common.model.dto.request.storage.DeleteResourceRequest();
  }

  public static im.turms.common.model.dto.request.storage.DeleteResourceRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DeleteResourceRequest>
      PARSER = new com.google.protobuf.AbstractParser<DeleteResourceRequest>() {
    @java.lang.Override
    public DeleteResourceRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DeleteResourceRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DeleteResourceRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DeleteResourceRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public im.turms.common.model.dto.request.storage.DeleteResourceRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


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
// source: model/user/user_info.proto

package im.turms.common.model.bo.user;

/**
 * Protobuf type {@code im.turms.proto.UserInfo}
 */
public final class UserInfo extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:im.turms.proto.UserInfo)
    UserInfoOrBuilder {
private static final long serialVersionUID = 0L;
  // Use UserInfo.newBuilder() to construct.
  private UserInfo(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private UserInfo() {
    name_ = "";
    intro_ = "";
    profileAccessStrategy_ = 0;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new UserInfo();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private UserInfo(
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
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000002;
            name_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000004;
            intro_ = s;
            break;
          }
          case 32: {
            bitField0_ |= 0x00000008;
            registrationDate_ = input.readInt64();
            break;
          }
          case 40: {
            bitField0_ |= 0x00000010;
            active_ = input.readBool();
            break;
          }
          case 48: {
            int rawValue = input.readEnum();
            bitField0_ |= 0x00000020;
            profileAccessStrategy_ = rawValue;
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
    return im.turms.common.model.bo.user.UserInfoOuterClass.internal_static_im_turms_proto_UserInfo_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return im.turms.common.model.bo.user.UserInfoOuterClass.internal_static_im_turms_proto_UserInfo_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            im.turms.common.model.bo.user.UserInfo.class, im.turms.common.model.bo.user.UserInfo.Builder.class);
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

  public static final int NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object name_;
  /**
   * <code>optional string name = 2;</code>
   * @return Whether the name field is set.
   */
  @java.lang.Override
  public boolean hasName() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional string name = 2;</code>
   * @return The name.
   */
  @java.lang.Override
  public java.lang.String getName() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      name_ = s;
      return s;
    }
  }
  /**
   * <code>optional string name = 2;</code>
   * @return The bytes for name.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getNameBytes() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      name_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int INTRO_FIELD_NUMBER = 3;
  private volatile java.lang.Object intro_;
  /**
   * <code>optional string intro = 3;</code>
   * @return Whether the intro field is set.
   */
  @java.lang.Override
  public boolean hasIntro() {
    return ((bitField0_ & 0x00000004) != 0);
  }
  /**
   * <code>optional string intro = 3;</code>
   * @return The intro.
   */
  @java.lang.Override
  public java.lang.String getIntro() {
    java.lang.Object ref = intro_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      intro_ = s;
      return s;
    }
  }
  /**
   * <code>optional string intro = 3;</code>
   * @return The bytes for intro.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getIntroBytes() {
    java.lang.Object ref = intro_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      intro_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int REGISTRATION_DATE_FIELD_NUMBER = 4;
  private long registrationDate_;
  /**
   * <code>optional int64 registration_date = 4;</code>
   * @return Whether the registrationDate field is set.
   */
  @java.lang.Override
  public boolean hasRegistrationDate() {
    return ((bitField0_ & 0x00000008) != 0);
  }
  /**
   * <code>optional int64 registration_date = 4;</code>
   * @return The registrationDate.
   */
  @java.lang.Override
  public long getRegistrationDate() {
    return registrationDate_;
  }

  public static final int ACTIVE_FIELD_NUMBER = 5;
  private boolean active_;
  /**
   * <code>optional bool active = 5;</code>
   * @return Whether the active field is set.
   */
  @java.lang.Override
  public boolean hasActive() {
    return ((bitField0_ & 0x00000010) != 0);
  }
  /**
   * <code>optional bool active = 5;</code>
   * @return The active.
   */
  @java.lang.Override
  public boolean getActive() {
    return active_;
  }

  public static final int PROFILE_ACCESS_STRATEGY_FIELD_NUMBER = 6;
  private int profileAccessStrategy_;
  /**
   * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
   * @return Whether the profileAccessStrategy field is set.
   */
  @java.lang.Override public boolean hasProfileAccessStrategy() {
    return ((bitField0_ & 0x00000020) != 0);
  }
  /**
   * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
   * @return The enum numeric value on the wire for profileAccessStrategy.
   */
  @java.lang.Override public int getProfileAccessStrategyValue() {
    return profileAccessStrategy_;
  }
  /**
   * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
   * @return The profileAccessStrategy.
   */
  @java.lang.Override public im.turms.common.constant.ProfileAccessStrategy getProfileAccessStrategy() {
    @SuppressWarnings("deprecation")
    im.turms.common.constant.ProfileAccessStrategy result = im.turms.common.constant.ProfileAccessStrategy.valueOf(profileAccessStrategy_);
    return result == null ? im.turms.common.constant.ProfileAccessStrategy.UNRECOGNIZED : result;
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
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, name_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, intro_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      output.writeInt64(4, registrationDate_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      output.writeBool(5, active_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      output.writeEnum(6, profileAccessStrategy_);
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
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, name_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, intro_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(4, registrationDate_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(5, active_);
    }
    if (((bitField0_ & 0x00000020) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(6, profileAccessStrategy_);
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
    if (!(obj instanceof im.turms.common.model.bo.user.UserInfo)) {
      return super.equals(obj);
    }
    im.turms.common.model.bo.user.UserInfo other = (im.turms.common.model.bo.user.UserInfo) obj;

    if (hasId() != other.hasId()) return false;
    if (hasId()) {
      if (getId()
          != other.getId()) return false;
    }
    if (hasName() != other.hasName()) return false;
    if (hasName()) {
      if (!getName()
          .equals(other.getName())) return false;
    }
    if (hasIntro() != other.hasIntro()) return false;
    if (hasIntro()) {
      if (!getIntro()
          .equals(other.getIntro())) return false;
    }
    if (hasRegistrationDate() != other.hasRegistrationDate()) return false;
    if (hasRegistrationDate()) {
      if (getRegistrationDate()
          != other.getRegistrationDate()) return false;
    }
    if (hasActive() != other.hasActive()) return false;
    if (hasActive()) {
      if (getActive()
          != other.getActive()) return false;
    }
    if (hasProfileAccessStrategy() != other.hasProfileAccessStrategy()) return false;
    if (hasProfileAccessStrategy()) {
      if (profileAccessStrategy_ != other.profileAccessStrategy_) return false;
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
    if (hasName()) {
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
    }
    if (hasIntro()) {
      hash = (37 * hash) + INTRO_FIELD_NUMBER;
      hash = (53 * hash) + getIntro().hashCode();
    }
    if (hasRegistrationDate()) {
      hash = (37 * hash) + REGISTRATION_DATE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getRegistrationDate());
    }
    if (hasActive()) {
      hash = (37 * hash) + ACTIVE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getActive());
    }
    if (hasProfileAccessStrategy()) {
      hash = (37 * hash) + PROFILE_ACCESS_STRATEGY_FIELD_NUMBER;
      hash = (53 * hash) + profileAccessStrategy_;
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.bo.user.UserInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.user.UserInfo parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static im.turms.common.model.bo.user.UserInfo parseFrom(
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
  public static Builder newBuilder(im.turms.common.model.bo.user.UserInfo prototype) {
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
   * Protobuf type {@code im.turms.proto.UserInfo}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:im.turms.proto.UserInfo)
      im.turms.common.model.bo.user.UserInfoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return im.turms.common.model.bo.user.UserInfoOuterClass.internal_static_im_turms_proto_UserInfo_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return im.turms.common.model.bo.user.UserInfoOuterClass.internal_static_im_turms_proto_UserInfo_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              im.turms.common.model.bo.user.UserInfo.class, im.turms.common.model.bo.user.UserInfo.Builder.class);
    }

    // Construct using im.turms.common.model.bo.user.UserInfo.newBuilder()
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
      name_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      intro_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      registrationDate_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000008);
      active_ = false;
      bitField0_ = (bitField0_ & ~0x00000010);
      profileAccessStrategy_ = 0;
      bitField0_ = (bitField0_ & ~0x00000020);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return im.turms.common.model.bo.user.UserInfoOuterClass.internal_static_im_turms_proto_UserInfo_descriptor;
    }

    @java.lang.Override
    public im.turms.common.model.bo.user.UserInfo getDefaultInstanceForType() {
      return im.turms.common.model.bo.user.UserInfo.getDefaultInstance();
    }

    @java.lang.Override
    public im.turms.common.model.bo.user.UserInfo build() {
      im.turms.common.model.bo.user.UserInfo result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public im.turms.common.model.bo.user.UserInfo buildPartial() {
      im.turms.common.model.bo.user.UserInfo result = new im.turms.common.model.bo.user.UserInfo(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.id_ = id_;
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.name_ = name_;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        to_bitField0_ |= 0x00000004;
      }
      result.intro_ = intro_;
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.registrationDate_ = registrationDate_;
        to_bitField0_ |= 0x00000008;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.active_ = active_;
        to_bitField0_ |= 0x00000010;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        to_bitField0_ |= 0x00000020;
      }
      result.profileAccessStrategy_ = profileAccessStrategy_;
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
      if (other instanceof im.turms.common.model.bo.user.UserInfo) {
        return mergeFrom((im.turms.common.model.bo.user.UserInfo)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(im.turms.common.model.bo.user.UserInfo other) {
      if (other == im.turms.common.model.bo.user.UserInfo.getDefaultInstance()) return this;
      if (other.hasId()) {
        setId(other.getId());
      }
      if (other.hasName()) {
        bitField0_ |= 0x00000002;
        name_ = other.name_;
        onChanged();
      }
      if (other.hasIntro()) {
        bitField0_ |= 0x00000004;
        intro_ = other.intro_;
        onChanged();
      }
      if (other.hasRegistrationDate()) {
        setRegistrationDate(other.getRegistrationDate());
      }
      if (other.hasActive()) {
        setActive(other.getActive());
      }
      if (other.hasProfileAccessStrategy()) {
        setProfileAccessStrategy(other.getProfileAccessStrategy());
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
      im.turms.common.model.bo.user.UserInfo parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (im.turms.common.model.bo.user.UserInfo) e.getUnfinishedMessage();
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

    private java.lang.Object name_ = "";
    /**
     * <code>optional string name = 2;</code>
     * @return Whether the name field is set.
     */
    public boolean hasName() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string name = 2;</code>
     * @return The name.
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string name = 2;</code>
     * @return The bytes for name.
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string name = 2;</code>
     * @param value The name to set.
     * @return This builder for chaining.
     */
    public Builder setName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      name_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearName() {
      bitField0_ = (bitField0_ & ~0x00000002);
      name_ = getDefaultInstance().getName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 2;</code>
     * @param value The bytes for name to set.
     * @return This builder for chaining.
     */
    public Builder setNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000002;
      name_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object intro_ = "";
    /**
     * <code>optional string intro = 3;</code>
     * @return Whether the intro field is set.
     */
    public boolean hasIntro() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string intro = 3;</code>
     * @return The intro.
     */
    public java.lang.String getIntro() {
      java.lang.Object ref = intro_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        intro_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string intro = 3;</code>
     * @return The bytes for intro.
     */
    public com.google.protobuf.ByteString
        getIntroBytes() {
      java.lang.Object ref = intro_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        intro_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string intro = 3;</code>
     * @param value The intro to set.
     * @return This builder for chaining.
     */
    public Builder setIntro(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      intro_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string intro = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearIntro() {
      bitField0_ = (bitField0_ & ~0x00000004);
      intro_ = getDefaultInstance().getIntro();
      onChanged();
      return this;
    }
    /**
     * <code>optional string intro = 3;</code>
     * @param value The bytes for intro to set.
     * @return This builder for chaining.
     */
    public Builder setIntroBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000004;
      intro_ = value;
      onChanged();
      return this;
    }

    private long registrationDate_ ;
    /**
     * <code>optional int64 registration_date = 4;</code>
     * @return Whether the registrationDate field is set.
     */
    @java.lang.Override
    public boolean hasRegistrationDate() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional int64 registration_date = 4;</code>
     * @return The registrationDate.
     */
    @java.lang.Override
    public long getRegistrationDate() {
      return registrationDate_;
    }
    /**
     * <code>optional int64 registration_date = 4;</code>
     * @param value The registrationDate to set.
     * @return This builder for chaining.
     */
    public Builder setRegistrationDate(long value) {
      bitField0_ |= 0x00000008;
      registrationDate_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 registration_date = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearRegistrationDate() {
      bitField0_ = (bitField0_ & ~0x00000008);
      registrationDate_ = 0L;
      onChanged();
      return this;
    }

    private boolean active_ ;
    /**
     * <code>optional bool active = 5;</code>
     * @return Whether the active field is set.
     */
    @java.lang.Override
    public boolean hasActive() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional bool active = 5;</code>
     * @return The active.
     */
    @java.lang.Override
    public boolean getActive() {
      return active_;
    }
    /**
     * <code>optional bool active = 5;</code>
     * @param value The active to set.
     * @return This builder for chaining.
     */
    public Builder setActive(boolean value) {
      bitField0_ |= 0x00000010;
      active_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bool active = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearActive() {
      bitField0_ = (bitField0_ & ~0x00000010);
      active_ = false;
      onChanged();
      return this;
    }

    private int profileAccessStrategy_ = 0;
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @return Whether the profileAccessStrategy field is set.
     */
    @java.lang.Override public boolean hasProfileAccessStrategy() {
      return ((bitField0_ & 0x00000020) != 0);
    }
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @return The enum numeric value on the wire for profileAccessStrategy.
     */
    @java.lang.Override public int getProfileAccessStrategyValue() {
      return profileAccessStrategy_;
    }
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @param value The enum numeric value on the wire for profileAccessStrategy to set.
     * @return This builder for chaining.
     */
    public Builder setProfileAccessStrategyValue(int value) {
      bitField0_ |= 0x00000020;
      profileAccessStrategy_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @return The profileAccessStrategy.
     */
    @java.lang.Override
    public im.turms.common.constant.ProfileAccessStrategy getProfileAccessStrategy() {
      @SuppressWarnings("deprecation")
      im.turms.common.constant.ProfileAccessStrategy result = im.turms.common.constant.ProfileAccessStrategy.valueOf(profileAccessStrategy_);
      return result == null ? im.turms.common.constant.ProfileAccessStrategy.UNRECOGNIZED : result;
    }
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @param value The profileAccessStrategy to set.
     * @return This builder for chaining.
     */
    public Builder setProfileAccessStrategy(im.turms.common.constant.ProfileAccessStrategy value) {
      if (value == null) {
        throw new NullPointerException();
      }
      bitField0_ |= 0x00000020;
      profileAccessStrategy_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>optional .im.turms.proto.ProfileAccessStrategy profile_access_strategy = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearProfileAccessStrategy() {
      bitField0_ = (bitField0_ & ~0x00000020);
      profileAccessStrategy_ = 0;
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


    // @@protoc_insertion_point(builder_scope:im.turms.proto.UserInfo)
  }

  // @@protoc_insertion_point(class_scope:im.turms.proto.UserInfo)
  private static final im.turms.common.model.bo.user.UserInfo DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new im.turms.common.model.bo.user.UserInfo();
  }

  public static im.turms.common.model.bo.user.UserInfo getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<UserInfo>
      PARSER = new com.google.protobuf.AbstractParser<UserInfo>() {
    @java.lang.Override
    public UserInfo parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new UserInfo(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<UserInfo> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<UserInfo> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public im.turms.common.model.bo.user.UserInfo getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


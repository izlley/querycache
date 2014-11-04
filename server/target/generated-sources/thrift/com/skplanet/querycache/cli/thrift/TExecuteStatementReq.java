/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.skplanet.querycache.cli.thrift;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TExecuteStatementReq implements org.apache.thrift.TBase<TExecuteStatementReq, TExecuteStatementReq._Fields>, java.io.Serializable, Cloneable, Comparable<TExecuteStatementReq> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TExecuteStatementReq");

  private static final org.apache.thrift.protocol.TField SESSION_HANDLE_FIELD_DESC = new org.apache.thrift.protocol.TField("sessionHandle", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField STATEMENT_FIELD_DESC = new org.apache.thrift.protocol.TField("statement", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField CONFIGURATION_FIELD_DESC = new org.apache.thrift.protocol.TField("configuration", org.apache.thrift.protocol.TType.MAP, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TExecuteStatementReqStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TExecuteStatementReqTupleSchemeFactory());
  }

  public TSessionHandle sessionHandle; // required
  public String statement; // required
  public Map<String,String> configuration; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SESSION_HANDLE((short)1, "sessionHandle"),
    STATEMENT((short)2, "statement"),
    CONFIGURATION((short)3, "configuration");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // SESSION_HANDLE
          return SESSION_HANDLE;
        case 2: // STATEMENT
          return STATEMENT;
        case 3: // CONFIGURATION
          return CONFIGURATION;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private _Fields optionals[] = {_Fields.CONFIGURATION};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SESSION_HANDLE, new org.apache.thrift.meta_data.FieldMetaData("sessionHandle", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TSessionHandle.class)));
    tmpMap.put(_Fields.STATEMENT, new org.apache.thrift.meta_data.FieldMetaData("statement", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CONFIGURATION, new org.apache.thrift.meta_data.FieldMetaData("configuration", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TExecuteStatementReq.class, metaDataMap);
  }

  public TExecuteStatementReq() {
  }

  public TExecuteStatementReq(
    TSessionHandle sessionHandle,
    String statement)
  {
    this();
    this.sessionHandle = sessionHandle;
    this.statement = statement;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TExecuteStatementReq(TExecuteStatementReq other) {
    if (other.isSetSessionHandle()) {
      this.sessionHandle = new TSessionHandle(other.sessionHandle);
    }
    if (other.isSetStatement()) {
      this.statement = other.statement;
    }
    if (other.isSetConfiguration()) {
      Map<String,String> __this__configuration = new HashMap<String,String>(other.configuration);
      this.configuration = __this__configuration;
    }
  }

  public TExecuteStatementReq deepCopy() {
    return new TExecuteStatementReq(this);
  }

  @Override
  public void clear() {
    this.sessionHandle = null;
    this.statement = null;
    this.configuration = null;
  }

  public TSessionHandle getSessionHandle() {
    return this.sessionHandle;
  }

  public TExecuteStatementReq setSessionHandle(TSessionHandle sessionHandle) {
    this.sessionHandle = sessionHandle;
    return this;
  }

  public void unsetSessionHandle() {
    this.sessionHandle = null;
  }

  /** Returns true if field sessionHandle is set (has been assigned a value) and false otherwise */
  public boolean isSetSessionHandle() {
    return this.sessionHandle != null;
  }

  public void setSessionHandleIsSet(boolean value) {
    if (!value) {
      this.sessionHandle = null;
    }
  }

  public String getStatement() {
    return this.statement;
  }

  public TExecuteStatementReq setStatement(String statement) {
    this.statement = statement;
    return this;
  }

  public void unsetStatement() {
    this.statement = null;
  }

  /** Returns true if field statement is set (has been assigned a value) and false otherwise */
  public boolean isSetStatement() {
    return this.statement != null;
  }

  public void setStatementIsSet(boolean value) {
    if (!value) {
      this.statement = null;
    }
  }

  public int getConfigurationSize() {
    return (this.configuration == null) ? 0 : this.configuration.size();
  }

  public void putToConfiguration(String key, String val) {
    if (this.configuration == null) {
      this.configuration = new HashMap<String,String>();
    }
    this.configuration.put(key, val);
  }

  public Map<String,String> getConfiguration() {
    return this.configuration;
  }

  public TExecuteStatementReq setConfiguration(Map<String,String> configuration) {
    this.configuration = configuration;
    return this;
  }

  public void unsetConfiguration() {
    this.configuration = null;
  }

  /** Returns true if field configuration is set (has been assigned a value) and false otherwise */
  public boolean isSetConfiguration() {
    return this.configuration != null;
  }

  public void setConfigurationIsSet(boolean value) {
    if (!value) {
      this.configuration = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SESSION_HANDLE:
      if (value == null) {
        unsetSessionHandle();
      } else {
        setSessionHandle((TSessionHandle)value);
      }
      break;

    case STATEMENT:
      if (value == null) {
        unsetStatement();
      } else {
        setStatement((String)value);
      }
      break;

    case CONFIGURATION:
      if (value == null) {
        unsetConfiguration();
      } else {
        setConfiguration((Map<String,String>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SESSION_HANDLE:
      return getSessionHandle();

    case STATEMENT:
      return getStatement();

    case CONFIGURATION:
      return getConfiguration();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SESSION_HANDLE:
      return isSetSessionHandle();
    case STATEMENT:
      return isSetStatement();
    case CONFIGURATION:
      return isSetConfiguration();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TExecuteStatementReq)
      return this.equals((TExecuteStatementReq)that);
    return false;
  }

  public boolean equals(TExecuteStatementReq that) {
    if (that == null)
      return false;

    boolean this_present_sessionHandle = true && this.isSetSessionHandle();
    boolean that_present_sessionHandle = true && that.isSetSessionHandle();
    if (this_present_sessionHandle || that_present_sessionHandle) {
      if (!(this_present_sessionHandle && that_present_sessionHandle))
        return false;
      if (!this.sessionHandle.equals(that.sessionHandle))
        return false;
    }

    boolean this_present_statement = true && this.isSetStatement();
    boolean that_present_statement = true && that.isSetStatement();
    if (this_present_statement || that_present_statement) {
      if (!(this_present_statement && that_present_statement))
        return false;
      if (!this.statement.equals(that.statement))
        return false;
    }

    boolean this_present_configuration = true && this.isSetConfiguration();
    boolean that_present_configuration = true && that.isSetConfiguration();
    if (this_present_configuration || that_present_configuration) {
      if (!(this_present_configuration && that_present_configuration))
        return false;
      if (!this.configuration.equals(that.configuration))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_sessionHandle = true && (isSetSessionHandle());
    builder.append(present_sessionHandle);
    if (present_sessionHandle)
      builder.append(sessionHandle);

    boolean present_statement = true && (isSetStatement());
    builder.append(present_statement);
    if (present_statement)
      builder.append(statement);

    boolean present_configuration = true && (isSetConfiguration());
    builder.append(present_configuration);
    if (present_configuration)
      builder.append(configuration);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(TExecuteStatementReq other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSessionHandle()).compareTo(other.isSetSessionHandle());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSessionHandle()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sessionHandle, other.sessionHandle);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStatement()).compareTo(other.isSetStatement());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatement()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statement, other.statement);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetConfiguration()).compareTo(other.isSetConfiguration());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConfiguration()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.configuration, other.configuration);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TExecuteStatementReq(");
    boolean first = true;

    sb.append("sessionHandle:");
    if (this.sessionHandle == null) {
      sb.append("null");
    } else {
      sb.append(this.sessionHandle);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("statement:");
    if (this.statement == null) {
      sb.append("null");
    } else {
      sb.append(this.statement);
    }
    first = false;
    if (isSetConfiguration()) {
      if (!first) sb.append(", ");
      sb.append("configuration:");
      if (this.configuration == null) {
        sb.append("null");
      } else {
        sb.append(this.configuration);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (sessionHandle == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sessionHandle' was not present! Struct: " + toString());
    }
    if (statement == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'statement' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (sessionHandle != null) {
      sessionHandle.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TExecuteStatementReqStandardSchemeFactory implements SchemeFactory {
    public TExecuteStatementReqStandardScheme getScheme() {
      return new TExecuteStatementReqStandardScheme();
    }
  }

  private static class TExecuteStatementReqStandardScheme extends StandardScheme<TExecuteStatementReq> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TExecuteStatementReq struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SESSION_HANDLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.sessionHandle = new TSessionHandle();
              struct.sessionHandle.read(iprot);
              struct.setSessionHandleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STATEMENT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.statement = iprot.readString();
              struct.setStatementIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CONFIGURATION
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map28 = iprot.readMapBegin();
                struct.configuration = new HashMap<String,String>(2*_map28.size);
                for (int _i29 = 0; _i29 < _map28.size; ++_i29)
                {
                  String _key30;
                  String _val31;
                  _key30 = iprot.readString();
                  _val31 = iprot.readString();
                  struct.configuration.put(_key30, _val31);
                }
                iprot.readMapEnd();
              }
              struct.setConfigurationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TExecuteStatementReq struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.sessionHandle != null) {
        oprot.writeFieldBegin(SESSION_HANDLE_FIELD_DESC);
        struct.sessionHandle.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.statement != null) {
        oprot.writeFieldBegin(STATEMENT_FIELD_DESC);
        oprot.writeString(struct.statement);
        oprot.writeFieldEnd();
      }
      if (struct.configuration != null) {
        if (struct.isSetConfiguration()) {
          oprot.writeFieldBegin(CONFIGURATION_FIELD_DESC);
          {
            oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, struct.configuration.size()));
            for (Map.Entry<String, String> _iter32 : struct.configuration.entrySet())
            {
              oprot.writeString(_iter32.getKey());
              oprot.writeString(_iter32.getValue());
            }
            oprot.writeMapEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TExecuteStatementReqTupleSchemeFactory implements SchemeFactory {
    public TExecuteStatementReqTupleScheme getScheme() {
      return new TExecuteStatementReqTupleScheme();
    }
  }

  private static class TExecuteStatementReqTupleScheme extends TupleScheme<TExecuteStatementReq> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TExecuteStatementReq struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct.sessionHandle.write(oprot);
      oprot.writeString(struct.statement);
      BitSet optionals = new BitSet();
      if (struct.isSetConfiguration()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetConfiguration()) {
        {
          oprot.writeI32(struct.configuration.size());
          for (Map.Entry<String, String> _iter33 : struct.configuration.entrySet())
          {
            oprot.writeString(_iter33.getKey());
            oprot.writeString(_iter33.getValue());
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TExecuteStatementReq struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.sessionHandle = new TSessionHandle();
      struct.sessionHandle.read(iprot);
      struct.setSessionHandleIsSet(true);
      struct.statement = iprot.readString();
      struct.setStatementIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TMap _map34 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.configuration = new HashMap<String,String>(2*_map34.size);
          for (int _i35 = 0; _i35 < _map34.size; ++_i35)
          {
            String _key36;
            String _val37;
            _key36 = iprot.readString();
            _val37 = iprot.readString();
            struct.configuration.put(_key36, _val37);
          }
        }
        struct.setConfigurationIsSet(true);
      }
    }
  }

}

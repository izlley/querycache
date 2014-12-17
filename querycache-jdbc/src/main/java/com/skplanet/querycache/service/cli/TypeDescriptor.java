package com.skplanet.querycache.service.cli;

import java.util.List;

import com.skplanet.querycache.thrift.TPrimitiveTypeEntry;
import com.skplanet.querycache.thrift.TTypeDesc;
import com.skplanet.querycache.thrift.TTypeEntry;

/**
 * TypeDescriptor.
 *
 */
public class TypeDescriptor {

  private final Type type;
  private String typeName = null;

  public TypeDescriptor(Type type) {
    this.type = type;
  }

  public TypeDescriptor(TTypeDesc tTypeDesc) {
    List<TTypeEntry> tTypeEntries = tTypeDesc.getTypes();
    TPrimitiveTypeEntry top = tTypeEntries.get(0).getPrimitiveEntry();
    this.type = Type.getType(top.getType());
  }

  public TypeDescriptor(String typeName) {
    this.type = Type.getType(typeName);
    if (this.type.isComplexType()) {
      this.typeName = typeName;
    }
  }

  public Type getType() {
    return type;
  }

  public TTypeDesc toTTypeDesc() {
    TTypeEntry entry = TTypeEntry.primitiveEntry(new TPrimitiveTypeEntry(type.toTType()));
    TTypeDesc desc = new TTypeDesc();
    desc.addToTypes(entry);
    return desc;
  }

  public String getTypeName() {
    if (typeName != null) {
      return typeName;
    } else {
      return type.getName();
    }
  }
}

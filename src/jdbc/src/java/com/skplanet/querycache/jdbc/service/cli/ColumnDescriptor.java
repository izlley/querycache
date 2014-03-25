package com.skplanet.querycache.service.cli;

//import org.apache.hadoop.hive.metastore.api.FieldSchema;
import com.skplanet.querycache.thrift.TColumnDesc;


/**
 * ColumnDescriptor.
 *
 */
public class ColumnDescriptor {
  private final String name;
  private final String comment;
  private final TypeDescriptor type;
  // ordinal position of this column in the schema
  private final int position;

  public ColumnDescriptor(String name, String comment, TypeDescriptor type, int position) {
    this.name = name;
    this.comment = comment;
    this.type = type;
    this.position = position;
  }

  public ColumnDescriptor(TColumnDesc tColumnDesc) {
    name = tColumnDesc.getColumnName();
    comment = tColumnDesc.getComment();
    type = new TypeDescriptor(tColumnDesc.getTypeDesc());
    position = tColumnDesc.getPosition();
  }

  /*
  public ColumnDescriptor(FieldSchema column, int position) {
    name = column.getName();
    comment = column.getComment();
    type = new TypeDescriptor(column.getType());
    this.position = position;
  }*/

  public static ColumnDescriptor newPrimitiveColumnDescriptor(String name, String comment, Type type, int position) {
    return new ColumnDescriptor(name, comment, new TypeDescriptor(type), position);
  }

  public String getName() {
    return name;
  }

  public String getComment() {
    return comment;
  }

  public TypeDescriptor getTypeDescriptor() {
    return type;
  }

  public int getOrdinalPosition() {
    return position;
  }

  public TColumnDesc toTColumnDesc() {
    TColumnDesc tColumnDesc = new TColumnDesc();
    tColumnDesc.setColumnName(name);
    tColumnDesc.setComment(comment);
    tColumnDesc.setTypeDesc(type.toTTypeDesc());
    tColumnDesc.setPosition(position);
    return tColumnDesc;
  }

  public Type getType() {
    return type.getType();
  }

  public boolean isPrimitive() {
    return type.getType().isPrimitiveType();
  }

  public String getTypeName() {
    return type.getTypeName();
  }
}

package com.skplanet.querycache.service.cli;

import java.util.ArrayList;
import java.util.List;

//import org.apache.hadoop.hive.metastore.api.FieldSchema;
//import org.apache.hadoop.hive.metastore.api.Schema;
import com.skplanet.querycache.thrift.TColumnDesc;
import com.skplanet.querycache.thrift.TTableSchema;

/**
 * TableSchema.
 *
 */
public class TableSchema {
  private final List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();

  public TableSchema() {
  }

  public TableSchema(int numColumns) {
    // TODO: remove this constructor
  }

  public TableSchema(TTableSchema tTableSchema) {
    for (TColumnDesc tColumnDesc : tTableSchema.getColumns()) {
      columns.add(new ColumnDescriptor(tColumnDesc));
    }
  }

  /*
  public TableSchema(List<FieldSchema> fieldSchemas) {
    int pos = 1;
    for (FieldSchema field : fieldSchemas) {
      columns.add(new ColumnDescriptor(field, pos++));
    }
  }

  public TableSchema(Schema schema) {
    this(schema.getFieldSchemas());
  }*/

  public List<ColumnDescriptor> getColumnDescriptors() {
    return new ArrayList<ColumnDescriptor>(columns);
  }

  public ColumnDescriptor getColumnDescriptorAt(int pos) {
    return columns.get(pos);
  }

  public int getSize() {
    return columns.size();
  }

  public void clear() {
    columns.clear();
  }


  public TTableSchema toTTableSchema() {
    TTableSchema tTableSchema = new TTableSchema();
    for (ColumnDescriptor col : columns) {
      tTableSchema.addToColumns(col.toTColumnDesc());
    }
    return tTableSchema;
  }

  public TableSchema addPrimitiveColumn(String columnName, Type columnType, String columnComment) {
    columns.add(ColumnDescriptor.newPrimitiveColumnDescriptor(columnName, columnComment, columnType, columns.size() + 1));
    return this;
  }

  public TableSchema addStringColumn(String columnName, String columnComment) {
    columns.add(ColumnDescriptor.newPrimitiveColumnDescriptor(columnName, columnComment, Type.STRING_TYPE, columns.size() + 1));
    return this;
  }
}


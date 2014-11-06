package com.skplanet.querycache.jdbc;

import static com.skplanet.querycache.thrift.TypesConstants.TYPE_NAMES;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.skplanet.querycache.service.cli.TableSchema;
import com.skplanet.querycache.cli.thrift.TCLIService;
import com.skplanet.querycache.thrift.TColumnDesc;
import com.skplanet.querycache.cli.thrift.TFetchOrientation;
import com.skplanet.querycache.cli.thrift.TFetchResultsReq;
import com.skplanet.querycache.cli.thrift.TFetchResultsResp;
import com.skplanet.querycache.cli.thrift.TGetResultSetMetadataReq;
import com.skplanet.querycache.cli.thrift.TGetResultSetMetadataResp;
import com.skplanet.querycache.cli.thrift.TOperationHandle;
import com.skplanet.querycache.thrift.TRow;
import com.skplanet.querycache.cli.thrift.TSessionHandle;
import com.skplanet.querycache.thrift.TTableSchema;

/**
 * QCQueryResultSet.
 *
 */
public class QCQueryResultSet extends QCBaseResultSet {

  public static final Log LOG = LogFactory.getLog(QCQueryResultSet.class);

  private TCLIService.Iface client;
  private TOperationHandle stmtHandle;
  private TSessionHandle sessHandle;
  private int maxRows;
  private int fetchSize;
  private int rowsFetched = 0;

  private List<TRow> fetchedRows;
  private Iterator<TRow> fetchedRowsItr;
  private boolean isClosed = false;
  private boolean emptyResultSet = false;
  private boolean isScrollable = false;
  private boolean fetchFirst = false;
  private boolean hasMoreRows = true;

  public static class Builder {

    private TCLIService.Iface client = null;
    private TOperationHandle stmtHandle = null;
    private TSessionHandle sessHandle  = null;

    /**
     * Sets the limit for the maximum number of rows that any ResultSet object produced by this
     * Statement can contain to the given number. If the limit is exceeded, the excess rows
     * are silently dropped. The value must be >= 0, and 0 means there is not limit.
     */
    private int maxRows = 0;
    private boolean retrieveSchema = true;
    private List<String> colNames;
    private List<String> colTypes;
    private int fetchSize = 50;
    private boolean emptyResultSet = false;
    private boolean isScrollable = false;

    public Builder setClient(TCLIService.Iface client) {
      this.client = client;
      return this;
    }

    public Builder setStmtHandle(TOperationHandle stmtHandle) {
      this.stmtHandle = stmtHandle;
      return this;
    }

    public Builder setSessionHandle(TSessionHandle sessHandle) {
      this.sessHandle = sessHandle;
      return this;
    }

    public Builder setMaxRows(int maxRows) {
      this.maxRows = maxRows;
      return this;
    }

    public Builder setSchema(List<String> colNames, List<String> colTypes) {
      this.colNames = new ArrayList<String>();
      this.colNames.addAll(colNames);
      this.colTypes = new ArrayList<String>();
      this.colTypes.addAll(colTypes);
      this.retrieveSchema = false;
      return this;
    }

    public Builder setFetchSize(int fetchSize) {
      this.fetchSize = fetchSize;
      return this;
    }

    public Builder setEmptyResultSet(boolean emptyResultSet) {
      this.emptyResultSet = emptyResultSet;
      return this;
    }

    public Builder setScrollable(boolean setScrollable) {
      this.isScrollable = setScrollable;
      return this;
    }

    public QCQueryResultSet build() throws SQLException {
      return new QCQueryResultSet(this);
    }
  }

  protected QCQueryResultSet(Builder builder) throws SQLException {
    this.client = builder.client;
    this.stmtHandle = builder.stmtHandle;
    this.sessHandle = builder.sessHandle;
    this.fetchSize = builder.fetchSize;
    columnNames = new ArrayList<String>();
    columnTypes = new ArrayList<String>();
    if (builder.retrieveSchema) {
      retrieveSchema();
    } else {
      this.columnNames.addAll(builder.colNames);
      this.columnTypes.addAll(builder.colTypes);
    }
    this.emptyResultSet = builder.emptyResultSet;
    if (builder.emptyResultSet) {
      this.maxRows = 0;
    } else {
      this.maxRows = builder.maxRows;
    }
    this.isScrollable = builder.isScrollable;
  }

  /**
   * Retrieve schema from the server
   */
  private void retrieveSchema() throws SQLException {
    try {
      TGetResultSetMetadataReq metadataReq = new TGetResultSetMetadataReq(stmtHandle);
      // TODO need session handle
      TGetResultSetMetadataResp  metadataResp = client.GetResultSetMetadata(metadataReq);
      Utils.verifySuccess(metadataResp.getStatus());

      StringBuilder namesSb = new StringBuilder();
      StringBuilder typesSb = new StringBuilder();

      TTableSchema schema = metadataResp.getSchema();
      if (schema == null || !schema.isSetColumns()) {
        // TODO: should probably throw an exception here.
        return;
      }
      setSchema(new TableSchema(schema));

      List<TColumnDesc> columns = schema.getColumns();
      for (int pos = 0; pos < schema.getColumnsSize(); pos++) {
        if (pos != 0) {
          namesSb.append(",");
          typesSb.append(",");
        }
        String columnName = columns.get(pos).getColumnName();
        columnNames.add(columnName);
        String columnTypeName = TYPE_NAMES.get(
            columns.get(pos).getTypeDesc().getTypes().get(0).getPrimitiveEntry().getType());
        columnTypes.add(columnTypeName);
      }
    } catch (SQLException eS) {
      throw eS; // rethrow the SQLException as is
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new SQLException("Could not create ResultSet: " + ex.getMessage(), ex);
    }
  }

  /**
   * Set the specified schema to the resultset
   * @param colNames
   * @param colTypes
   */
  public void setSchema(List<String> colNames, List<String> colTypes) {
    columnNames.addAll(colNames);
    columnTypes.addAll(colTypes);
  }

  @Override
  public void close() throws SQLException {
    // Need reset during re-open when needed
    client = null;
    stmtHandle = null;
    sessHandle = null;
    isClosed = true;
  }

  /**
   * Moves the cursor down one row from its current position.
   *
   * @see java.sql.ResultSet#next()
   * @throws SQLException
   *           if a database access error occurs.
   */
  public boolean next() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    if (emptyResultSet || (maxRows > 0 && rowsFetched >= maxRows) ||
        (!hasMoreRows && fetchedRowsItr != null && !fetchedRowsItr.hasNext())) {
      return false;
    }

    try {
      TFetchOrientation orientation = TFetchOrientation.FETCH_NEXT;
      if (fetchFirst) {
        // If we are asked to start from begining, clear the current fetched resultset
        orientation = TFetchOrientation.FETCH_FIRST;
        fetchedRows = null;
        fetchedRowsItr = null;
        fetchFirst = false;
      }
      
      if (fetchedRows == null || !fetchedRowsItr.hasNext()) {
        TFetchResultsReq fetchReq = new TFetchResultsReq(stmtHandle,
            orientation, fetchSize);
        TFetchResultsResp fetchResp = client.FetchResults(fetchReq);
        Utils.verifySuccessWithInfo(fetchResp.getStatus());
        fetchedRows = fetchResp.getResults().getRows();
        fetchedRowsItr = fetchedRows.iterator();
        hasMoreRows = fetchResp.hasMoreRows;
      }

      String rowStr = "";
      if (fetchedRowsItr.hasNext()) {
        row = fetchedRowsItr.next();
      } else {
        return false;
      }

      rowsFetched++;
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("Fetched row string: " + rowStr);
      }

    } catch (SQLException eS) {
      throw eS;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new SQLException("Error retrieving next row", ex);
    }
    // NOTE: fetchOne dosn't throw new SQLException("Method not supported").
    return true;
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    return super.getMetaData();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    fetchSize = rows;
  }

  @Override
  public int getType() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    if (isScrollable) {
      return ResultSet.TYPE_SCROLL_INSENSITIVE;
    } else {
      return ResultSet.TYPE_FORWARD_ONLY;
    }
  }

  @Override
  public int getFetchSize() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    return fetchSize;
  }

  /**
   * Moves the cursor before the first row of the resultset.
   *
   * @see java.sql.ResultSet#next()
   * @throws SQLException
   *           if a database access error occurs.
   */
  @Override
  public void beforeFirst() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    if (!isScrollable) {
      throw new SQLException("Method not supported for TYPE_FORWARD_ONLY resultset");
    }
    fetchFirst = true;
    rowsFetched = 0;
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    return (rowsFetched == 0);
  }

  @Override
  public int getRow() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    // it does not follow JDK spec :
    //   this doesn't return the current row number, but return the number of rows
    //   in the resultset.
    if (fetchedRows != null) {
      return fetchedRows.size();
    }
    return 0;
  }

  @Override
  public boolean last() throws SQLException {
    if (isClosed) {
      throw new SQLException("Resultset is closed");
    }
    // it does not follow JDK spec :
    //   Even if the result set type is TYPE_FORWARD_ONLY, exception will not occur.
    //if (!isScrollable) {
    //  throw new SQLException("Method not supported for TYPE_FORWARD_ONLY resultset");
    //}
    
    if (emptyResultSet || (maxRows > 0 && rowsFetched >= maxRows)) {
      return false;
    }

    try {
      TFetchOrientation orientation = TFetchOrientation.FETCH_NEXT;
      
      if (fetchedRows == null) {
        TFetchResultsReq fetchReq = new TFetchResultsReq(stmtHandle,
            orientation, fetchSize);
        TFetchResultsResp fetchResp = client.FetchResults(fetchReq);
        Utils.verifySuccessWithInfo(fetchResp.getStatus());
        fetchedRows = fetchResp.getResults().getRows();
        fetchedRowsItr = fetchedRows.iterator();
      }

      if (fetchedRows.size() == 0) {
        return false;
      } else {
        return true;
      }
    } catch (SQLException eS) {
      throw eS;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new SQLException("Error moving the cursor to the last row.", ex);
    }
  }

  public <T> T getObject(String columnLabel, Class<T> type)  throws SQLException {
    //JDK 1.7
    throw new SQLException("Method not supported");
  }

  public <T> T getObject(int columnIndex, Class<T> type)  throws SQLException {
    //JDK 1.7
    throw new SQLException("Method not supported");
  }
}

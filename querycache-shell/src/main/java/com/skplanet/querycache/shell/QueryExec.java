/**
 * Created by nazgul33 on 15. 3. 12.
 */
package com.skplanet.querycache.shell;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class QueryExec {
    public static final Logger LOG = LoggerFactory.getLogger("qcshell");

    public static final int OUTPUT_TSV = 0;
    public static final int OUTPUT_CSV = 1;
    public static final int OUTPUT_PRETTY = 2;
    public static final int OUTPUT_MAX = 2;
    QueryCacheShell.ParsedOptions options;
    Connection con = null;
    BufferedWriter output;
    int outputType;

    CSVPrinter csvFilePrinter;
    TSVWriter tsvFilePrinter;
    PrettyWriter prettyFilePrinter;

    QueryExec(QueryCacheShell.ParsedOptions options, OutputStreamWriter output) throws SQLException, IOException {
        this.options = options;
        this.con = DriverManager.getConnection(options.url, options.user, options.passwd);
        this.output = new BufferedWriter(output);
        this.outputType = options.outputType;
        switch (outputType) {
            case OUTPUT_CSV: {
                csvFilePrinter = new CSVPrinter( output, CSVFormat.DEFAULT.withRecordSeparator("\n") );
                break;
            }
            case OUTPUT_TSV: {
                tsvFilePrinter = new TSVWriter( output, "\n" );
                break;
            }
            case OUTPUT_PRETTY: {
                prettyFilePrinter = new PrettyWriter( output, "\n" );
                break;
            }
        }
    }

    public static int getOutputTypeFromString(String type) {
        if ("csv".equals(type)) return OUTPUT_CSV;
        if ("tsv".equals(type)) return OUTPUT_TSV;
        if ("default".equals(type)) return OUTPUT_PRETTY;
        return -1;
    }

    public boolean execSingle(String query) {
        boolean bRet = true;
        Statement stmt = null;
        boolean bHasResultSet;
        long rowCount = 0;

        long startTime = System.currentTimeMillis();

        try {
            stmt = con.createStatement();
            stmt.setFetchSize(1000);
            bHasResultSet = stmt.execute(query);
            if (!bHasResultSet) {
                LOG.info("Query Successful.");
                stmt.close();
                return true;
            }

            LOG.info("Fetching results...");
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            ArrayList<String> row = new ArrayList<>();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rsmd.getColumnName(i));
                switch (rsmd.getColumnType(i)) {
                    case Types.VARCHAR:
                    case Types.CHAR:
                    case Types.BIGINT:
                    case Types.INTEGER:
                    case Types.TINYINT:
                    case Types.FLOAT:
                    case Types.DOUBLE:
                    case Types.DATE:
                    case Types.TIME:
                        break;
                    default:
                        LOG.error("\nNot supported type at column {}({})", i, rsmd.getColumnName(i));
                        break;
                }
            }
            switch (outputType) {
                case OUTPUT_CSV: {
                    csvFilePrinter.printRecord(row);
                    break;
                }
                case OUTPUT_TSV: {
                    tsvFilePrinter.printRecord(row);
                    break;
                }
                case OUTPUT_PRETTY: {
                    prettyFilePrinter.setHeader(row);
                    break;
                }
            }

            while (rs.next()) {
                row.clear();
                for (int i = 1; i <= columnCount; i++) {
                    int type = rsmd.getColumnType(i);
                    String colStr;
                    switch (type) {
                        case Types.VARCHAR:
                        case Types.CHAR:
                            colStr = rs.getString(i);
                            break;
                        case Types.BIGINT:
                            colStr = String.valueOf(rs.getLong(i));
                            break;
                        case Types.INTEGER:
                        case Types.TINYINT:
                            colStr = String.valueOf(rs.getInt(i));
                            break;
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            colStr = String.valueOf(rs.getDouble(i));
                            break;
                        case Types.DATE:
                            colStr = rs.getDate(i).toString();
                            break;
                        case Types.TIME:
                            colStr = rs.getTime(i).toString();
                            break;
                        default:
                            colStr = "";
                            break;
                    }
                    row.add(colStr);
                }
                switch (outputType) {
                    case OUTPUT_CSV:
                        csvFilePrinter.printRecord(row);
                        break;
                    case OUTPUT_TSV:
                        tsvFilePrinter.printRecord(row);
                        break;
                    case OUTPUT_PRETTY:
                        prettyFilePrinter.printRecord(row);
                        break;
                }
                rowCount++;
            }
            if (outputType == OUTPUT_PRETTY) {
                prettyFilePrinter.flushRecords();
            }
            this.output.flush();
            LOG.info("{} rows fetched.", rowCount);
        } catch (SQLException e) {
            LOG.error("executing query : {}", query, e);
            bRet = false;
        } catch (IOException e) {
            LOG.error("writing output", e);
            bRet = false;
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            // ignore
        }

        try {
            output.flush();
        } catch (IOException e) {
            LOG.error("flushing output", e);
        }

        LOG.info("Query finished in {}ms", System.currentTimeMillis() - startTime);

        return bRet;
    }

    public boolean exec() {
        if (options.query_list == null || options.query_list.size() == 0) {
            LOG.error("No query ready to run");
            return false;
        }

        for (String s: options.query_list) {
            LOG.debug("SQL : {}", s);
        }

        for (String q: options.query_list) {
            execSingle(q);
        }

        return true;
    }

    public boolean close() {
        if (this.con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // ignore?
            }
        }
        return true;
    }
}

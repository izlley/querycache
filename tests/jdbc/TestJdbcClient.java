package test.com.skplanet.querycache.jdbc;

import java.sql.*;
import java.lang.*;

public class TestJdbcClient {
  static String gQuery = "select * from MOCB limit 10";

  public static void main(String[] args) throws InterruptedException {
    
    /*
    String sLimit = "";
    String sSetSize = "";
    boolean isSetFetchsize = false;
    if (args.length == 1) {
      sLimit = " LIMIT " + args[0];
    } else if (args.length == 2) {
      sLimit = " LIMIT " + args[0];
      isSetFetchsize = true;
      sSetSize = args[1];
    }
    */

    try {
      Class.forName("com.skplanet.querycache.jdbc.QCDriver");
    } catch (ClassNotFoundException e) {
      System.out.println("Class not found " + e);
    }
    try {
      //
      // JDBC Connect
      //
      Connection con = DriverManager
          .getConnection("jdbc:bdb://localhost:8282/;auth=noSasl");
      // con.setAutoCommit(false);
      Statement stmt = con.createStatement();
      // ResultSet rs = stmt.execute("SELECT * FROM tstore.log_iap LIMIT " +
      // sLimit);
      // stmt.setMaxRows(6000);
      // PreparedStatement stmt = con.prepareStatement(gQuery);
      /*if (isSetFetchsize == true) {
        System.out.println(Integer.parseInt(sSetSize));
        stmt.setFetchSize(Integer.parseInt(sSetSize));
      }*/

      //
      // exectueQuery
      //
      // stmt.execute("use tstore");
      // Boolean result = stmt.execute("SELECT * FROM log_iap LIMIT " + sLimit);
      // stmt.execute();
      ResultSet rs = stmt.executeQuery(gQuery);// + sLimit);
      // ResultSet rs = stmt.getResultSet();
      ResultSetMetaData rsmd = rs.getMetaData();

      /* Print col names */
      int sColCnt = rsmd.getColumnCount();
      String sColnames = "";
      for (int i = 1; i <= sColCnt; i++) {
        sColnames += rsmd.getColumnName(i);
        sColnames += "  ";
      }
      System.out.println(sColnames);

      //
      // Fetch rows
      //
      while (rs.next()) {
        for (int i = 1; i <= sColCnt; i++) {
          int type = rsmd.getColumnType(i);
          switch (type) {
            case Types.VARCHAR:
            case Types.CHAR:
              System.out.print(rs.getString(i));
              break;
            case Types.BIGINT:
              System.out.print(rs.getLong(i));
              break;
            case Types.INTEGER:
            case Types.TINYINT:
              System.out.print(rs.getInt(i));
              break;
            case Types.FLOAT:
            case Types.DOUBLE:
              System.out.print(rs.getDouble(i));
              break;
          }
          System.out.print("  ");
        }
        System.out.println();
      }
      //
      // Close
      //
      stmt.close();
      con.close();
    } catch (SQLException e) {
      System.out.println("SQL exception occured" + e);
    }
  }
}

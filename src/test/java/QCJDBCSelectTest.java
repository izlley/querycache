import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileReader;

public class QCJDBCSelectTest {

  static String gQuery = "";
  static String[] gMbrid = new String[1000000];

  static enum ConnType {
    IMPALA_JDBC, HIVE_JDBC, PHOENIX_JDBC, MYSQL_JDBC,
  }
  
  static Random randomGenerator = new Random();

  static String mbridPath = "/home/1000632/jdbc_test/mbrid.res";

  private static void loadMbrid(String aPath) {
    int i = 0;
    try {
      Scanner in = new Scanner(new FileReader(aPath));
      while (in.hasNext()) {
        gMbrid[i++] = in.next();
      }
      //for (String id : gMbrid)
      //  System.out.println(id);
    } catch (Exception e) {
      System.out.println("file read error" + e.getMessage());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    String sLimit = "";
    String sUrl = "";
    String sHost = "";
    int fetchSize = 1024;
    int maxRows = 1024;
    int loopCnt = 1;
    int upperCnt = 0;
    boolean isFetchsize = false;
    boolean isMaxrows = false;
    boolean isSilence = false;
    long startTime = 0;
    long endTime = 0;
    long totalTime = 0;
    long sumTime = 0;
    long upperLimit = 0;
    long histogramInerval = 50;
    long successCnt = 0;
    long failedCnt = 0;
    long minTime = Long.MAX_VALUE;
    long maxTime = 0;
    String sPort = "8655";
    String sQuery = "";
    ConnType sConnType = ConnType.PHOENIX_JDBC;
    List<Long> sProfile = new ArrayList<Long>();
    long[] sHistogram = new long[21];
    Connection con = null;

    if (args.length < 2) {
      doExit();
    }

    //loadMbrid(mbridPath);

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-query")) {
        gQuery = args[++i];
      } else if (args[i].equals("-type")) {
        String type = args[++i];
        if (type.equals("impala")) {
          sConnType = ConnType.IMPALA_JDBC;
        } else if (type.equals("hive")) {
          sConnType = ConnType.HIVE_JDBC;
        } else if (type.equals("phoenix")) {
          sConnType = ConnType.PHOENIX_JDBC;
        } else if (type.equals("mysql")) {
          sConnType = ConnType.MYSQL_JDBC;
        } else {
          doExit();
        }
      } else if (args[i].equals("-limit")) {
        sLimit = " limit " + args[++i];
      } else if (args[i].equals("-fetchsize")) {
        isFetchsize = true;
        fetchSize = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-maxrows")) {
        isMaxrows = true;
        maxRows = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-silence")) {
        isSilence = true;
      } else if (args[i].equals("-loopcnt")) {
        loopCnt = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-upper")) {
        upperLimit = Long.parseLong(args[++i]);
      } else if (args[i].equals("-histogram_interval")) {
        histogramInerval = Long.parseLong(args[++i]);
      }  else if (args[i].equals("-host")) {
        sHost = args[++i];
      } else if (args[i].equals("-port")) {
        sPort = args[++i];
      } else {
        doExit();
      }
    }
    
    if (sHost.isEmpty()) {
      doExit();
    }

    try {
      Class.forName("com.skplanet.querycache.jdbc.QCDriver");
    } catch (ClassNotFoundException e) {
      System.out.println("Class not found " + e);
    }

    switch (sConnType) {
      case IMPALA_JDBC:
        sUrl = "jdbc:impala://" + sHost + ":" + sPort;
        break;
      case HIVE_JDBC:
        sUrl = "jdbc:hive://" + sHost + ":" + sPort;
        break;
      case PHOENIX_JDBC:
        //sUrl = "jdbc:phoenix://127.0.0.1:8655";
        sUrl = "jdbc:bdb://" + sHost + ":" + sPort;
        break;
      case MYSQL_JDBC:
        // sUrl = "jdbc:mysql://127.0.0.1:8655?user=hive&password=hive";
        sUrl = "jdbc:mysql://" + sHost + ":" + sPort;
        break;
      default:
        break;
    }

    for (int idx = 1; idx <= loopCnt; idx++) {
      try {
        if (gQuery.isEmpty()) {
          // generate query
          sQuery = "SELECT BNFT_VALUE FROM SW_CUST_BENEFIT WHERE MBR_ID= '" +
            randomGenerator.nextInt(24000000) + "'";
          //sQuery = "SELECT BNFT_VALUE FROM OCB_CUST_BENEFIT WHERE MBR_ID= '" +
          //  gMbrid[randomGenerator.nextInt(1000000)] + "'";
          //sQuery = "SELECT BNFT_VALUE FROM OCB_CUST_BENEFIT WHERE MBR_ID= '" +
          //  randomGenerator.nextInt(200000000) + "'";
        } else {
          sQuery = gQuery;
        }
        
        //
        // Connect to Querycache server
        //
        startTime = System.currentTimeMillis();
        //System.out.println(sUrl);
        con = DriverManager.getConnection(sUrl);
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        //
        // Create stmt
        //
        startTime = System.currentTimeMillis();
        Statement stmt = con.createStatement();
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        //if (isMaxrows == true) {
        //  stmt.setMaxRows(maxRows);
        //}
        //if (isFetchsize == true) {
        //  stmt.setFetchSize(fetchSize);
        //}

        //
        // Exec query
        //
        startTime = System.currentTimeMillis();
        ResultSet rs = stmt.executeQuery(sQuery);// + sLimit);
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        //
        // getMetadata
        //
        startTime = System.currentTimeMillis();
        ResultSetMetaData rsmd = rs.getMetaData();

        /* Print col names */
        int sColCnt = rsmd.getColumnCount();
        if (!isSilence) {
          String sColnames = "";
          if (!isSilence) {
            for (int i = 1; i <= sColCnt; i++) {
              sColnames += rsmd.getColumnName(i);
              sColnames += "  ";
            }
            System.out.println(sColnames);
          }
        }
        
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        //
        // Fetch rows
        //
        startTime = System.currentTimeMillis();
        /*
         * boolean last = rs.last(); int rowcount = rs.getRow();
         * System.out.println("LAST = " + last + ", ROWCNT = " + rowcount);
         */

        while (rs.next()) {
          for (int i = 1; i <= sColCnt; i++) {
            int type = rsmd.getColumnType(i);
            /*
             * if (!isSilence) {System.out.print(rs.getString("i1"));} else
             * {rs.getString("i1");} if (!isSilence) {System.out.print("  ");}
             * if (!isSilence) {System.out.print(rs.getString("i2"));} else
             * {rs.getString("j2");}
             */
            switch (type) {
              case Types.VARCHAR:
              case Types.CHAR:
                if (!isSilence) {
                  System.out.print(rs.getString(i));
                } else {
                  rs.getString(i);
                }
                break;
              case Types.BIGINT:
                if (!isSilence) {
                  System.out.print(rs.getLong(i));
                } else {
                  rs.getLong(i);
                }
                break;
              case Types.INTEGER:
              case Types.TINYINT:
                if (!isSilence) {
                  System.out.print(rs.getInt(i));
                } else {
                  rs.getInt(i);
                }
                break;
              case Types.FLOAT:
              case Types.DOUBLE:
                if (!isSilence) {
                  System.out.print(rs.getDouble(i));
                } else {
                  rs.getDouble(i);
                }
                break;
            }

            if (!isSilence) {
              System.out.print("  ");
            }
          }
          if (!isSilence) {
            System.out.println();
          }
        }

        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        //
        // Close
        //
        startTime = System.currentTimeMillis();
        stmt.close();
        con.close();
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);
        con = null;

        for (int i = 0; i < sProfile.size(); i++) {
          totalTime += sProfile.get(i);
        }
        sumTime += totalTime;
        successCnt++;

        if (maxTime < totalTime)
          maxTime = totalTime;
        if (minTime > totalTime)
          minTime = totalTime;
        
        if (totalTime < 1000) {
          sHistogram[(int)(totalTime/histogramInerval)] += 1;
        } else {
          sHistogram[20] += 1;
        }

        if (totalTime > upperLimit) {
          upperCnt++;
          DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
          System.out.println(dateFormat.format(new Date()) + ": #" + upperCnt +" TX PROFILE info.\n" +
                             "  -Query : " + sQuery);
          for (int i = 0; i < sProfile.size(); i++) {
            switch (i) {
              case 0:
              System.out.println("    -Connection time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
              case 1:
              System.out.println("    -Get Stmt time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
              case 2:
              System.out.println("    -ExecuteQuery time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
              case 3:
              System.out.println("    -GetMeta time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
              case 4:
              System.out.println("    -Fetch time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
              case 5:
              System.out.println("    -Close time elapsed : "
                  + sProfile.get(i) + "ms");
              break;
            }
          }
          System.out.println("    >>Total time elapsed :" + totalTime + "ms");
        }
      } catch (SQLException e) {
        System.out.println("SQL exception occured" + e);
        failedCnt++;
      } finally {
        try {
          if (con != null) {
            con.close();
          }
          sProfile.clear();
          totalTime = 0;
        } catch (SQLException e) {
          System.out.println("SQL exception occured" + e);
        }
      }
    }
    
    System.out.println("#### Summary ####");
    System.out.println("-# requests : " + loopCnt);
    System.out.println("-# success  : " + successCnt);
    System.out.println("-# failed   : " + failedCnt);
    System.out.println("-Avg latency(millis) : " + sumTime/successCnt);
    System.out.println("-Min latency(millis) : " + minTime);
    System.out.println("-Max latency(millis) : " + maxTime);
    //
    // print latency histogram
    //
    System.out.println("== Latency histogram ==");
    int i = 0;
    for (; i < sHistogram.length/2; i++) {
      System.out.format("| %3d ~ %3d ", i*histogramInerval, histogramInerval*(i+1)-1);
    }
    System.out.printf("|\n");
    for (i = 0; i < sHistogram.length/2; i++) {
      System.out.format("| %9d ", sHistogram[i]);
    }
    System.out.printf("|\n");
    int j = i;
    for (; i < sHistogram.length; i++) {
      if (i == (sHistogram.length - 1)) {
        System.out.format("| %3d ~    |\n", i*histogramInerval);
      } else {
        System.out.format("| %3d ~ %3d ", i*histogramInerval, histogramInerval*(i+1)-1);
      }
    }
    for (; j < sHistogram.length; j++) {
      System.out.format("| %9d ", sHistogram[j]);
    }
    System.out.print("|\n");
  }

  private static void doExit() {
    System.out
        .println("Usage: -type [impala|hive|phoenix]\n" +
                 "       -limit <query_str>\n" +
                 "       -loopcnt <#cnt>\n" + 
                 "       -upper <num(millisec)>\n" +
                 "       -fetchsize <fetch_num>\n" +
                 "       -maxrows <maxrow_num>\n" +
                 "       -host <hostname>\n");
    System.exit(1);
  }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.FileReader;

public class QueryRunner {
  static String gQuery = "";

  static enum ConnType {
    EDA_IMPALA, DAAS_IMPALA, EDA_HIVE, DAAS_PHOENIX,
  }
  
  static Random randomGenerator = new Random();
  
  static String loadFilePath = "/home/1000632/jdbc_test/mbrid.res";

  private static void loadDataFromFile(String aPath) {
    int i = 0;
    try {
      Scanner in = new Scanner(new FileReader(aPath));
      while (in.hasNext()) {
        //gData[i++] = in.next();
      }
    } catch (Exception e) {
      System.out.println("file read error" + e.getMessage());
    }
  }
  
  public static void main(String[] args) throws InterruptedException {
    String sLimit = "";
    String sUrl = "";
    String sHost = "localhost";
    String sUser ="";
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
    ConnType sConnType = ConnType.EDA_IMPALA;
    List<Long> sProfile = new ArrayList<Long>();
    long[] sHistogram = new long[21];
    Connection con = null;

    if (args.length < 2) {
      doExit();
    }

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-query")) {
        gQuery = args[++i];
      } else if (args[i].equals("-type")) {
        String type = args[++i];
        if (type.equals("eda-impala")) {
          sConnType = ConnType.EDA_IMPALA;
        } else if (type.equals("daas-impala")) {
          sConnType = ConnType.DAAS_IMPALA;
        } else if (type.equals("eda-hive")) {
          sConnType = ConnType.EDA_HIVE;
        } else if (type.equals("daas-phoenix")) {
          sConnType = ConnType.DAAS_PHOENIX;
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
      }  else if (args[i].equals("-user")) {
        sUser = args[++i];
      } else if (args[i].equals("-port")) {
        sPort = args[++i];
      } else if (args[i].equals("-help")) {
        doExit();
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
      case EDA_IMPALA:
        sUrl = "jdbc:eda-impala://" + sHost + ":" + sPort;
        break;
      case DAAS_IMPALA:
        sUrl = "jdbc:daas-impala://" + sHost + ":" + sPort;
        break;
      case EDA_HIVE:
        sUrl = "jdbc:eda-hive://" + sHost + ":" + sPort;
        break;
      case DAAS_PHOENIX:
        // sUrl = "jdbc:bdb://127.0.0.1:8655";
        sUrl = "jdbc:daas-phoenix://" + sHost + ":" + sPort;
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
        } else {
          sQuery = gQuery;
        }
        
        //
        // Connect to Querycache server
        //
        System.out.println("Connecting...");
        startTime = System.currentTimeMillis();
        con = DriverManager.getConnection(sUrl, sUser.isEmpty()?null:sUser, null);
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
        System.out.println("Executing...");
        startTime = System.currentTimeMillis();
        boolean isSelect = stmt.execute(sQuery + sLimit);
        //ResultSet rs = stmt.execute(sQuery + sLimit);
        endTime = System.currentTimeMillis();
        sProfile.add(endTime - startTime);

        // case of insert/ddl query
        if (isSelect == false) {
          // dummy insert
          sProfile.add(0L);
          sProfile.add(0L);
        }
        
        if (isSelect) {
          System.out.println("Fetching results...");
          System.out.println("---------Print the results---------");
          //
          // getMetadata
          //
          startTime = System.currentTimeMillis();
          ResultSet rs = stmt.getResultSet();
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
          System.out.println("-----------------------------------");
        }
        
        //
        // Close
        //
        System.out.println("Closing...");
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
        System.out.println("Finalizing...");
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
    if (successCnt > 0) {
      System.out.println("-Avg latency(millis) : " + sumTime/successCnt);
      System.out.println("-Min latency(millis) : " + minTime);
      System.out.println("-Max latency(millis) : " + maxTime);
    }
    
    //
    // print latency histogram
    //
    System.out.println("#### Latency histogram ####");
    for (int i = 0; i < sHistogram.length; i++) {
      if (i == (sHistogram.length - 1)) {
        System.out.format("| %3d ~    |\n", i*histogramInerval);
      } else {
        System.out.format("| %3d ~ %3d ", i*histogramInerval, histogramInerval*(i+1)-1);
      }
    }
    for (int i = 0; i < sHistogram.length; i++) {
      System.out.format("| %9d ", sHistogram[i]);
    }
    System.out.print("|\n");
  }

  private static void doExit() {
    System.out
        .println("Usage: -type [daas-impala|eda-impala|eda-hive|daas-phoenix]\n" +
                 "       -query <query_str>\n" +
                 "       -limit <limit_cnt>\n" +
                 "       -loopcnt <#cnt>\n" + 
                 "       -upper <num(millisec)>\n" +
                 "       -fetchsize <fetch_num>\n" +
                 "       -maxrows <maxrow_num>\n" +
                 "       -host <hostname>\n" +
                 "       -histogram_interval <time_milli>\n" +
                 "       -user <user_id>\n" +
                 "       -port <port_num>\n" +
                 "       -silence");
    System.exit(1);
  }
}


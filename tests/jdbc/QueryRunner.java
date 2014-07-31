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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class QueryRunner {
  private static String gQuery = "";
  private static String sUrl = "";
  private static String sUser ="";
  private static String sResFilePrefix = "test.res";
  private static boolean isFetchsize = false;
  private static boolean isMaxrows = false;
  private static boolean isSilence = false;
  private static int fetchSize = 1024;
  private static int maxRows = 1024;
  private static int loopCnt = 1;
  private static int numThreads = 1;
  private static int warmupCnt = 0;
  private static long upperLimit = 0;
  private static long histogramInerval = 50;
  private static List<String> pkCols = new ArrayList<String>(1000000);
  private static ExecType type = ExecType.SINGLE_QUERY;
  private static final int HIST_SIZE = 21;
  // fileds for summary info
  private static final String summaryFileName = "summary.res";
  private static AtomicLong totSuccessCnt = new AtomicLong(0L);
  private static AtomicLong totFailureCnt = new AtomicLong(0L);
  private static AtomicLong totWarmupCnt = new AtomicLong(0L);
  private static AtomicLong totSumTime = new AtomicLong(0L);
  private static long totMaxTime = 0;
  private static long totMinTime = 0;
  private static long[] totHistogram = new long[HIST_SIZE];

  private static enum ExecType {
    SINGLE_QUERY, GEN_QUERY_BY_PK_FILE, GEN_QUERY_BY_PK_QUERY,
  }

  private static class PerfTestWorker implements Runnable {
    private int threadId;
    
    PerfTestWorker(int threadId) {
      this.threadId = threadId;
    }
    
    @Override
    public void run() {
      Random randomGenerator = new Random();
      int  upperCnt = 0;
      long startTime = 0;
      long endTime = 0;
      long totalTime = 0;
      long sumTime = 0;
      long successCnt = 0;
      long failedCnt = 0;
      long minTime = Long.MAX_VALUE;
      long maxTime = 0;
      List<Long> sProfile = new ArrayList<Long>();
      long[] sHistogram = new long[HIST_SIZE];
      String sQuery = "";
      Connection con = null;
      BufferedWriter bwriter = null;
      try {
        File file = new File(sResFilePrefix + "-" + threadId);
        if (!file.exists()) {
          file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsolutePath());
        bwriter = new BufferedWriter(fw);
      } catch (IOException e) {
        System.err.println("ERROR : Can't crete a file :" + e);
        System.exit(1);
      }
      
      for (int idx = 1; idx <= loopCnt; idx++) {
        try {
          if (gQuery.isEmpty()) {
            System.err.println("ERROR : no query info");
            doExit();
          } else {
            sQuery = gQuery;
            if (type == ExecType.GEN_QUERY_BY_PK_FILE || type == ExecType.GEN_QUERY_BY_PK_QUERY) {
              String pkval = pkCols.get(randomGenerator.nextInt(pkCols.size()));
              sQuery = sQuery.replace("?", pkval);
            }
          }
          
          //
          // Connect to DB server
          //
          bwriter.write("Connecting...\n");
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
          bwriter.write("Executing...\n");
          startTime = System.currentTimeMillis();
          boolean isSelect = stmt.execute(sQuery);
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
            bwriter.write("Fetching results...\n");
            bwriter.write("---------Print the results---------\n");
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
              for (int i = 1; i <= sColCnt; i++) {
                sColnames += rsmd.getColumnName(i);
                sColnames += "  ";
              }
              bwriter.write(sColnames + '\n');
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
                switch (type) {
                  case Types.VARCHAR:
                  case Types.CHAR:
                    if (!isSilence) {
                      bwriter.write(rs.getString(i));
                    } else {
                      rs.getString(i);
                    }
                    break;
                  case Types.BIGINT:
                    if (!isSilence) {
                      bwriter.write(String.valueOf(rs.getLong(i)));
                    } else {
                      rs.getLong(i);
                    }
                    break;
                  case Types.INTEGER:
                  case Types.TINYINT:
                    if (!isSilence) {
                      bwriter.write(String.valueOf(rs.getInt(i)));
                    } else {
                      rs.getInt(i);
                    }
                    break;
                  case Types.FLOAT:
                  case Types.DOUBLE:
                    if (!isSilence) {
                      bwriter.write(String.valueOf(rs.getDouble(i)));
                    } else {
                      rs.getDouble(i);
                    }
                    break;
                }

                if (!isSilence) {
                  bwriter.write("  ");
                }
              }
              if (!isSilence) {
                bwriter.write('\n');
              }
            }
            endTime = System.currentTimeMillis();
            sProfile.add(endTime - startTime);
            bwriter.write("-----------------------------------\n");
          }
          
          //
          // Close
          //
          bwriter.write("Closing...\n");
          startTime = System.currentTimeMillis();
          stmt.close();
          con.close();
          endTime = System.currentTimeMillis();
          sProfile.add(endTime - startTime);
          con = null;
          
          if (warmupCnt >= idx) {
            bwriter.write("# A WARMING UP TX\n");
            sProfile.clear();
            continue;
          }
          
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
            bwriter.write(dateFormat.format(new Date()) + ": #" + upperCnt +" TX PROFILE info.\n" +
                               "  -Query : " + sQuery + '\n');
            for (int i = 0; i < sProfile.size(); i++) {
              switch (i) {
                case 0:
                  bwriter.write("    -Connection time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
                case 1:
                  bwriter.write("    -Get Stmt time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
                case 2:
                  bwriter.write("    -ExecuteQuery time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
                case 3:
                  bwriter.write("    -GetMeta time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
                case 4:
                  bwriter.write("    -Fetch time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
                case 5:
                  bwriter.write("    -Close time elapsed : "
                    + sProfile.get(i) + "ms\n");
                break;
              }
            }
            bwriter.write("    >>Total time elapsed :" + totalTime + "ms\n");
          }
        } catch (SQLException e) {
          System.err.println("ERROR : SQL exception occured" + e);
          failedCnt++;
        } catch (IOException e) {
          System.err.println("ERROR : Can't write data to the file :" + e);
          System.exit(1);
        }
        finally {
          try {
            bwriter.write("Finalizing...\n");
            if (con != null) {
              con.close();
            }
            sProfile.clear();
            totalTime = 0;
          } catch (SQLException e) {
            System.err.println("ERROR : SQL exception occured" + e);
          } catch (IOException e) {
            System.err.println("ERROR : Can't write data to the file :" + e);
            System.exit(1);
          }
        }
      }
      
      try {
        bwriter.write("#### Summary ####\n");
        bwriter.write("-# requests : " + loopCnt + '\n');
        bwriter.write("-# success  : " + successCnt + '\n');
        bwriter.write("-# failed   : " + failedCnt + '\n');
        bwriter.write("-# warmup   : " + warmupCnt + '\n');
        if (successCnt > 0) {
          bwriter
              .write("-Avg latency(millis) : " + sumTime / successCnt + '\n');
          bwriter.write("-Min latency(millis) : " + minTime + '\n');
          bwriter.write("-Max latency(millis) : " + maxTime + '\n');
          bwriter.write("-TPS                 : " + ((double)loopCnt/((double)sumTime/1000.)) + '\n');
        }
        
        totSuccessCnt.addAndGet(successCnt);
        totFailureCnt.addAndGet(failedCnt);
        totWarmupCnt.addAndGet(warmupCnt);
        totSumTime.addAndGet(sumTime);
        setTotMinTime(minTime);
        setTotMaxTime(maxTime);

        //
        // print latency histogram
        //
        bwriter.write("#### Latency histogram ####\n");
        for (int i = 0; i < sHistogram.length; i++) {
          if (i == (sHistogram.length - 1)) {
            bwriter.write(String.format("| %3d ~    |\n",
                i * histogramInerval));
          } else {
            bwriter.write(String.format("| %3d ~ %3d ",
                i * histogramInerval, histogramInerval * (i + 1) - 1));
          }
        }
        for (int i = 0; i < sHistogram.length; i++) {
          bwriter.write(String.format("| %9d ", sHistogram[i]));
        }
        addTotHistogram(sHistogram);
        bwriter.write("|\n");
        bwriter.close();
      } catch (IOException e) {
        System.err.println("ERROR : Can't crete a file :" + e);
        System.exit(1);
      }
    }
    
    private static synchronized void setTotMinTime(long val) {
      if (totMinTime > val)
        totMinTime = val;
    }
    
    private static synchronized void setTotMaxTime(long val) {
      if (totMaxTime < val)
        totMaxTime = val;
    }
    
    private static synchronized void addTotHistogram(long vals[]) {
      for(int i = 0; i < vals.length; i++) {
        totHistogram[i] += vals[i];
      }
    }
  }
  
  public static void main(String[] args) throws InterruptedException {
    String urlPrefix = "";
    String urlPostfix = "";
    String sQuery = "";
    String pkQuery = "";
    String pkFilePath = "";
    String sLimit = "";
    String sHost = "localhost";
    String sPort = "";
    String className = "com.skplanet.querycache.jdbc.QCDriver";
    
    if (args.length < 2) {
      doExit();
    }

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-query")) {
        sQuery = args[++i];
      } else if (args[i].equals("-urlprefix")) {
        urlPrefix = args[++i];
      } else if (args[i].equals("-urlpostfix")) {
        urlPostfix = args[++i];
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
      } else if (args[i].equals("-multiclients")) {
        numThreads = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-resultfilename")) {
        sResFilePrefix = args[++i];
      } else if (args[i].equals("-warmupcnt")) {
        warmupCnt = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-pkquery")) {
        pkQuery = args[++i];
        type = ExecType.GEN_QUERY_BY_PK_QUERY;
      } else if (args[i].equals("-pkfile")) {
        pkFilePath = args[++i];
        type = ExecType.GEN_QUERY_BY_PK_FILE;
      } else if (args[i].equals("-jdbcclassname")) {
        className = args[++i];
      } else if (args[i].equals("-help")) {
        doExit();
      } else {
        doExit();
      }
    }
    
    if (sQuery.isEmpty() || sHost.isEmpty() || sPort.isEmpty() ||
        numThreads < 1 || urlPrefix.isEmpty()) {
      doExit();
    }
    
    //
    // set query
    //
    gQuery = sQuery + sLimit;

    //
    // load a specific jdbc driver
    //
    try {
      Class.forName(className);
    } catch (ClassNotFoundException e) {
      System.out.println("Class not found " + e);
    }

    //
    // make the url string
    //
    sUrl = urlPrefix + sHost + ":" + sPort + "/" + urlPostfix;
    
    //
    // pre-execute phase : run the pk query to get pk column values
    //
    switch (type) {
      case GEN_QUERY_BY_PK_QUERY:
        loadPKColumnByQuery(pkQuery);
        break;
      case GEN_QUERY_BY_PK_FILE:
        loadPKColumnByFile(pkFilePath);
        break;
      default:
        break;
    }

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      Runnable worker = new PerfTestWorker(i+1);
      executor.execute(worker);
    }
    // This will make the executor accept no new threads
    // and finish all existing threads in the queue
    executor.shutdown();
    
    if (!executor.awaitTermination(300, TimeUnit.SECONDS))
      System.err.println("ERROR : ThreadPool did not terminate");
    
    //
    // Summary test results
    //
    try {
      File file = new File(summaryFileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsolutePath());
      BufferedWriter summaryWriter = new BufferedWriter(fw);
      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      summaryWriter.write("#### Summary (" + dateFormat.format(new Date()) + ") ####\n");
      summaryWriter.write("1. requests : " + (totSuccessCnt.get() + totFailureCnt.get() +
          totWarmupCnt.get()) + '\n');
      summaryWriter.write("2. success  : " + totSuccessCnt.get() + '\n');
      summaryWriter.write("3. failed   : " + totFailureCnt.get() + '\n');
      summaryWriter.write("4. warmup   : " + totWarmupCnt.get() + '\n');
      if (totSuccessCnt.get() > 0) {
        summaryWriter
            .write("5. Avg latency(millis) : " + totSumTime.get() / totSuccessCnt.get() + '\n');
        summaryWriter.write("6. Min latency(millis) : " + totMinTime + '\n');
        summaryWriter.write("7. Max latency(millis) : " + totMaxTime + '\n');
        summaryWriter.write("8. TPS                 : " + ((double)(totSuccessCnt.get() + totFailureCnt.get() +
            totWarmupCnt.get())/((double)totSumTime.get()/1000.)) + '\n');
      }

      //
      // print latency histogram
      //
      summaryWriter.write("9. Latency histogram\n");
      for (int i = 0; i < totHistogram.length; i++) {
        if (i == (totHistogram.length - 1)) {
          summaryWriter.write(String.format("| %3d ~    |\n",
              i * histogramInerval));
        } else {
          summaryWriter.write(String.format("| %3d ~ %3d ",
              i * histogramInerval, histogramInerval * (i + 1) - 1));
        }
      }
      for (int i = 0; i < totHistogram.length; i++) {
        summaryWriter.write(String.format("| %9d ", totHistogram[i]));
      }
      summaryWriter.write("|\n\n");
      summaryWriter.close();
    } catch (IOException e) {
      System.err.println("ERROR : Can't write a summary file :" + e);
      System.exit(1);
    }
    
    System.out.println("Finished all threads");
  }

  private static void loadPKColumnByQuery(String prepareQuery) {
    Connection connection = null;
    Statement  statement = null;

    try {
      connection = DriverManager.getConnection(sUrl);
      statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(prepareQuery);
      while (resultSet.next()) {
        pkCols.add(resultSet.getString(1));
      }   
      System.out.println("-the num of PK columns = " + pkCols.size());
    } catch (SQLException e) {
      System.err.println("ERROR : Pk query execution failed.");
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        if (statement != null) {
          statement.close();
        }   
        if (connection != null) {
          connection.close();
        }   
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private static void loadPKColumnByFile(String aPath) {
    try {
      Scanner in = new Scanner(new FileReader(aPath));
      while (in.hasNext()) {
        pkCols.add(in.next());
      }
      in.close();
      System.out.println("-the num of PK columns = " + pkCols.size());
    } catch (Exception e) {
      System.err.println("ERROR : file read error" + e.getMessage());
      System.exit(1);
    }
  }
  
  private static void doExit() {
    System.out
        .println("Usage: -urlprefix <name(e.g. hive, mysql, phoenix)>\n" +
                 "       -query <query_str>\n" +
                 "       -jdbcclassname <fullclassname>\n" +
                 "       -limit <limit_cnt>\n" +
                 "       -loopcnt <#cnt>\n" + 
                 "       -pkquery <pkquery_str>\n" +
                 "       -pkfile <pkfile_path>\n" +
                 "       -upper <num(millisec)>\n" +
                 "       -fetchsize <fetch_num>\n" +
                 "       -maxrows <maxrow_num>\n" +
                 "       -host <hostname>\n" +
                 "       -histogram_interval <time_milli>\n" +
                 "       -user <user_id>\n" +
                 "       -port <port_num>\n" +
                 "       -multiclients <thread_num>\n" +
                 "       -resultfilename <filename>\n" +
                 "       -warmupcnt <#cnt>\n" +
                 "       -silence\n");
    System.exit(1);
  }
}
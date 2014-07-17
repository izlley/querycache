/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.*;

import org.apache.hadoop.hive.ql.QTestUtil;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.history.HiveHistoryViewer;
import org.apache.hadoop.hive.ql.history.HiveHistory.QueryInfo;
import org.apache.hadoop.hive.ql.history.HiveHistory.Keys;
import org.apache.hadoop.hive.ql.history.HiveHistory.TaskInfo;
import org.apache.hadoop.hive.ql.session.SessionState;



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class TestContribCliDriver extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      String hadoopVer;
      if ("".equals("miniMR"))
        miniMR = true;
      hadoopVer = "2.0.0-cdh4.2.0";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/results/clientpositive", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/contrib/test/logs/contribclientpositive", miniMR, hadoopVer);

      // do a one time initialization
      qt.cleanUp();
      qt.createSources();

    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in static initialization");
    }
  }

  public TestContribCliDriver(String name) {
    super(name);
  }

  @Override
  protected void setUp() {
    try {
      qt.clearTestSideEffects();
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in setup");
    }
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testCliDriver_shutdown() {
    System.out.println ("Cleaning up " + "TestContribCliDriver");
  }

  @Override
  protected void tearDown() {
    try {
      qt.clearPostTestEffects();
      if (getName().equals("testCliDriver_shutdown"))
        qt.shutdown();
    }
    catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in tearDown");
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestContribCliDriver("testCliDriver_dboutput"));
    suite.addTest(new TestContribCliDriver("testCliDriver_fileformat_base64"));
    suite.addTest(new TestContribCliDriver("testCliDriver_java_mr_example"));
    suite.addTest(new TestContribCliDriver("testCliDriver_lateral_view_explode2"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_regex"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_s3"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes2"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes3"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes4"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes5"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes6"));
    suite.addTest(new TestContribCliDriver("testCliDriver_serde_typedbytes_null"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_avg"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_group_concat"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_max"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_max_n"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_min"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udaf_example_min_n"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udf_example_add"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udf_example_arraymapstruct"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udf_example_format"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udf_row_sequence"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udtf_explode2"));
    suite.addTest(new TestContribCliDriver("testCliDriver_udtf_output_on_close"));
    suite.addTest(new TestContribCliDriver("testCliDriver_shutdown"));
    return suite;
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testCliDriver_dboutput() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dboutput.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/dboutput.q");

      if (qt.shouldBeSkipped("dboutput.q")) {
        return;
      }

      qt.cliInit("dboutput.q", false);
      int ecode = qt.executeClient("dboutput.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("dboutput.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dboutput.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dboutput.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_fileformat_base64() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fileformat_base64.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/fileformat_base64.q");

      if (qt.shouldBeSkipped("fileformat_base64.q")) {
        return;
      }

      qt.cliInit("fileformat_base64.q", false);
      int ecode = qt.executeClient("fileformat_base64.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("fileformat_base64.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fileformat_base64.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fileformat_base64.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_java_mr_example() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "java_mr_example.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/java_mr_example.q");

      if (qt.shouldBeSkipped("java_mr_example.q")) {
        return;
      }

      qt.cliInit("java_mr_example.q", false);
      int ecode = qt.executeClient("java_mr_example.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("java_mr_example.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "java_mr_example.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "java_mr_example.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_lateral_view_explode2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lateral_view_explode2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/lateral_view_explode2.q");

      if (qt.shouldBeSkipped("lateral_view_explode2.q")) {
        return;
      }

      qt.cliInit("lateral_view_explode2.q", false);
      int ecode = qt.executeClient("lateral_view_explode2.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("lateral_view_explode2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lateral_view_explode2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lateral_view_explode2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_regex() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_regex.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_regex.q");

      if (qt.shouldBeSkipped("serde_regex.q")) {
        return;
      }

      qt.cliInit("serde_regex.q", false);
      int ecode = qt.executeClient("serde_regex.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_regex.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_regex.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_regex.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_s3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_s3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_s3.q");

      if (qt.shouldBeSkipped("serde_s3.q")) {
        return;
      }

      qt.cliInit("serde_s3.q", false);
      int ecode = qt.executeClient("serde_s3.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_s3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_s3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_s3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes.q");

      if (qt.shouldBeSkipped("serde_typedbytes.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes.q", false);
      int ecode = qt.executeClient("serde_typedbytes.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes2.q");

      if (qt.shouldBeSkipped("serde_typedbytes2.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes2.q", false);
      int ecode = qt.executeClient("serde_typedbytes2.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes3.q");

      if (qt.shouldBeSkipped("serde_typedbytes3.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes3.q", false);
      int ecode = qt.executeClient("serde_typedbytes3.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes4.q");

      if (qt.shouldBeSkipped("serde_typedbytes4.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes4.q", false);
      int ecode = qt.executeClient("serde_typedbytes4.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes5.q");

      if (qt.shouldBeSkipped("serde_typedbytes5.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes5.q", false);
      int ecode = qt.executeClient("serde_typedbytes5.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes6.q");

      if (qt.shouldBeSkipped("serde_typedbytes6.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes6.q", false);
      int ecode = qt.executeClient("serde_typedbytes6.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_serde_typedbytes_null() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_typedbytes_null.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/serde_typedbytes_null.q");

      if (qt.shouldBeSkipped("serde_typedbytes_null.q")) {
        return;
      }

      qt.cliInit("serde_typedbytes_null.q", false);
      int ecode = qt.executeClient("serde_typedbytes_null.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("serde_typedbytes_null.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_typedbytes_null.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_typedbytes_null.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_avg() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_avg.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_avg.q");

      if (qt.shouldBeSkipped("udaf_example_avg.q")) {
        return;
      }

      qt.cliInit("udaf_example_avg.q", false);
      int ecode = qt.executeClient("udaf_example_avg.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_avg.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_avg.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_avg.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_group_concat() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_group_concat.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_group_concat.q");

      if (qt.shouldBeSkipped("udaf_example_group_concat.q")) {
        return;
      }

      qt.cliInit("udaf_example_group_concat.q", false);
      int ecode = qt.executeClient("udaf_example_group_concat.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_group_concat.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_group_concat.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_group_concat.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_max() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_max.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_max.q");

      if (qt.shouldBeSkipped("udaf_example_max.q")) {
        return;
      }

      qt.cliInit("udaf_example_max.q", false);
      int ecode = qt.executeClient("udaf_example_max.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_max.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_max.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_max.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_max_n() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_max_n.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_max_n.q");

      if (qt.shouldBeSkipped("udaf_example_max_n.q")) {
        return;
      }

      qt.cliInit("udaf_example_max_n.q", false);
      int ecode = qt.executeClient("udaf_example_max_n.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_max_n.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_max_n.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_max_n.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_min() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_min.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_min.q");

      if (qt.shouldBeSkipped("udaf_example_min.q")) {
        return;
      }

      qt.cliInit("udaf_example_min.q", false);
      int ecode = qt.executeClient("udaf_example_min.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_min.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_min.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_min.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udaf_example_min_n() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_example_min_n.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udaf_example_min_n.q");

      if (qt.shouldBeSkipped("udaf_example_min_n.q")) {
        return;
      }

      qt.cliInit("udaf_example_min_n.q", false);
      int ecode = qt.executeClient("udaf_example_min_n.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udaf_example_min_n.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_example_min_n.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_example_min_n.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udf_example_add() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_example_add.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udf_example_add.q");

      if (qt.shouldBeSkipped("udf_example_add.q")) {
        return;
      }

      qt.cliInit("udf_example_add.q", false);
      int ecode = qt.executeClient("udf_example_add.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udf_example_add.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_example_add.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_example_add.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udf_example_arraymapstruct() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_example_arraymapstruct.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udf_example_arraymapstruct.q");

      if (qt.shouldBeSkipped("udf_example_arraymapstruct.q")) {
        return;
      }

      qt.cliInit("udf_example_arraymapstruct.q", false);
      int ecode = qt.executeClient("udf_example_arraymapstruct.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udf_example_arraymapstruct.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_example_arraymapstruct.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_example_arraymapstruct.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udf_example_format() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_example_format.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udf_example_format.q");

      if (qt.shouldBeSkipped("udf_example_format.q")) {
        return;
      }

      qt.cliInit("udf_example_format.q", false);
      int ecode = qt.executeClient("udf_example_format.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udf_example_format.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_example_format.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_example_format.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udf_row_sequence() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_row_sequence.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udf_row_sequence.q");

      if (qt.shouldBeSkipped("udf_row_sequence.q")) {
        return;
      }

      qt.cliInit("udf_row_sequence.q", false);
      int ecode = qt.executeClient("udf_row_sequence.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udf_row_sequence.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_row_sequence.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_row_sequence.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udtf_explode2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udtf_explode2.q");

      if (qt.shouldBeSkipped("udtf_explode2.q")) {
        return;
      }

      qt.cliInit("udtf_explode2.q", false);
      int ecode = qt.executeClient("udtf_explode2.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udtf_explode2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_explode2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_explode2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_udtf_output_on_close() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_output_on_close.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientpositive/udtf_output_on_close.q");

      if (qt.shouldBeSkipped("udtf_output_on_close.q")) {
        return;
      }

      qt.cliInit("udtf_output_on_close.q", false);
      int ecode = qt.executeClient("udtf_output_on_close.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode + debugHint);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if(jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history" + debugHint);
          }
        }
      }

      ecode = qt.checkCliDriverResults("udtf_output_on_close.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_output_on_close.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_output_on_close.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


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

public class TestMinimrCliDriver extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      String hadoopVer;
      if ("miniMR".equals("miniMR"))
        miniMR = true;
      hadoopVer = "2.0.0-cdh4.2.0";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/results/clientpositive", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/ql/test/logs/clientpositive", miniMR, hadoopVer);

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

  public TestMinimrCliDriver(String name) {
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
    System.out.println ("Cleaning up " + "TestMinimrCliDriver");
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
    suite.addTest(new TestMinimrCliDriver("testCliDriver_bucket4"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_bucket_num_reducers"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_bucketizedhiveinputformat"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_bucketmapjoin6"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_bucketmapjoin7"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_disable_merge_for_bucketing"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_groupby2"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_input16_cc"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_join1"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_optrstat_groupby"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_reduce_deduplicate"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_scriptfile1"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_smb_mapjoin_8"));
    suite.addTest(new TestMinimrCliDriver("testCliDriver_shutdown"));
    return suite;
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testCliDriver_bucket4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucket4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/bucket4.q");

      if (qt.shouldBeSkipped("bucket4.q")) {
        return;
      }

      qt.cliInit("bucket4.q", false);
      int ecode = qt.executeClient("bucket4.q");
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

      ecode = qt.checkCliDriverResults("bucket4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucket4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucket4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_bucket_num_reducers() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucket_num_reducers.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/bucket_num_reducers.q");

      if (qt.shouldBeSkipped("bucket_num_reducers.q")) {
        return;
      }

      qt.cliInit("bucket_num_reducers.q", false);
      int ecode = qt.executeClient("bucket_num_reducers.q");
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

      ecode = qt.checkCliDriverResults("bucket_num_reducers.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucket_num_reducers.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucket_num_reducers.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_bucketizedhiveinputformat() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucketizedhiveinputformat.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/bucketizedhiveinputformat.q");

      if (qt.shouldBeSkipped("bucketizedhiveinputformat.q")) {
        return;
      }

      qt.cliInit("bucketizedhiveinputformat.q", false);
      int ecode = qt.executeClient("bucketizedhiveinputformat.q");
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

      ecode = qt.checkCliDriverResults("bucketizedhiveinputformat.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucketizedhiveinputformat.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucketizedhiveinputformat.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_bucketmapjoin6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucketmapjoin6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/bucketmapjoin6.q");

      if (qt.shouldBeSkipped("bucketmapjoin6.q")) {
        return;
      }

      qt.cliInit("bucketmapjoin6.q", false);
      int ecode = qt.executeClient("bucketmapjoin6.q");
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

      ecode = qt.checkCliDriverResults("bucketmapjoin6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucketmapjoin6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucketmapjoin6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_bucketmapjoin7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucketmapjoin7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/bucketmapjoin7.q");

      if (qt.shouldBeSkipped("bucketmapjoin7.q")) {
        return;
      }

      qt.cliInit("bucketmapjoin7.q", false);
      int ecode = qt.executeClient("bucketmapjoin7.q");
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

      ecode = qt.checkCliDriverResults("bucketmapjoin7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucketmapjoin7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucketmapjoin7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_disable_merge_for_bucketing() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "disable_merge_for_bucketing.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/disable_merge_for_bucketing.q");

      if (qt.shouldBeSkipped("disable_merge_for_bucketing.q")) {
        return;
      }

      qt.cliInit("disable_merge_for_bucketing.q", false);
      int ecode = qt.executeClient("disable_merge_for_bucketing.q");
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

      ecode = qt.checkCliDriverResults("disable_merge_for_bucketing.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "disable_merge_for_bucketing.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "disable_merge_for_bucketing.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_groupby2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/groupby2.q");

      if (qt.shouldBeSkipped("groupby2.q")) {
        return;
      }

      qt.cliInit("groupby2.q", false);
      int ecode = qt.executeClient("groupby2.q");
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

      ecode = qt.checkCliDriverResults("groupby2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_input16_cc() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input16_cc.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/input16_cc.q");

      if (qt.shouldBeSkipped("input16_cc.q")) {
        return;
      }

      qt.cliInit("input16_cc.q", false);
      int ecode = qt.executeClient("input16_cc.q");
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

      ecode = qt.checkCliDriverResults("input16_cc.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input16_cc.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input16_cc.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_join1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/join1.q");

      if (qt.shouldBeSkipped("join1.q")) {
        return;
      }

      qt.cliInit("join1.q", false);
      int ecode = qt.executeClient("join1.q");
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

      ecode = qt.checkCliDriverResults("join1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_optrstat_groupby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "optrstat_groupby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/optrstat_groupby.q");

      if (qt.shouldBeSkipped("optrstat_groupby.q")) {
        return;
      }

      qt.cliInit("optrstat_groupby.q", false);
      int ecode = qt.executeClient("optrstat_groupby.q");
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

      ecode = qt.checkCliDriverResults("optrstat_groupby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "optrstat_groupby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "optrstat_groupby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_reduce_deduplicate() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "reduce_deduplicate.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/reduce_deduplicate.q");

      if (qt.shouldBeSkipped("reduce_deduplicate.q")) {
        return;
      }

      qt.cliInit("reduce_deduplicate.q", false);
      int ecode = qt.executeClient("reduce_deduplicate.q");
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

      ecode = qt.checkCliDriverResults("reduce_deduplicate.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "reduce_deduplicate.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "reduce_deduplicate.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_scriptfile1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "scriptfile1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/scriptfile1.q");

      if (qt.shouldBeSkipped("scriptfile1.q")) {
        return;
      }

      qt.cliInit("scriptfile1.q", false);
      int ecode = qt.executeClient("scriptfile1.q");
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

      ecode = qt.checkCliDriverResults("scriptfile1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "scriptfile1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "scriptfile1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_smb_mapjoin_8() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "smb_mapjoin_8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive/smb_mapjoin_8.q");

      if (qt.shouldBeSkipped("smb_mapjoin_8.q")) {
        return;
      }

      qt.cliInit("smb_mapjoin_8.q", false);
      int ecode = qt.executeClient("smb_mapjoin_8.q");
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

      ecode = qt.checkCliDriverResults("smb_mapjoin_8.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "smb_mapjoin_8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "smb_mapjoin_8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


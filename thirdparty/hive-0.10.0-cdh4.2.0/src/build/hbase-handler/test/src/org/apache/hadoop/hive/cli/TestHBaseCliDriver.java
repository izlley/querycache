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

import org.apache.hadoop.hive.hbase.HBaseQTestUtil;
import org.apache.hadoop.hive.hbase.HBaseTestSetup;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.history.HiveHistoryViewer;
import org.apache.hadoop.hive.ql.history.HiveHistory.QueryInfo;
import org.apache.hadoop.hive.ql.history.HiveHistory.Keys;
import org.apache.hadoop.hive.ql.history.HiveHistory.TaskInfo;
import org.apache.hadoop.hive.ql.session.SessionState;



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class TestHBaseCliDriver extends TestCase {

  private HBaseQTestUtil qt;
  private HBaseTestSetup setup;

  public TestHBaseCliDriver(String name, HBaseTestSetup setup) {
    super(name);
    qt = null;
    this.setup = setup;
  }

  @Override
  protected void setUp() {
    try {
      boolean miniMR = false;
      if ("".equals("miniMR")) {
        miniMR = true;
      }
      qt = new HBaseQTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/results/positive", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/hbase-handler/test/logs/hbase-handler", miniMR, setup);

    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in setup");
    }
  }

  @Override
  protected void tearDown() {
    try {
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
    HBaseTestSetup setup = new HBaseTestSetup(suite);
    suite.addTest(new TestHBaseCliDriver("testCliDriver_external_table_ppd", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_binary_external_table_queries", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_binary_map_queries", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_binary_storage_queries", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_joins", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_ppd_key_range", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_pushdown", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_queries", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_stats", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_stats2", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_hbase_stats_empty_partition", setup));
    suite.addTest(new TestHBaseCliDriver("testCliDriver_ppd_key_ranges", setup));
    return setup;
  }

  public void testCliDriver_external_table_ppd() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "external_table_ppd.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/external_table_ppd.q");

      if (qt.shouldBeSkipped("external_table_ppd.q")) {
        System.out.println("Test external_table_ppd.q skipped");
        return;
      }

      qt.cliInit("external_table_ppd.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("external_table_ppd.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("external_table_ppd.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "external_table_ppd.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "external_table_ppd.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_binary_external_table_queries() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_binary_external_table_queries.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_binary_external_table_queries.q");

      if (qt.shouldBeSkipped("hbase_binary_external_table_queries.q")) {
        System.out.println("Test hbase_binary_external_table_queries.q skipped");
        return;
      }

      qt.cliInit("hbase_binary_external_table_queries.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_binary_external_table_queries.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_binary_external_table_queries.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_binary_external_table_queries.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_binary_external_table_queries.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_binary_map_queries() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_binary_map_queries.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_binary_map_queries.q");

      if (qt.shouldBeSkipped("hbase_binary_map_queries.q")) {
        System.out.println("Test hbase_binary_map_queries.q skipped");
        return;
      }

      qt.cliInit("hbase_binary_map_queries.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_binary_map_queries.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_binary_map_queries.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_binary_map_queries.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_binary_map_queries.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_binary_storage_queries() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_binary_storage_queries.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_binary_storage_queries.q");

      if (qt.shouldBeSkipped("hbase_binary_storage_queries.q")) {
        System.out.println("Test hbase_binary_storage_queries.q skipped");
        return;
      }

      qt.cliInit("hbase_binary_storage_queries.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_binary_storage_queries.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_binary_storage_queries.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_binary_storage_queries.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_binary_storage_queries.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_joins() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_joins.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_joins.q");

      if (qt.shouldBeSkipped("hbase_joins.q")) {
        System.out.println("Test hbase_joins.q skipped");
        return;
      }

      qt.cliInit("hbase_joins.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_joins.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_joins.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_joins.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_joins.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_ppd_key_range() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_ppd_key_range.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_ppd_key_range.q");

      if (qt.shouldBeSkipped("hbase_ppd_key_range.q")) {
        System.out.println("Test hbase_ppd_key_range.q skipped");
        return;
      }

      qt.cliInit("hbase_ppd_key_range.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_ppd_key_range.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_ppd_key_range.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_ppd_key_range.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_ppd_key_range.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_pushdown() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_pushdown.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_pushdown.q");

      if (qt.shouldBeSkipped("hbase_pushdown.q")) {
        System.out.println("Test hbase_pushdown.q skipped");
        return;
      }

      qt.cliInit("hbase_pushdown.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_pushdown.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_pushdown.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_pushdown.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_pushdown.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_queries() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_queries.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_queries.q");

      if (qt.shouldBeSkipped("hbase_queries.q")) {
        System.out.println("Test hbase_queries.q skipped");
        return;
      }

      qt.cliInit("hbase_queries.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_queries.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_queries.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_queries.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_queries.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_stats() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_stats.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_stats.q");

      if (qt.shouldBeSkipped("hbase_stats.q")) {
        System.out.println("Test hbase_stats.q skipped");
        return;
      }

      qt.cliInit("hbase_stats.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_stats.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_stats.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_stats.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_stats.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_stats2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_stats2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_stats2.q");

      if (qt.shouldBeSkipped("hbase_stats2.q")) {
        System.out.println("Test hbase_stats2.q skipped");
        return;
      }

      qt.cliInit("hbase_stats2.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_stats2.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_stats2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_stats2.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_stats2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_hbase_stats_empty_partition() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_stats_empty_partition.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_stats_empty_partition.q");

      if (qt.shouldBeSkipped("hbase_stats_empty_partition.q")) {
        System.out.println("Test hbase_stats_empty_partition.q skipped");
        return;
      }

      qt.cliInit("hbase_stats_empty_partition.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_stats_empty_partition.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("hbase_stats_empty_partition.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_stats_empty_partition.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_stats_empty_partition.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_ppd_key_ranges() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "ppd_key_ranges.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/ppd_key_ranges.q");

      if (qt.shouldBeSkipped("ppd_key_ranges.q")) {
        System.out.println("Test ppd_key_ranges.q skipped");
        return;
      }

      qt.cliInit("ppd_key_ranges.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("ppd_key_ranges.q");
      if (ecode != 0) {
        fail("Client Execution failed with error code = " + ecode);
      }
      if (SessionState.get() != null) {
        HiveHistoryViewer hv = new HiveHistoryViewer(SessionState.get()
          .getHiveHistory().getHistFileName());
        Map<String, QueryInfo> jobInfoMap = hv.getJobInfoMap();
        Map<String, TaskInfo> taskInfoMap = hv.getTaskInfoMap();

        if (jobInfoMap.size() != 0) {
          String cmd = (String)jobInfoMap.keySet().toArray()[0];
          QueryInfo ji = jobInfoMap.get(cmd);

          if (!ji.hm.get(Keys.QUERY_RET_CODE.name()).equals("0")) {
              fail("Wrong return code in hive history");
          }
        }
      }

      ecode = qt.checkCliDriverResults("ppd_key_ranges.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "ppd_key_ranges.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "ppd_key_ranges.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


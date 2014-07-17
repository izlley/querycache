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

public class TestHBaseMinimrCliDriver extends TestCase {

  private HBaseQTestUtil qt;
  private HBaseTestSetup setup;

  public TestHBaseMinimrCliDriver(String name, HBaseTestSetup setup) {
    super(name);
    qt = null;
    this.setup = setup;
  }

  @Override
  protected void setUp() {
    try {
      boolean miniMR = false;
      if ("miniMR".equals("miniMR")) {
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
    suite.addTest(new TestHBaseMinimrCliDriver("testCliDriver_hbase_bulk", setup));
    return setup;
  }

  public void testCliDriver_hbase_bulk() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "hbase_bulk.m");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/positive/hbase_bulk.m");

      if (qt.shouldBeSkipped("hbase_bulk.m")) {
        System.out.println("Test hbase_bulk.m skipped");
        return;
      }

      qt.cliInit("hbase_bulk.m");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("hbase_bulk.m");
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

      ecode = qt.checkCliDriverResults("hbase_bulk.m");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "hbase_bulk.m");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "hbase_bulk.m" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}

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

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class TestNegativeMinimrCliDriver extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      String hadoopVer;
      if ("miniMR".equals("miniMR"))
        miniMR = true;
      hadoopVer = "2.0.0-cdh4.2.0";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/results/clientnegative", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/ql/test/logs/clientnegative", miniMR, hadoopVer);
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

  public TestNegativeMinimrCliDriver(String name) {
    super(name);
  }

  @Override
  protected void setUp() {
    try {
      qt.clearTestSideEffects();
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in setup");
    }
  }

  @Override
  protected void tearDown() {
    try {
      qt.clearPostTestEffects();
      if (getName().equals("testNegativeCliDriver_shutdown"))
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
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_cluster_tasklog_retrieval"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_mapreduce_stack_trace"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_mapreduce_stack_trace_hadoop20"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_mapreduce_stack_trace_turnoff"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_mapreduce_stack_trace_turnoff_hadoop20"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_minimr_broken_pipe"));
    suite.addTest(new TestNegativeMinimrCliDriver("testNegativeCliDriver_shutdown"));
    return suite;
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testNegativeCliDriver_shutdown() {
    System.out.println ("Cleaning up " + "TestNegativeMinimrCliDriver");
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testNegativeCliDriver_cluster_tasklog_retrieval() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "cluster_tasklog_retrieval.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/cluster_tasklog_retrieval.q");

      if (qt.shouldBeSkipped("cluster_tasklog_retrieval.q")) {
        System.out.println("Test cluster_tasklog_retrieval.q skipped");
        return;
      }

      qt.cliInit("cluster_tasklog_retrieval.q", false);
      int ecode = qt.executeClient("cluster_tasklog_retrieval.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("cluster_tasklog_retrieval.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "cluster_tasklog_retrieval.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "cluster_tasklog_retrieval.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_mapreduce_stack_trace() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "mapreduce_stack_trace.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/mapreduce_stack_trace.q");

      if (qt.shouldBeSkipped("mapreduce_stack_trace.q")) {
        System.out.println("Test mapreduce_stack_trace.q skipped");
        return;
      }

      qt.cliInit("mapreduce_stack_trace.q", false);
      int ecode = qt.executeClient("mapreduce_stack_trace.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("mapreduce_stack_trace.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "mapreduce_stack_trace.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "mapreduce_stack_trace.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_mapreduce_stack_trace_hadoop20() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "mapreduce_stack_trace_hadoop20.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/mapreduce_stack_trace_hadoop20.q");

      if (qt.shouldBeSkipped("mapreduce_stack_trace_hadoop20.q")) {
        System.out.println("Test mapreduce_stack_trace_hadoop20.q skipped");
        return;
      }

      qt.cliInit("mapreduce_stack_trace_hadoop20.q", false);
      int ecode = qt.executeClient("mapreduce_stack_trace_hadoop20.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("mapreduce_stack_trace_hadoop20.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "mapreduce_stack_trace_hadoop20.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "mapreduce_stack_trace_hadoop20.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_mapreduce_stack_trace_turnoff() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "mapreduce_stack_trace_turnoff.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/mapreduce_stack_trace_turnoff.q");

      if (qt.shouldBeSkipped("mapreduce_stack_trace_turnoff.q")) {
        System.out.println("Test mapreduce_stack_trace_turnoff.q skipped");
        return;
      }

      qt.cliInit("mapreduce_stack_trace_turnoff.q", false);
      int ecode = qt.executeClient("mapreduce_stack_trace_turnoff.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("mapreduce_stack_trace_turnoff.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "mapreduce_stack_trace_turnoff.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "mapreduce_stack_trace_turnoff.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_mapreduce_stack_trace_turnoff_hadoop20() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "mapreduce_stack_trace_turnoff_hadoop20.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/mapreduce_stack_trace_turnoff_hadoop20.q");

      if (qt.shouldBeSkipped("mapreduce_stack_trace_turnoff_hadoop20.q")) {
        System.out.println("Test mapreduce_stack_trace_turnoff_hadoop20.q skipped");
        return;
      }

      qt.cliInit("mapreduce_stack_trace_turnoff_hadoop20.q", false);
      int ecode = qt.executeClient("mapreduce_stack_trace_turnoff_hadoop20.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("mapreduce_stack_trace_turnoff_hadoop20.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "mapreduce_stack_trace_turnoff_hadoop20.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "mapreduce_stack_trace_turnoff_hadoop20.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_minimr_broken_pipe() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "minimr_broken_pipe.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/minimr_broken_pipe.q");

      if (qt.shouldBeSkipped("minimr_broken_pipe.q")) {
        System.out.println("Test minimr_broken_pipe.q skipped");
        return;
      }

      qt.cliInit("minimr_broken_pipe.q", false);
      int ecode = qt.executeClient("minimr_broken_pipe.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("minimr_broken_pipe.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "minimr_broken_pipe.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "minimr_broken_pipe.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


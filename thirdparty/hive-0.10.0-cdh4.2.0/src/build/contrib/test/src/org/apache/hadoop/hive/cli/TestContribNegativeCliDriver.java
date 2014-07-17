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

public class TestContribNegativeCliDriver extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      String hadoopVer;
      if ("".equals("miniMR"))
        miniMR = true;
      hadoopVer = "";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/results/clientnegative", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/contrib/test/logs/contribclientnegative", miniMR, hadoopVer);
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

  public TestContribNegativeCliDriver(String name) {
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
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_case_with_row_sequence"));
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_invalid_row_sequence"));
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_serde_regex"));
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_udtf_explode2"));
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_url_hook"));
    suite.addTest(new TestContribNegativeCliDriver("testNegativeCliDriver_shutdown"));
    return suite;
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testNegativeCliDriver_shutdown() {
    System.out.println ("Cleaning up " + "TestContribNegativeCliDriver");
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testNegativeCliDriver_case_with_row_sequence() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "case_with_row_sequence.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientnegative/case_with_row_sequence.q");

      if (qt.shouldBeSkipped("case_with_row_sequence.q")) {
        System.out.println("Test case_with_row_sequence.q skipped");
        return;
      }

      qt.cliInit("case_with_row_sequence.q", false);
      int ecode = qt.executeClient("case_with_row_sequence.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("case_with_row_sequence.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "case_with_row_sequence.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "case_with_row_sequence.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_row_sequence() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_row_sequence.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientnegative/invalid_row_sequence.q");

      if (qt.shouldBeSkipped("invalid_row_sequence.q")) {
        System.out.println("Test invalid_row_sequence.q skipped");
        return;
      }

      qt.cliInit("invalid_row_sequence.q", false);
      int ecode = qt.executeClient("invalid_row_sequence.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_row_sequence.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_row_sequence.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_row_sequence.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_serde_regex() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_regex.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientnegative/serde_regex.q");

      if (qt.shouldBeSkipped("serde_regex.q")) {
        System.out.println("Test serde_regex.q skipped");
        return;
      }

      qt.cliInit("serde_regex.q", false);
      int ecode = qt.executeClient("serde_regex.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
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

  public void testNegativeCliDriver_udtf_explode2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientnegative/udtf_explode2.q");

      if (qt.shouldBeSkipped("udtf_explode2.q")) {
        System.out.println("Test udtf_explode2.q skipped");
        return;
      }

      qt.cliInit("udtf_explode2.q", false);
      int ecode = qt.executeClient("udtf_explode2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
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

  public void testNegativeCliDriver_url_hook() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "url_hook.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/queries/clientnegative/url_hook.q");

      if (qt.shouldBeSkipped("url_hook.q")) {
        System.out.println("Test url_hook.q skipped");
        return;
      }

      qt.cliInit("url_hook.q", false);
      int ecode = qt.executeClient("url_hook.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("url_hook.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "url_hook.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "url_hook.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


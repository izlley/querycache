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

public class TestHBaseNegativeCliDriver extends TestCase {

  private HBaseQTestUtil qt;
  private HBaseTestSetup setup;

  public TestHBaseNegativeCliDriver(String name, HBaseTestSetup setup) {
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

      qt = new HBaseQTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/results/negative", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/hbase-handler/test/logs/hbase-handler", miniMR, setup);

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
    suite.addTest(new TestHBaseNegativeCliDriver("testCliDriver_cascade_dbdrop", setup));
    suite.addTest(new TestHBaseNegativeCliDriver("testCliDriver_cascade_dbdrop_hadoop20", setup));
    return setup;
  }

  public void testCliDriver_cascade_dbdrop() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "cascade_dbdrop.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/negative/cascade_dbdrop.q");

      if (qt.shouldBeSkipped("cascade_dbdrop.q")) {
        System.out.println("Test cascade_dbdrop.q skipped");
        return;
      }

      qt.cliInit("cascade_dbdrop.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("cascade_dbdrop.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode);
      }

      ecode = qt.checkCliDriverResults("cascade_dbdrop.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "cascade_dbdrop.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "cascade_dbdrop.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testCliDriver_cascade_dbdrop_hadoop20() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "cascade_dbdrop_hadoop20.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/hbase-handler/src/test/queries/negative/cascade_dbdrop_hadoop20.q");

      if (qt.shouldBeSkipped("cascade_dbdrop_hadoop20.q")) {
        System.out.println("Test cascade_dbdrop_hadoop20.q skipped");
        return;
      }

      qt.cliInit("cascade_dbdrop_hadoop20.q");
      qt.clearTestSideEffects();
      int ecode = qt.executeClient("cascade_dbdrop_hadoop20.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode);
      }

      ecode = qt.checkCliDriverResults("cascade_dbdrop_hadoop20.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode);
      }
      qt.clearPostTestEffects();

    } catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "cascade_dbdrop_hadoop20.q");
      System.out.flush();
      fail("Unexpected exception");
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "cascade_dbdrop_hadoop20.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


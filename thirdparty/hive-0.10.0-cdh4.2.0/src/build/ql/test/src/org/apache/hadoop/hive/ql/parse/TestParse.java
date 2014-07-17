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
package org.apache.hadoop.hive.ql.parse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.*;

import org.apache.hadoop.hive.ql.QTestUtil;
import org.apache.hadoop.hive.ql.exec.Task;

public class TestParse extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      if ("".equals("miniMR"))
        miniMR = true;
      String hadoopVer = "2.0.0-cdh4.2.0";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/results/compiler", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/ql/test/logs/positive", miniMR, hadoopVer);
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in static initialization");
    }
  }


  public TestParse(String name) {
    super(name);
  }

  @Override
  protected void tearDown() {
    try {
      qt.clearPostTestEffects();
      if (getName().equals("testParse_shutdown"))
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

    suite.addTest(new TestParse("testParse_case_sensitivity"));
    suite.addTest(new TestParse("testParse_cast1"));
    suite.addTest(new TestParse("testParse_groupby1"));
    suite.addTest(new TestParse("testParse_groupby2"));
    suite.addTest(new TestParse("testParse_groupby3"));
    suite.addTest(new TestParse("testParse_groupby4"));
    suite.addTest(new TestParse("testParse_groupby5"));
    suite.addTest(new TestParse("testParse_groupby6"));
    suite.addTest(new TestParse("testParse_input1"));
    suite.addTest(new TestParse("testParse_input2"));
    suite.addTest(new TestParse("testParse_input20"));
    suite.addTest(new TestParse("testParse_input3"));
    suite.addTest(new TestParse("testParse_input4"));
    suite.addTest(new TestParse("testParse_input5"));
    suite.addTest(new TestParse("testParse_input6"));
    suite.addTest(new TestParse("testParse_input7"));
    suite.addTest(new TestParse("testParse_input8"));
    suite.addTest(new TestParse("testParse_input9"));
    suite.addTest(new TestParse("testParse_input_part1"));
    suite.addTest(new TestParse("testParse_input_testsequencefile"));
    suite.addTest(new TestParse("testParse_input_testxpath"));
    suite.addTest(new TestParse("testParse_input_testxpath2"));
    suite.addTest(new TestParse("testParse_join1"));
    suite.addTest(new TestParse("testParse_join2"));
    suite.addTest(new TestParse("testParse_join3"));
    suite.addTest(new TestParse("testParse_join4"));
    suite.addTest(new TestParse("testParse_join5"));
    suite.addTest(new TestParse("testParse_join6"));
    suite.addTest(new TestParse("testParse_join7"));
    suite.addTest(new TestParse("testParse_join8"));
    suite.addTest(new TestParse("testParse_sample1"));
    suite.addTest(new TestParse("testParse_sample2"));
    suite.addTest(new TestParse("testParse_sample3"));
    suite.addTest(new TestParse("testParse_sample4"));
    suite.addTest(new TestParse("testParse_sample5"));
    suite.addTest(new TestParse("testParse_sample6"));
    suite.addTest(new TestParse("testParse_sample7"));
    suite.addTest(new TestParse("testParse_subq"));
    suite.addTest(new TestParse("testParse_udf1"));
    suite.addTest(new TestParse("testParse_udf4"));
    suite.addTest(new TestParse("testParse_udf6"));
    suite.addTest(new TestParse("testParse_udf_case"));
    suite.addTest(new TestParse("testParse_udf_when"));
    suite.addTest(new TestParse("testParse_union"));
    suite.addTest(new TestParse("testParse_shutdown"));
    return suite;
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testParse_shutdown() {
    System.out.println ("Cleaning up " + "TestParse");
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testParse_case_sensitivity() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "case_sensitivity.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/case_sensitivity.q");

      qt.init("case_sensitivity.q");
      ASTNode tree = qt.parseQuery("case_sensitivity.q");
      int ecode = qt.checkParseResults("case_sensitivity.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("case_sensitivity.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "case_sensitivity.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "case_sensitivity.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "case_sensitivity.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_cast1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "cast1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/cast1.q");

      qt.init("cast1.q");
      ASTNode tree = qt.parseQuery("cast1.q");
      int ecode = qt.checkParseResults("cast1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("cast1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "cast1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "cast1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "cast1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_groupby1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby1.q");

      qt.init("groupby1.q");
      ASTNode tree = qt.parseQuery("groupby1.q");
      int ecode = qt.checkParseResults("groupby1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_groupby2() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby2.q");

      qt.init("groupby2.q");
      ASTNode tree = qt.parseQuery("groupby2.q");
      int ecode = qt.checkParseResults("groupby2.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby2.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby2.q");
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

  public void testParse_groupby3() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby3.q");

      qt.init("groupby3.q");
      ASTNode tree = qt.parseQuery("groupby3.q");
      int ecode = qt.checkParseResults("groupby3.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby3.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby3.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_groupby4() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby4.q");

      qt.init("groupby4.q");
      ASTNode tree = qt.parseQuery("groupby4.q");
      int ecode = qt.checkParseResults("groupby4.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby4.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby4.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_groupby5() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby5.q");

      qt.init("groupby5.q");
      ASTNode tree = qt.parseQuery("groupby5.q");
      int ecode = qt.checkParseResults("groupby5.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby5.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby5.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_groupby6() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/groupby6.q");

      qt.init("groupby6.q");
      ASTNode tree = qt.parseQuery("groupby6.q");
      int ecode = qt.checkParseResults("groupby6.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("groupby6.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "groupby6.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input1.q");

      qt.init("input1.q");
      ASTNode tree = qt.parseQuery("input1.q");
      int ecode = qt.checkParseResults("input1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input2() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input2.q");

      qt.init("input2.q");
      ASTNode tree = qt.parseQuery("input2.q");
      int ecode = qt.checkParseResults("input2.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input2.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input2.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input20() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input20.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input20.q");

      qt.init("input20.q");
      ASTNode tree = qt.parseQuery("input20.q");
      int ecode = qt.checkParseResults("input20.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input20.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input20.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input20.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input20.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input3() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input3.q");

      qt.init("input3.q");
      ASTNode tree = qt.parseQuery("input3.q");
      int ecode = qt.checkParseResults("input3.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input3.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input3.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input4() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input4.q");

      qt.init("input4.q");
      ASTNode tree = qt.parseQuery("input4.q");
      int ecode = qt.checkParseResults("input4.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input4.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input4.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input5() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input5.q");

      qt.init("input5.q");
      ASTNode tree = qt.parseQuery("input5.q");
      int ecode = qt.checkParseResults("input5.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input5.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input5.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input6() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input6.q");

      qt.init("input6.q");
      ASTNode tree = qt.parseQuery("input6.q");
      int ecode = qt.checkParseResults("input6.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input6.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input6.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input7() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input7.q");

      qt.init("input7.q");
      ASTNode tree = qt.parseQuery("input7.q");
      int ecode = qt.checkParseResults("input7.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input7.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input7.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input8() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input8.q");

      qt.init("input8.q");
      ASTNode tree = qt.parseQuery("input8.q");
      int ecode = qt.checkParseResults("input8.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input8.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input8.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input9() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input9.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input9.q");

      qt.init("input9.q");
      ASTNode tree = qt.parseQuery("input9.q");
      int ecode = qt.checkParseResults("input9.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input9.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input9.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input9.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input9.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input_part1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input_part1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input_part1.q");

      qt.init("input_part1.q");
      ASTNode tree = qt.parseQuery("input_part1.q");
      int ecode = qt.checkParseResults("input_part1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input_part1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input_part1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input_part1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input_part1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input_testsequencefile() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input_testsequencefile.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input_testsequencefile.q");

      qt.init("input_testsequencefile.q");
      ASTNode tree = qt.parseQuery("input_testsequencefile.q");
      int ecode = qt.checkParseResults("input_testsequencefile.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input_testsequencefile.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input_testsequencefile.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input_testsequencefile.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input_testsequencefile.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input_testxpath() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input_testxpath.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input_testxpath.q");

      qt.init("input_testxpath.q");
      ASTNode tree = qt.parseQuery("input_testxpath.q");
      int ecode = qt.checkParseResults("input_testxpath.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input_testxpath.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input_testxpath.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input_testxpath.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input_testxpath.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_input_testxpath2() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input_testxpath2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/input_testxpath2.q");

      qt.init("input_testxpath2.q");
      ASTNode tree = qt.parseQuery("input_testxpath2.q");
      int ecode = qt.checkParseResults("input_testxpath2.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("input_testxpath2.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "input_testxpath2.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input_testxpath2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input_testxpath2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join1.q");

      qt.init("join1.q");
      ASTNode tree = qt.parseQuery("join1.q");
      int ecode = qt.checkParseResults("join1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join1.q");
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

  public void testParse_join2() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join2.q");

      qt.init("join2.q");
      ASTNode tree = qt.parseQuery("join2.q");
      int ecode = qt.checkParseResults("join2.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join2.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join2.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join3() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join3.q");

      qt.init("join3.q");
      ASTNode tree = qt.parseQuery("join3.q");
      int ecode = qt.checkParseResults("join3.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join3.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join3.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join4() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join4.q");

      qt.init("join4.q");
      ASTNode tree = qt.parseQuery("join4.q");
      int ecode = qt.checkParseResults("join4.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join4.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join4.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join5() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join5.q");

      qt.init("join5.q");
      ASTNode tree = qt.parseQuery("join5.q");
      int ecode = qt.checkParseResults("join5.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join5.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join5.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join6() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join6.q");

      qt.init("join6.q");
      ASTNode tree = qt.parseQuery("join6.q");
      int ecode = qt.checkParseResults("join6.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join6.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join6.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join7() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join7.q");

      qt.init("join7.q");
      ASTNode tree = qt.parseQuery("join7.q");
      int ecode = qt.checkParseResults("join7.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join7.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join7.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_join8() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/join8.q");

      qt.init("join8.q");
      ASTNode tree = qt.parseQuery("join8.q");
      int ecode = qt.checkParseResults("join8.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("join8.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "join8.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample1.q");

      qt.init("sample1.q");
      ASTNode tree = qt.parseQuery("sample1.q");
      int ecode = qt.checkParseResults("sample1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample2() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample2.q");

      qt.init("sample2.q");
      ASTNode tree = qt.parseQuery("sample2.q");
      int ecode = qt.checkParseResults("sample2.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample2.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample2.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample3() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample3.q");

      qt.init("sample3.q");
      ASTNode tree = qt.parseQuery("sample3.q");
      int ecode = qt.checkParseResults("sample3.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample3.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample3.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample4() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample4.q");

      qt.init("sample4.q");
      ASTNode tree = qt.parseQuery("sample4.q");
      int ecode = qt.checkParseResults("sample4.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample4.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample4.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample5() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample5.q");

      qt.init("sample5.q");
      ASTNode tree = qt.parseQuery("sample5.q");
      int ecode = qt.checkParseResults("sample5.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample5.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample5.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample6() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample6.q");

      qt.init("sample6.q");
      ASTNode tree = qt.parseQuery("sample6.q");
      int ecode = qt.checkParseResults("sample6.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample6.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample6.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_sample7() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/sample7.q");

      qt.init("sample7.q");
      ASTNode tree = qt.parseQuery("sample7.q");
      int ecode = qt.checkParseResults("sample7.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("sample7.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "sample7.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_subq() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "subq.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/subq.q");

      qt.init("subq.q");
      ASTNode tree = qt.parseQuery("subq.q");
      int ecode = qt.checkParseResults("subq.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("subq.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "subq.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "subq.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "subq.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_udf1() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/udf1.q");

      qt.init("udf1.q");
      ASTNode tree = qt.parseQuery("udf1.q");
      int ecode = qt.checkParseResults("udf1.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("udf1.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "udf1.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_udf4() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/udf4.q");

      qt.init("udf4.q");
      ASTNode tree = qt.parseQuery("udf4.q");
      int ecode = qt.checkParseResults("udf4.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("udf4.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "udf4.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_udf6() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/udf6.q");

      qt.init("udf6.q");
      ASTNode tree = qt.parseQuery("udf6.q");
      int ecode = qt.checkParseResults("udf6.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("udf6.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "udf6.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_udf_case() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_case.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/udf_case.q");

      qt.init("udf_case.q");
      ASTNode tree = qt.parseQuery("udf_case.q");
      int ecode = qt.checkParseResults("udf_case.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("udf_case.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "udf_case.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_case.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_case.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_udf_when() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_when.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/udf_when.q");

      qt.init("udf_when.q");
      ASTNode tree = qt.parseQuery("udf_when.q");
      int ecode = qt.checkParseResults("udf_when.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("udf_when.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "udf_when.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_when.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_when.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParse_union() throws Exception {
    org.apache.hadoop.hive.ql.exec.Operator.resetId();
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "union.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/positive/union.q");

      qt.init("union.q");
      ASTNode tree = qt.parseQuery("union.q");
      int ecode = qt.checkParseResults("union.q", tree);
      if (ecode != 0) {
        fail("Parse has unexpected out with error code = " + ecode + debugHint);
      }
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      ecode = qt.checkPlan("union.q", tasks);
      if (ecode != 0) {
        fail("Semantic Analysis has unexpected output with error code = " + ecode
            + debugHint);
      }
      System.out.println("Done query: " + "union.q");
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "union.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "union.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}

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

public class TestParseNegative extends TestCase {

  private static QTestUtil qt;
  
  static {
    try {
      boolean miniMR = false;
      if ("".equals("miniMR"))
        miniMR = true;
      String hadoopVer = "2.0.0-cdh4.2.0";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/results/compiler/errors", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/ql/test/logs/negative", miniMR, hadoopVer);
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in static initialization");
    }
  }

  public TestParseNegative(String name) {
    super(name);
  }

  @Override
  protected void tearDown() {
    try {
      qt.clearPostTestEffects();
      if (getName().equals("testParseNegative_shutdown"))
        qt.shutdown();
    }
    catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in tearDown");
    }
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testParseNegative_shutdown() {
    System.out.println ("Cleaning up " + "TestParseNegative");
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(new TestParseNegative("testParseNegative_ambiguous_join_col"));
    suite.addTest(new TestParseNegative("testParseNegative_duplicate_alias"));
    suite.addTest(new TestParseNegative("testParseNegative_garbage"));
    suite.addTest(new TestParseNegative("testParseNegative_insert_wrong_number_columns"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_create_table"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_dot"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_function_param2"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_index"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_list_index"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_list_index2"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_map_index"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_map_index2"));
    suite.addTest(new TestParseNegative("testParseNegative_invalid_select"));
    suite.addTest(new TestParseNegative("testParseNegative_missing_overwrite"));
    suite.addTest(new TestParseNegative("testParseNegative_nonkey_groupby"));
    suite.addTest(new TestParseNegative("testParseNegative_quoted_string"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column1"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column2"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column3"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column4"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column5"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_column6"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_function1"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_function2"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_function3"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_function4"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_table1"));
    suite.addTest(new TestParseNegative("testParseNegative_unknown_table2"));
    suite.addTest(new TestParseNegative("testParseNegative_wrong_distinct1"));
    suite.addTest(new TestParseNegative("testParseNegative_wrong_distinct2"));
    suite.addTest(new TestParseNegative("testParseNegative_shutdown"));
    return suite;
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testParseNegative_ambiguous_join_col() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "ambiguous_join_col.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/ambiguous_join_col.q");

      qt.init("ambiguous_join_col.q");
      ASTNode tree = qt.parseQuery("ambiguous_join_col.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "ambiguous_join_col.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("ambiguous_join_col.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("ambiguous_join_col.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "ambiguous_join_col.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "ambiguous_join_col.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_duplicate_alias() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_alias.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/duplicate_alias.q");

      qt.init("duplicate_alias.q");
      ASTNode tree = qt.parseQuery("duplicate_alias.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "duplicate_alias.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("duplicate_alias.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("duplicate_alias.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_alias.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_alias.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_garbage() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "garbage.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/garbage.q");

      qt.init("garbage.q");
      ASTNode tree = qt.parseQuery("garbage.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "garbage.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("garbage.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("garbage.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "garbage.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "garbage.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_insert_wrong_number_columns() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_wrong_number_columns.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/insert_wrong_number_columns.q");

      qt.init("insert_wrong_number_columns.q");
      ASTNode tree = qt.parseQuery("insert_wrong_number_columns.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "insert_wrong_number_columns.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("insert_wrong_number_columns.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("insert_wrong_number_columns.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_wrong_number_columns.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_wrong_number_columns.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_create_table() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_create_table.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_create_table.q");

      qt.init("invalid_create_table.q");
      ASTNode tree = qt.parseQuery("invalid_create_table.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_create_table.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_create_table.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_create_table.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_create_table.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_create_table.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_dot() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_dot.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_dot.q");

      qt.init("invalid_dot.q");
      ASTNode tree = qt.parseQuery("invalid_dot.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_dot.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_dot.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_dot.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_dot.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_dot.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_function_param2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_function_param2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_function_param2.q");

      qt.init("invalid_function_param2.q");
      ASTNode tree = qt.parseQuery("invalid_function_param2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_function_param2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_function_param2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_function_param2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_function_param2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_function_param2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_index() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_index.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_index.q");

      qt.init("invalid_index.q");
      ASTNode tree = qt.parseQuery("invalid_index.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_index.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_index.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_index.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_index.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_index.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_list_index() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_list_index.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_list_index.q");

      qt.init("invalid_list_index.q");
      ASTNode tree = qt.parseQuery("invalid_list_index.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_list_index.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_list_index.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_list_index.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_list_index.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_list_index.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_list_index2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_list_index2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_list_index2.q");

      qt.init("invalid_list_index2.q");
      ASTNode tree = qt.parseQuery("invalid_list_index2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_list_index2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_list_index2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_list_index2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_list_index2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_list_index2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_map_index() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_map_index.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_map_index.q");

      qt.init("invalid_map_index.q");
      ASTNode tree = qt.parseQuery("invalid_map_index.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_map_index.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_map_index.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_map_index.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_map_index.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_map_index.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_map_index2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_map_index2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_map_index2.q");

      qt.init("invalid_map_index2.q");
      ASTNode tree = qt.parseQuery("invalid_map_index2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_map_index2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_map_index2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_map_index2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_map_index2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_map_index2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_invalid_select() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_select.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/invalid_select.q");

      qt.init("invalid_select.q");
      ASTNode tree = qt.parseQuery("invalid_select.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "invalid_select.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("invalid_select.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("invalid_select.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_select.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_select.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_missing_overwrite() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "missing_overwrite.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/missing_overwrite.q");

      qt.init("missing_overwrite.q");
      ASTNode tree = qt.parseQuery("missing_overwrite.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "missing_overwrite.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("missing_overwrite.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("missing_overwrite.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "missing_overwrite.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "missing_overwrite.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_nonkey_groupby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "nonkey_groupby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/nonkey_groupby.q");

      qt.init("nonkey_groupby.q");
      ASTNode tree = qt.parseQuery("nonkey_groupby.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "nonkey_groupby.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("nonkey_groupby.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("nonkey_groupby.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "nonkey_groupby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "nonkey_groupby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_quoted_string() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "quoted_string.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/quoted_string.q");

      qt.init("quoted_string.q");
      ASTNode tree = qt.parseQuery("quoted_string.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "quoted_string.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("quoted_string.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("quoted_string.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "quoted_string.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "quoted_string.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column1.q");

      qt.init("unknown_column1.q");
      ASTNode tree = qt.parseQuery("unknown_column1.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column1.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column1.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column1.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column2.q");

      qt.init("unknown_column2.q");
      ASTNode tree = qt.parseQuery("unknown_column2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column3.q");

      qt.init("unknown_column3.q");
      ASTNode tree = qt.parseQuery("unknown_column3.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column3.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column3.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column3.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column4.q");

      qt.init("unknown_column4.q");
      ASTNode tree = qt.parseQuery("unknown_column4.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column4.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column4.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column4.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column5.q");

      qt.init("unknown_column5.q");
      ASTNode tree = qt.parseQuery("unknown_column5.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column5.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column5.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column5.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_column6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_column6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_column6.q");

      qt.init("unknown_column6.q");
      ASTNode tree = qt.parseQuery("unknown_column6.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_column6.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_column6.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_column6.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_column6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_column6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_function1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_function1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_function1.q");

      qt.init("unknown_function1.q");
      ASTNode tree = qt.parseQuery("unknown_function1.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_function1.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_function1.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_function1.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_function1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_function1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_function2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_function2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_function2.q");

      qt.init("unknown_function2.q");
      ASTNode tree = qt.parseQuery("unknown_function2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_function2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_function2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_function2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_function2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_function2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_function3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_function3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_function3.q");

      qt.init("unknown_function3.q");
      ASTNode tree = qt.parseQuery("unknown_function3.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_function3.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_function3.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_function3.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_function3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_function3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_function4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_function4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_function4.q");

      qt.init("unknown_function4.q");
      ASTNode tree = qt.parseQuery("unknown_function4.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_function4.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_function4.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_function4.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_function4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_function4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_table1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_table1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_table1.q");

      qt.init("unknown_table1.q");
      ASTNode tree = qt.parseQuery("unknown_table1.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_table1.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_table1.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_table1.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_table1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_table1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_unknown_table2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "unknown_table2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/unknown_table2.q");

      qt.init("unknown_table2.q");
      ASTNode tree = qt.parseQuery("unknown_table2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "unknown_table2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("unknown_table2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("unknown_table2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "unknown_table2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "unknown_table2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_wrong_distinct1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "wrong_distinct1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/wrong_distinct1.q");

      qt.init("wrong_distinct1.q");
      ASTNode tree = qt.parseQuery("wrong_distinct1.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "wrong_distinct1.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("wrong_distinct1.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("wrong_distinct1.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "wrong_distinct1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "wrong_distinct1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testParseNegative_wrong_distinct2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "wrong_distinct2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/negative/wrong_distinct2.q");

      qt.init("wrong_distinct2.q");
      ASTNode tree = qt.parseQuery("wrong_distinct2.q");
      List<Task<? extends Serializable>> tasks = qt.analyzeAST(tree);
      fail("Unexpected success for query: " + "wrong_distinct2.q" + debugHint);
    }
    catch (ParseException pe) {
      int ecode = qt.checkNegativeResults("wrong_distinct2.q", pe);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (SemanticException se) {
      int ecode = qt.checkNegativeResults("wrong_distinct2.q", se);
      if (ecode != 0) {
        fail("failed with error code = " + ecode + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "wrong_distinct2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "wrong_distinct2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


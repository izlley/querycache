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

public class TestContribParse extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      if ("".equals("miniMR"))
        miniMR = true;
      String hadoopVer = "";
      qt = new QTestUtil("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/contrib/src/test/results/compiler", "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/contrib/test/logs/contribpositive", miniMR, hadoopVer);
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.flush();
      fail("Unexpected exception in static initialization");
    }
  }


  public TestContribParse(String name) {
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

    suite.addTest(new TestContribParse("testParse_shutdown"));
    return suite;
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testParse_shutdown() {
    System.out.println ("Cleaning up " + "TestContribParse");
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

}

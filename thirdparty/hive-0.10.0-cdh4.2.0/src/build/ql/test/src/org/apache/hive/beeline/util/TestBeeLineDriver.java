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
package org.apache.hive.beeline.util;

import static org.junit.Assert.fail;
import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.QTestUtil;
import org.apache.hive.service.server.HiveServer2;
import org.apache.hive.testutils.junit.runners.ConcurrentTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ConcurrentTestRunner.class)
public class TestBeeLineDriver {
  private static final String hiveRootDirectory = "/home/leejy/work/hive-0.10.0-cdh4.2.0/src";
  private static final String queryDirectory = "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientpositive";
  private static final String logDirectory = "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/build/ql/test/logs/beelinepositive";
  private static final String resultsDirectory = "/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/results/beelinepositive";
  private static boolean overwrite = false;
  private static String scratchDirectory;
  private static QTestUtil.QTestSetup miniZKCluster = null;

  private static HiveServer2 hiveServer2;

  @BeforeClass
  public static void beforeClass() throws Exception {
    HiveConf hiveConf = new HiveConf();
    hiveConf.logVars(System.err);
    System.err.flush();

    scratchDirectory = hiveConf.getVar(SCRATCHDIR);

    String testOutputOverwrite = System.getProperty("test.output.overwrite");
    if (testOutputOverwrite != null && "true".equalsIgnoreCase(testOutputOverwrite)) {
      overwrite = true;
    }

    miniZKCluster = new QTestUtil.QTestSetup();
    miniZKCluster.preTest(hiveConf);

    System.setProperty("hive.zookeeper.quorum",
        hiveConf.get("hive.zookeeper.quorum"));
    System.setProperty("hive.zookeeper.client.port",
        hiveConf.get("hive.zookeeper.client.port"));
    
    String disableserver = System.getProperty("test.service.disable.server");
    if (null != disableserver && disableserver.equalsIgnoreCase("true")) {
      System.err.println("test.service.disable.server=true "
        + "Skipping HiveServer2 initialization!");
      return;
    }

    hiveServer2 = new HiveServer2();
    hiveServer2.init(hiveConf);
    System.err.println("Starting HiveServer2...");
    hiveServer2.start();
    Thread.sleep(5000);
  }


  @AfterClass
  public static void afterClass() {
    try {
      if (hiveServer2 != null) {
        System.err.println("Stopping HiveServer2...");
        hiveServer2.stop();
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    
    if (miniZKCluster != null) {
      try {
        miniZKCluster.tearDown();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /*
  public TestBeeLineDriver() {
  }
  */

  protected static void runTest(String qFileName) throws Exception {
    QFileClient qClient = new QFileClient(new HiveConf(), hiveRootDirectory,
        queryDirectory, logDirectory, resultsDirectory)
    .setQFileName(qFileName)
    .setUsername("user")
    .setPassword("password")
    .setJdbcUrl("jdbc:hive2://localhost:10000")
    .setJdbcDriver("org.apache.hive.jdbc.HiveDriver")
    .setTestDataDirectory(hiveRootDirectory + "/data/files")
    .setTestScriptDirectory(hiveRootDirectory + "/data/scripts");

    long startTime = System.currentTimeMillis();
    System.out.println(">>> STARTED " + qFileName
        + " (Thread " + Thread.currentThread().getName() + ")");
    try {
      qClient.run();
    } catch (Exception e) {
      System.err.println(">>> FAILED " + qFileName + " with exception:");
      e.printStackTrace();
      throw e;
    }
    long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
    String time = "(" + elapsedTime + "s)";
    
    if (qClient.compareResults()) {
      System.out.println(">>> PASSED " + qFileName + " " + time);
    } else {
      if (qClient.hasErrors()) {
        System.err.println(">>> FAILED " + qFileName + " (ERROR) " + time);
        fail();
      }
      if (overwrite) {
        System.err.println(">>> PASSED " + qFileName + " (OVERWRITE) " + time);
        qClient.overwriteResults();
      } else {
        System.err.println(">>> FAILED " + qFileName + " (DIFF) " + time);
        fail();
      }
    }
  }

  
  @Test
  public void testBeeLineDriver_add_part_exist() throws Exception {
    runTest("add_part_exist.q");
  }
  @Test
  public void testBeeLineDriver_add_partition_no_whitelist() throws Exception {
    runTest("add_partition_no_whitelist.q");
  }
  @Test
  public void testBeeLineDriver_add_partition_with_whitelist() throws Exception {
    runTest("add_partition_with_whitelist.q");
  }
  @Test
  public void testBeeLineDriver_alter1() throws Exception {
    runTest("alter1.q");
  }
  @Test
  public void testBeeLineDriver_alter2() throws Exception {
    runTest("alter2.q");
  }
  @Test
  public void testBeeLineDriver_alter3() throws Exception {
    runTest("alter3.q");
  }
  @Test
  public void testBeeLineDriver_alter4() throws Exception {
    runTest("alter4.q");
  }
  @Test
  public void testBeeLineDriver_alter5() throws Exception {
    runTest("alter5.q");
  }
  @Test
  public void testBeeLineDriver_alter_concatenate_indexed_table() throws Exception {
    runTest("alter_concatenate_indexed_table.q");
  }
  @Test
  public void testBeeLineDriver_alter_index() throws Exception {
    runTest("alter_index.q");
  }
  @Test
  public void testBeeLineDriver_alter_merge() throws Exception {
    runTest("alter_merge.q");
  }
  @Test
  public void testBeeLineDriver_alter_merge_2() throws Exception {
    runTest("alter_merge_2.q");
  }
  @Test
  public void testBeeLineDriver_alter_merge_stats() throws Exception {
    runTest("alter_merge_stats.q");
  }
  @Test
  public void testBeeLineDriver_alter_numbuckets_partitioned_table() throws Exception {
    runTest("alter_numbuckets_partitioned_table.q");
  }
  @Test
  public void testBeeLineDriver_alter_numbuckets_partitioned_table2() throws Exception {
    runTest("alter_numbuckets_partitioned_table2.q");
  }
  @Test
  public void testBeeLineDriver_alter_partition_format_loc() throws Exception {
    runTest("alter_partition_format_loc.q");
  }
  @Test
  public void testBeeLineDriver_alter_partition_protect_mode() throws Exception {
    runTest("alter_partition_protect_mode.q");
  }
  @Test
  public void testBeeLineDriver_alter_rename_partition() throws Exception {
    runTest("alter_rename_partition.q");
  }
  @Test
  public void testBeeLineDriver_alter_rename_partition_authorization() throws Exception {
    runTest("alter_rename_partition_authorization.q");
  }
  @Test
  public void testBeeLineDriver_alter_skewed_table() throws Exception {
    runTest("alter_skewed_table.q");
  }
  @Test
  public void testBeeLineDriver_alter_table_serde() throws Exception {
    runTest("alter_table_serde.q");
  }
  @Test
  public void testBeeLineDriver_alter_table_serde2() throws Exception {
    runTest("alter_table_serde2.q");
  }
  @Test
  public void testBeeLineDriver_alter_view_rename() throws Exception {
    runTest("alter_view_rename.q");
  }
  @Test
  public void testBeeLineDriver_archive() throws Exception {
    runTest("archive.q");
  }
  @Test
  public void testBeeLineDriver_archive_corrupt() throws Exception {
    runTest("archive_corrupt.q");
  }
  @Test
  public void testBeeLineDriver_archive_excludeHadoop20() throws Exception {
    runTest("archive_excludeHadoop20.q");
  }
  @Test
  public void testBeeLineDriver_archive_mr_1806() throws Exception {
    runTest("archive_mr_1806.q");
  }
  @Test
  public void testBeeLineDriver_archive_multi() throws Exception {
    runTest("archive_multi.q");
  }
  @Test
  public void testBeeLineDriver_archive_multi_mr_1806() throws Exception {
    runTest("archive_multi_mr_1806.q");
  }
  @Test
  public void testBeeLineDriver_authorization_1() throws Exception {
    runTest("authorization_1.q");
  }
  @Test
  public void testBeeLineDriver_authorization_2() throws Exception {
    runTest("authorization_2.q");
  }
  @Test
  public void testBeeLineDriver_authorization_3() throws Exception {
    runTest("authorization_3.q");
  }
  @Test
  public void testBeeLineDriver_authorization_4() throws Exception {
    runTest("authorization_4.q");
  }
  @Test
  public void testBeeLineDriver_authorization_5() throws Exception {
    runTest("authorization_5.q");
  }
  @Test
  public void testBeeLineDriver_authorization_6() throws Exception {
    runTest("authorization_6.q");
  }
  @Test
  public void testBeeLineDriver_authorization_7() throws Exception {
    runTest("authorization_7.q");
  }
  @Test
  public void testBeeLineDriver_auto_join0() throws Exception {
    runTest("auto_join0.q");
  }
  @Test
  public void testBeeLineDriver_auto_join1() throws Exception {
    runTest("auto_join1.q");
  }
  @Test
  public void testBeeLineDriver_auto_join10() throws Exception {
    runTest("auto_join10.q");
  }
  @Test
  public void testBeeLineDriver_auto_join11() throws Exception {
    runTest("auto_join11.q");
  }
  @Test
  public void testBeeLineDriver_auto_join12() throws Exception {
    runTest("auto_join12.q");
  }
  @Test
  public void testBeeLineDriver_auto_join13() throws Exception {
    runTest("auto_join13.q");
  }
  @Test
  public void testBeeLineDriver_auto_join14() throws Exception {
    runTest("auto_join14.q");
  }
  @Test
  public void testBeeLineDriver_auto_join14_hadoop20() throws Exception {
    runTest("auto_join14_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_auto_join15() throws Exception {
    runTest("auto_join15.q");
  }
  @Test
  public void testBeeLineDriver_auto_join16() throws Exception {
    runTest("auto_join16.q");
  }
  @Test
  public void testBeeLineDriver_auto_join17() throws Exception {
    runTest("auto_join17.q");
  }
  @Test
  public void testBeeLineDriver_auto_join18() throws Exception {
    runTest("auto_join18.q");
  }
  @Test
  public void testBeeLineDriver_auto_join18_multi_distinct() throws Exception {
    runTest("auto_join18_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_auto_join19() throws Exception {
    runTest("auto_join19.q");
  }
  @Test
  public void testBeeLineDriver_auto_join2() throws Exception {
    runTest("auto_join2.q");
  }
  @Test
  public void testBeeLineDriver_auto_join20() throws Exception {
    runTest("auto_join20.q");
  }
  @Test
  public void testBeeLineDriver_auto_join21() throws Exception {
    runTest("auto_join21.q");
  }
  @Test
  public void testBeeLineDriver_auto_join22() throws Exception {
    runTest("auto_join22.q");
  }
  @Test
  public void testBeeLineDriver_auto_join23() throws Exception {
    runTest("auto_join23.q");
  }
  @Test
  public void testBeeLineDriver_auto_join24() throws Exception {
    runTest("auto_join24.q");
  }
  @Test
  public void testBeeLineDriver_auto_join25() throws Exception {
    runTest("auto_join25.q");
  }
  @Test
  public void testBeeLineDriver_auto_join26() throws Exception {
    runTest("auto_join26.q");
  }
  @Test
  public void testBeeLineDriver_auto_join27() throws Exception {
    runTest("auto_join27.q");
  }
  @Test
  public void testBeeLineDriver_auto_join28() throws Exception {
    runTest("auto_join28.q");
  }
  @Test
  public void testBeeLineDriver_auto_join29() throws Exception {
    runTest("auto_join29.q");
  }
  @Test
  public void testBeeLineDriver_auto_join3() throws Exception {
    runTest("auto_join3.q");
  }
  @Test
  public void testBeeLineDriver_auto_join30() throws Exception {
    runTest("auto_join30.q");
  }
  @Test
  public void testBeeLineDriver_auto_join31() throws Exception {
    runTest("auto_join31.q");
  }
  @Test
  public void testBeeLineDriver_auto_join4() throws Exception {
    runTest("auto_join4.q");
  }
  @Test
  public void testBeeLineDriver_auto_join5() throws Exception {
    runTest("auto_join5.q");
  }
  @Test
  public void testBeeLineDriver_auto_join6() throws Exception {
    runTest("auto_join6.q");
  }
  @Test
  public void testBeeLineDriver_auto_join7() throws Exception {
    runTest("auto_join7.q");
  }
  @Test
  public void testBeeLineDriver_auto_join8() throws Exception {
    runTest("auto_join8.q");
  }
  @Test
  public void testBeeLineDriver_auto_join9() throws Exception {
    runTest("auto_join9.q");
  }
  @Test
  public void testBeeLineDriver_auto_join_filters() throws Exception {
    runTest("auto_join_filters.q");
  }
  @Test
  public void testBeeLineDriver_auto_join_nulls() throws Exception {
    runTest("auto_join_nulls.q");
  }
  @Test
  public void testBeeLineDriver_autogen_colalias() throws Exception {
    runTest("autogen_colalias.q");
  }
  @Test
  public void testBeeLineDriver_avro_change_schema() throws Exception {
    runTest("avro_change_schema.q");
  }
  @Test
  public void testBeeLineDriver_avro_evolved_schemas() throws Exception {
    runTest("avro_evolved_schemas.q");
  }
  @Test
  public void testBeeLineDriver_avro_joins() throws Exception {
    runTest("avro_joins.q");
  }
  @Test
  public void testBeeLineDriver_avro_sanity_test() throws Exception {
    runTest("avro_sanity_test.q");
  }
  @Test
  public void testBeeLineDriver_avro_schema_error_message() throws Exception {
    runTest("avro_schema_error_message.q");
  }
  @Test
  public void testBeeLineDriver_avro_schema_literal() throws Exception {
    runTest("avro_schema_literal.q");
  }
  @Test
  public void testBeeLineDriver_ba_table1() throws Exception {
    runTest("ba_table1.q");
  }
  @Test
  public void testBeeLineDriver_ba_table2() throws Exception {
    runTest("ba_table2.q");
  }
  @Test
  public void testBeeLineDriver_ba_table3() throws Exception {
    runTest("ba_table3.q");
  }
  @Test
  public void testBeeLineDriver_ba_table_udfs() throws Exception {
    runTest("ba_table_udfs.q");
  }
  @Test
  public void testBeeLineDriver_ba_table_union() throws Exception {
    runTest("ba_table_union.q");
  }
  @Test
  public void testBeeLineDriver_binary_constant() throws Exception {
    runTest("binary_constant.q");
  }
  @Test
  public void testBeeLineDriver_binary_output_format() throws Exception {
    runTest("binary_output_format.q");
  }
  @Test
  public void testBeeLineDriver_binary_table_bincolserde() throws Exception {
    runTest("binary_table_bincolserde.q");
  }
  @Test
  public void testBeeLineDriver_binary_table_colserde() throws Exception {
    runTest("binary_table_colserde.q");
  }
  @Test
  public void testBeeLineDriver_binarysortable_1() throws Exception {
    runTest("binarysortable_1.q");
  }
  @Test
  public void testBeeLineDriver_bucket1() throws Exception {
    runTest("bucket1.q");
  }
  @Test
  public void testBeeLineDriver_bucket2() throws Exception {
    runTest("bucket2.q");
  }
  @Test
  public void testBeeLineDriver_bucket3() throws Exception {
    runTest("bucket3.q");
  }
  @Test
  public void testBeeLineDriver_bucket4() throws Exception {
    runTest("bucket4.q");
  }
  @Test
  public void testBeeLineDriver_bucket_groupby() throws Exception {
    runTest("bucket_groupby.q");
  }
  @Test
  public void testBeeLineDriver_bucket_map_join_1() throws Exception {
    runTest("bucket_map_join_1.q");
  }
  @Test
  public void testBeeLineDriver_bucket_map_join_2() throws Exception {
    runTest("bucket_map_join_2.q");
  }
  @Test
  public void testBeeLineDriver_bucket_num_reducers() throws Exception {
    runTest("bucket_num_reducers.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_1() throws Exception {
    runTest("bucketcontext_1.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_2() throws Exception {
    runTest("bucketcontext_2.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_3() throws Exception {
    runTest("bucketcontext_3.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_4() throws Exception {
    runTest("bucketcontext_4.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_5() throws Exception {
    runTest("bucketcontext_5.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_6() throws Exception {
    runTest("bucketcontext_6.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_7() throws Exception {
    runTest("bucketcontext_7.q");
  }
  @Test
  public void testBeeLineDriver_bucketcontext_8() throws Exception {
    runTest("bucketcontext_8.q");
  }
  @Test
  public void testBeeLineDriver_bucketizedhiveinputformat() throws Exception {
    runTest("bucketizedhiveinputformat.q");
  }
  @Test
  public void testBeeLineDriver_bucketizedhiveinputformat_auto() throws Exception {
    runTest("bucketizedhiveinputformat_auto.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin1() throws Exception {
    runTest("bucketmapjoin1.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin10() throws Exception {
    runTest("bucketmapjoin10.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin11() throws Exception {
    runTest("bucketmapjoin11.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin12() throws Exception {
    runTest("bucketmapjoin12.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin13() throws Exception {
    runTest("bucketmapjoin13.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin2() throws Exception {
    runTest("bucketmapjoin2.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin3() throws Exception {
    runTest("bucketmapjoin3.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin4() throws Exception {
    runTest("bucketmapjoin4.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin5() throws Exception {
    runTest("bucketmapjoin5.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin6() throws Exception {
    runTest("bucketmapjoin6.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin7() throws Exception {
    runTest("bucketmapjoin7.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin8() throws Exception {
    runTest("bucketmapjoin8.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin9() throws Exception {
    runTest("bucketmapjoin9.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin_negative() throws Exception {
    runTest("bucketmapjoin_negative.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin_negative2() throws Exception {
    runTest("bucketmapjoin_negative2.q");
  }
  @Test
  public void testBeeLineDriver_bucketmapjoin_negative3() throws Exception {
    runTest("bucketmapjoin_negative3.q");
  }
  @Test
  public void testBeeLineDriver_case_sensitivity() throws Exception {
    runTest("case_sensitivity.q");
  }
  @Test
  public void testBeeLineDriver_cast1() throws Exception {
    runTest("cast1.q");
  }
  @Test
  public void testBeeLineDriver_cluster() throws Exception {
    runTest("cluster.q");
  }
  @Test
  public void testBeeLineDriver_columnarserde_create_shortcut() throws Exception {
    runTest("columnarserde_create_shortcut.q");
  }
  @Test
  public void testBeeLineDriver_columnstats_partlvl() throws Exception {
    runTest("columnstats_partlvl.q");
  }
  @Test
  public void testBeeLineDriver_columnstats_tbllvl() throws Exception {
    runTest("columnstats_tbllvl.q");
  }
  @Test
  public void testBeeLineDriver_combine1() throws Exception {
    runTest("combine1.q");
  }
  @Test
  public void testBeeLineDriver_combine2() throws Exception {
    runTest("combine2.q");
  }
  @Test
  public void testBeeLineDriver_combine2_hadoop20() throws Exception {
    runTest("combine2_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_combine2_win() throws Exception {
    runTest("combine2_win.q");
  }
  @Test
  public void testBeeLineDriver_combine3() throws Exception {
    runTest("combine3.q");
  }
  @Test
  public void testBeeLineDriver_compute_stats_binary() throws Exception {
    runTest("compute_stats_binary.q");
  }
  @Test
  public void testBeeLineDriver_compute_stats_boolean() throws Exception {
    runTest("compute_stats_boolean.q");
  }
  @Test
  public void testBeeLineDriver_compute_stats_double() throws Exception {
    runTest("compute_stats_double.q");
  }
  @Test
  public void testBeeLineDriver_compute_stats_long() throws Exception {
    runTest("compute_stats_long.q");
  }
  @Test
  public void testBeeLineDriver_compute_stats_string() throws Exception {
    runTest("compute_stats_string.q");
  }
  @Test
  public void testBeeLineDriver_concatenate_inherit_table_location() throws Exception {
    runTest("concatenate_inherit_table_location.q");
  }
  @Test
  public void testBeeLineDriver_constant_prop() throws Exception {
    runTest("constant_prop.q");
  }
  @Test
  public void testBeeLineDriver_convert_enum_to_string() throws Exception {
    runTest("convert_enum_to_string.q");
  }
  @Test
  public void testBeeLineDriver_count() throws Exception {
    runTest("count.q");
  }
  @Test
  public void testBeeLineDriver_cp_mj_rc() throws Exception {
    runTest("cp_mj_rc.q");
  }
  @Test
  public void testBeeLineDriver_create_1() throws Exception {
    runTest("create_1.q");
  }
  @Test
  public void testBeeLineDriver_create_alter_list_bucketing_table1() throws Exception {
    runTest("create_alter_list_bucketing_table1.q");
  }
  @Test
  public void testBeeLineDriver_create_big_view() throws Exception {
    runTest("create_big_view.q");
  }
  @Test
  public void testBeeLineDriver_create_default_prop() throws Exception {
    runTest("create_default_prop.q");
  }
  @Test
  public void testBeeLineDriver_create_escape() throws Exception {
    runTest("create_escape.q");
  }
  @Test
  public void testBeeLineDriver_create_genericudaf() throws Exception {
    runTest("create_genericudaf.q");
  }
  @Test
  public void testBeeLineDriver_create_genericudf() throws Exception {
    runTest("create_genericudf.q");
  }
  @Test
  public void testBeeLineDriver_create_insert_outputformat() throws Exception {
    runTest("create_insert_outputformat.q");
  }
  @Test
  public void testBeeLineDriver_create_like() throws Exception {
    runTest("create_like.q");
  }
  @Test
  public void testBeeLineDriver_create_like2() throws Exception {
    runTest("create_like2.q");
  }
  @Test
  public void testBeeLineDriver_create_like_view() throws Exception {
    runTest("create_like_view.q");
  }
  @Test
  public void testBeeLineDriver_create_merge_compressed() throws Exception {
    runTest("create_merge_compressed.q");
  }
  @Test
  public void testBeeLineDriver_create_nested_type() throws Exception {
    runTest("create_nested_type.q");
  }
  @Test
  public void testBeeLineDriver_create_or_replace_view() throws Exception {
    runTest("create_or_replace_view.q");
  }
  @Test
  public void testBeeLineDriver_create_skewed_table1() throws Exception {
    runTest("create_skewed_table1.q");
  }
  @Test
  public void testBeeLineDriver_create_struct_table() throws Exception {
    runTest("create_struct_table.q");
  }
  @Test
  public void testBeeLineDriver_create_udaf() throws Exception {
    runTest("create_udaf.q");
  }
  @Test
  public void testBeeLineDriver_create_union_table() throws Exception {
    runTest("create_union_table.q");
  }
  @Test
  public void testBeeLineDriver_create_view() throws Exception {
    runTest("create_view.q");
  }
  @Test
  public void testBeeLineDriver_create_view_partitioned() throws Exception {
    runTest("create_view_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_cross_join() throws Exception {
    runTest("cross_join.q");
  }
  @Test
  public void testBeeLineDriver_ct_case_insensitive() throws Exception {
    runTest("ct_case_insensitive.q");
  }
  @Test
  public void testBeeLineDriver_ctas() throws Exception {
    runTest("ctas.q");
  }
  @Test
  public void testBeeLineDriver_ctas_hadoop20() throws Exception {
    runTest("ctas_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_ctas_uses_database_location() throws Exception {
    runTest("ctas_uses_database_location.q");
  }
  @Test
  public void testBeeLineDriver_custom_input_output_format() throws Exception {
    runTest("custom_input_output_format.q");
  }
  @Test
  public void testBeeLineDriver_database() throws Exception {
    runTest("database.q");
  }
  @Test
  public void testBeeLineDriver_database_drop() throws Exception {
    runTest("database_drop.q");
  }
  @Test
  public void testBeeLineDriver_database_location() throws Exception {
    runTest("database_location.q");
  }
  @Test
  public void testBeeLineDriver_database_properties() throws Exception {
    runTest("database_properties.q");
  }
  @Test
  public void testBeeLineDriver_ddltime() throws Exception {
    runTest("ddltime.q");
  }
  @Test
  public void testBeeLineDriver_decimal_1() throws Exception {
    runTest("decimal_1.q");
  }
  @Test
  public void testBeeLineDriver_decimal_2() throws Exception {
    runTest("decimal_2.q");
  }
  @Test
  public void testBeeLineDriver_decimal_3() throws Exception {
    runTest("decimal_3.q");
  }
  @Test
  public void testBeeLineDriver_decimal_serde() throws Exception {
    runTest("decimal_serde.q");
  }
  @Test
  public void testBeeLineDriver_decimal_udf() throws Exception {
    runTest("decimal_udf.q");
  }
  @Test
  public void testBeeLineDriver_default_partition_name() throws Exception {
    runTest("default_partition_name.q");
  }
  @Test
  public void testBeeLineDriver_delimiter() throws Exception {
    runTest("delimiter.q");
  }
  @Test
  public void testBeeLineDriver_desc_non_existent_tbl() throws Exception {
    runTest("desc_non_existent_tbl.q");
  }
  @Test
  public void testBeeLineDriver_describe_database_json() throws Exception {
    runTest("describe_database_json.q");
  }
  @Test
  public void testBeeLineDriver_describe_formatted_view_partitioned() throws Exception {
    runTest("describe_formatted_view_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_describe_formatted_view_partitioned_json() throws Exception {
    runTest("describe_formatted_view_partitioned_json.q");
  }
  @Test
  public void testBeeLineDriver_describe_syntax() throws Exception {
    runTest("describe_syntax.q");
  }
  @Test
  public void testBeeLineDriver_describe_table() throws Exception {
    runTest("describe_table.q");
  }
  @Test
  public void testBeeLineDriver_describe_table_json() throws Exception {
    runTest("describe_table_json.q");
  }
  @Test
  public void testBeeLineDriver_describe_xpath() throws Exception {
    runTest("describe_xpath.q");
  }
  @Test
  public void testBeeLineDriver_diff_part_input_formats() throws Exception {
    runTest("diff_part_input_formats.q");
  }
  @Test
  public void testBeeLineDriver_disable_file_format_check() throws Exception {
    runTest("disable_file_format_check.q");
  }
  @Test
  public void testBeeLineDriver_disable_merge_for_bucketing() throws Exception {
    runTest("disable_merge_for_bucketing.q");
  }
  @Test
  public void testBeeLineDriver_driverhook() throws Exception {
    runTest("driverhook.q");
  }
  @Test
  public void testBeeLineDriver_drop_database_removes_partition_dirs() throws Exception {
    runTest("drop_database_removes_partition_dirs.q");
  }
  @Test
  public void testBeeLineDriver_drop_function() throws Exception {
    runTest("drop_function.q");
  }
  @Test
  public void testBeeLineDriver_drop_index() throws Exception {
    runTest("drop_index.q");
  }
  @Test
  public void testBeeLineDriver_drop_index_removes_partition_dirs() throws Exception {
    runTest("drop_index_removes_partition_dirs.q");
  }
  @Test
  public void testBeeLineDriver_drop_multi_partitions() throws Exception {
    runTest("drop_multi_partitions.q");
  }
  @Test
  public void testBeeLineDriver_drop_partitions_filter() throws Exception {
    runTest("drop_partitions_filter.q");
  }
  @Test
  public void testBeeLineDriver_drop_partitions_filter2() throws Exception {
    runTest("drop_partitions_filter2.q");
  }
  @Test
  public void testBeeLineDriver_drop_partitions_filter3() throws Exception {
    runTest("drop_partitions_filter3.q");
  }
  @Test
  public void testBeeLineDriver_drop_table() throws Exception {
    runTest("drop_table.q");
  }
  @Test
  public void testBeeLineDriver_drop_table2() throws Exception {
    runTest("drop_table2.q");
  }
  @Test
  public void testBeeLineDriver_drop_table_removes_partition_dirs() throws Exception {
    runTest("drop_table_removes_partition_dirs.q");
  }
  @Test
  public void testBeeLineDriver_drop_udf() throws Exception {
    runTest("drop_udf.q");
  }
  @Test
  public void testBeeLineDriver_drop_view() throws Exception {
    runTest("drop_view.q");
  }
  @Test
  public void testBeeLineDriver_enforce_order() throws Exception {
    runTest("enforce_order.q");
  }
  @Test
  public void testBeeLineDriver_escape1() throws Exception {
    runTest("escape1.q");
  }
  @Test
  public void testBeeLineDriver_escape2() throws Exception {
    runTest("escape2.q");
  }
  @Test
  public void testBeeLineDriver_escape_clusterby1() throws Exception {
    runTest("escape_clusterby1.q");
  }
  @Test
  public void testBeeLineDriver_escape_distributeby1() throws Exception {
    runTest("escape_distributeby1.q");
  }
  @Test
  public void testBeeLineDriver_escape_orderby1() throws Exception {
    runTest("escape_orderby1.q");
  }
  @Test
  public void testBeeLineDriver_escape_sortby1() throws Exception {
    runTest("escape_sortby1.q");
  }
  @Test
  public void testBeeLineDriver_exim_00_nonpart_empty() throws Exception {
    runTest("exim_00_nonpart_empty.q");
  }
  @Test
  public void testBeeLineDriver_exim_01_nonpart() throws Exception {
    runTest("exim_01_nonpart.q");
  }
  @Test
  public void testBeeLineDriver_exim_02_00_part_empty() throws Exception {
    runTest("exim_02_00_part_empty.q");
  }
  @Test
  public void testBeeLineDriver_exim_02_part() throws Exception {
    runTest("exim_02_part.q");
  }
  @Test
  public void testBeeLineDriver_exim_03_nonpart_over_compat() throws Exception {
    runTest("exim_03_nonpart_over_compat.q");
  }
  @Test
  public void testBeeLineDriver_exim_04_all_part() throws Exception {
    runTest("exim_04_all_part.q");
  }
  @Test
  public void testBeeLineDriver_exim_04_evolved_parts() throws Exception {
    runTest("exim_04_evolved_parts.q");
  }
  @Test
  public void testBeeLineDriver_exim_05_some_part() throws Exception {
    runTest("exim_05_some_part.q");
  }
  @Test
  public void testBeeLineDriver_exim_06_one_part() throws Exception {
    runTest("exim_06_one_part.q");
  }
  @Test
  public void testBeeLineDriver_exim_07_all_part_over_nonoverlap() throws Exception {
    runTest("exim_07_all_part_over_nonoverlap.q");
  }
  @Test
  public void testBeeLineDriver_exim_08_nonpart_rename() throws Exception {
    runTest("exim_08_nonpart_rename.q");
  }
  @Test
  public void testBeeLineDriver_exim_09_part_spec_nonoverlap() throws Exception {
    runTest("exim_09_part_spec_nonoverlap.q");
  }
  @Test
  public void testBeeLineDriver_exim_10_external_managed() throws Exception {
    runTest("exim_10_external_managed.q");
  }
  @Test
  public void testBeeLineDriver_exim_11_managed_external() throws Exception {
    runTest("exim_11_managed_external.q");
  }
  @Test
  public void testBeeLineDriver_exim_12_external_location() throws Exception {
    runTest("exim_12_external_location.q");
  }
  @Test
  public void testBeeLineDriver_exim_13_managed_location() throws Exception {
    runTest("exim_13_managed_location.q");
  }
  @Test
  public void testBeeLineDriver_exim_14_managed_location_over_existing() throws Exception {
    runTest("exim_14_managed_location_over_existing.q");
  }
  @Test
  public void testBeeLineDriver_exim_15_external_part() throws Exception {
    runTest("exim_15_external_part.q");
  }
  @Test
  public void testBeeLineDriver_exim_16_part_external() throws Exception {
    runTest("exim_16_part_external.q");
  }
  @Test
  public void testBeeLineDriver_exim_17_part_managed() throws Exception {
    runTest("exim_17_part_managed.q");
  }
  @Test
  public void testBeeLineDriver_exim_18_part_external() throws Exception {
    runTest("exim_18_part_external.q");
  }
  @Test
  public void testBeeLineDriver_exim_19_00_part_external_location() throws Exception {
    runTest("exim_19_00_part_external_location.q");
  }
  @Test
  public void testBeeLineDriver_exim_19_part_external_location() throws Exception {
    runTest("exim_19_part_external_location.q");
  }
  @Test
  public void testBeeLineDriver_exim_20_part_managed_location() throws Exception {
    runTest("exim_20_part_managed_location.q");
  }
  @Test
  public void testBeeLineDriver_exim_21_export_authsuccess() throws Exception {
    runTest("exim_21_export_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_exim_22_import_exist_authsuccess() throws Exception {
    runTest("exim_22_import_exist_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_exim_23_import_part_authsuccess() throws Exception {
    runTest("exim_23_import_part_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_exim_24_import_nonexist_authsuccess() throws Exception {
    runTest("exim_24_import_nonexist_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_explain_dependency() throws Exception {
    runTest("explain_dependency.q");
  }
  @Test
  public void testBeeLineDriver_explode_null() throws Exception {
    runTest("explode_null.q");
  }
  @Test
  public void testBeeLineDriver_fileformat_mix() throws Exception {
    runTest("fileformat_mix.q");
  }
  @Test
  public void testBeeLineDriver_fileformat_sequencefile() throws Exception {
    runTest("fileformat_sequencefile.q");
  }
  @Test
  public void testBeeLineDriver_fileformat_text() throws Exception {
    runTest("fileformat_text.q");
  }
  @Test
  public void testBeeLineDriver_filter_join_breaktask() throws Exception {
    runTest("filter_join_breaktask.q");
  }
  @Test
  public void testBeeLineDriver_filter_join_breaktask2() throws Exception {
    runTest("filter_join_breaktask2.q");
  }
  @Test
  public void testBeeLineDriver_global_limit() throws Exception {
    runTest("global_limit.q");
  }
  @Test
  public void testBeeLineDriver_groupby1() throws Exception {
    runTest("groupby1.q");
  }
  @Test
  public void testBeeLineDriver_groupby10() throws Exception {
    runTest("groupby10.q");
  }
  @Test
  public void testBeeLineDriver_groupby11() throws Exception {
    runTest("groupby11.q");
  }
  @Test
  public void testBeeLineDriver_groupby1_limit() throws Exception {
    runTest("groupby1_limit.q");
  }
  @Test
  public void testBeeLineDriver_groupby1_map() throws Exception {
    runTest("groupby1_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby1_map_nomap() throws Exception {
    runTest("groupby1_map_nomap.q");
  }
  @Test
  public void testBeeLineDriver_groupby1_map_skew() throws Exception {
    runTest("groupby1_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby1_noskew() throws Exception {
    runTest("groupby1_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby2() throws Exception {
    runTest("groupby2.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_limit() throws Exception {
    runTest("groupby2_limit.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_map() throws Exception {
    runTest("groupby2_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_map_multi_distinct() throws Exception {
    runTest("groupby2_map_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_map_skew() throws Exception {
    runTest("groupby2_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_noskew() throws Exception {
    runTest("groupby2_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby2_noskew_multi_distinct() throws Exception {
    runTest("groupby2_noskew_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby3() throws Exception {
    runTest("groupby3.q");
  }
  @Test
  public void testBeeLineDriver_groupby3_map() throws Exception {
    runTest("groupby3_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby3_map_multi_distinct() throws Exception {
    runTest("groupby3_map_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby3_map_skew() throws Exception {
    runTest("groupby3_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby3_noskew() throws Exception {
    runTest("groupby3_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby3_noskew_multi_distinct() throws Exception {
    runTest("groupby3_noskew_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby4() throws Exception {
    runTest("groupby4.q");
  }
  @Test
  public void testBeeLineDriver_groupby4_map() throws Exception {
    runTest("groupby4_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby4_map_skew() throws Exception {
    runTest("groupby4_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby4_noskew() throws Exception {
    runTest("groupby4_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby5() throws Exception {
    runTest("groupby5.q");
  }
  @Test
  public void testBeeLineDriver_groupby5_map() throws Exception {
    runTest("groupby5_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby5_map_skew() throws Exception {
    runTest("groupby5_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby5_noskew() throws Exception {
    runTest("groupby5_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby6() throws Exception {
    runTest("groupby6.q");
  }
  @Test
  public void testBeeLineDriver_groupby6_map() throws Exception {
    runTest("groupby6_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby6_map_skew() throws Exception {
    runTest("groupby6_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby6_noskew() throws Exception {
    runTest("groupby6_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby7() throws Exception {
    runTest("groupby7.q");
  }
  @Test
  public void testBeeLineDriver_groupby7_map() throws Exception {
    runTest("groupby7_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby7_map_multi_single_reducer() throws Exception {
    runTest("groupby7_map_multi_single_reducer.q");
  }
  @Test
  public void testBeeLineDriver_groupby7_map_skew() throws Exception {
    runTest("groupby7_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby7_noskew() throws Exception {
    runTest("groupby7_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby7_noskew_multi_single_reducer() throws Exception {
    runTest("groupby7_noskew_multi_single_reducer.q");
  }
  @Test
  public void testBeeLineDriver_groupby8() throws Exception {
    runTest("groupby8.q");
  }
  @Test
  public void testBeeLineDriver_groupby8_map() throws Exception {
    runTest("groupby8_map.q");
  }
  @Test
  public void testBeeLineDriver_groupby8_map_skew() throws Exception {
    runTest("groupby8_map_skew.q");
  }
  @Test
  public void testBeeLineDriver_groupby8_noskew() throws Exception {
    runTest("groupby8_noskew.q");
  }
  @Test
  public void testBeeLineDriver_groupby9() throws Exception {
    runTest("groupby9.q");
  }
  @Test
  public void testBeeLineDriver_groupby_bigdata() throws Exception {
    runTest("groupby_bigdata.q");
  }
  @Test
  public void testBeeLineDriver_groupby_complex_types() throws Exception {
    runTest("groupby_complex_types.q");
  }
  @Test
  public void testBeeLineDriver_groupby_complex_types_multi_single_reducer() throws Exception {
    runTest("groupby_complex_types_multi_single_reducer.q");
  }
  @Test
  public void testBeeLineDriver_groupby_cube1() throws Exception {
    runTest("groupby_cube1.q");
  }
  @Test
  public void testBeeLineDriver_groupby_grouping_id1() throws Exception {
    runTest("groupby_grouping_id1.q");
  }
  @Test
  public void testBeeLineDriver_groupby_grouping_id2() throws Exception {
    runTest("groupby_grouping_id2.q");
  }
  @Test
  public void testBeeLineDriver_groupby_grouping_sets1() throws Exception {
    runTest("groupby_grouping_sets1.q");
  }
  @Test
  public void testBeeLineDriver_groupby_map_ppr() throws Exception {
    runTest("groupby_map_ppr.q");
  }
  @Test
  public void testBeeLineDriver_groupby_map_ppr_multi_distinct() throws Exception {
    runTest("groupby_map_ppr_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby_multi_single_reducer() throws Exception {
    runTest("groupby_multi_single_reducer.q");
  }
  @Test
  public void testBeeLineDriver_groupby_multi_single_reducer2() throws Exception {
    runTest("groupby_multi_single_reducer2.q");
  }
  @Test
  public void testBeeLineDriver_groupby_neg_float() throws Exception {
    runTest("groupby_neg_float.q");
  }
  @Test
  public void testBeeLineDriver_groupby_ppd() throws Exception {
    runTest("groupby_ppd.q");
  }
  @Test
  public void testBeeLineDriver_groupby_ppr() throws Exception {
    runTest("groupby_ppr.q");
  }
  @Test
  public void testBeeLineDriver_groupby_ppr_multi_distinct() throws Exception {
    runTest("groupby_ppr_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_groupby_rollup1() throws Exception {
    runTest("groupby_rollup1.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_1() throws Exception {
    runTest("groupby_sort_1.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_2() throws Exception {
    runTest("groupby_sort_2.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_3() throws Exception {
    runTest("groupby_sort_3.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_4() throws Exception {
    runTest("groupby_sort_4.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_5() throws Exception {
    runTest("groupby_sort_5.q");
  }
  @Test
  public void testBeeLineDriver_groupby_sort_skew_1() throws Exception {
    runTest("groupby_sort_skew_1.q");
  }
  @Test
  public void testBeeLineDriver_having() throws Exception {
    runTest("having.q");
  }
  @Test
  public void testBeeLineDriver_hook_context_cs() throws Exception {
    runTest("hook_context_cs.q");
  }
  @Test
  public void testBeeLineDriver_hook_order() throws Exception {
    runTest("hook_order.q");
  }
  @Test
  public void testBeeLineDriver_implicit_cast1() throws Exception {
    runTest("implicit_cast1.q");
  }
  @Test
  public void testBeeLineDriver_index_auth() throws Exception {
    runTest("index_auth.q");
  }
  @Test
  public void testBeeLineDriver_index_auto() throws Exception {
    runTest("index_auto.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_empty() throws Exception {
    runTest("index_auto_empty.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_file_format() throws Exception {
    runTest("index_auto_file_format.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_mult_tables() throws Exception {
    runTest("index_auto_mult_tables.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_mult_tables_compact() throws Exception {
    runTest("index_auto_mult_tables_compact.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_multiple() throws Exception {
    runTest("index_auto_multiple.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_partitioned() throws Exception {
    runTest("index_auto_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_self_join() throws Exception {
    runTest("index_auto_self_join.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_unused() throws Exception {
    runTest("index_auto_unused.q");
  }
  @Test
  public void testBeeLineDriver_index_auto_update() throws Exception {
    runTest("index_auto_update.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap() throws Exception {
    runTest("index_bitmap.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap1() throws Exception {
    runTest("index_bitmap1.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap2() throws Exception {
    runTest("index_bitmap2.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap3() throws Exception {
    runTest("index_bitmap3.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap_auto() throws Exception {
    runTest("index_bitmap_auto.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap_auto_partitioned() throws Exception {
    runTest("index_bitmap_auto_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap_compression() throws Exception {
    runTest("index_bitmap_compression.q");
  }
  @Test
  public void testBeeLineDriver_index_bitmap_rc() throws Exception {
    runTest("index_bitmap_rc.q");
  }
  @Test
  public void testBeeLineDriver_index_compact() throws Exception {
    runTest("index_compact.q");
  }
  @Test
  public void testBeeLineDriver_index_compact_1() throws Exception {
    runTest("index_compact_1.q");
  }
  @Test
  public void testBeeLineDriver_index_compact_2() throws Exception {
    runTest("index_compact_2.q");
  }
  @Test
  public void testBeeLineDriver_index_compact_3() throws Exception {
    runTest("index_compact_3.q");
  }
  @Test
  public void testBeeLineDriver_index_compact_binary_search() throws Exception {
    runTest("index_compact_binary_search.q");
  }
  @Test
  public void testBeeLineDriver_index_compression() throws Exception {
    runTest("index_compression.q");
  }
  @Test
  public void testBeeLineDriver_index_creation() throws Exception {
    runTest("index_creation.q");
  }
  @Test
  public void testBeeLineDriver_index_stale() throws Exception {
    runTest("index_stale.q");
  }
  @Test
  public void testBeeLineDriver_index_stale_partitioned() throws Exception {
    runTest("index_stale_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_infer_const_type() throws Exception {
    runTest("infer_const_type.q");
  }
  @Test
  public void testBeeLineDriver_init_file() throws Exception {
    runTest("init_file.q");
  }
  @Test
  public void testBeeLineDriver_innerjoin() throws Exception {
    runTest("innerjoin.q");
  }
  @Test
  public void testBeeLineDriver_inoutdriver() throws Exception {
    runTest("inoutdriver.q");
  }
  @Test
  public void testBeeLineDriver_input() throws Exception {
    runTest("input.q");
  }
  @Test
  public void testBeeLineDriver_input0() throws Exception {
    runTest("input0.q");
  }
  @Test
  public void testBeeLineDriver_input1() throws Exception {
    runTest("input1.q");
  }
  @Test
  public void testBeeLineDriver_input10() throws Exception {
    runTest("input10.q");
  }
  @Test
  public void testBeeLineDriver_input11() throws Exception {
    runTest("input11.q");
  }
  @Test
  public void testBeeLineDriver_input11_limit() throws Exception {
    runTest("input11_limit.q");
  }
  @Test
  public void testBeeLineDriver_input12() throws Exception {
    runTest("input12.q");
  }
  @Test
  public void testBeeLineDriver_input12_hadoop20() throws Exception {
    runTest("input12_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_input13() throws Exception {
    runTest("input13.q");
  }
  @Test
  public void testBeeLineDriver_input14() throws Exception {
    runTest("input14.q");
  }
  @Test
  public void testBeeLineDriver_input14_limit() throws Exception {
    runTest("input14_limit.q");
  }
  @Test
  public void testBeeLineDriver_input15() throws Exception {
    runTest("input15.q");
  }
  @Test
  public void testBeeLineDriver_input16() throws Exception {
    runTest("input16.q");
  }
  @Test
  public void testBeeLineDriver_input16_cc() throws Exception {
    runTest("input16_cc.q");
  }
  @Test
  public void testBeeLineDriver_input17() throws Exception {
    runTest("input17.q");
  }
  @Test
  public void testBeeLineDriver_input18() throws Exception {
    runTest("input18.q");
  }
  @Test
  public void testBeeLineDriver_input19() throws Exception {
    runTest("input19.q");
  }
  @Test
  public void testBeeLineDriver_input1_limit() throws Exception {
    runTest("input1_limit.q");
  }
  @Test
  public void testBeeLineDriver_input2() throws Exception {
    runTest("input2.q");
  }
  @Test
  public void testBeeLineDriver_input20() throws Exception {
    runTest("input20.q");
  }
  @Test
  public void testBeeLineDriver_input21() throws Exception {
    runTest("input21.q");
  }
  @Test
  public void testBeeLineDriver_input22() throws Exception {
    runTest("input22.q");
  }
  @Test
  public void testBeeLineDriver_input23() throws Exception {
    runTest("input23.q");
  }
  @Test
  public void testBeeLineDriver_input24() throws Exception {
    runTest("input24.q");
  }
  @Test
  public void testBeeLineDriver_input25() throws Exception {
    runTest("input25.q");
  }
  @Test
  public void testBeeLineDriver_input26() throws Exception {
    runTest("input26.q");
  }
  @Test
  public void testBeeLineDriver_input28() throws Exception {
    runTest("input28.q");
  }
  @Test
  public void testBeeLineDriver_input2_limit() throws Exception {
    runTest("input2_limit.q");
  }
  @Test
  public void testBeeLineDriver_input3() throws Exception {
    runTest("input3.q");
  }
  @Test
  public void testBeeLineDriver_input30() throws Exception {
    runTest("input30.q");
  }
  @Test
  public void testBeeLineDriver_input31() throws Exception {
    runTest("input31.q");
  }
  @Test
  public void testBeeLineDriver_input32() throws Exception {
    runTest("input32.q");
  }
  @Test
  public void testBeeLineDriver_input33() throws Exception {
    runTest("input33.q");
  }
  @Test
  public void testBeeLineDriver_input34() throws Exception {
    runTest("input34.q");
  }
  @Test
  public void testBeeLineDriver_input35() throws Exception {
    runTest("input35.q");
  }
  @Test
  public void testBeeLineDriver_input36() throws Exception {
    runTest("input36.q");
  }
  @Test
  public void testBeeLineDriver_input37() throws Exception {
    runTest("input37.q");
  }
  @Test
  public void testBeeLineDriver_input38() throws Exception {
    runTest("input38.q");
  }
  @Test
  public void testBeeLineDriver_input39() throws Exception {
    runTest("input39.q");
  }
  @Test
  public void testBeeLineDriver_input39_hadoop20() throws Exception {
    runTest("input39_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_input3_limit() throws Exception {
    runTest("input3_limit.q");
  }
  @Test
  public void testBeeLineDriver_input4() throws Exception {
    runTest("input4.q");
  }
  @Test
  public void testBeeLineDriver_input40() throws Exception {
    runTest("input40.q");
  }
  @Test
  public void testBeeLineDriver_input41() throws Exception {
    runTest("input41.q");
  }
  @Test
  public void testBeeLineDriver_input42() throws Exception {
    runTest("input42.q");
  }
  @Test
  public void testBeeLineDriver_input43() throws Exception {
    runTest("input43.q");
  }
  @Test
  public void testBeeLineDriver_input44() throws Exception {
    runTest("input44.q");
  }
  @Test
  public void testBeeLineDriver_input45() throws Exception {
    runTest("input45.q");
  }
  @Test
  public void testBeeLineDriver_input46() throws Exception {
    runTest("input46.q");
  }
  @Test
  public void testBeeLineDriver_input49() throws Exception {
    runTest("input49.q");
  }
  @Test
  public void testBeeLineDriver_input4_cb_delim() throws Exception {
    runTest("input4_cb_delim.q");
  }
  @Test
  public void testBeeLineDriver_input4_limit() throws Exception {
    runTest("input4_limit.q");
  }
  @Test
  public void testBeeLineDriver_input5() throws Exception {
    runTest("input5.q");
  }
  @Test
  public void testBeeLineDriver_input6() throws Exception {
    runTest("input6.q");
  }
  @Test
  public void testBeeLineDriver_input7() throws Exception {
    runTest("input7.q");
  }
  @Test
  public void testBeeLineDriver_input8() throws Exception {
    runTest("input8.q");
  }
  @Test
  public void testBeeLineDriver_input9() throws Exception {
    runTest("input9.q");
  }
  @Test
  public void testBeeLineDriver_input_columnarserde() throws Exception {
    runTest("input_columnarserde.q");
  }
  @Test
  public void testBeeLineDriver_input_dfs() throws Exception {
    runTest("input_dfs.q");
  }
  @Test
  public void testBeeLineDriver_input_dynamicserde() throws Exception {
    runTest("input_dynamicserde.q");
  }
  @Test
  public void testBeeLineDriver_input_lazyserde() throws Exception {
    runTest("input_lazyserde.q");
  }
  @Test
  public void testBeeLineDriver_input_limit() throws Exception {
    runTest("input_limit.q");
  }
  @Test
  public void testBeeLineDriver_input_part0() throws Exception {
    runTest("input_part0.q");
  }
  @Test
  public void testBeeLineDriver_input_part1() throws Exception {
    runTest("input_part1.q");
  }
  @Test
  public void testBeeLineDriver_input_part10() throws Exception {
    runTest("input_part10.q");
  }
  @Test
  public void testBeeLineDriver_input_part10_win() throws Exception {
    runTest("input_part10_win.q");
  }
  @Test
  public void testBeeLineDriver_input_part2() throws Exception {
    runTest("input_part2.q");
  }
  @Test
  public void testBeeLineDriver_input_part3() throws Exception {
    runTest("input_part3.q");
  }
  @Test
  public void testBeeLineDriver_input_part4() throws Exception {
    runTest("input_part4.q");
  }
  @Test
  public void testBeeLineDriver_input_part5() throws Exception {
    runTest("input_part5.q");
  }
  @Test
  public void testBeeLineDriver_input_part6() throws Exception {
    runTest("input_part6.q");
  }
  @Test
  public void testBeeLineDriver_input_part7() throws Exception {
    runTest("input_part7.q");
  }
  @Test
  public void testBeeLineDriver_input_part8() throws Exception {
    runTest("input_part8.q");
  }
  @Test
  public void testBeeLineDriver_input_part9() throws Exception {
    runTest("input_part9.q");
  }
  @Test
  public void testBeeLineDriver_input_testsequencefile() throws Exception {
    runTest("input_testsequencefile.q");
  }
  @Test
  public void testBeeLineDriver_input_testxpath() throws Exception {
    runTest("input_testxpath.q");
  }
  @Test
  public void testBeeLineDriver_input_testxpath2() throws Exception {
    runTest("input_testxpath2.q");
  }
  @Test
  public void testBeeLineDriver_input_testxpath3() throws Exception {
    runTest("input_testxpath3.q");
  }
  @Test
  public void testBeeLineDriver_input_testxpath4() throws Exception {
    runTest("input_testxpath4.q");
  }
  @Test
  public void testBeeLineDriver_inputddl1() throws Exception {
    runTest("inputddl1.q");
  }
  @Test
  public void testBeeLineDriver_inputddl2() throws Exception {
    runTest("inputddl2.q");
  }
  @Test
  public void testBeeLineDriver_inputddl3() throws Exception {
    runTest("inputddl3.q");
  }
  @Test
  public void testBeeLineDriver_inputddl4() throws Exception {
    runTest("inputddl4.q");
  }
  @Test
  public void testBeeLineDriver_inputddl5() throws Exception {
    runTest("inputddl5.q");
  }
  @Test
  public void testBeeLineDriver_inputddl6() throws Exception {
    runTest("inputddl6.q");
  }
  @Test
  public void testBeeLineDriver_inputddl7() throws Exception {
    runTest("inputddl7.q");
  }
  @Test
  public void testBeeLineDriver_inputddl8() throws Exception {
    runTest("inputddl8.q");
  }
  @Test
  public void testBeeLineDriver_insert1() throws Exception {
    runTest("insert1.q");
  }
  @Test
  public void testBeeLineDriver_insert1_overwrite_partitions() throws Exception {
    runTest("insert1_overwrite_partitions.q");
  }
  @Test
  public void testBeeLineDriver_insert2_overwrite_partitions() throws Exception {
    runTest("insert2_overwrite_partitions.q");
  }
  @Test
  public void testBeeLineDriver_insert_compressed() throws Exception {
    runTest("insert_compressed.q");
  }
  @Test
  public void testBeeLineDriver_insert_into1() throws Exception {
    runTest("insert_into1.q");
  }
  @Test
  public void testBeeLineDriver_insert_into2() throws Exception {
    runTest("insert_into2.q");
  }
  @Test
  public void testBeeLineDriver_insert_into3() throws Exception {
    runTest("insert_into3.q");
  }
  @Test
  public void testBeeLineDriver_insert_into4() throws Exception {
    runTest("insert_into4.q");
  }
  @Test
  public void testBeeLineDriver_insert_into5() throws Exception {
    runTest("insert_into5.q");
  }
  @Test
  public void testBeeLineDriver_insert_into6() throws Exception {
    runTest("insert_into6.q");
  }
  @Test
  public void testBeeLineDriver_insertexternal1() throws Exception {
    runTest("insertexternal1.q");
  }
  @Test
  public void testBeeLineDriver_join0() throws Exception {
    runTest("join0.q");
  }
  @Test
  public void testBeeLineDriver_join1() throws Exception {
    runTest("join1.q");
  }
  @Test
  public void testBeeLineDriver_join10() throws Exception {
    runTest("join10.q");
  }
  @Test
  public void testBeeLineDriver_join11() throws Exception {
    runTest("join11.q");
  }
  @Test
  public void testBeeLineDriver_join12() throws Exception {
    runTest("join12.q");
  }
  @Test
  public void testBeeLineDriver_join13() throws Exception {
    runTest("join13.q");
  }
  @Test
  public void testBeeLineDriver_join14() throws Exception {
    runTest("join14.q");
  }
  @Test
  public void testBeeLineDriver_join14_hadoop20() throws Exception {
    runTest("join14_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_join15() throws Exception {
    runTest("join15.q");
  }
  @Test
  public void testBeeLineDriver_join16() throws Exception {
    runTest("join16.q");
  }
  @Test
  public void testBeeLineDriver_join17() throws Exception {
    runTest("join17.q");
  }
  @Test
  public void testBeeLineDriver_join18() throws Exception {
    runTest("join18.q");
  }
  @Test
  public void testBeeLineDriver_join18_multi_distinct() throws Exception {
    runTest("join18_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_join19() throws Exception {
    runTest("join19.q");
  }
  @Test
  public void testBeeLineDriver_join2() throws Exception {
    runTest("join2.q");
  }
  @Test
  public void testBeeLineDriver_join20() throws Exception {
    runTest("join20.q");
  }
  @Test
  public void testBeeLineDriver_join21() throws Exception {
    runTest("join21.q");
  }
  @Test
  public void testBeeLineDriver_join22() throws Exception {
    runTest("join22.q");
  }
  @Test
  public void testBeeLineDriver_join23() throws Exception {
    runTest("join23.q");
  }
  @Test
  public void testBeeLineDriver_join24() throws Exception {
    runTest("join24.q");
  }
  @Test
  public void testBeeLineDriver_join25() throws Exception {
    runTest("join25.q");
  }
  @Test
  public void testBeeLineDriver_join26() throws Exception {
    runTest("join26.q");
  }
  @Test
  public void testBeeLineDriver_join27() throws Exception {
    runTest("join27.q");
  }
  @Test
  public void testBeeLineDriver_join28() throws Exception {
    runTest("join28.q");
  }
  @Test
  public void testBeeLineDriver_join29() throws Exception {
    runTest("join29.q");
  }
  @Test
  public void testBeeLineDriver_join3() throws Exception {
    runTest("join3.q");
  }
  @Test
  public void testBeeLineDriver_join30() throws Exception {
    runTest("join30.q");
  }
  @Test
  public void testBeeLineDriver_join31() throws Exception {
    runTest("join31.q");
  }
  @Test
  public void testBeeLineDriver_join32() throws Exception {
    runTest("join32.q");
  }
  @Test
  public void testBeeLineDriver_join33() throws Exception {
    runTest("join33.q");
  }
  @Test
  public void testBeeLineDriver_join34() throws Exception {
    runTest("join34.q");
  }
  @Test
  public void testBeeLineDriver_join35() throws Exception {
    runTest("join35.q");
  }
  @Test
  public void testBeeLineDriver_join36() throws Exception {
    runTest("join36.q");
  }
  @Test
  public void testBeeLineDriver_join37() throws Exception {
    runTest("join37.q");
  }
  @Test
  public void testBeeLineDriver_join38() throws Exception {
    runTest("join38.q");
  }
  @Test
  public void testBeeLineDriver_join39() throws Exception {
    runTest("join39.q");
  }
  @Test
  public void testBeeLineDriver_join4() throws Exception {
    runTest("join4.q");
  }
  @Test
  public void testBeeLineDriver_join40() throws Exception {
    runTest("join40.q");
  }
  @Test
  public void testBeeLineDriver_join5() throws Exception {
    runTest("join5.q");
  }
  @Test
  public void testBeeLineDriver_join6() throws Exception {
    runTest("join6.q");
  }
  @Test
  public void testBeeLineDriver_join7() throws Exception {
    runTest("join7.q");
  }
  @Test
  public void testBeeLineDriver_join8() throws Exception {
    runTest("join8.q");
  }
  @Test
  public void testBeeLineDriver_join9() throws Exception {
    runTest("join9.q");
  }
  @Test
  public void testBeeLineDriver_join_1to1() throws Exception {
    runTest("join_1to1.q");
  }
  @Test
  public void testBeeLineDriver_join_casesensitive() throws Exception {
    runTest("join_casesensitive.q");
  }
  @Test
  public void testBeeLineDriver_join_empty() throws Exception {
    runTest("join_empty.q");
  }
  @Test
  public void testBeeLineDriver_join_filters() throws Exception {
    runTest("join_filters.q");
  }
  @Test
  public void testBeeLineDriver_join_filters_overlap() throws Exception {
    runTest("join_filters_overlap.q");
  }
  @Test
  public void testBeeLineDriver_join_hive_626() throws Exception {
    runTest("join_hive_626.q");
  }
  @Test
  public void testBeeLineDriver_join_map_ppr() throws Exception {
    runTest("join_map_ppr.q");
  }
  @Test
  public void testBeeLineDriver_join_nulls() throws Exception {
    runTest("join_nulls.q");
  }
  @Test
  public void testBeeLineDriver_join_nullsafe() throws Exception {
    runTest("join_nullsafe.q");
  }
  @Test
  public void testBeeLineDriver_join_rc() throws Exception {
    runTest("join_rc.q");
  }
  @Test
  public void testBeeLineDriver_join_reorder() throws Exception {
    runTest("join_reorder.q");
  }
  @Test
  public void testBeeLineDriver_join_reorder2() throws Exception {
    runTest("join_reorder2.q");
  }
  @Test
  public void testBeeLineDriver_join_reorder3() throws Exception {
    runTest("join_reorder3.q");
  }
  @Test
  public void testBeeLineDriver_join_thrift() throws Exception {
    runTest("join_thrift.q");
  }
  @Test
  public void testBeeLineDriver_join_view() throws Exception {
    runTest("join_view.q");
  }
  @Test
  public void testBeeLineDriver_keyword_1() throws Exception {
    runTest("keyword_1.q");
  }
  @Test
  public void testBeeLineDriver_lateral_view() throws Exception {
    runTest("lateral_view.q");
  }
  @Test
  public void testBeeLineDriver_lateral_view_cp() throws Exception {
    runTest("lateral_view_cp.q");
  }
  @Test
  public void testBeeLineDriver_lateral_view_ppd() throws Exception {
    runTest("lateral_view_ppd.q");
  }
  @Test
  public void testBeeLineDriver_leftsemijoin() throws Exception {
    runTest("leftsemijoin.q");
  }
  @Test
  public void testBeeLineDriver_lineage1() throws Exception {
    runTest("lineage1.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_multiskew_1() throws Exception {
    runTest("list_bucket_query_multiskew_1.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_multiskew_2() throws Exception {
    runTest("list_bucket_query_multiskew_2.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_multiskew_3() throws Exception {
    runTest("list_bucket_query_multiskew_3.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_oneskew_1() throws Exception {
    runTest("list_bucket_query_oneskew_1.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_oneskew_2() throws Exception {
    runTest("list_bucket_query_oneskew_2.q");
  }
  @Test
  public void testBeeLineDriver_list_bucket_query_oneskew_3() throws Exception {
    runTest("list_bucket_query_oneskew_3.q");
  }
  @Test
  public void testBeeLineDriver_literal_double() throws Exception {
    runTest("literal_double.q");
  }
  @Test
  public void testBeeLineDriver_literal_ints() throws Exception {
    runTest("literal_ints.q");
  }
  @Test
  public void testBeeLineDriver_literal_string() throws Exception {
    runTest("literal_string.q");
  }
  @Test
  public void testBeeLineDriver_load_binary_data() throws Exception {
    runTest("load_binary_data.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part1() throws Exception {
    runTest("load_dyn_part1.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part10() throws Exception {
    runTest("load_dyn_part10.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part11() throws Exception {
    runTest("load_dyn_part11.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part12() throws Exception {
    runTest("load_dyn_part12.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part13() throws Exception {
    runTest("load_dyn_part13.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part14() throws Exception {
    runTest("load_dyn_part14.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part14_win() throws Exception {
    runTest("load_dyn_part14_win.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part15() throws Exception {
    runTest("load_dyn_part15.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part2() throws Exception {
    runTest("load_dyn_part2.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part3() throws Exception {
    runTest("load_dyn_part3.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part4() throws Exception {
    runTest("load_dyn_part4.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part5() throws Exception {
    runTest("load_dyn_part5.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part6() throws Exception {
    runTest("load_dyn_part6.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part7() throws Exception {
    runTest("load_dyn_part7.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part8() throws Exception {
    runTest("load_dyn_part8.q");
  }
  @Test
  public void testBeeLineDriver_load_dyn_part9() throws Exception {
    runTest("load_dyn_part9.q");
  }
  @Test
  public void testBeeLineDriver_load_exist_part_authsuccess() throws Exception {
    runTest("load_exist_part_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_load_fs() throws Exception {
    runTest("load_fs.q");
  }
  @Test
  public void testBeeLineDriver_load_nonpart_authsuccess() throws Exception {
    runTest("load_nonpart_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_load_overwrite() throws Exception {
    runTest("load_overwrite.q");
  }
  @Test
  public void testBeeLineDriver_load_part_authsuccess() throws Exception {
    runTest("load_part_authsuccess.q");
  }
  @Test
  public void testBeeLineDriver_loadpart1() throws Exception {
    runTest("loadpart1.q");
  }
  @Test
  public void testBeeLineDriver_loadpart_err() throws Exception {
    runTest("loadpart_err.q");
  }
  @Test
  public void testBeeLineDriver_lock1() throws Exception {
    runTest("lock1.q");
  }
  @Test
  public void testBeeLineDriver_lock2() throws Exception {
    runTest("lock2.q");
  }
  @Test
  public void testBeeLineDriver_lock3() throws Exception {
    runTest("lock3.q");
  }
  @Test
  public void testBeeLineDriver_lock4() throws Exception {
    runTest("lock4.q");
  }
  @Test
  public void testBeeLineDriver_louter_join_ppr() throws Exception {
    runTest("louter_join_ppr.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin1() throws Exception {
    runTest("mapjoin1.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_distinct() throws Exception {
    runTest("mapjoin_distinct.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_filter_on_outerjoin() throws Exception {
    runTest("mapjoin_filter_on_outerjoin.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_hook() throws Exception {
    runTest("mapjoin_hook.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_mapjoin() throws Exception {
    runTest("mapjoin_mapjoin.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_subquery() throws Exception {
    runTest("mapjoin_subquery.q");
  }
  @Test
  public void testBeeLineDriver_mapjoin_subquery2() throws Exception {
    runTest("mapjoin_subquery2.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce1() throws Exception {
    runTest("mapreduce1.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce2() throws Exception {
    runTest("mapreduce2.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce3() throws Exception {
    runTest("mapreduce3.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce4() throws Exception {
    runTest("mapreduce4.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce5() throws Exception {
    runTest("mapreduce5.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce6() throws Exception {
    runTest("mapreduce6.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce7() throws Exception {
    runTest("mapreduce7.q");
  }
  @Test
  public void testBeeLineDriver_mapreduce8() throws Exception {
    runTest("mapreduce8.q");
  }
  @Test
  public void testBeeLineDriver_merge1() throws Exception {
    runTest("merge1.q");
  }
  @Test
  public void testBeeLineDriver_merge2() throws Exception {
    runTest("merge2.q");
  }
  @Test
  public void testBeeLineDriver_merge3() throws Exception {
    runTest("merge3.q");
  }
  @Test
  public void testBeeLineDriver_merge4() throws Exception {
    runTest("merge4.q");
  }
  @Test
  public void testBeeLineDriver_merge_dynamic_partition() throws Exception {
    runTest("merge_dynamic_partition.q");
  }
  @Test
  public void testBeeLineDriver_merge_dynamic_partition2() throws Exception {
    runTest("merge_dynamic_partition2.q");
  }
  @Test
  public void testBeeLineDriver_merge_dynamic_partition3() throws Exception {
    runTest("merge_dynamic_partition3.q");
  }
  @Test
  public void testBeeLineDriver_merge_dynamic_partition4() throws Exception {
    runTest("merge_dynamic_partition4.q");
  }
  @Test
  public void testBeeLineDriver_merge_dynamic_partition5() throws Exception {
    runTest("merge_dynamic_partition5.q");
  }
  @Test
  public void testBeeLineDriver_mergejoins() throws Exception {
    runTest("mergejoins.q");
  }
  @Test
  public void testBeeLineDriver_metadata_export_drop() throws Exception {
    runTest("metadata_export_drop.q");
  }
  @Test
  public void testBeeLineDriver_metadataonly1() throws Exception {
    runTest("metadataonly1.q");
  }
  @Test
  public void testBeeLineDriver_mi() throws Exception {
    runTest("mi.q");
  }
  @Test
  public void testBeeLineDriver_misc_json() throws Exception {
    runTest("misc_json.q");
  }
  @Test
  public void testBeeLineDriver_multi_insert() throws Exception {
    runTest("multi_insert.q");
  }
  @Test
  public void testBeeLineDriver_multi_insert_gby() throws Exception {
    runTest("multi_insert_gby.q");
  }
  @Test
  public void testBeeLineDriver_multi_insert_move_tasks_share_dependencies() throws Exception {
    runTest("multi_insert_move_tasks_share_dependencies.q");
  }
  @Test
  public void testBeeLineDriver_multi_join_union() throws Exception {
    runTest("multi_join_union.q");
  }
  @Test
  public void testBeeLineDriver_multi_sahooks() throws Exception {
    runTest("multi_sahooks.q");
  }
  @Test
  public void testBeeLineDriver_multigroupby_singlemr() throws Exception {
    runTest("multigroupby_singlemr.q");
  }
  @Test
  public void testBeeLineDriver_nestedvirtual() throws Exception {
    runTest("nestedvirtual.q");
  }
  @Test
  public void testBeeLineDriver_newline() throws Exception {
    runTest("newline.q");
  }
  @Test
  public void testBeeLineDriver_no_hooks() throws Exception {
    runTest("no_hooks.q");
  }
  @Test
  public void testBeeLineDriver_noalias_subq1() throws Exception {
    runTest("noalias_subq1.q");
  }
  @Test
  public void testBeeLineDriver_nomore_ambiguous_table_col() throws Exception {
    runTest("nomore_ambiguous_table_col.q");
  }
  @Test
  public void testBeeLineDriver_nonmr_fetch() throws Exception {
    runTest("nonmr_fetch.q");
  }
  @Test
  public void testBeeLineDriver_notable_alias1() throws Exception {
    runTest("notable_alias1.q");
  }
  @Test
  public void testBeeLineDriver_notable_alias2() throws Exception {
    runTest("notable_alias2.q");
  }
  @Test
  public void testBeeLineDriver_null_column() throws Exception {
    runTest("null_column.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup() throws Exception {
    runTest("nullgroup.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup2() throws Exception {
    runTest("nullgroup2.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup3() throws Exception {
    runTest("nullgroup3.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup4() throws Exception {
    runTest("nullgroup4.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup4_multi_distinct() throws Exception {
    runTest("nullgroup4_multi_distinct.q");
  }
  @Test
  public void testBeeLineDriver_nullgroup5() throws Exception {
    runTest("nullgroup5.q");
  }
  @Test
  public void testBeeLineDriver_nullinput() throws Exception {
    runTest("nullinput.q");
  }
  @Test
  public void testBeeLineDriver_nullinput2() throws Exception {
    runTest("nullinput2.q");
  }
  @Test
  public void testBeeLineDriver_nullscript() throws Exception {
    runTest("nullscript.q");
  }
  @Test
  public void testBeeLineDriver_num_op_type_conv() throws Exception {
    runTest("num_op_type_conv.q");
  }
  @Test
  public void testBeeLineDriver_ops_comparison() throws Exception {
    runTest("ops_comparison.q");
  }
  @Test
  public void testBeeLineDriver_optrstat_groupby() throws Exception {
    runTest("optrstat_groupby.q");
  }
  @Test
  public void testBeeLineDriver_order() throws Exception {
    runTest("order.q");
  }
  @Test
  public void testBeeLineDriver_order2() throws Exception {
    runTest("order2.q");
  }
  @Test
  public void testBeeLineDriver_outer_join_ppr() throws Exception {
    runTest("outer_join_ppr.q");
  }
  @Test
  public void testBeeLineDriver_overridden_confs() throws Exception {
    runTest("overridden_confs.q");
  }
  @Test
  public void testBeeLineDriver_parallel() throws Exception {
    runTest("parallel.q");
  }
  @Test
  public void testBeeLineDriver_parenthesis_star_by() throws Exception {
    runTest("parenthesis_star_by.q");
  }
  @Test
  public void testBeeLineDriver_part_inherit_tbl_props() throws Exception {
    runTest("part_inherit_tbl_props.q");
  }
  @Test
  public void testBeeLineDriver_part_inherit_tbl_props_empty() throws Exception {
    runTest("part_inherit_tbl_props_empty.q");
  }
  @Test
  public void testBeeLineDriver_part_inherit_tbl_props_with_star() throws Exception {
    runTest("part_inherit_tbl_props_with_star.q");
  }
  @Test
  public void testBeeLineDriver_partcols1() throws Exception {
    runTest("partcols1.q");
  }
  @Test
  public void testBeeLineDriver_partition_schema1() throws Exception {
    runTest("partition_schema1.q");
  }
  @Test
  public void testBeeLineDriver_partition_serde_format() throws Exception {
    runTest("partition_serde_format.q");
  }
  @Test
  public void testBeeLineDriver_partition_special_char() throws Exception {
    runTest("partition_special_char.q");
  }
  @Test
  public void testBeeLineDriver_partition_vs_table_metadata() throws Exception {
    runTest("partition_vs_table_metadata.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat() throws Exception {
    runTest("partition_wise_fileformat.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat2() throws Exception {
    runTest("partition_wise_fileformat2.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat3() throws Exception {
    runTest("partition_wise_fileformat3.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat4() throws Exception {
    runTest("partition_wise_fileformat4.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat5() throws Exception {
    runTest("partition_wise_fileformat5.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat6() throws Exception {
    runTest("partition_wise_fileformat6.q");
  }
  @Test
  public void testBeeLineDriver_partition_wise_fileformat7() throws Exception {
    runTest("partition_wise_fileformat7.q");
  }
  @Test
  public void testBeeLineDriver_partitions_json() throws Exception {
    runTest("partitions_json.q");
  }
  @Test
  public void testBeeLineDriver_pcr() throws Exception {
    runTest("pcr.q");
  }
  @Test
  public void testBeeLineDriver_ppd1() throws Exception {
    runTest("ppd1.q");
  }
  @Test
  public void testBeeLineDriver_ppd2() throws Exception {
    runTest("ppd2.q");
  }
  @Test
  public void testBeeLineDriver_ppd_clusterby() throws Exception {
    runTest("ppd_clusterby.q");
  }
  @Test
  public void testBeeLineDriver_ppd_constant_expr() throws Exception {
    runTest("ppd_constant_expr.q");
  }
  @Test
  public void testBeeLineDriver_ppd_gby() throws Exception {
    runTest("ppd_gby.q");
  }
  @Test
  public void testBeeLineDriver_ppd_gby2() throws Exception {
    runTest("ppd_gby2.q");
  }
  @Test
  public void testBeeLineDriver_ppd_gby_join() throws Exception {
    runTest("ppd_gby_join.q");
  }
  @Test
  public void testBeeLineDriver_ppd_join() throws Exception {
    runTest("ppd_join.q");
  }
  @Test
  public void testBeeLineDriver_ppd_join2() throws Exception {
    runTest("ppd_join2.q");
  }
  @Test
  public void testBeeLineDriver_ppd_join3() throws Exception {
    runTest("ppd_join3.q");
  }
  @Test
  public void testBeeLineDriver_ppd_join_filter() throws Exception {
    runTest("ppd_join_filter.q");
  }
  @Test
  public void testBeeLineDriver_ppd_multi_insert() throws Exception {
    runTest("ppd_multi_insert.q");
  }
  @Test
  public void testBeeLineDriver_ppd_outer_join1() throws Exception {
    runTest("ppd_outer_join1.q");
  }
  @Test
  public void testBeeLineDriver_ppd_outer_join2() throws Exception {
    runTest("ppd_outer_join2.q");
  }
  @Test
  public void testBeeLineDriver_ppd_outer_join3() throws Exception {
    runTest("ppd_outer_join3.q");
  }
  @Test
  public void testBeeLineDriver_ppd_outer_join4() throws Exception {
    runTest("ppd_outer_join4.q");
  }
  @Test
  public void testBeeLineDriver_ppd_outer_join5() throws Exception {
    runTest("ppd_outer_join5.q");
  }
  @Test
  public void testBeeLineDriver_ppd_random() throws Exception {
    runTest("ppd_random.q");
  }
  @Test
  public void testBeeLineDriver_ppd_repeated_alias() throws Exception {
    runTest("ppd_repeated_alias.q");
  }
  @Test
  public void testBeeLineDriver_ppd_transform() throws Exception {
    runTest("ppd_transform.q");
  }
  @Test
  public void testBeeLineDriver_ppd_udf_case() throws Exception {
    runTest("ppd_udf_case.q");
  }
  @Test
  public void testBeeLineDriver_ppd_udf_col() throws Exception {
    runTest("ppd_udf_col.q");
  }
  @Test
  public void testBeeLineDriver_ppd_union() throws Exception {
    runTest("ppd_union.q");
  }
  @Test
  public void testBeeLineDriver_ppd_union_view() throws Exception {
    runTest("ppd_union_view.q");
  }
  @Test
  public void testBeeLineDriver_ppr_allchildsarenull() throws Exception {
    runTest("ppr_allchildsarenull.q");
  }
  @Test
  public void testBeeLineDriver_ppr_pushdown() throws Exception {
    runTest("ppr_pushdown.q");
  }
  @Test
  public void testBeeLineDriver_ppr_pushdown2() throws Exception {
    runTest("ppr_pushdown2.q");
  }
  @Test
  public void testBeeLineDriver_ppr_pushdown3() throws Exception {
    runTest("ppr_pushdown3.q");
  }
  @Test
  public void testBeeLineDriver_print_header() throws Exception {
    runTest("print_header.q");
  }
  @Test
  public void testBeeLineDriver_progress_1() throws Exception {
    runTest("progress_1.q");
  }
  @Test
  public void testBeeLineDriver_protectmode() throws Exception {
    runTest("protectmode.q");
  }
  @Test
  public void testBeeLineDriver_protectmode2() throws Exception {
    runTest("protectmode2.q");
  }
  @Test
  public void testBeeLineDriver_ql_rewrite_gbtoidx() throws Exception {
    runTest("ql_rewrite_gbtoidx.q");
  }
  @Test
  public void testBeeLineDriver_query_properties() throws Exception {
    runTest("query_properties.q");
  }
  @Test
  public void testBeeLineDriver_query_result_fileformat() throws Exception {
    runTest("query_result_fileformat.q");
  }
  @Test
  public void testBeeLineDriver_query_with_semi() throws Exception {
    runTest("query_with_semi.q");
  }
  @Test
  public void testBeeLineDriver_quote1() throws Exception {
    runTest("quote1.q");
  }
  @Test
  public void testBeeLineDriver_quote2() throws Exception {
    runTest("quote2.q");
  }
  @Test
  public void testBeeLineDriver_rand_partitionpruner1() throws Exception {
    runTest("rand_partitionpruner1.q");
  }
  @Test
  public void testBeeLineDriver_rand_partitionpruner2() throws Exception {
    runTest("rand_partitionpruner2.q");
  }
  @Test
  public void testBeeLineDriver_rand_partitionpruner3() throws Exception {
    runTest("rand_partitionpruner3.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_bigdata() throws Exception {
    runTest("rcfile_bigdata.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_columnar() throws Exception {
    runTest("rcfile_columnar.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_createas1() throws Exception {
    runTest("rcfile_createas1.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_default_format() throws Exception {
    runTest("rcfile_default_format.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_lazydecompress() throws Exception {
    runTest("rcfile_lazydecompress.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_merge1() throws Exception {
    runTest("rcfile_merge1.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_merge2() throws Exception {
    runTest("rcfile_merge2.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_merge3() throws Exception {
    runTest("rcfile_merge3.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_merge4() throws Exception {
    runTest("rcfile_merge4.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_null_value() throws Exception {
    runTest("rcfile_null_value.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_toleratecorruptions() throws Exception {
    runTest("rcfile_toleratecorruptions.q");
  }
  @Test
  public void testBeeLineDriver_rcfile_union() throws Exception {
    runTest("rcfile_union.q");
  }
  @Test
  public void testBeeLineDriver_recursive_dir() throws Exception {
    runTest("recursive_dir.q");
  }
  @Test
  public void testBeeLineDriver_reduce_deduplicate() throws Exception {
    runTest("reduce_deduplicate.q");
  }
  @Test
  public void testBeeLineDriver_reduce_deduplicate_exclude_gby() throws Exception {
    runTest("reduce_deduplicate_exclude_gby.q");
  }
  @Test
  public void testBeeLineDriver_reduce_deduplicate_exclude_join() throws Exception {
    runTest("reduce_deduplicate_exclude_join.q");
  }
  @Test
  public void testBeeLineDriver_regex_col() throws Exception {
    runTest("regex_col.q");
  }
  @Test
  public void testBeeLineDriver_regexp_extract() throws Exception {
    runTest("regexp_extract.q");
  }
  @Test
  public void testBeeLineDriver_rename_column() throws Exception {
    runTest("rename_column.q");
  }
  @Test
  public void testBeeLineDriver_rename_external_partition_location() throws Exception {
    runTest("rename_external_partition_location.q");
  }
  @Test
  public void testBeeLineDriver_rename_partition_location() throws Exception {
    runTest("rename_partition_location.q");
  }
  @Test
  public void testBeeLineDriver_repair() throws Exception {
    runTest("repair.q");
  }
  @Test
  public void testBeeLineDriver_repair_hadoop23() throws Exception {
    runTest("repair_hadoop23.q");
  }
  @Test
  public void testBeeLineDriver_reset_conf() throws Exception {
    runTest("reset_conf.q");
  }
  @Test
  public void testBeeLineDriver_router_join_ppr() throws Exception {
    runTest("router_join_ppr.q");
  }
  @Test
  public void testBeeLineDriver_sample1() throws Exception {
    runTest("sample1.q");
  }
  @Test
  public void testBeeLineDriver_sample10() throws Exception {
    runTest("sample10.q");
  }
  @Test
  public void testBeeLineDriver_sample2() throws Exception {
    runTest("sample2.q");
  }
  @Test
  public void testBeeLineDriver_sample3() throws Exception {
    runTest("sample3.q");
  }
  @Test
  public void testBeeLineDriver_sample4() throws Exception {
    runTest("sample4.q");
  }
  @Test
  public void testBeeLineDriver_sample5() throws Exception {
    runTest("sample5.q");
  }
  @Test
  public void testBeeLineDriver_sample6() throws Exception {
    runTest("sample6.q");
  }
  @Test
  public void testBeeLineDriver_sample7() throws Exception {
    runTest("sample7.q");
  }
  @Test
  public void testBeeLineDriver_sample8() throws Exception {
    runTest("sample8.q");
  }
  @Test
  public void testBeeLineDriver_sample9() throws Exception {
    runTest("sample9.q");
  }
  @Test
  public void testBeeLineDriver_sample_islocalmode_hook() throws Exception {
    runTest("sample_islocalmode_hook.q");
  }
  @Test
  public void testBeeLineDriver_sample_islocalmode_hook_hadoop20() throws Exception {
    runTest("sample_islocalmode_hook_hadoop20.q");
  }
  @Test
  public void testBeeLineDriver_script_env_var1() throws Exception {
    runTest("script_env_var1.q");
  }
  @Test
  public void testBeeLineDriver_script_env_var2() throws Exception {
    runTest("script_env_var2.q");
  }
  @Test
  public void testBeeLineDriver_script_pipe() throws Exception {
    runTest("script_pipe.q");
  }
  @Test
  public void testBeeLineDriver_scriptfile1() throws Exception {
    runTest("scriptfile1.q");
  }
  @Test
  public void testBeeLineDriver_select_as_omitted() throws Exception {
    runTest("select_as_omitted.q");
  }
  @Test
  public void testBeeLineDriver_select_transform_hint() throws Exception {
    runTest("select_transform_hint.q");
  }
  @Test
  public void testBeeLineDriver_semijoin() throws Exception {
    runTest("semijoin.q");
  }
  @Test
  public void testBeeLineDriver_serde_regex() throws Exception {
    runTest("serde_regex.q");
  }
  @Test
  public void testBeeLineDriver_serde_reported_schema() throws Exception {
    runTest("serde_reported_schema.q");
  }
  @Test
  public void testBeeLineDriver_set_processor_namespaces() throws Exception {
    runTest("set_processor_namespaces.q");
  }
  @Test
  public void testBeeLineDriver_set_variable_sub() throws Exception {
    runTest("set_variable_sub.q");
  }
  @Test
  public void testBeeLineDriver_show_columns() throws Exception {
    runTest("show_columns.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_alter() throws Exception {
    runTest("show_create_table_alter.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_db_table() throws Exception {
    runTest("show_create_table_db_table.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_delimited() throws Exception {
    runTest("show_create_table_delimited.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_partitioned() throws Exception {
    runTest("show_create_table_partitioned.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_serde() throws Exception {
    runTest("show_create_table_serde.q");
  }
  @Test
  public void testBeeLineDriver_show_create_table_view() throws Exception {
    runTest("show_create_table_view.q");
  }
  @Test
  public void testBeeLineDriver_show_describe_func_quotes() throws Exception {
    runTest("show_describe_func_quotes.q");
  }
  @Test
  public void testBeeLineDriver_show_functions() throws Exception {
    runTest("show_functions.q");
  }
  @Test
  public void testBeeLineDriver_show_indexes_edge_cases() throws Exception {
    runTest("show_indexes_edge_cases.q");
  }
  @Test
  public void testBeeLineDriver_show_indexes_syntax() throws Exception {
    runTest("show_indexes_syntax.q");
  }
  @Test
  public void testBeeLineDriver_show_partitions() throws Exception {
    runTest("show_partitions.q");
  }
  @Test
  public void testBeeLineDriver_show_tables() throws Exception {
    runTest("show_tables.q");
  }
  @Test
  public void testBeeLineDriver_show_tablestatus() throws Exception {
    runTest("show_tablestatus.q");
  }
  @Test
  public void testBeeLineDriver_show_tblproperties() throws Exception {
    runTest("show_tblproperties.q");
  }
  @Test
  public void testBeeLineDriver_showparts() throws Exception {
    runTest("showparts.q");
  }
  @Test
  public void testBeeLineDriver_skewjoin() throws Exception {
    runTest("skewjoin.q");
  }
  @Test
  public void testBeeLineDriver_skewjoin_union_remove_1() throws Exception {
    runTest("skewjoin_union_remove_1.q");
  }
  @Test
  public void testBeeLineDriver_skewjoin_union_remove_2() throws Exception {
    runTest("skewjoin_union_remove_2.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt1() throws Exception {
    runTest("skewjoinopt1.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt10() throws Exception {
    runTest("skewjoinopt10.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt11() throws Exception {
    runTest("skewjoinopt11.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt12() throws Exception {
    runTest("skewjoinopt12.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt13() throws Exception {
    runTest("skewjoinopt13.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt14() throws Exception {
    runTest("skewjoinopt14.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt15() throws Exception {
    runTest("skewjoinopt15.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt16() throws Exception {
    runTest("skewjoinopt16.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt17() throws Exception {
    runTest("skewjoinopt17.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt18() throws Exception {
    runTest("skewjoinopt18.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt19() throws Exception {
    runTest("skewjoinopt19.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt2() throws Exception {
    runTest("skewjoinopt2.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt20() throws Exception {
    runTest("skewjoinopt20.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt3() throws Exception {
    runTest("skewjoinopt3.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt4() throws Exception {
    runTest("skewjoinopt4.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt5() throws Exception {
    runTest("skewjoinopt5.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt6() throws Exception {
    runTest("skewjoinopt6.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt7() throws Exception {
    runTest("skewjoinopt7.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt8() throws Exception {
    runTest("skewjoinopt8.q");
  }
  @Test
  public void testBeeLineDriver_skewjoinopt9() throws Exception {
    runTest("skewjoinopt9.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin9() throws Exception {
    runTest("smb_mapjoin9.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_1() throws Exception {
    runTest("smb_mapjoin_1.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_10() throws Exception {
    runTest("smb_mapjoin_10.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_11() throws Exception {
    runTest("smb_mapjoin_11.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_12() throws Exception {
    runTest("smb_mapjoin_12.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_13() throws Exception {
    runTest("smb_mapjoin_13.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_2() throws Exception {
    runTest("smb_mapjoin_2.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_3() throws Exception {
    runTest("smb_mapjoin_3.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_4() throws Exception {
    runTest("smb_mapjoin_4.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_5() throws Exception {
    runTest("smb_mapjoin_5.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_6() throws Exception {
    runTest("smb_mapjoin_6.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_7() throws Exception {
    runTest("smb_mapjoin_7.q");
  }
  @Test
  public void testBeeLineDriver_smb_mapjoin_8() throws Exception {
    runTest("smb_mapjoin_8.q");
  }
  @Test
  public void testBeeLineDriver_sort() throws Exception {
    runTest("sort.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_1() throws Exception {
    runTest("sort_merge_join_desc_1.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_2() throws Exception {
    runTest("sort_merge_join_desc_2.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_3() throws Exception {
    runTest("sort_merge_join_desc_3.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_4() throws Exception {
    runTest("sort_merge_join_desc_4.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_5() throws Exception {
    runTest("sort_merge_join_desc_5.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_6() throws Exception {
    runTest("sort_merge_join_desc_6.q");
  }
  @Test
  public void testBeeLineDriver_sort_merge_join_desc_7() throws Exception {
    runTest("sort_merge_join_desc_7.q");
  }
  @Test
  public void testBeeLineDriver_source() throws Exception {
    runTest("source.q");
  }
  @Test
  public void testBeeLineDriver_split_sample() throws Exception {
    runTest("split_sample.q");
  }
  @Test
  public void testBeeLineDriver_stats0() throws Exception {
    runTest("stats0.q");
  }
  @Test
  public void testBeeLineDriver_stats1() throws Exception {
    runTest("stats1.q");
  }
  @Test
  public void testBeeLineDriver_stats10() throws Exception {
    runTest("stats10.q");
  }
  @Test
  public void testBeeLineDriver_stats11() throws Exception {
    runTest("stats11.q");
  }
  @Test
  public void testBeeLineDriver_stats12() throws Exception {
    runTest("stats12.q");
  }
  @Test
  public void testBeeLineDriver_stats13() throws Exception {
    runTest("stats13.q");
  }
  @Test
  public void testBeeLineDriver_stats14() throws Exception {
    runTest("stats14.q");
  }
  @Test
  public void testBeeLineDriver_stats15() throws Exception {
    runTest("stats15.q");
  }
  @Test
  public void testBeeLineDriver_stats16() throws Exception {
    runTest("stats16.q");
  }
  @Test
  public void testBeeLineDriver_stats18() throws Exception {
    runTest("stats18.q");
  }
  @Test
  public void testBeeLineDriver_stats2() throws Exception {
    runTest("stats2.q");
  }
  @Test
  public void testBeeLineDriver_stats3() throws Exception {
    runTest("stats3.q");
  }
  @Test
  public void testBeeLineDriver_stats4() throws Exception {
    runTest("stats4.q");
  }
  @Test
  public void testBeeLineDriver_stats5() throws Exception {
    runTest("stats5.q");
  }
  @Test
  public void testBeeLineDriver_stats6() throws Exception {
    runTest("stats6.q");
  }
  @Test
  public void testBeeLineDriver_stats7() throws Exception {
    runTest("stats7.q");
  }
  @Test
  public void testBeeLineDriver_stats8() throws Exception {
    runTest("stats8.q");
  }
  @Test
  public void testBeeLineDriver_stats9() throws Exception {
    runTest("stats9.q");
  }
  @Test
  public void testBeeLineDriver_stats_aggregator_error_1() throws Exception {
    runTest("stats_aggregator_error_1.q");
  }
  @Test
  public void testBeeLineDriver_stats_empty_dyn_part() throws Exception {
    runTest("stats_empty_dyn_part.q");
  }
  @Test
  public void testBeeLineDriver_stats_empty_partition() throws Exception {
    runTest("stats_empty_partition.q");
  }
  @Test
  public void testBeeLineDriver_stats_publisher_error_1() throws Exception {
    runTest("stats_publisher_error_1.q");
  }
  @Test
  public void testBeeLineDriver_str_to_map() throws Exception {
    runTest("str_to_map.q");
  }
  @Test
  public void testBeeLineDriver_subq() throws Exception {
    runTest("subq.q");
  }
  @Test
  public void testBeeLineDriver_subq2() throws Exception {
    runTest("subq2.q");
  }
  @Test
  public void testBeeLineDriver_symlink_text_input_format() throws Exception {
    runTest("symlink_text_input_format.q");
  }
  @Test
  public void testBeeLineDriver_table_access_keys_stats() throws Exception {
    runTest("table_access_keys_stats.q");
  }
  @Test
  public void testBeeLineDriver_tablename_with_select() throws Exception {
    runTest("tablename_with_select.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_1() throws Exception {
    runTest("timestamp_1.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_2() throws Exception {
    runTest("timestamp_2.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_3() throws Exception {
    runTest("timestamp_3.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_comparison() throws Exception {
    runTest("timestamp_comparison.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_lazy() throws Exception {
    runTest("timestamp_lazy.q");
  }
  @Test
  public void testBeeLineDriver_timestamp_udf() throws Exception {
    runTest("timestamp_udf.q");
  }
  @Test
  public void testBeeLineDriver_touch() throws Exception {
    runTest("touch.q");
  }
  @Test
  public void testBeeLineDriver_transform1() throws Exception {
    runTest("transform1.q");
  }
  @Test
  public void testBeeLineDriver_transform2() throws Exception {
    runTest("transform2.q");
  }
  @Test
  public void testBeeLineDriver_transform_ppr1() throws Exception {
    runTest("transform_ppr1.q");
  }
  @Test
  public void testBeeLineDriver_transform_ppr2() throws Exception {
    runTest("transform_ppr2.q");
  }
  @Test
  public void testBeeLineDriver_type_cast_1() throws Exception {
    runTest("type_cast_1.q");
  }
  @Test
  public void testBeeLineDriver_type_widening() throws Exception {
    runTest("type_widening.q");
  }
  @Test
  public void testBeeLineDriver_udaf_collect_set() throws Exception {
    runTest("udaf_collect_set.q");
  }
  @Test
  public void testBeeLineDriver_udaf_context_ngrams() throws Exception {
    runTest("udaf_context_ngrams.q");
  }
  @Test
  public void testBeeLineDriver_udaf_corr() throws Exception {
    runTest("udaf_corr.q");
  }
  @Test
  public void testBeeLineDriver_udaf_covar_pop() throws Exception {
    runTest("udaf_covar_pop.q");
  }
  @Test
  public void testBeeLineDriver_udaf_covar_samp() throws Exception {
    runTest("udaf_covar_samp.q");
  }
  @Test
  public void testBeeLineDriver_udaf_histogram_numeric() throws Exception {
    runTest("udaf_histogram_numeric.q");
  }
  @Test
  public void testBeeLineDriver_udaf_ngrams() throws Exception {
    runTest("udaf_ngrams.q");
  }
  @Test
  public void testBeeLineDriver_udaf_number_format() throws Exception {
    runTest("udaf_number_format.q");
  }
  @Test
  public void testBeeLineDriver_udaf_percentile_approx() throws Exception {
    runTest("udaf_percentile_approx.q");
  }
  @Test
  public void testBeeLineDriver_udf1() throws Exception {
    runTest("udf1.q");
  }
  @Test
  public void testBeeLineDriver_udf2() throws Exception {
    runTest("udf2.q");
  }
  @Test
  public void testBeeLineDriver_udf3() throws Exception {
    runTest("udf3.q");
  }
  @Test
  public void testBeeLineDriver_udf4() throws Exception {
    runTest("udf4.q");
  }
  @Test
  public void testBeeLineDriver_udf5() throws Exception {
    runTest("udf5.q");
  }
  @Test
  public void testBeeLineDriver_udf6() throws Exception {
    runTest("udf6.q");
  }
  @Test
  public void testBeeLineDriver_udf7() throws Exception {
    runTest("udf7.q");
  }
  @Test
  public void testBeeLineDriver_udf8() throws Exception {
    runTest("udf8.q");
  }
  @Test
  public void testBeeLineDriver_udf9() throws Exception {
    runTest("udf9.q");
  }
  @Test
  public void testBeeLineDriver_udf_10_trims() throws Exception {
    runTest("udf_10_trims.q");
  }
  @Test
  public void testBeeLineDriver_udf_E() throws Exception {
    runTest("udf_E.q");
  }
  @Test
  public void testBeeLineDriver_udf_PI() throws Exception {
    runTest("udf_PI.q");
  }
  @Test
  public void testBeeLineDriver_udf_abs() throws Exception {
    runTest("udf_abs.q");
  }
  @Test
  public void testBeeLineDriver_udf_acos() throws Exception {
    runTest("udf_acos.q");
  }
  @Test
  public void testBeeLineDriver_udf_add() throws Exception {
    runTest("udf_add.q");
  }
  @Test
  public void testBeeLineDriver_udf_array() throws Exception {
    runTest("udf_array.q");
  }
  @Test
  public void testBeeLineDriver_udf_array_contains() throws Exception {
    runTest("udf_array_contains.q");
  }
  @Test
  public void testBeeLineDriver_udf_ascii() throws Exception {
    runTest("udf_ascii.q");
  }
  @Test
  public void testBeeLineDriver_udf_asin() throws Exception {
    runTest("udf_asin.q");
  }
  @Test
  public void testBeeLineDriver_udf_atan() throws Exception {
    runTest("udf_atan.q");
  }
  @Test
  public void testBeeLineDriver_udf_avg() throws Exception {
    runTest("udf_avg.q");
  }
  @Test
  public void testBeeLineDriver_udf_between() throws Exception {
    runTest("udf_between.q");
  }
  @Test
  public void testBeeLineDriver_udf_bigint() throws Exception {
    runTest("udf_bigint.q");
  }
  @Test
  public void testBeeLineDriver_udf_bin() throws Exception {
    runTest("udf_bin.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitmap_and() throws Exception {
    runTest("udf_bitmap_and.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitmap_empty() throws Exception {
    runTest("udf_bitmap_empty.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitmap_or() throws Exception {
    runTest("udf_bitmap_or.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitwise_and() throws Exception {
    runTest("udf_bitwise_and.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitwise_not() throws Exception {
    runTest("udf_bitwise_not.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitwise_or() throws Exception {
    runTest("udf_bitwise_or.q");
  }
  @Test
  public void testBeeLineDriver_udf_bitwise_xor() throws Exception {
    runTest("udf_bitwise_xor.q");
  }
  @Test
  public void testBeeLineDriver_udf_boolean() throws Exception {
    runTest("udf_boolean.q");
  }
  @Test
  public void testBeeLineDriver_udf_case() throws Exception {
    runTest("udf_case.q");
  }
  @Test
  public void testBeeLineDriver_udf_case_column_pruning() throws Exception {
    runTest("udf_case_column_pruning.q");
  }
  @Test
  public void testBeeLineDriver_udf_case_thrift() throws Exception {
    runTest("udf_case_thrift.q");
  }
  @Test
  public void testBeeLineDriver_udf_ceil() throws Exception {
    runTest("udf_ceil.q");
  }
  @Test
  public void testBeeLineDriver_udf_ceiling() throws Exception {
    runTest("udf_ceiling.q");
  }
  @Test
  public void testBeeLineDriver_udf_coalesce() throws Exception {
    runTest("udf_coalesce.q");
  }
  @Test
  public void testBeeLineDriver_udf_compare_java_string() throws Exception {
    runTest("udf_compare_java_string.q");
  }
  @Test
  public void testBeeLineDriver_udf_concat() throws Exception {
    runTest("udf_concat.q");
  }
  @Test
  public void testBeeLineDriver_udf_concat_insert1() throws Exception {
    runTest("udf_concat_insert1.q");
  }
  @Test
  public void testBeeLineDriver_udf_concat_insert2() throws Exception {
    runTest("udf_concat_insert2.q");
  }
  @Test
  public void testBeeLineDriver_udf_concat_ws() throws Exception {
    runTest("udf_concat_ws.q");
  }
  @Test
  public void testBeeLineDriver_udf_conv() throws Exception {
    runTest("udf_conv.q");
  }
  @Test
  public void testBeeLineDriver_udf_cos() throws Exception {
    runTest("udf_cos.q");
  }
  @Test
  public void testBeeLineDriver_udf_count() throws Exception {
    runTest("udf_count.q");
  }
  @Test
  public void testBeeLineDriver_udf_date_add() throws Exception {
    runTest("udf_date_add.q");
  }
  @Test
  public void testBeeLineDriver_udf_date_sub() throws Exception {
    runTest("udf_date_sub.q");
  }
  @Test
  public void testBeeLineDriver_udf_datediff() throws Exception {
    runTest("udf_datediff.q");
  }
  @Test
  public void testBeeLineDriver_udf_day() throws Exception {
    runTest("udf_day.q");
  }
  @Test
  public void testBeeLineDriver_udf_dayofmonth() throws Exception {
    runTest("udf_dayofmonth.q");
  }
  @Test
  public void testBeeLineDriver_udf_degrees() throws Exception {
    runTest("udf_degrees.q");
  }
  @Test
  public void testBeeLineDriver_udf_div() throws Exception {
    runTest("udf_div.q");
  }
  @Test
  public void testBeeLineDriver_udf_divide() throws Exception {
    runTest("udf_divide.q");
  }
  @Test
  public void testBeeLineDriver_udf_double() throws Exception {
    runTest("udf_double.q");
  }
  @Test
  public void testBeeLineDriver_udf_elt() throws Exception {
    runTest("udf_elt.q");
  }
  @Test
  public void testBeeLineDriver_udf_equal() throws Exception {
    runTest("udf_equal.q");
  }
  @Test
  public void testBeeLineDriver_udf_exp() throws Exception {
    runTest("udf_exp.q");
  }
  @Test
  public void testBeeLineDriver_udf_explode() throws Exception {
    runTest("udf_explode.q");
  }
  @Test
  public void testBeeLineDriver_udf_field() throws Exception {
    runTest("udf_field.q");
  }
  @Test
  public void testBeeLineDriver_udf_find_in_set() throws Exception {
    runTest("udf_find_in_set.q");
  }
  @Test
  public void testBeeLineDriver_udf_float() throws Exception {
    runTest("udf_float.q");
  }
  @Test
  public void testBeeLineDriver_udf_floor() throws Exception {
    runTest("udf_floor.q");
  }
  @Test
  public void testBeeLineDriver_udf_format_number() throws Exception {
    runTest("udf_format_number.q");
  }
  @Test
  public void testBeeLineDriver_udf_from_unixtime() throws Exception {
    runTest("udf_from_unixtime.q");
  }
  @Test
  public void testBeeLineDriver_udf_get_json_object() throws Exception {
    runTest("udf_get_json_object.q");
  }
  @Test
  public void testBeeLineDriver_udf_greaterthan() throws Exception {
    runTest("udf_greaterthan.q");
  }
  @Test
  public void testBeeLineDriver_udf_greaterthanorequal() throws Exception {
    runTest("udf_greaterthanorequal.q");
  }
  @Test
  public void testBeeLineDriver_udf_hash() throws Exception {
    runTest("udf_hash.q");
  }
  @Test
  public void testBeeLineDriver_udf_hex() throws Exception {
    runTest("udf_hex.q");
  }
  @Test
  public void testBeeLineDriver_udf_hour() throws Exception {
    runTest("udf_hour.q");
  }
  @Test
  public void testBeeLineDriver_udf_if() throws Exception {
    runTest("udf_if.q");
  }
  @Test
  public void testBeeLineDriver_udf_in() throws Exception {
    runTest("udf_in.q");
  }
  @Test
  public void testBeeLineDriver_udf_in_file() throws Exception {
    runTest("udf_in_file.q");
  }
  @Test
  public void testBeeLineDriver_udf_index() throws Exception {
    runTest("udf_index.q");
  }
  @Test
  public void testBeeLineDriver_udf_inline() throws Exception {
    runTest("udf_inline.q");
  }
  @Test
  public void testBeeLineDriver_udf_instr() throws Exception {
    runTest("udf_instr.q");
  }
  @Test
  public void testBeeLineDriver_udf_int() throws Exception {
    runTest("udf_int.q");
  }
  @Test
  public void testBeeLineDriver_udf_isnotnull() throws Exception {
    runTest("udf_isnotnull.q");
  }
  @Test
  public void testBeeLineDriver_udf_isnull() throws Exception {
    runTest("udf_isnull.q");
  }
  @Test
  public void testBeeLineDriver_udf_isnull_isnotnull() throws Exception {
    runTest("udf_isnull_isnotnull.q");
  }
  @Test
  public void testBeeLineDriver_udf_java_method() throws Exception {
    runTest("udf_java_method.q");
  }
  @Test
  public void testBeeLineDriver_udf_lcase() throws Exception {
    runTest("udf_lcase.q");
  }
  @Test
  public void testBeeLineDriver_udf_length() throws Exception {
    runTest("udf_length.q");
  }
  @Test
  public void testBeeLineDriver_udf_lessthan() throws Exception {
    runTest("udf_lessthan.q");
  }
  @Test
  public void testBeeLineDriver_udf_lessthanorequal() throws Exception {
    runTest("udf_lessthanorequal.q");
  }
  @Test
  public void testBeeLineDriver_udf_like() throws Exception {
    runTest("udf_like.q");
  }
  @Test
  public void testBeeLineDriver_udf_ln() throws Exception {
    runTest("udf_ln.q");
  }
  @Test
  public void testBeeLineDriver_udf_locate() throws Exception {
    runTest("udf_locate.q");
  }
  @Test
  public void testBeeLineDriver_udf_log() throws Exception {
    runTest("udf_log.q");
  }
  @Test
  public void testBeeLineDriver_udf_log10() throws Exception {
    runTest("udf_log10.q");
  }
  @Test
  public void testBeeLineDriver_udf_log2() throws Exception {
    runTest("udf_log2.q");
  }
  @Test
  public void testBeeLineDriver_udf_logic_java_boolean() throws Exception {
    runTest("udf_logic_java_boolean.q");
  }
  @Test
  public void testBeeLineDriver_udf_lower() throws Exception {
    runTest("udf_lower.q");
  }
  @Test
  public void testBeeLineDriver_udf_lpad() throws Exception {
    runTest("udf_lpad.q");
  }
  @Test
  public void testBeeLineDriver_udf_ltrim() throws Exception {
    runTest("udf_ltrim.q");
  }
  @Test
  public void testBeeLineDriver_udf_map() throws Exception {
    runTest("udf_map.q");
  }
  @Test
  public void testBeeLineDriver_udf_map_keys() throws Exception {
    runTest("udf_map_keys.q");
  }
  @Test
  public void testBeeLineDriver_udf_map_values() throws Exception {
    runTest("udf_map_values.q");
  }
  @Test
  public void testBeeLineDriver_udf_max() throws Exception {
    runTest("udf_max.q");
  }
  @Test
  public void testBeeLineDriver_udf_min() throws Exception {
    runTest("udf_min.q");
  }
  @Test
  public void testBeeLineDriver_udf_minute() throws Exception {
    runTest("udf_minute.q");
  }
  @Test
  public void testBeeLineDriver_udf_modulo() throws Exception {
    runTest("udf_modulo.q");
  }
  @Test
  public void testBeeLineDriver_udf_month() throws Exception {
    runTest("udf_month.q");
  }
  @Test
  public void testBeeLineDriver_udf_named_struct() throws Exception {
    runTest("udf_named_struct.q");
  }
  @Test
  public void testBeeLineDriver_udf_negative() throws Exception {
    runTest("udf_negative.q");
  }
  @Test
  public void testBeeLineDriver_udf_not() throws Exception {
    runTest("udf_not.q");
  }
  @Test
  public void testBeeLineDriver_udf_notequal() throws Exception {
    runTest("udf_notequal.q");
  }
  @Test
  public void testBeeLineDriver_udf_notop() throws Exception {
    runTest("udf_notop.q");
  }
  @Test
  public void testBeeLineDriver_udf_or() throws Exception {
    runTest("udf_or.q");
  }
  @Test
  public void testBeeLineDriver_udf_parse_url() throws Exception {
    runTest("udf_parse_url.q");
  }
  @Test
  public void testBeeLineDriver_udf_percentile() throws Exception {
    runTest("udf_percentile.q");
  }
  @Test
  public void testBeeLineDriver_udf_pmod() throws Exception {
    runTest("udf_pmod.q");
  }
  @Test
  public void testBeeLineDriver_udf_positive() throws Exception {
    runTest("udf_positive.q");
  }
  @Test
  public void testBeeLineDriver_udf_pow() throws Exception {
    runTest("udf_pow.q");
  }
  @Test
  public void testBeeLineDriver_udf_power() throws Exception {
    runTest("udf_power.q");
  }
  @Test
  public void testBeeLineDriver_udf_printf() throws Exception {
    runTest("udf_printf.q");
  }
  @Test
  public void testBeeLineDriver_udf_radians() throws Exception {
    runTest("udf_radians.q");
  }
  @Test
  public void testBeeLineDriver_udf_rand() throws Exception {
    runTest("udf_rand.q");
  }
  @Test
  public void testBeeLineDriver_udf_reflect() throws Exception {
    runTest("udf_reflect.q");
  }
  @Test
  public void testBeeLineDriver_udf_regexp() throws Exception {
    runTest("udf_regexp.q");
  }
  @Test
  public void testBeeLineDriver_udf_regexp_extract() throws Exception {
    runTest("udf_regexp_extract.q");
  }
  @Test
  public void testBeeLineDriver_udf_regexp_replace() throws Exception {
    runTest("udf_regexp_replace.q");
  }
  @Test
  public void testBeeLineDriver_udf_repeat() throws Exception {
    runTest("udf_repeat.q");
  }
  @Test
  public void testBeeLineDriver_udf_reverse() throws Exception {
    runTest("udf_reverse.q");
  }
  @Test
  public void testBeeLineDriver_udf_rlike() throws Exception {
    runTest("udf_rlike.q");
  }
  @Test
  public void testBeeLineDriver_udf_round() throws Exception {
    runTest("udf_round.q");
  }
  @Test
  public void testBeeLineDriver_udf_round_2() throws Exception {
    runTest("udf_round_2.q");
  }
  @Test
  public void testBeeLineDriver_udf_rpad() throws Exception {
    runTest("udf_rpad.q");
  }
  @Test
  public void testBeeLineDriver_udf_rtrim() throws Exception {
    runTest("udf_rtrim.q");
  }
  @Test
  public void testBeeLineDriver_udf_second() throws Exception {
    runTest("udf_second.q");
  }
  @Test
  public void testBeeLineDriver_udf_sentences() throws Exception {
    runTest("udf_sentences.q");
  }
  @Test
  public void testBeeLineDriver_udf_sign() throws Exception {
    runTest("udf_sign.q");
  }
  @Test
  public void testBeeLineDriver_udf_sin() throws Exception {
    runTest("udf_sin.q");
  }
  @Test
  public void testBeeLineDriver_udf_size() throws Exception {
    runTest("udf_size.q");
  }
  @Test
  public void testBeeLineDriver_udf_smallint() throws Exception {
    runTest("udf_smallint.q");
  }
  @Test
  public void testBeeLineDriver_udf_sort_array() throws Exception {
    runTest("udf_sort_array.q");
  }
  @Test
  public void testBeeLineDriver_udf_space() throws Exception {
    runTest("udf_space.q");
  }
  @Test
  public void testBeeLineDriver_udf_split() throws Exception {
    runTest("udf_split.q");
  }
  @Test
  public void testBeeLineDriver_udf_sqrt() throws Exception {
    runTest("udf_sqrt.q");
  }
  @Test
  public void testBeeLineDriver_udf_std() throws Exception {
    runTest("udf_std.q");
  }
  @Test
  public void testBeeLineDriver_udf_stddev() throws Exception {
    runTest("udf_stddev.q");
  }
  @Test
  public void testBeeLineDriver_udf_stddev_pop() throws Exception {
    runTest("udf_stddev_pop.q");
  }
  @Test
  public void testBeeLineDriver_udf_stddev_samp() throws Exception {
    runTest("udf_stddev_samp.q");
  }
  @Test
  public void testBeeLineDriver_udf_string() throws Exception {
    runTest("udf_string.q");
  }
  @Test
  public void testBeeLineDriver_udf_struct() throws Exception {
    runTest("udf_struct.q");
  }
  @Test
  public void testBeeLineDriver_udf_substr() throws Exception {
    runTest("udf_substr.q");
  }
  @Test
  public void testBeeLineDriver_udf_substring() throws Exception {
    runTest("udf_substring.q");
  }
  @Test
  public void testBeeLineDriver_udf_subtract() throws Exception {
    runTest("udf_subtract.q");
  }
  @Test
  public void testBeeLineDriver_udf_sum() throws Exception {
    runTest("udf_sum.q");
  }
  @Test
  public void testBeeLineDriver_udf_tan() throws Exception {
    runTest("udf_tan.q");
  }
  @Test
  public void testBeeLineDriver_udf_testlength() throws Exception {
    runTest("udf_testlength.q");
  }
  @Test
  public void testBeeLineDriver_udf_testlength2() throws Exception {
    runTest("udf_testlength2.q");
  }
  @Test
  public void testBeeLineDriver_udf_tinyint() throws Exception {
    runTest("udf_tinyint.q");
  }
  @Test
  public void testBeeLineDriver_udf_to_date() throws Exception {
    runTest("udf_to_date.q");
  }
  @Test
  public void testBeeLineDriver_udf_translate() throws Exception {
    runTest("udf_translate.q");
  }
  @Test
  public void testBeeLineDriver_udf_trim() throws Exception {
    runTest("udf_trim.q");
  }
  @Test
  public void testBeeLineDriver_udf_ucase() throws Exception {
    runTest("udf_ucase.q");
  }
  @Test
  public void testBeeLineDriver_udf_unhex() throws Exception {
    runTest("udf_unhex.q");
  }
  @Test
  public void testBeeLineDriver_udf_union() throws Exception {
    runTest("udf_union.q");
  }
  @Test
  public void testBeeLineDriver_udf_unix_timestamp() throws Exception {
    runTest("udf_unix_timestamp.q");
  }
  @Test
  public void testBeeLineDriver_udf_upper() throws Exception {
    runTest("udf_upper.q");
  }
  @Test
  public void testBeeLineDriver_udf_var_pop() throws Exception {
    runTest("udf_var_pop.q");
  }
  @Test
  public void testBeeLineDriver_udf_var_samp() throws Exception {
    runTest("udf_var_samp.q");
  }
  @Test
  public void testBeeLineDriver_udf_variance() throws Exception {
    runTest("udf_variance.q");
  }
  @Test
  public void testBeeLineDriver_udf_weekofyear() throws Exception {
    runTest("udf_weekofyear.q");
  }
  @Test
  public void testBeeLineDriver_udf_when() throws Exception {
    runTest("udf_when.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath() throws Exception {
    runTest("udf_xpath.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_boolean() throws Exception {
    runTest("udf_xpath_boolean.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_double() throws Exception {
    runTest("udf_xpath_double.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_float() throws Exception {
    runTest("udf_xpath_float.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_int() throws Exception {
    runTest("udf_xpath_int.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_long() throws Exception {
    runTest("udf_xpath_long.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_short() throws Exception {
    runTest("udf_xpath_short.q");
  }
  @Test
  public void testBeeLineDriver_udf_xpath_string() throws Exception {
    runTest("udf_xpath_string.q");
  }
  @Test
  public void testBeeLineDriver_udtf_explode() throws Exception {
    runTest("udtf_explode.q");
  }
  @Test
  public void testBeeLineDriver_udtf_json_tuple() throws Exception {
    runTest("udtf_json_tuple.q");
  }
  @Test
  public void testBeeLineDriver_udtf_parse_url_tuple() throws Exception {
    runTest("udtf_parse_url_tuple.q");
  }
  @Test
  public void testBeeLineDriver_udtf_stack() throws Exception {
    runTest("udtf_stack.q");
  }
  @Test
  public void testBeeLineDriver_union() throws Exception {
    runTest("union.q");
  }
  @Test
  public void testBeeLineDriver_union10() throws Exception {
    runTest("union10.q");
  }
  @Test
  public void testBeeLineDriver_union11() throws Exception {
    runTest("union11.q");
  }
  @Test
  public void testBeeLineDriver_union12() throws Exception {
    runTest("union12.q");
  }
  @Test
  public void testBeeLineDriver_union13() throws Exception {
    runTest("union13.q");
  }
  @Test
  public void testBeeLineDriver_union14() throws Exception {
    runTest("union14.q");
  }
  @Test
  public void testBeeLineDriver_union15() throws Exception {
    runTest("union15.q");
  }
  @Test
  public void testBeeLineDriver_union16() throws Exception {
    runTest("union16.q");
  }
  @Test
  public void testBeeLineDriver_union17() throws Exception {
    runTest("union17.q");
  }
  @Test
  public void testBeeLineDriver_union18() throws Exception {
    runTest("union18.q");
  }
  @Test
  public void testBeeLineDriver_union19() throws Exception {
    runTest("union19.q");
  }
  @Test
  public void testBeeLineDriver_union2() throws Exception {
    runTest("union2.q");
  }
  @Test
  public void testBeeLineDriver_union20() throws Exception {
    runTest("union20.q");
  }
  @Test
  public void testBeeLineDriver_union21() throws Exception {
    runTest("union21.q");
  }
  @Test
  public void testBeeLineDriver_union22() throws Exception {
    runTest("union22.q");
  }
  @Test
  public void testBeeLineDriver_union23() throws Exception {
    runTest("union23.q");
  }
  @Test
  public void testBeeLineDriver_union24() throws Exception {
    runTest("union24.q");
  }
  @Test
  public void testBeeLineDriver_union25() throws Exception {
    runTest("union25.q");
  }
  @Test
  public void testBeeLineDriver_union26() throws Exception {
    runTest("union26.q");
  }
  @Test
  public void testBeeLineDriver_union27() throws Exception {
    runTest("union27.q");
  }
  @Test
  public void testBeeLineDriver_union28() throws Exception {
    runTest("union28.q");
  }
  @Test
  public void testBeeLineDriver_union29() throws Exception {
    runTest("union29.q");
  }
  @Test
  public void testBeeLineDriver_union3() throws Exception {
    runTest("union3.q");
  }
  @Test
  public void testBeeLineDriver_union30() throws Exception {
    runTest("union30.q");
  }
  @Test
  public void testBeeLineDriver_union31() throws Exception {
    runTest("union31.q");
  }
  @Test
  public void testBeeLineDriver_union32() throws Exception {
    runTest("union32.q");
  }
  @Test
  public void testBeeLineDriver_union4() throws Exception {
    runTest("union4.q");
  }
  @Test
  public void testBeeLineDriver_union5() throws Exception {
    runTest("union5.q");
  }
  @Test
  public void testBeeLineDriver_union6() throws Exception {
    runTest("union6.q");
  }
  @Test
  public void testBeeLineDriver_union7() throws Exception {
    runTest("union7.q");
  }
  @Test
  public void testBeeLineDriver_union8() throws Exception {
    runTest("union8.q");
  }
  @Test
  public void testBeeLineDriver_union9() throws Exception {
    runTest("union9.q");
  }
  @Test
  public void testBeeLineDriver_union_lateralview() throws Exception {
    runTest("union_lateralview.q");
  }
  @Test
  public void testBeeLineDriver_union_null() throws Exception {
    runTest("union_null.q");
  }
  @Test
  public void testBeeLineDriver_union_ppr() throws Exception {
    runTest("union_ppr.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_1() throws Exception {
    runTest("union_remove_1.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_10() throws Exception {
    runTest("union_remove_10.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_11() throws Exception {
    runTest("union_remove_11.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_12() throws Exception {
    runTest("union_remove_12.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_13() throws Exception {
    runTest("union_remove_13.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_14() throws Exception {
    runTest("union_remove_14.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_15() throws Exception {
    runTest("union_remove_15.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_16() throws Exception {
    runTest("union_remove_16.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_17() throws Exception {
    runTest("union_remove_17.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_18() throws Exception {
    runTest("union_remove_18.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_19() throws Exception {
    runTest("union_remove_19.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_2() throws Exception {
    runTest("union_remove_2.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_20() throws Exception {
    runTest("union_remove_20.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_21() throws Exception {
    runTest("union_remove_21.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_22() throws Exception {
    runTest("union_remove_22.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_23() throws Exception {
    runTest("union_remove_23.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_24() throws Exception {
    runTest("union_remove_24.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_3() throws Exception {
    runTest("union_remove_3.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_4() throws Exception {
    runTest("union_remove_4.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_5() throws Exception {
    runTest("union_remove_5.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_6() throws Exception {
    runTest("union_remove_6.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_7() throws Exception {
    runTest("union_remove_7.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_8() throws Exception {
    runTest("union_remove_8.q");
  }
  @Test
  public void testBeeLineDriver_union_remove_9() throws Exception {
    runTest("union_remove_9.q");
  }
  @Test
  public void testBeeLineDriver_union_script() throws Exception {
    runTest("union_script.q");
  }
  @Test
  public void testBeeLineDriver_union_view() throws Exception {
    runTest("union_view.q");
  }
  @Test
  public void testBeeLineDriver_uniquejoin() throws Exception {
    runTest("uniquejoin.q");
  }
  @Test
  public void testBeeLineDriver_updateAccessTime() throws Exception {
    runTest("updateAccessTime.q");
  }
  @Test
  public void testBeeLineDriver_view() throws Exception {
    runTest("view.q");
  }
  @Test
  public void testBeeLineDriver_virtual_column() throws Exception {
    runTest("virtual_column.q");
  }

}



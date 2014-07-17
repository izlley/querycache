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

public class TestNegativeCliDriver extends TestCase {

  private static QTestUtil qt;

  static {
    try {
      boolean miniMR = false;
      String hadoopVer;
      if ("".equals("miniMR"))
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

  public TestNegativeCliDriver(String name) {
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
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_add_partition_with_whitelist"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_addpart1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_concatenate_indexed_table"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_non_native"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_partition_invalidspec"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_partition_nodrop"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_partition_nodrop_table"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_partition_offline"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_rename_partition_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_rename_partition_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_rename_partition_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_table_wrong_regex"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure8"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_alter_view_failure9"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_altern1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_ambiguous_col"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_analyze"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_analyze1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_analyze_view"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_corrupt"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_insert1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_insert2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_insert3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_insert4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_multi7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_partspec1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_partspec2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_partspec3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_partspec4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_archive_partspec5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_fail_7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_authorization_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_autolocal1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bad_exec_hooks"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bad_indextype"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bad_sample_clause"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bucket_mapjoin_mismatch1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bucket_mapjoin_wrong_table_metadata_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_bucket_mapjoin_wrong_table_metadata_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_cachingprintstream"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clusterbydistributeby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clusterbyorderby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clusterbysortby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clustern1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clustern2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clustern3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_clustern4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_change_skewedcol_type1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_rename1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_rename2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_rename3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_rename4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_column_rename5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_partlvl_dp"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_partlvl_incorrect_num_keys"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_partlvl_invalid_values"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_partlvl_multiple_part_clause"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_tbllvl"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_tbllvl_complex_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_columnstats_tbllvl_incorrect_column"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_compare_double_bigint"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_compare_string_bigint"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_insert_outputformat"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_or_replace_view8"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_skewed_table_col_name_value_no_mismatch"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_skewed_table_dup_col_name"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_skewed_table_failure_invalid_col_name"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_table_failure1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_table_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_table_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_table_failure4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_table_wrong_regex"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_udaf_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_unknown_genericudf"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_unknown_udf_udaf"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure8"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_create_view_failure9"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_ctas"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_create_already_exists"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_create_invalid_name"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_drop_does_not_exist"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_drop_not_empty"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_drop_not_empty_restrict"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_database_switch_does_not_exist"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_ddltime"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_default_partition_name"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_deletejar"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_desc_failure1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_desc_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_desc_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_describe_xpath1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_describe_xpath2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_describe_xpath3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_describe_xpath4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_function_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_index_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_native_udf"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_partition_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_partition_filter_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_partition_filter_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_table_failure1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_table_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_table_failure3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_view_failure1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_drop_view_failure2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_duplicate_alias_in_transform"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_duplicate_alias_in_transform_schema"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_duplicate_insert1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_duplicate_insert2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_duplicate_insert3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part_max"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part_max_per_node"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dyn_part_merge"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_dynamic_partitions_with_whitelist"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_00_unsupported_schema"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_01_nonpart_over_loaded"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_02_all_part_over_overlap"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_03_nonpart_noncompat_colschema"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_04_nonpart_noncompat_colnumber"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_05_nonpart_noncompat_coltype"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_06_nonpart_noncompat_storage"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_07_nonpart_noncompat_ifof"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_08_nonpart_noncompat_serde"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_09_nonpart_noncompat_serdeparam"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_10_nonpart_noncompat_bucketing"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_11_nonpart_noncompat_sorting"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_12_nonnative_export"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_13_nonnative_import"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_14_nonpart_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_15_part_nonpart"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_16_part_noncompat_schema"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_17_part_spec_underspec"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_18_part_spec_missing"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_19_external_over_existing"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_20_managed_location_over_existing"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_21_part_managed_external"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_22_export_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_23_import_exist_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_24_import_part_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_exim_25_import_nonexist_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_external1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_external2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fetchtask_ioexception"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fileformat_bad_class"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fileformat_void_input"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fileformat_void_output"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fs_default_name1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_fs_default_name2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_genericFileFormat"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby2_map_skew_multi_distinct"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby2_multi_distinct"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby3_map_skew_multi_distinct"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby3_multi_distinct"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_cube1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_cube2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_id1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_sets1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_sets2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_sets3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_sets4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_grouping_sets5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_key"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_rollup1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_groupby_rollup2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_having1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_index_bitmap_no_map_aggr"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_index_compact_entry_limit"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_index_compact_size_limit"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_input1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_input2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_input4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_input41"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_input_part0_neg"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insert_into1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insert_into2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insert_into3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insert_into4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insert_view_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insertexternal1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_insertover_dynapart_ifnotexists"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_avg_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_from_binary_6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_cast_to_binary_6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_config1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_create_tbl1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_create_tbl2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_max_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_min_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_select_column"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_select_column_with_subquery"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_select_column_with_tablename"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_select_expression"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_std_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_stddev_samp_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_sum_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_t_alter1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_t_alter2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_t_create1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_t_create2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_t_transform"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_tbl_name"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_var_samp_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalid_variance_syntax"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_invalidate_view1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_join2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_join_nonexistent_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_joinneg"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lateral_view_alias"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lateral_view_join"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_line_terminator"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_exist_part_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_non_native"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_nonpart_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_part_authfail"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_part_nospec"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_stored_as_dirs"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_view_failure"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_wrong_fileformat"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_wrong_fileformat_rc_seq"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_wrong_fileformat_txt_seq"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_load_wrong_noof_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_local_mapred_error_cache"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lockneg1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lockneg2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lockneg3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lockneg4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_lockneg5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_merge_negative_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_merge_negative_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_no_matching_udf"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_nonkey_groupby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_nopart_insert"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_nopart_load"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_notable_alias3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_notable_alias4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_orderbysortby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_part_col_complex_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_part1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_part2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_part_no_drop"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl8"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_protectmode_tbl_no_drop"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_recursive_view"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_regex_col_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_regex_col_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_regex_col_groupby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_sample"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_script_broken_pipe1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_script_broken_pipe2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_script_broken_pipe3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_script_error"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_select_charliteral"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_select_udtf_alias"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_semijoin1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_semijoin2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_semijoin3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_semijoin4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_serde_regex"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_serde_regex2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_serde_regex3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_set_hiveconf_validation0"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_set_hiveconf_validation1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_columns1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_columns2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_columns3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_create_table_does_not_exist"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_create_table_index"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_partitions1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tableproperties1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tables_bad1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tables_bad2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tables_bad_db1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tables_bad_db2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tablestatus"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_show_tablestatus_not_existing_part"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_smb_bucketmapjoin"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_sortmerge_mapjoin_mismatch_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_split_sample_out_of_range"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_split_sample_wrong_format"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_stats_aggregator_error_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_stats_aggregator_error_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_stats_publisher_error_1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_stats_publisher_error_2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_strict_join"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_strict_orderby"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_strict_pruning"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_subq_insert"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_touch1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_touch2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udaf_invalid_place"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_array_contains_wrong1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_array_contains_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_assert_true"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_assert_true2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_case_type_wrong"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_case_type_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_case_type_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_coalesce"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_concat_ws_wrong1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_concat_ws_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_concat_ws_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_elt_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_elt_wrong_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_field_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_field_wrong_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong5"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong6"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_format_number_wrong7"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_function_does_not_implement_udf"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_if_not_bool"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_if_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_in"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_instr_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_instr_wrong_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_locate_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_locate_wrong_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_map_keys_arg_num"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_map_keys_arg_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_map_values_arg_num"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_map_values_arg_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_max"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_min"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_printf_wrong1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_printf_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_printf_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_printf_wrong4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_reflect_neg"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_size_wrong_args_len"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_size_wrong_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_sort_array_wrong1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_sort_array_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_sort_array_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_test_error"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_test_error_reduce"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_when_type_wrong"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_when_type_wrong2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udf_when_type_wrong3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udfnull"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_explode_not_supported1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_explode_not_supported2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_explode_not_supported3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_explode_not_supported4"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_invalid_place"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_not_supported1"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_not_supported2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_udtf_not_supported3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_union"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_union2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_union3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_uniquejoin"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_uniquejoin2"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_uniquejoin3"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_wrong_column_type"));
    suite.addTest(new TestNegativeCliDriver("testNegativeCliDriver_shutdown"));
    return suite;
  }

  /**
   * Dummy last test. This is only meant to shutdown qt
   */
  public void testNegativeCliDriver_shutdown() {
    System.out.println ("Cleaning up " + "TestNegativeCliDriver");
  }

  static String debugHint = "\nSee build/ql/tmp/hive.log, "
     + "or try \"ant test ... -Dtest.silent=false\" to get more logs.";

  public void testNegativeCliDriver_add_partition_with_whitelist() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "add_partition_with_whitelist.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/add_partition_with_whitelist.q");

      if (qt.shouldBeSkipped("add_partition_with_whitelist.q")) {
        System.out.println("Test add_partition_with_whitelist.q skipped");
        return;
      }

      qt.cliInit("add_partition_with_whitelist.q", false);
      int ecode = qt.executeClient("add_partition_with_whitelist.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("add_partition_with_whitelist.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "add_partition_with_whitelist.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "add_partition_with_whitelist.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_addpart1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "addpart1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/addpart1.q");

      if (qt.shouldBeSkipped("addpart1.q")) {
        System.out.println("Test addpart1.q skipped");
        return;
      }

      qt.cliInit("addpart1.q", false);
      int ecode = qt.executeClient("addpart1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("addpart1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "addpart1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "addpart1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_concatenate_indexed_table() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_concatenate_indexed_table.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_concatenate_indexed_table.q");

      if (qt.shouldBeSkipped("alter_concatenate_indexed_table.q")) {
        System.out.println("Test alter_concatenate_indexed_table.q skipped");
        return;
      }

      qt.cliInit("alter_concatenate_indexed_table.q", false);
      int ecode = qt.executeClient("alter_concatenate_indexed_table.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_concatenate_indexed_table.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_concatenate_indexed_table.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_concatenate_indexed_table.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_non_native() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_non_native.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_non_native.q");

      if (qt.shouldBeSkipped("alter_non_native.q")) {
        System.out.println("Test alter_non_native.q skipped");
        return;
      }

      qt.cliInit("alter_non_native.q", false);
      int ecode = qt.executeClient("alter_non_native.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_non_native.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_non_native.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_non_native.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_partition_invalidspec() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_partition_invalidspec.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_partition_invalidspec.q");

      if (qt.shouldBeSkipped("alter_partition_invalidspec.q")) {
        System.out.println("Test alter_partition_invalidspec.q skipped");
        return;
      }

      qt.cliInit("alter_partition_invalidspec.q", false);
      int ecode = qt.executeClient("alter_partition_invalidspec.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_partition_invalidspec.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_partition_invalidspec.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_partition_invalidspec.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_partition_nodrop() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_partition_nodrop.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_partition_nodrop.q");

      if (qt.shouldBeSkipped("alter_partition_nodrop.q")) {
        System.out.println("Test alter_partition_nodrop.q skipped");
        return;
      }

      qt.cliInit("alter_partition_nodrop.q", false);
      int ecode = qt.executeClient("alter_partition_nodrop.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_partition_nodrop.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_partition_nodrop.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_partition_nodrop.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_partition_nodrop_table() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_partition_nodrop_table.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_partition_nodrop_table.q");

      if (qt.shouldBeSkipped("alter_partition_nodrop_table.q")) {
        System.out.println("Test alter_partition_nodrop_table.q skipped");
        return;
      }

      qt.cliInit("alter_partition_nodrop_table.q", false);
      int ecode = qt.executeClient("alter_partition_nodrop_table.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_partition_nodrop_table.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_partition_nodrop_table.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_partition_nodrop_table.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_partition_offline() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_partition_offline.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_partition_offline.q");

      if (qt.shouldBeSkipped("alter_partition_offline.q")) {
        System.out.println("Test alter_partition_offline.q skipped");
        return;
      }

      qt.cliInit("alter_partition_offline.q", false);
      int ecode = qt.executeClient("alter_partition_offline.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_partition_offline.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_partition_offline.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_partition_offline.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_rename_partition_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_rename_partition_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_rename_partition_failure.q");

      if (qt.shouldBeSkipped("alter_rename_partition_failure.q")) {
        System.out.println("Test alter_rename_partition_failure.q skipped");
        return;
      }

      qt.cliInit("alter_rename_partition_failure.q", false);
      int ecode = qt.executeClient("alter_rename_partition_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_rename_partition_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_rename_partition_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_rename_partition_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_rename_partition_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_rename_partition_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_rename_partition_failure2.q");

      if (qt.shouldBeSkipped("alter_rename_partition_failure2.q")) {
        System.out.println("Test alter_rename_partition_failure2.q skipped");
        return;
      }

      qt.cliInit("alter_rename_partition_failure2.q", false);
      int ecode = qt.executeClient("alter_rename_partition_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_rename_partition_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_rename_partition_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_rename_partition_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_rename_partition_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_rename_partition_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_rename_partition_failure3.q");

      if (qt.shouldBeSkipped("alter_rename_partition_failure3.q")) {
        System.out.println("Test alter_rename_partition_failure3.q skipped");
        return;
      }

      qt.cliInit("alter_rename_partition_failure3.q", false);
      int ecode = qt.executeClient("alter_rename_partition_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_rename_partition_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_rename_partition_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_rename_partition_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_table_wrong_regex() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_table_wrong_regex.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_table_wrong_regex.q");

      if (qt.shouldBeSkipped("alter_table_wrong_regex.q")) {
        System.out.println("Test alter_table_wrong_regex.q skipped");
        return;
      }

      qt.cliInit("alter_table_wrong_regex.q", false);
      int ecode = qt.executeClient("alter_table_wrong_regex.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_table_wrong_regex.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_table_wrong_regex.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_table_wrong_regex.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure.q");

      if (qt.shouldBeSkipped("alter_view_failure.q")) {
        System.out.println("Test alter_view_failure.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure.q", false);
      int ecode = qt.executeClient("alter_view_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure2.q");

      if (qt.shouldBeSkipped("alter_view_failure2.q")) {
        System.out.println("Test alter_view_failure2.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure2.q", false);
      int ecode = qt.executeClient("alter_view_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure3.q");

      if (qt.shouldBeSkipped("alter_view_failure3.q")) {
        System.out.println("Test alter_view_failure3.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure3.q", false);
      int ecode = qt.executeClient("alter_view_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure4.q");

      if (qt.shouldBeSkipped("alter_view_failure4.q")) {
        System.out.println("Test alter_view_failure4.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure4.q", false);
      int ecode = qt.executeClient("alter_view_failure4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure5.q");

      if (qt.shouldBeSkipped("alter_view_failure5.q")) {
        System.out.println("Test alter_view_failure5.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure5.q", false);
      int ecode = qt.executeClient("alter_view_failure5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure6.q");

      if (qt.shouldBeSkipped("alter_view_failure6.q")) {
        System.out.println("Test alter_view_failure6.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure6.q", false);
      int ecode = qt.executeClient("alter_view_failure6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure7.q");

      if (qt.shouldBeSkipped("alter_view_failure7.q")) {
        System.out.println("Test alter_view_failure7.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure7.q", false);
      int ecode = qt.executeClient("alter_view_failure7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure8() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure8.q");

      if (qt.shouldBeSkipped("alter_view_failure8.q")) {
        System.out.println("Test alter_view_failure8.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure8.q", false);
      int ecode = qt.executeClient("alter_view_failure8.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure8.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_alter_view_failure9() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "alter_view_failure9.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/alter_view_failure9.q");

      if (qt.shouldBeSkipped("alter_view_failure9.q")) {
        System.out.println("Test alter_view_failure9.q skipped");
        return;
      }

      qt.cliInit("alter_view_failure9.q", false);
      int ecode = qt.executeClient("alter_view_failure9.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("alter_view_failure9.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "alter_view_failure9.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "alter_view_failure9.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_altern1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "altern1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/altern1.q");

      if (qt.shouldBeSkipped("altern1.q")) {
        System.out.println("Test altern1.q skipped");
        return;
      }

      qt.cliInit("altern1.q", false);
      int ecode = qt.executeClient("altern1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("altern1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "altern1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "altern1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_ambiguous_col() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "ambiguous_col.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/ambiguous_col.q");

      if (qt.shouldBeSkipped("ambiguous_col.q")) {
        System.out.println("Test ambiguous_col.q skipped");
        return;
      }

      qt.cliInit("ambiguous_col.q", false);
      int ecode = qt.executeClient("ambiguous_col.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("ambiguous_col.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "ambiguous_col.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "ambiguous_col.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_analyze() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "analyze.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/analyze.q");

      if (qt.shouldBeSkipped("analyze.q")) {
        System.out.println("Test analyze.q skipped");
        return;
      }

      qt.cliInit("analyze.q", false);
      int ecode = qt.executeClient("analyze.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("analyze.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "analyze.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "analyze.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_analyze1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "analyze1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/analyze1.q");

      if (qt.shouldBeSkipped("analyze1.q")) {
        System.out.println("Test analyze1.q skipped");
        return;
      }

      qt.cliInit("analyze1.q", false);
      int ecode = qt.executeClient("analyze1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("analyze1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "analyze1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "analyze1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_analyze_view() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "analyze_view.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/analyze_view.q");

      if (qt.shouldBeSkipped("analyze_view.q")) {
        System.out.println("Test analyze_view.q skipped");
        return;
      }

      qt.cliInit("analyze_view.q", false);
      int ecode = qt.executeClient("analyze_view.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("analyze_view.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "analyze_view.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "analyze_view.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive1.q");

      if (qt.shouldBeSkipped("archive1.q")) {
        System.out.println("Test archive1.q skipped");
        return;
      }

      qt.cliInit("archive1.q", false);
      int ecode = qt.executeClient("archive1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive2.q");

      if (qt.shouldBeSkipped("archive2.q")) {
        System.out.println("Test archive2.q skipped");
        return;
      }

      qt.cliInit("archive2.q", false);
      int ecode = qt.executeClient("archive2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive3.q");

      if (qt.shouldBeSkipped("archive3.q")) {
        System.out.println("Test archive3.q skipped");
        return;
      }

      qt.cliInit("archive3.q", false);
      int ecode = qt.executeClient("archive3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive4.q");

      if (qt.shouldBeSkipped("archive4.q")) {
        System.out.println("Test archive4.q skipped");
        return;
      }

      qt.cliInit("archive4.q", false);
      int ecode = qt.executeClient("archive4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive5.q");

      if (qt.shouldBeSkipped("archive5.q")) {
        System.out.println("Test archive5.q skipped");
        return;
      }

      qt.cliInit("archive5.q", false);
      int ecode = qt.executeClient("archive5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_corrupt() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_corrupt.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_corrupt.q");

      if (qt.shouldBeSkipped("archive_corrupt.q")) {
        System.out.println("Test archive_corrupt.q skipped");
        return;
      }

      qt.cliInit("archive_corrupt.q", false);
      int ecode = qt.executeClient("archive_corrupt.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_corrupt.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_corrupt.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_corrupt.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_insert1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_insert1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_insert1.q");

      if (qt.shouldBeSkipped("archive_insert1.q")) {
        System.out.println("Test archive_insert1.q skipped");
        return;
      }

      qt.cliInit("archive_insert1.q", false);
      int ecode = qt.executeClient("archive_insert1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_insert1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_insert1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_insert1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_insert2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_insert2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_insert2.q");

      if (qt.shouldBeSkipped("archive_insert2.q")) {
        System.out.println("Test archive_insert2.q skipped");
        return;
      }

      qt.cliInit("archive_insert2.q", false);
      int ecode = qt.executeClient("archive_insert2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_insert2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_insert2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_insert2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_insert3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_insert3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_insert3.q");

      if (qt.shouldBeSkipped("archive_insert3.q")) {
        System.out.println("Test archive_insert3.q skipped");
        return;
      }

      qt.cliInit("archive_insert3.q", false);
      int ecode = qt.executeClient("archive_insert3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_insert3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_insert3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_insert3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_insert4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_insert4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_insert4.q");

      if (qt.shouldBeSkipped("archive_insert4.q")) {
        System.out.println("Test archive_insert4.q skipped");
        return;
      }

      qt.cliInit("archive_insert4.q", false);
      int ecode = qt.executeClient("archive_insert4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_insert4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_insert4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_insert4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi1.q");

      if (qt.shouldBeSkipped("archive_multi1.q")) {
        System.out.println("Test archive_multi1.q skipped");
        return;
      }

      qt.cliInit("archive_multi1.q", false);
      int ecode = qt.executeClient("archive_multi1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi2.q");

      if (qt.shouldBeSkipped("archive_multi2.q")) {
        System.out.println("Test archive_multi2.q skipped");
        return;
      }

      qt.cliInit("archive_multi2.q", false);
      int ecode = qt.executeClient("archive_multi2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi3.q");

      if (qt.shouldBeSkipped("archive_multi3.q")) {
        System.out.println("Test archive_multi3.q skipped");
        return;
      }

      qt.cliInit("archive_multi3.q", false);
      int ecode = qt.executeClient("archive_multi3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi4.q");

      if (qt.shouldBeSkipped("archive_multi4.q")) {
        System.out.println("Test archive_multi4.q skipped");
        return;
      }

      qt.cliInit("archive_multi4.q", false);
      int ecode = qt.executeClient("archive_multi4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi5.q");

      if (qt.shouldBeSkipped("archive_multi5.q")) {
        System.out.println("Test archive_multi5.q skipped");
        return;
      }

      qt.cliInit("archive_multi5.q", false);
      int ecode = qt.executeClient("archive_multi5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi6.q");

      if (qt.shouldBeSkipped("archive_multi6.q")) {
        System.out.println("Test archive_multi6.q skipped");
        return;
      }

      qt.cliInit("archive_multi6.q", false);
      int ecode = qt.executeClient("archive_multi6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_multi7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_multi7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_multi7.q");

      if (qt.shouldBeSkipped("archive_multi7.q")) {
        System.out.println("Test archive_multi7.q skipped");
        return;
      }

      qt.cliInit("archive_multi7.q", false);
      int ecode = qt.executeClient("archive_multi7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_multi7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_multi7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_multi7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_partspec1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_partspec1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_partspec1.q");

      if (qt.shouldBeSkipped("archive_partspec1.q")) {
        System.out.println("Test archive_partspec1.q skipped");
        return;
      }

      qt.cliInit("archive_partspec1.q", false);
      int ecode = qt.executeClient("archive_partspec1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_partspec1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_partspec1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_partspec1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_partspec2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_partspec2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_partspec2.q");

      if (qt.shouldBeSkipped("archive_partspec2.q")) {
        System.out.println("Test archive_partspec2.q skipped");
        return;
      }

      qt.cliInit("archive_partspec2.q", false);
      int ecode = qt.executeClient("archive_partspec2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_partspec2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_partspec2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_partspec2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_partspec3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_partspec3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_partspec3.q");

      if (qt.shouldBeSkipped("archive_partspec3.q")) {
        System.out.println("Test archive_partspec3.q skipped");
        return;
      }

      qt.cliInit("archive_partspec3.q", false);
      int ecode = qt.executeClient("archive_partspec3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_partspec3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_partspec3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_partspec3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_partspec4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_partspec4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_partspec4.q");

      if (qt.shouldBeSkipped("archive_partspec4.q")) {
        System.out.println("Test archive_partspec4.q skipped");
        return;
      }

      qt.cliInit("archive_partspec4.q", false);
      int ecode = qt.executeClient("archive_partspec4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_partspec4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_partspec4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_partspec4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_archive_partspec5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "archive_partspec5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/archive_partspec5.q");

      if (qt.shouldBeSkipped("archive_partspec5.q")) {
        System.out.println("Test archive_partspec5.q skipped");
        return;
      }

      qt.cliInit("archive_partspec5.q", false);
      int ecode = qt.executeClient("archive_partspec5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("archive_partspec5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "archive_partspec5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "archive_partspec5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_1.q");

      if (qt.shouldBeSkipped("authorization_fail_1.q")) {
        System.out.println("Test authorization_fail_1.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_1.q", false);
      int ecode = qt.executeClient("authorization_fail_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_2.q");

      if (qt.shouldBeSkipped("authorization_fail_2.q")) {
        System.out.println("Test authorization_fail_2.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_2.q", false);
      int ecode = qt.executeClient("authorization_fail_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_3.q");

      if (qt.shouldBeSkipped("authorization_fail_3.q")) {
        System.out.println("Test authorization_fail_3.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_3.q", false);
      int ecode = qt.executeClient("authorization_fail_3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_4.q");

      if (qt.shouldBeSkipped("authorization_fail_4.q")) {
        System.out.println("Test authorization_fail_4.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_4.q", false);
      int ecode = qt.executeClient("authorization_fail_4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_5.q");

      if (qt.shouldBeSkipped("authorization_fail_5.q")) {
        System.out.println("Test authorization_fail_5.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_5.q", false);
      int ecode = qt.executeClient("authorization_fail_5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_6.q");

      if (qt.shouldBeSkipped("authorization_fail_6.q")) {
        System.out.println("Test authorization_fail_6.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_6.q", false);
      int ecode = qt.executeClient("authorization_fail_6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_fail_7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_fail_7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_fail_7.q");

      if (qt.shouldBeSkipped("authorization_fail_7.q")) {
        System.out.println("Test authorization_fail_7.q skipped");
        return;
      }

      qt.cliInit("authorization_fail_7.q", false);
      int ecode = qt.executeClient("authorization_fail_7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_fail_7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_fail_7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_fail_7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_authorization_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "authorization_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/authorization_part.q");

      if (qt.shouldBeSkipped("authorization_part.q")) {
        System.out.println("Test authorization_part.q skipped");
        return;
      }

      qt.cliInit("authorization_part.q", false);
      int ecode = qt.executeClient("authorization_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("authorization_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "authorization_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "authorization_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_autolocal1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "autolocal1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/autolocal1.q");

      if (qt.shouldBeSkipped("autolocal1.q")) {
        System.out.println("Test autolocal1.q skipped");
        return;
      }

      qt.cliInit("autolocal1.q", false);
      int ecode = qt.executeClient("autolocal1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("autolocal1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "autolocal1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "autolocal1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bad_exec_hooks() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bad_exec_hooks.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bad_exec_hooks.q");

      if (qt.shouldBeSkipped("bad_exec_hooks.q")) {
        System.out.println("Test bad_exec_hooks.q skipped");
        return;
      }

      qt.cliInit("bad_exec_hooks.q", false);
      int ecode = qt.executeClient("bad_exec_hooks.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bad_exec_hooks.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bad_exec_hooks.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bad_exec_hooks.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bad_indextype() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bad_indextype.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bad_indextype.q");

      if (qt.shouldBeSkipped("bad_indextype.q")) {
        System.out.println("Test bad_indextype.q skipped");
        return;
      }

      qt.cliInit("bad_indextype.q", false);
      int ecode = qt.executeClient("bad_indextype.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bad_indextype.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bad_indextype.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bad_indextype.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bad_sample_clause() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bad_sample_clause.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bad_sample_clause.q");

      if (qt.shouldBeSkipped("bad_sample_clause.q")) {
        System.out.println("Test bad_sample_clause.q skipped");
        return;
      }

      qt.cliInit("bad_sample_clause.q", false);
      int ecode = qt.executeClient("bad_sample_clause.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bad_sample_clause.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bad_sample_clause.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bad_sample_clause.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bucket_mapjoin_mismatch1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucket_mapjoin_mismatch1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bucket_mapjoin_mismatch1.q");

      if (qt.shouldBeSkipped("bucket_mapjoin_mismatch1.q")) {
        System.out.println("Test bucket_mapjoin_mismatch1.q skipped");
        return;
      }

      qt.cliInit("bucket_mapjoin_mismatch1.q", false);
      int ecode = qt.executeClient("bucket_mapjoin_mismatch1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bucket_mapjoin_mismatch1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucket_mapjoin_mismatch1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucket_mapjoin_mismatch1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bucket_mapjoin_wrong_table_metadata_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucket_mapjoin_wrong_table_metadata_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bucket_mapjoin_wrong_table_metadata_1.q");

      if (qt.shouldBeSkipped("bucket_mapjoin_wrong_table_metadata_1.q")) {
        System.out.println("Test bucket_mapjoin_wrong_table_metadata_1.q skipped");
        return;
      }

      qt.cliInit("bucket_mapjoin_wrong_table_metadata_1.q", false);
      int ecode = qt.executeClient("bucket_mapjoin_wrong_table_metadata_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bucket_mapjoin_wrong_table_metadata_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucket_mapjoin_wrong_table_metadata_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucket_mapjoin_wrong_table_metadata_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_bucket_mapjoin_wrong_table_metadata_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "bucket_mapjoin_wrong_table_metadata_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/bucket_mapjoin_wrong_table_metadata_2.q");

      if (qt.shouldBeSkipped("bucket_mapjoin_wrong_table_metadata_2.q")) {
        System.out.println("Test bucket_mapjoin_wrong_table_metadata_2.q skipped");
        return;
      }

      qt.cliInit("bucket_mapjoin_wrong_table_metadata_2.q", false);
      int ecode = qt.executeClient("bucket_mapjoin_wrong_table_metadata_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("bucket_mapjoin_wrong_table_metadata_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "bucket_mapjoin_wrong_table_metadata_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "bucket_mapjoin_wrong_table_metadata_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_cachingprintstream() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "cachingprintstream.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/cachingprintstream.q");

      if (qt.shouldBeSkipped("cachingprintstream.q")) {
        System.out.println("Test cachingprintstream.q skipped");
        return;
      }

      qt.cliInit("cachingprintstream.q", false);
      int ecode = qt.executeClient("cachingprintstream.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("cachingprintstream.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "cachingprintstream.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "cachingprintstream.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clusterbydistributeby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clusterbydistributeby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clusterbydistributeby.q");

      if (qt.shouldBeSkipped("clusterbydistributeby.q")) {
        System.out.println("Test clusterbydistributeby.q skipped");
        return;
      }

      qt.cliInit("clusterbydistributeby.q", false);
      int ecode = qt.executeClient("clusterbydistributeby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clusterbydistributeby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clusterbydistributeby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clusterbydistributeby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clusterbyorderby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clusterbyorderby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clusterbyorderby.q");

      if (qt.shouldBeSkipped("clusterbyorderby.q")) {
        System.out.println("Test clusterbyorderby.q skipped");
        return;
      }

      qt.cliInit("clusterbyorderby.q", false);
      int ecode = qt.executeClient("clusterbyorderby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clusterbyorderby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clusterbyorderby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clusterbyorderby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clusterbysortby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clusterbysortby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clusterbysortby.q");

      if (qt.shouldBeSkipped("clusterbysortby.q")) {
        System.out.println("Test clusterbysortby.q skipped");
        return;
      }

      qt.cliInit("clusterbysortby.q", false);
      int ecode = qt.executeClient("clusterbysortby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clusterbysortby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clusterbysortby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clusterbysortby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clustern1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clustern1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clustern1.q");

      if (qt.shouldBeSkipped("clustern1.q")) {
        System.out.println("Test clustern1.q skipped");
        return;
      }

      qt.cliInit("clustern1.q", false);
      int ecode = qt.executeClient("clustern1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clustern1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clustern1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clustern1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clustern2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clustern2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clustern2.q");

      if (qt.shouldBeSkipped("clustern2.q")) {
        System.out.println("Test clustern2.q skipped");
        return;
      }

      qt.cliInit("clustern2.q", false);
      int ecode = qt.executeClient("clustern2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clustern2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clustern2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clustern2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clustern3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clustern3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clustern3.q");

      if (qt.shouldBeSkipped("clustern3.q")) {
        System.out.println("Test clustern3.q skipped");
        return;
      }

      qt.cliInit("clustern3.q", false);
      int ecode = qt.executeClient("clustern3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clustern3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clustern3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clustern3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_clustern4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "clustern4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/clustern4.q");

      if (qt.shouldBeSkipped("clustern4.q")) {
        System.out.println("Test clustern4.q skipped");
        return;
      }

      qt.cliInit("clustern4.q", false);
      int ecode = qt.executeClient("clustern4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("clustern4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "clustern4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "clustern4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_change_skewedcol_type1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_change_skewedcol_type1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_change_skewedcol_type1.q");

      if (qt.shouldBeSkipped("column_change_skewedcol_type1.q")) {
        System.out.println("Test column_change_skewedcol_type1.q skipped");
        return;
      }

      qt.cliInit("column_change_skewedcol_type1.q", false);
      int ecode = qt.executeClient("column_change_skewedcol_type1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_change_skewedcol_type1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_change_skewedcol_type1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_change_skewedcol_type1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_rename1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_rename1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_rename1.q");

      if (qt.shouldBeSkipped("column_rename1.q")) {
        System.out.println("Test column_rename1.q skipped");
        return;
      }

      qt.cliInit("column_rename1.q", false);
      int ecode = qt.executeClient("column_rename1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_rename1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_rename1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_rename1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_rename2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_rename2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_rename2.q");

      if (qt.shouldBeSkipped("column_rename2.q")) {
        System.out.println("Test column_rename2.q skipped");
        return;
      }

      qt.cliInit("column_rename2.q", false);
      int ecode = qt.executeClient("column_rename2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_rename2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_rename2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_rename2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_rename3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_rename3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_rename3.q");

      if (qt.shouldBeSkipped("column_rename3.q")) {
        System.out.println("Test column_rename3.q skipped");
        return;
      }

      qt.cliInit("column_rename3.q", false);
      int ecode = qt.executeClient("column_rename3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_rename3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_rename3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_rename3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_rename4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_rename4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_rename4.q");

      if (qt.shouldBeSkipped("column_rename4.q")) {
        System.out.println("Test column_rename4.q skipped");
        return;
      }

      qt.cliInit("column_rename4.q", false);
      int ecode = qt.executeClient("column_rename4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_rename4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_rename4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_rename4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_column_rename5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "column_rename5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/column_rename5.q");

      if (qt.shouldBeSkipped("column_rename5.q")) {
        System.out.println("Test column_rename5.q skipped");
        return;
      }

      qt.cliInit("column_rename5.q", false);
      int ecode = qt.executeClient("column_rename5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("column_rename5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "column_rename5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "column_rename5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_partlvl_dp() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_partlvl_dp.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_partlvl_dp.q");

      if (qt.shouldBeSkipped("columnstats_partlvl_dp.q")) {
        System.out.println("Test columnstats_partlvl_dp.q skipped");
        return;
      }

      qt.cliInit("columnstats_partlvl_dp.q", false);
      int ecode = qt.executeClient("columnstats_partlvl_dp.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_partlvl_dp.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_partlvl_dp.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_partlvl_dp.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_partlvl_incorrect_num_keys() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_partlvl_incorrect_num_keys.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_partlvl_incorrect_num_keys.q");

      if (qt.shouldBeSkipped("columnstats_partlvl_incorrect_num_keys.q")) {
        System.out.println("Test columnstats_partlvl_incorrect_num_keys.q skipped");
        return;
      }

      qt.cliInit("columnstats_partlvl_incorrect_num_keys.q", false);
      int ecode = qt.executeClient("columnstats_partlvl_incorrect_num_keys.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_partlvl_incorrect_num_keys.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_partlvl_incorrect_num_keys.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_partlvl_incorrect_num_keys.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_partlvl_invalid_values() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_partlvl_invalid_values.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_partlvl_invalid_values.q");

      if (qt.shouldBeSkipped("columnstats_partlvl_invalid_values.q")) {
        System.out.println("Test columnstats_partlvl_invalid_values.q skipped");
        return;
      }

      qt.cliInit("columnstats_partlvl_invalid_values.q", false);
      int ecode = qt.executeClient("columnstats_partlvl_invalid_values.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_partlvl_invalid_values.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_partlvl_invalid_values.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_partlvl_invalid_values.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_partlvl_multiple_part_clause() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_partlvl_multiple_part_clause.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_partlvl_multiple_part_clause.q");

      if (qt.shouldBeSkipped("columnstats_partlvl_multiple_part_clause.q")) {
        System.out.println("Test columnstats_partlvl_multiple_part_clause.q skipped");
        return;
      }

      qt.cliInit("columnstats_partlvl_multiple_part_clause.q", false);
      int ecode = qt.executeClient("columnstats_partlvl_multiple_part_clause.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_partlvl_multiple_part_clause.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_partlvl_multiple_part_clause.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_partlvl_multiple_part_clause.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_tbllvl() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_tbllvl.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_tbllvl.q");

      if (qt.shouldBeSkipped("columnstats_tbllvl.q")) {
        System.out.println("Test columnstats_tbllvl.q skipped");
        return;
      }

      qt.cliInit("columnstats_tbllvl.q", false);
      int ecode = qt.executeClient("columnstats_tbllvl.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_tbllvl.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_tbllvl.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_tbllvl.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_tbllvl_complex_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_tbllvl_complex_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_tbllvl_complex_type.q");

      if (qt.shouldBeSkipped("columnstats_tbllvl_complex_type.q")) {
        System.out.println("Test columnstats_tbllvl_complex_type.q skipped");
        return;
      }

      qt.cliInit("columnstats_tbllvl_complex_type.q", false);
      int ecode = qt.executeClient("columnstats_tbllvl_complex_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_tbllvl_complex_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_tbllvl_complex_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_tbllvl_complex_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_columnstats_tbllvl_incorrect_column() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "columnstats_tbllvl_incorrect_column.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/columnstats_tbllvl_incorrect_column.q");

      if (qt.shouldBeSkipped("columnstats_tbllvl_incorrect_column.q")) {
        System.out.println("Test columnstats_tbllvl_incorrect_column.q skipped");
        return;
      }

      qt.cliInit("columnstats_tbllvl_incorrect_column.q", false);
      int ecode = qt.executeClient("columnstats_tbllvl_incorrect_column.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("columnstats_tbllvl_incorrect_column.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "columnstats_tbllvl_incorrect_column.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "columnstats_tbllvl_incorrect_column.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_compare_double_bigint() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "compare_double_bigint.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/compare_double_bigint.q");

      if (qt.shouldBeSkipped("compare_double_bigint.q")) {
        System.out.println("Test compare_double_bigint.q skipped");
        return;
      }

      qt.cliInit("compare_double_bigint.q", false);
      int ecode = qt.executeClient("compare_double_bigint.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("compare_double_bigint.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "compare_double_bigint.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "compare_double_bigint.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_compare_string_bigint() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "compare_string_bigint.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/compare_string_bigint.q");

      if (qt.shouldBeSkipped("compare_string_bigint.q")) {
        System.out.println("Test compare_string_bigint.q skipped");
        return;
      }

      qt.cliInit("compare_string_bigint.q", false);
      int ecode = qt.executeClient("compare_string_bigint.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("compare_string_bigint.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "compare_string_bigint.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "compare_string_bigint.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_insert_outputformat() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_insert_outputformat.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_insert_outputformat.q");

      if (qt.shouldBeSkipped("create_insert_outputformat.q")) {
        System.out.println("Test create_insert_outputformat.q skipped");
        return;
      }

      qt.cliInit("create_insert_outputformat.q", false);
      int ecode = qt.executeClient("create_insert_outputformat.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_insert_outputformat.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_insert_outputformat.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_insert_outputformat.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view1.q");

      if (qt.shouldBeSkipped("create_or_replace_view1.q")) {
        System.out.println("Test create_or_replace_view1.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view1.q", false);
      int ecode = qt.executeClient("create_or_replace_view1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view2.q");

      if (qt.shouldBeSkipped("create_or_replace_view2.q")) {
        System.out.println("Test create_or_replace_view2.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view2.q", false);
      int ecode = qt.executeClient("create_or_replace_view2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view3.q");

      if (qt.shouldBeSkipped("create_or_replace_view3.q")) {
        System.out.println("Test create_or_replace_view3.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view3.q", false);
      int ecode = qt.executeClient("create_or_replace_view3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view4.q");

      if (qt.shouldBeSkipped("create_or_replace_view4.q")) {
        System.out.println("Test create_or_replace_view4.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view4.q", false);
      int ecode = qt.executeClient("create_or_replace_view4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view5.q");

      if (qt.shouldBeSkipped("create_or_replace_view5.q")) {
        System.out.println("Test create_or_replace_view5.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view5.q", false);
      int ecode = qt.executeClient("create_or_replace_view5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view6.q");

      if (qt.shouldBeSkipped("create_or_replace_view6.q")) {
        System.out.println("Test create_or_replace_view6.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view6.q", false);
      int ecode = qt.executeClient("create_or_replace_view6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view7.q");

      if (qt.shouldBeSkipped("create_or_replace_view7.q")) {
        System.out.println("Test create_or_replace_view7.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view7.q", false);
      int ecode = qt.executeClient("create_or_replace_view7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_or_replace_view8() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_or_replace_view8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_or_replace_view8.q");

      if (qt.shouldBeSkipped("create_or_replace_view8.q")) {
        System.out.println("Test create_or_replace_view8.q skipped");
        return;
      }

      qt.cliInit("create_or_replace_view8.q", false);
      int ecode = qt.executeClient("create_or_replace_view8.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_or_replace_view8.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_or_replace_view8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_or_replace_view8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_skewed_table_col_name_value_no_mismatch() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_skewed_table_col_name_value_no_mismatch.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_skewed_table_col_name_value_no_mismatch.q");

      if (qt.shouldBeSkipped("create_skewed_table_col_name_value_no_mismatch.q")) {
        System.out.println("Test create_skewed_table_col_name_value_no_mismatch.q skipped");
        return;
      }

      qt.cliInit("create_skewed_table_col_name_value_no_mismatch.q", false);
      int ecode = qt.executeClient("create_skewed_table_col_name_value_no_mismatch.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_skewed_table_col_name_value_no_mismatch.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_skewed_table_col_name_value_no_mismatch.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_skewed_table_col_name_value_no_mismatch.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_skewed_table_dup_col_name() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_skewed_table_dup_col_name.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_skewed_table_dup_col_name.q");

      if (qt.shouldBeSkipped("create_skewed_table_dup_col_name.q")) {
        System.out.println("Test create_skewed_table_dup_col_name.q skipped");
        return;
      }

      qt.cliInit("create_skewed_table_dup_col_name.q", false);
      int ecode = qt.executeClient("create_skewed_table_dup_col_name.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_skewed_table_dup_col_name.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_skewed_table_dup_col_name.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_skewed_table_dup_col_name.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_skewed_table_failure_invalid_col_name() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_skewed_table_failure_invalid_col_name.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_skewed_table_failure_invalid_col_name.q");

      if (qt.shouldBeSkipped("create_skewed_table_failure_invalid_col_name.q")) {
        System.out.println("Test create_skewed_table_failure_invalid_col_name.q skipped");
        return;
      }

      qt.cliInit("create_skewed_table_failure_invalid_col_name.q", false);
      int ecode = qt.executeClient("create_skewed_table_failure_invalid_col_name.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_skewed_table_failure_invalid_col_name.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_skewed_table_failure_invalid_col_name.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_skewed_table_failure_invalid_col_name.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_table_failure1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_table_failure1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_table_failure1.q");

      if (qt.shouldBeSkipped("create_table_failure1.q")) {
        System.out.println("Test create_table_failure1.q skipped");
        return;
      }

      qt.cliInit("create_table_failure1.q", false);
      int ecode = qt.executeClient("create_table_failure1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_table_failure1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_table_failure1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_table_failure1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_table_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_table_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_table_failure2.q");

      if (qt.shouldBeSkipped("create_table_failure2.q")) {
        System.out.println("Test create_table_failure2.q skipped");
        return;
      }

      qt.cliInit("create_table_failure2.q", false);
      int ecode = qt.executeClient("create_table_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_table_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_table_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_table_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_table_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_table_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_table_failure3.q");

      if (qt.shouldBeSkipped("create_table_failure3.q")) {
        System.out.println("Test create_table_failure3.q skipped");
        return;
      }

      qt.cliInit("create_table_failure3.q", false);
      int ecode = qt.executeClient("create_table_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_table_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_table_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_table_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_table_failure4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_table_failure4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_table_failure4.q");

      if (qt.shouldBeSkipped("create_table_failure4.q")) {
        System.out.println("Test create_table_failure4.q skipped");
        return;
      }

      qt.cliInit("create_table_failure4.q", false);
      int ecode = qt.executeClient("create_table_failure4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_table_failure4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_table_failure4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_table_failure4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_table_wrong_regex() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_table_wrong_regex.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_table_wrong_regex.q");

      if (qt.shouldBeSkipped("create_table_wrong_regex.q")) {
        System.out.println("Test create_table_wrong_regex.q skipped");
        return;
      }

      qt.cliInit("create_table_wrong_regex.q", false);
      int ecode = qt.executeClient("create_table_wrong_regex.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_table_wrong_regex.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_table_wrong_regex.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_table_wrong_regex.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_udaf_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_udaf_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_udaf_failure.q");

      if (qt.shouldBeSkipped("create_udaf_failure.q")) {
        System.out.println("Test create_udaf_failure.q skipped");
        return;
      }

      qt.cliInit("create_udaf_failure.q", false);
      int ecode = qt.executeClient("create_udaf_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_udaf_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_udaf_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_udaf_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_unknown_genericudf() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_unknown_genericudf.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_unknown_genericudf.q");

      if (qt.shouldBeSkipped("create_unknown_genericudf.q")) {
        System.out.println("Test create_unknown_genericudf.q skipped");
        return;
      }

      qt.cliInit("create_unknown_genericudf.q", false);
      int ecode = qt.executeClient("create_unknown_genericudf.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_unknown_genericudf.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_unknown_genericudf.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_unknown_genericudf.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_unknown_udf_udaf() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_unknown_udf_udaf.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_unknown_udf_udaf.q");

      if (qt.shouldBeSkipped("create_unknown_udf_udaf.q")) {
        System.out.println("Test create_unknown_udf_udaf.q skipped");
        return;
      }

      qt.cliInit("create_unknown_udf_udaf.q", false);
      int ecode = qt.executeClient("create_unknown_udf_udaf.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_unknown_udf_udaf.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_unknown_udf_udaf.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_unknown_udf_udaf.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure1.q");

      if (qt.shouldBeSkipped("create_view_failure1.q")) {
        System.out.println("Test create_view_failure1.q skipped");
        return;
      }

      qt.cliInit("create_view_failure1.q", false);
      int ecode = qt.executeClient("create_view_failure1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure2.q");

      if (qt.shouldBeSkipped("create_view_failure2.q")) {
        System.out.println("Test create_view_failure2.q skipped");
        return;
      }

      qt.cliInit("create_view_failure2.q", false);
      int ecode = qt.executeClient("create_view_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure3.q");

      if (qt.shouldBeSkipped("create_view_failure3.q")) {
        System.out.println("Test create_view_failure3.q skipped");
        return;
      }

      qt.cliInit("create_view_failure3.q", false);
      int ecode = qt.executeClient("create_view_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure4.q");

      if (qt.shouldBeSkipped("create_view_failure4.q")) {
        System.out.println("Test create_view_failure4.q skipped");
        return;
      }

      qt.cliInit("create_view_failure4.q", false);
      int ecode = qt.executeClient("create_view_failure4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure5.q");

      if (qt.shouldBeSkipped("create_view_failure5.q")) {
        System.out.println("Test create_view_failure5.q skipped");
        return;
      }

      qt.cliInit("create_view_failure5.q", false);
      int ecode = qt.executeClient("create_view_failure5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure6.q");

      if (qt.shouldBeSkipped("create_view_failure6.q")) {
        System.out.println("Test create_view_failure6.q skipped");
        return;
      }

      qt.cliInit("create_view_failure6.q", false);
      int ecode = qt.executeClient("create_view_failure6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure7.q");

      if (qt.shouldBeSkipped("create_view_failure7.q")) {
        System.out.println("Test create_view_failure7.q skipped");
        return;
      }

      qt.cliInit("create_view_failure7.q", false);
      int ecode = qt.executeClient("create_view_failure7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure8() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure8.q");

      if (qt.shouldBeSkipped("create_view_failure8.q")) {
        System.out.println("Test create_view_failure8.q skipped");
        return;
      }

      qt.cliInit("create_view_failure8.q", false);
      int ecode = qt.executeClient("create_view_failure8.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure8.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_create_view_failure9() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "create_view_failure9.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/create_view_failure9.q");

      if (qt.shouldBeSkipped("create_view_failure9.q")) {
        System.out.println("Test create_view_failure9.q skipped");
        return;
      }

      qt.cliInit("create_view_failure9.q", false);
      int ecode = qt.executeClient("create_view_failure9.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("create_view_failure9.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "create_view_failure9.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "create_view_failure9.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_ctas() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "ctas.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/ctas.q");

      if (qt.shouldBeSkipped("ctas.q")) {
        System.out.println("Test ctas.q skipped");
        return;
      }

      qt.cliInit("ctas.q", false);
      int ecode = qt.executeClient("ctas.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("ctas.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "ctas.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "ctas.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_create_already_exists() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_create_already_exists.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_create_already_exists.q");

      if (qt.shouldBeSkipped("database_create_already_exists.q")) {
        System.out.println("Test database_create_already_exists.q skipped");
        return;
      }

      qt.cliInit("database_create_already_exists.q", false);
      int ecode = qt.executeClient("database_create_already_exists.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_create_already_exists.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_create_already_exists.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_create_already_exists.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_create_invalid_name() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_create_invalid_name.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_create_invalid_name.q");

      if (qt.shouldBeSkipped("database_create_invalid_name.q")) {
        System.out.println("Test database_create_invalid_name.q skipped");
        return;
      }

      qt.cliInit("database_create_invalid_name.q", false);
      int ecode = qt.executeClient("database_create_invalid_name.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_create_invalid_name.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_create_invalid_name.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_create_invalid_name.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_drop_does_not_exist() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_drop_does_not_exist.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_drop_does_not_exist.q");

      if (qt.shouldBeSkipped("database_drop_does_not_exist.q")) {
        System.out.println("Test database_drop_does_not_exist.q skipped");
        return;
      }

      qt.cliInit("database_drop_does_not_exist.q", false);
      int ecode = qt.executeClient("database_drop_does_not_exist.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_drop_does_not_exist.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_drop_does_not_exist.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_drop_does_not_exist.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_drop_not_empty() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_drop_not_empty.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_drop_not_empty.q");

      if (qt.shouldBeSkipped("database_drop_not_empty.q")) {
        System.out.println("Test database_drop_not_empty.q skipped");
        return;
      }

      qt.cliInit("database_drop_not_empty.q", false);
      int ecode = qt.executeClient("database_drop_not_empty.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_drop_not_empty.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_drop_not_empty.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_drop_not_empty.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_drop_not_empty_restrict() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_drop_not_empty_restrict.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_drop_not_empty_restrict.q");

      if (qt.shouldBeSkipped("database_drop_not_empty_restrict.q")) {
        System.out.println("Test database_drop_not_empty_restrict.q skipped");
        return;
      }

      qt.cliInit("database_drop_not_empty_restrict.q", false);
      int ecode = qt.executeClient("database_drop_not_empty_restrict.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_drop_not_empty_restrict.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_drop_not_empty_restrict.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_drop_not_empty_restrict.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_database_switch_does_not_exist() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "database_switch_does_not_exist.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/database_switch_does_not_exist.q");

      if (qt.shouldBeSkipped("database_switch_does_not_exist.q")) {
        System.out.println("Test database_switch_does_not_exist.q skipped");
        return;
      }

      qt.cliInit("database_switch_does_not_exist.q", false);
      int ecode = qt.executeClient("database_switch_does_not_exist.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("database_switch_does_not_exist.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "database_switch_does_not_exist.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "database_switch_does_not_exist.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_ddltime() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "ddltime.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/ddltime.q");

      if (qt.shouldBeSkipped("ddltime.q")) {
        System.out.println("Test ddltime.q skipped");
        return;
      }

      qt.cliInit("ddltime.q", false);
      int ecode = qt.executeClient("ddltime.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("ddltime.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "ddltime.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "ddltime.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_default_partition_name() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "default_partition_name.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/default_partition_name.q");

      if (qt.shouldBeSkipped("default_partition_name.q")) {
        System.out.println("Test default_partition_name.q skipped");
        return;
      }

      qt.cliInit("default_partition_name.q", false);
      int ecode = qt.executeClient("default_partition_name.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("default_partition_name.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "default_partition_name.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "default_partition_name.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_deletejar() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "deletejar.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/deletejar.q");

      if (qt.shouldBeSkipped("deletejar.q")) {
        System.out.println("Test deletejar.q skipped");
        return;
      }

      qt.cliInit("deletejar.q", false);
      int ecode = qt.executeClient("deletejar.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("deletejar.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "deletejar.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "deletejar.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_desc_failure1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "desc_failure1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/desc_failure1.q");

      if (qt.shouldBeSkipped("desc_failure1.q")) {
        System.out.println("Test desc_failure1.q skipped");
        return;
      }

      qt.cliInit("desc_failure1.q", false);
      int ecode = qt.executeClient("desc_failure1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("desc_failure1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "desc_failure1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "desc_failure1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_desc_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "desc_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/desc_failure2.q");

      if (qt.shouldBeSkipped("desc_failure2.q")) {
        System.out.println("Test desc_failure2.q skipped");
        return;
      }

      qt.cliInit("desc_failure2.q", false);
      int ecode = qt.executeClient("desc_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("desc_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "desc_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "desc_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_desc_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "desc_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/desc_failure3.q");

      if (qt.shouldBeSkipped("desc_failure3.q")) {
        System.out.println("Test desc_failure3.q skipped");
        return;
      }

      qt.cliInit("desc_failure3.q", false);
      int ecode = qt.executeClient("desc_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("desc_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "desc_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "desc_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_describe_xpath1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "describe_xpath1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/describe_xpath1.q");

      if (qt.shouldBeSkipped("describe_xpath1.q")) {
        System.out.println("Test describe_xpath1.q skipped");
        return;
      }

      qt.cliInit("describe_xpath1.q", false);
      int ecode = qt.executeClient("describe_xpath1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("describe_xpath1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "describe_xpath1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "describe_xpath1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_describe_xpath2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "describe_xpath2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/describe_xpath2.q");

      if (qt.shouldBeSkipped("describe_xpath2.q")) {
        System.out.println("Test describe_xpath2.q skipped");
        return;
      }

      qt.cliInit("describe_xpath2.q", false);
      int ecode = qt.executeClient("describe_xpath2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("describe_xpath2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "describe_xpath2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "describe_xpath2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_describe_xpath3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "describe_xpath3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/describe_xpath3.q");

      if (qt.shouldBeSkipped("describe_xpath3.q")) {
        System.out.println("Test describe_xpath3.q skipped");
        return;
      }

      qt.cliInit("describe_xpath3.q", false);
      int ecode = qt.executeClient("describe_xpath3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("describe_xpath3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "describe_xpath3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "describe_xpath3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_describe_xpath4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "describe_xpath4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/describe_xpath4.q");

      if (qt.shouldBeSkipped("describe_xpath4.q")) {
        System.out.println("Test describe_xpath4.q skipped");
        return;
      }

      qt.cliInit("describe_xpath4.q", false);
      int ecode = qt.executeClient("describe_xpath4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("describe_xpath4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "describe_xpath4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "describe_xpath4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_function_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_function_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_function_failure.q");

      if (qt.shouldBeSkipped("drop_function_failure.q")) {
        System.out.println("Test drop_function_failure.q skipped");
        return;
      }

      qt.cliInit("drop_function_failure.q", false);
      int ecode = qt.executeClient("drop_function_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_function_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_function_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_function_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_index_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_index_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_index_failure.q");

      if (qt.shouldBeSkipped("drop_index_failure.q")) {
        System.out.println("Test drop_index_failure.q skipped");
        return;
      }

      qt.cliInit("drop_index_failure.q", false);
      int ecode = qt.executeClient("drop_index_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_index_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_index_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_index_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_native_udf() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_native_udf.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_native_udf.q");

      if (qt.shouldBeSkipped("drop_native_udf.q")) {
        System.out.println("Test drop_native_udf.q skipped");
        return;
      }

      qt.cliInit("drop_native_udf.q", false);
      int ecode = qt.executeClient("drop_native_udf.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_native_udf.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_native_udf.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_native_udf.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_partition_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_partition_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_partition_failure.q");

      if (qt.shouldBeSkipped("drop_partition_failure.q")) {
        System.out.println("Test drop_partition_failure.q skipped");
        return;
      }

      qt.cliInit("drop_partition_failure.q", false);
      int ecode = qt.executeClient("drop_partition_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_partition_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_partition_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_partition_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_partition_filter_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_partition_filter_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_partition_filter_failure.q");

      if (qt.shouldBeSkipped("drop_partition_filter_failure.q")) {
        System.out.println("Test drop_partition_filter_failure.q skipped");
        return;
      }

      qt.cliInit("drop_partition_filter_failure.q", false);
      int ecode = qt.executeClient("drop_partition_filter_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_partition_filter_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_partition_filter_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_partition_filter_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_partition_filter_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_partition_filter_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_partition_filter_failure2.q");

      if (qt.shouldBeSkipped("drop_partition_filter_failure2.q")) {
        System.out.println("Test drop_partition_filter_failure2.q skipped");
        return;
      }

      qt.cliInit("drop_partition_filter_failure2.q", false);
      int ecode = qt.executeClient("drop_partition_filter_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_partition_filter_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_partition_filter_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_partition_filter_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_table_failure1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_table_failure1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_table_failure1.q");

      if (qt.shouldBeSkipped("drop_table_failure1.q")) {
        System.out.println("Test drop_table_failure1.q skipped");
        return;
      }

      qt.cliInit("drop_table_failure1.q", false);
      int ecode = qt.executeClient("drop_table_failure1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_table_failure1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_table_failure1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_table_failure1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_table_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_table_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_table_failure2.q");

      if (qt.shouldBeSkipped("drop_table_failure2.q")) {
        System.out.println("Test drop_table_failure2.q skipped");
        return;
      }

      qt.cliInit("drop_table_failure2.q", false);
      int ecode = qt.executeClient("drop_table_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_table_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_table_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_table_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_table_failure3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_table_failure3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_table_failure3.q");

      if (qt.shouldBeSkipped("drop_table_failure3.q")) {
        System.out.println("Test drop_table_failure3.q skipped");
        return;
      }

      qt.cliInit("drop_table_failure3.q", false);
      int ecode = qt.executeClient("drop_table_failure3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_table_failure3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_table_failure3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_table_failure3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_view_failure1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_view_failure1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_view_failure1.q");

      if (qt.shouldBeSkipped("drop_view_failure1.q")) {
        System.out.println("Test drop_view_failure1.q skipped");
        return;
      }

      qt.cliInit("drop_view_failure1.q", false);
      int ecode = qt.executeClient("drop_view_failure1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_view_failure1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_view_failure1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_view_failure1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_drop_view_failure2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "drop_view_failure2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/drop_view_failure2.q");

      if (qt.shouldBeSkipped("drop_view_failure2.q")) {
        System.out.println("Test drop_view_failure2.q skipped");
        return;
      }

      qt.cliInit("drop_view_failure2.q", false);
      int ecode = qt.executeClient("drop_view_failure2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("drop_view_failure2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "drop_view_failure2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "drop_view_failure2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_duplicate_alias_in_transform() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_alias_in_transform.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/duplicate_alias_in_transform.q");

      if (qt.shouldBeSkipped("duplicate_alias_in_transform.q")) {
        System.out.println("Test duplicate_alias_in_transform.q skipped");
        return;
      }

      qt.cliInit("duplicate_alias_in_transform.q", false);
      int ecode = qt.executeClient("duplicate_alias_in_transform.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("duplicate_alias_in_transform.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_alias_in_transform.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_alias_in_transform.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_duplicate_alias_in_transform_schema() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_alias_in_transform_schema.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/duplicate_alias_in_transform_schema.q");

      if (qt.shouldBeSkipped("duplicate_alias_in_transform_schema.q")) {
        System.out.println("Test duplicate_alias_in_transform_schema.q skipped");
        return;
      }

      qt.cliInit("duplicate_alias_in_transform_schema.q", false);
      int ecode = qt.executeClient("duplicate_alias_in_transform_schema.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("duplicate_alias_in_transform_schema.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_alias_in_transform_schema.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_alias_in_transform_schema.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_duplicate_insert1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_insert1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/duplicate_insert1.q");

      if (qt.shouldBeSkipped("duplicate_insert1.q")) {
        System.out.println("Test duplicate_insert1.q skipped");
        return;
      }

      qt.cliInit("duplicate_insert1.q", false);
      int ecode = qt.executeClient("duplicate_insert1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("duplicate_insert1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_insert1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_insert1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_duplicate_insert2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_insert2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/duplicate_insert2.q");

      if (qt.shouldBeSkipped("duplicate_insert2.q")) {
        System.out.println("Test duplicate_insert2.q skipped");
        return;
      }

      qt.cliInit("duplicate_insert2.q", false);
      int ecode = qt.executeClient("duplicate_insert2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("duplicate_insert2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_insert2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_insert2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_duplicate_insert3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "duplicate_insert3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/duplicate_insert3.q");

      if (qt.shouldBeSkipped("duplicate_insert3.q")) {
        System.out.println("Test duplicate_insert3.q skipped");
        return;
      }

      qt.cliInit("duplicate_insert3.q", false);
      int ecode = qt.executeClient("duplicate_insert3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("duplicate_insert3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "duplicate_insert3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "duplicate_insert3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part1.q");

      if (qt.shouldBeSkipped("dyn_part1.q")) {
        System.out.println("Test dyn_part1.q skipped");
        return;
      }

      qt.cliInit("dyn_part1.q", false);
      int ecode = qt.executeClient("dyn_part1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part2.q");

      if (qt.shouldBeSkipped("dyn_part2.q")) {
        System.out.println("Test dyn_part2.q skipped");
        return;
      }

      qt.cliInit("dyn_part2.q", false);
      int ecode = qt.executeClient("dyn_part2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part3.q");

      if (qt.shouldBeSkipped("dyn_part3.q")) {
        System.out.println("Test dyn_part3.q skipped");
        return;
      }

      qt.cliInit("dyn_part3.q", false);
      int ecode = qt.executeClient("dyn_part3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part4.q");

      if (qt.shouldBeSkipped("dyn_part4.q")) {
        System.out.println("Test dyn_part4.q skipped");
        return;
      }

      qt.cliInit("dyn_part4.q", false);
      int ecode = qt.executeClient("dyn_part4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part_max() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part_max.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part_max.q");

      if (qt.shouldBeSkipped("dyn_part_max.q")) {
        System.out.println("Test dyn_part_max.q skipped");
        return;
      }

      qt.cliInit("dyn_part_max.q", false);
      int ecode = qt.executeClient("dyn_part_max.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part_max.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part_max.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part_max.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part_max_per_node() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part_max_per_node.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part_max_per_node.q");

      if (qt.shouldBeSkipped("dyn_part_max_per_node.q")) {
        System.out.println("Test dyn_part_max_per_node.q skipped");
        return;
      }

      qt.cliInit("dyn_part_max_per_node.q", false);
      int ecode = qt.executeClient("dyn_part_max_per_node.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part_max_per_node.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part_max_per_node.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part_max_per_node.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dyn_part_merge() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dyn_part_merge.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dyn_part_merge.q");

      if (qt.shouldBeSkipped("dyn_part_merge.q")) {
        System.out.println("Test dyn_part_merge.q skipped");
        return;
      }

      qt.cliInit("dyn_part_merge.q", false);
      int ecode = qt.executeClient("dyn_part_merge.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dyn_part_merge.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dyn_part_merge.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dyn_part_merge.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_dynamic_partitions_with_whitelist() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "dynamic_partitions_with_whitelist.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/dynamic_partitions_with_whitelist.q");

      if (qt.shouldBeSkipped("dynamic_partitions_with_whitelist.q")) {
        System.out.println("Test dynamic_partitions_with_whitelist.q skipped");
        return;
      }

      qt.cliInit("dynamic_partitions_with_whitelist.q", false);
      int ecode = qt.executeClient("dynamic_partitions_with_whitelist.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("dynamic_partitions_with_whitelist.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "dynamic_partitions_with_whitelist.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "dynamic_partitions_with_whitelist.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_00_unsupported_schema() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_00_unsupported_schema.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_00_unsupported_schema.q");

      if (qt.shouldBeSkipped("exim_00_unsupported_schema.q")) {
        System.out.println("Test exim_00_unsupported_schema.q skipped");
        return;
      }

      qt.cliInit("exim_00_unsupported_schema.q", false);
      int ecode = qt.executeClient("exim_00_unsupported_schema.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_00_unsupported_schema.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_00_unsupported_schema.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_00_unsupported_schema.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_01_nonpart_over_loaded() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_01_nonpart_over_loaded.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_01_nonpart_over_loaded.q");

      if (qt.shouldBeSkipped("exim_01_nonpart_over_loaded.q")) {
        System.out.println("Test exim_01_nonpart_over_loaded.q skipped");
        return;
      }

      qt.cliInit("exim_01_nonpart_over_loaded.q", false);
      int ecode = qt.executeClient("exim_01_nonpart_over_loaded.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_01_nonpart_over_loaded.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_01_nonpart_over_loaded.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_01_nonpart_over_loaded.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_02_all_part_over_overlap() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_02_all_part_over_overlap.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_02_all_part_over_overlap.q");

      if (qt.shouldBeSkipped("exim_02_all_part_over_overlap.q")) {
        System.out.println("Test exim_02_all_part_over_overlap.q skipped");
        return;
      }

      qt.cliInit("exim_02_all_part_over_overlap.q", false);
      int ecode = qt.executeClient("exim_02_all_part_over_overlap.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_02_all_part_over_overlap.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_02_all_part_over_overlap.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_02_all_part_over_overlap.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_03_nonpart_noncompat_colschema() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_03_nonpart_noncompat_colschema.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_03_nonpart_noncompat_colschema.q");

      if (qt.shouldBeSkipped("exim_03_nonpart_noncompat_colschema.q")) {
        System.out.println("Test exim_03_nonpart_noncompat_colschema.q skipped");
        return;
      }

      qt.cliInit("exim_03_nonpart_noncompat_colschema.q", false);
      int ecode = qt.executeClient("exim_03_nonpart_noncompat_colschema.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_03_nonpart_noncompat_colschema.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_03_nonpart_noncompat_colschema.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_03_nonpart_noncompat_colschema.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_04_nonpart_noncompat_colnumber() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_04_nonpart_noncompat_colnumber.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_04_nonpart_noncompat_colnumber.q");

      if (qt.shouldBeSkipped("exim_04_nonpart_noncompat_colnumber.q")) {
        System.out.println("Test exim_04_nonpart_noncompat_colnumber.q skipped");
        return;
      }

      qt.cliInit("exim_04_nonpart_noncompat_colnumber.q", false);
      int ecode = qt.executeClient("exim_04_nonpart_noncompat_colnumber.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_04_nonpart_noncompat_colnumber.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_04_nonpart_noncompat_colnumber.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_04_nonpart_noncompat_colnumber.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_05_nonpart_noncompat_coltype() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_05_nonpart_noncompat_coltype.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_05_nonpart_noncompat_coltype.q");

      if (qt.shouldBeSkipped("exim_05_nonpart_noncompat_coltype.q")) {
        System.out.println("Test exim_05_nonpart_noncompat_coltype.q skipped");
        return;
      }

      qt.cliInit("exim_05_nonpart_noncompat_coltype.q", false);
      int ecode = qt.executeClient("exim_05_nonpart_noncompat_coltype.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_05_nonpart_noncompat_coltype.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_05_nonpart_noncompat_coltype.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_05_nonpart_noncompat_coltype.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_06_nonpart_noncompat_storage() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_06_nonpart_noncompat_storage.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_06_nonpart_noncompat_storage.q");

      if (qt.shouldBeSkipped("exim_06_nonpart_noncompat_storage.q")) {
        System.out.println("Test exim_06_nonpart_noncompat_storage.q skipped");
        return;
      }

      qt.cliInit("exim_06_nonpart_noncompat_storage.q", false);
      int ecode = qt.executeClient("exim_06_nonpart_noncompat_storage.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_06_nonpart_noncompat_storage.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_06_nonpart_noncompat_storage.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_06_nonpart_noncompat_storage.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_07_nonpart_noncompat_ifof() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_07_nonpart_noncompat_ifof.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_07_nonpart_noncompat_ifof.q");

      if (qt.shouldBeSkipped("exim_07_nonpart_noncompat_ifof.q")) {
        System.out.println("Test exim_07_nonpart_noncompat_ifof.q skipped");
        return;
      }

      qt.cliInit("exim_07_nonpart_noncompat_ifof.q", false);
      int ecode = qt.executeClient("exim_07_nonpart_noncompat_ifof.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_07_nonpart_noncompat_ifof.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_07_nonpart_noncompat_ifof.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_07_nonpart_noncompat_ifof.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_08_nonpart_noncompat_serde() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_08_nonpart_noncompat_serde.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_08_nonpart_noncompat_serde.q");

      if (qt.shouldBeSkipped("exim_08_nonpart_noncompat_serde.q")) {
        System.out.println("Test exim_08_nonpart_noncompat_serde.q skipped");
        return;
      }

      qt.cliInit("exim_08_nonpart_noncompat_serde.q", false);
      int ecode = qt.executeClient("exim_08_nonpart_noncompat_serde.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_08_nonpart_noncompat_serde.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_08_nonpart_noncompat_serde.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_08_nonpart_noncompat_serde.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_09_nonpart_noncompat_serdeparam() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_09_nonpart_noncompat_serdeparam.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_09_nonpart_noncompat_serdeparam.q");

      if (qt.shouldBeSkipped("exim_09_nonpart_noncompat_serdeparam.q")) {
        System.out.println("Test exim_09_nonpart_noncompat_serdeparam.q skipped");
        return;
      }

      qt.cliInit("exim_09_nonpart_noncompat_serdeparam.q", false);
      int ecode = qt.executeClient("exim_09_nonpart_noncompat_serdeparam.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_09_nonpart_noncompat_serdeparam.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_09_nonpart_noncompat_serdeparam.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_09_nonpart_noncompat_serdeparam.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_10_nonpart_noncompat_bucketing() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_10_nonpart_noncompat_bucketing.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_10_nonpart_noncompat_bucketing.q");

      if (qt.shouldBeSkipped("exim_10_nonpart_noncompat_bucketing.q")) {
        System.out.println("Test exim_10_nonpart_noncompat_bucketing.q skipped");
        return;
      }

      qt.cliInit("exim_10_nonpart_noncompat_bucketing.q", false);
      int ecode = qt.executeClient("exim_10_nonpart_noncompat_bucketing.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_10_nonpart_noncompat_bucketing.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_10_nonpart_noncompat_bucketing.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_10_nonpart_noncompat_bucketing.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_11_nonpart_noncompat_sorting() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_11_nonpart_noncompat_sorting.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_11_nonpart_noncompat_sorting.q");

      if (qt.shouldBeSkipped("exim_11_nonpart_noncompat_sorting.q")) {
        System.out.println("Test exim_11_nonpart_noncompat_sorting.q skipped");
        return;
      }

      qt.cliInit("exim_11_nonpart_noncompat_sorting.q", false);
      int ecode = qt.executeClient("exim_11_nonpart_noncompat_sorting.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_11_nonpart_noncompat_sorting.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_11_nonpart_noncompat_sorting.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_11_nonpart_noncompat_sorting.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_12_nonnative_export() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_12_nonnative_export.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_12_nonnative_export.q");

      if (qt.shouldBeSkipped("exim_12_nonnative_export.q")) {
        System.out.println("Test exim_12_nonnative_export.q skipped");
        return;
      }

      qt.cliInit("exim_12_nonnative_export.q", false);
      int ecode = qt.executeClient("exim_12_nonnative_export.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_12_nonnative_export.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_12_nonnative_export.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_12_nonnative_export.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_13_nonnative_import() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_13_nonnative_import.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_13_nonnative_import.q");

      if (qt.shouldBeSkipped("exim_13_nonnative_import.q")) {
        System.out.println("Test exim_13_nonnative_import.q skipped");
        return;
      }

      qt.cliInit("exim_13_nonnative_import.q", false);
      int ecode = qt.executeClient("exim_13_nonnative_import.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_13_nonnative_import.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_13_nonnative_import.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_13_nonnative_import.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_14_nonpart_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_14_nonpart_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_14_nonpart_part.q");

      if (qt.shouldBeSkipped("exim_14_nonpart_part.q")) {
        System.out.println("Test exim_14_nonpart_part.q skipped");
        return;
      }

      qt.cliInit("exim_14_nonpart_part.q", false);
      int ecode = qt.executeClient("exim_14_nonpart_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_14_nonpart_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_14_nonpart_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_14_nonpart_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_15_part_nonpart() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_15_part_nonpart.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_15_part_nonpart.q");

      if (qt.shouldBeSkipped("exim_15_part_nonpart.q")) {
        System.out.println("Test exim_15_part_nonpart.q skipped");
        return;
      }

      qt.cliInit("exim_15_part_nonpart.q", false);
      int ecode = qt.executeClient("exim_15_part_nonpart.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_15_part_nonpart.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_15_part_nonpart.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_15_part_nonpart.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_16_part_noncompat_schema() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_16_part_noncompat_schema.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_16_part_noncompat_schema.q");

      if (qt.shouldBeSkipped("exim_16_part_noncompat_schema.q")) {
        System.out.println("Test exim_16_part_noncompat_schema.q skipped");
        return;
      }

      qt.cliInit("exim_16_part_noncompat_schema.q", false);
      int ecode = qt.executeClient("exim_16_part_noncompat_schema.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_16_part_noncompat_schema.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_16_part_noncompat_schema.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_16_part_noncompat_schema.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_17_part_spec_underspec() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_17_part_spec_underspec.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_17_part_spec_underspec.q");

      if (qt.shouldBeSkipped("exim_17_part_spec_underspec.q")) {
        System.out.println("Test exim_17_part_spec_underspec.q skipped");
        return;
      }

      qt.cliInit("exim_17_part_spec_underspec.q", false);
      int ecode = qt.executeClient("exim_17_part_spec_underspec.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_17_part_spec_underspec.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_17_part_spec_underspec.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_17_part_spec_underspec.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_18_part_spec_missing() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_18_part_spec_missing.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_18_part_spec_missing.q");

      if (qt.shouldBeSkipped("exim_18_part_spec_missing.q")) {
        System.out.println("Test exim_18_part_spec_missing.q skipped");
        return;
      }

      qt.cliInit("exim_18_part_spec_missing.q", false);
      int ecode = qt.executeClient("exim_18_part_spec_missing.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_18_part_spec_missing.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_18_part_spec_missing.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_18_part_spec_missing.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_19_external_over_existing() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_19_external_over_existing.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_19_external_over_existing.q");

      if (qt.shouldBeSkipped("exim_19_external_over_existing.q")) {
        System.out.println("Test exim_19_external_over_existing.q skipped");
        return;
      }

      qt.cliInit("exim_19_external_over_existing.q", false);
      int ecode = qt.executeClient("exim_19_external_over_existing.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_19_external_over_existing.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_19_external_over_existing.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_19_external_over_existing.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_20_managed_location_over_existing() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_20_managed_location_over_existing.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_20_managed_location_over_existing.q");

      if (qt.shouldBeSkipped("exim_20_managed_location_over_existing.q")) {
        System.out.println("Test exim_20_managed_location_over_existing.q skipped");
        return;
      }

      qt.cliInit("exim_20_managed_location_over_existing.q", false);
      int ecode = qt.executeClient("exim_20_managed_location_over_existing.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_20_managed_location_over_existing.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_20_managed_location_over_existing.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_20_managed_location_over_existing.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_21_part_managed_external() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_21_part_managed_external.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_21_part_managed_external.q");

      if (qt.shouldBeSkipped("exim_21_part_managed_external.q")) {
        System.out.println("Test exim_21_part_managed_external.q skipped");
        return;
      }

      qt.cliInit("exim_21_part_managed_external.q", false);
      int ecode = qt.executeClient("exim_21_part_managed_external.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_21_part_managed_external.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_21_part_managed_external.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_21_part_managed_external.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_22_export_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_22_export_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_22_export_authfail.q");

      if (qt.shouldBeSkipped("exim_22_export_authfail.q")) {
        System.out.println("Test exim_22_export_authfail.q skipped");
        return;
      }

      qt.cliInit("exim_22_export_authfail.q", false);
      int ecode = qt.executeClient("exim_22_export_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_22_export_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_22_export_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_22_export_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_23_import_exist_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_23_import_exist_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_23_import_exist_authfail.q");

      if (qt.shouldBeSkipped("exim_23_import_exist_authfail.q")) {
        System.out.println("Test exim_23_import_exist_authfail.q skipped");
        return;
      }

      qt.cliInit("exim_23_import_exist_authfail.q", false);
      int ecode = qt.executeClient("exim_23_import_exist_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_23_import_exist_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_23_import_exist_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_23_import_exist_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_24_import_part_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_24_import_part_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_24_import_part_authfail.q");

      if (qt.shouldBeSkipped("exim_24_import_part_authfail.q")) {
        System.out.println("Test exim_24_import_part_authfail.q skipped");
        return;
      }

      qt.cliInit("exim_24_import_part_authfail.q", false);
      int ecode = qt.executeClient("exim_24_import_part_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_24_import_part_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_24_import_part_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_24_import_part_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_exim_25_import_nonexist_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "exim_25_import_nonexist_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/exim_25_import_nonexist_authfail.q");

      if (qt.shouldBeSkipped("exim_25_import_nonexist_authfail.q")) {
        System.out.println("Test exim_25_import_nonexist_authfail.q skipped");
        return;
      }

      qt.cliInit("exim_25_import_nonexist_authfail.q", false);
      int ecode = qt.executeClient("exim_25_import_nonexist_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("exim_25_import_nonexist_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "exim_25_import_nonexist_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "exim_25_import_nonexist_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_external1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "external1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/external1.q");

      if (qt.shouldBeSkipped("external1.q")) {
        System.out.println("Test external1.q skipped");
        return;
      }

      qt.cliInit("external1.q", false);
      int ecode = qt.executeClient("external1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("external1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "external1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "external1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_external2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "external2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/external2.q");

      if (qt.shouldBeSkipped("external2.q")) {
        System.out.println("Test external2.q skipped");
        return;
      }

      qt.cliInit("external2.q", false);
      int ecode = qt.executeClient("external2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("external2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "external2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "external2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fetchtask_ioexception() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fetchtask_ioexception.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fetchtask_ioexception.q");

      if (qt.shouldBeSkipped("fetchtask_ioexception.q")) {
        System.out.println("Test fetchtask_ioexception.q skipped");
        return;
      }

      qt.cliInit("fetchtask_ioexception.q", false);
      int ecode = qt.executeClient("fetchtask_ioexception.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fetchtask_ioexception.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fetchtask_ioexception.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fetchtask_ioexception.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fileformat_bad_class() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fileformat_bad_class.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fileformat_bad_class.q");

      if (qt.shouldBeSkipped("fileformat_bad_class.q")) {
        System.out.println("Test fileformat_bad_class.q skipped");
        return;
      }

      qt.cliInit("fileformat_bad_class.q", false);
      int ecode = qt.executeClient("fileformat_bad_class.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fileformat_bad_class.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fileformat_bad_class.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fileformat_bad_class.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fileformat_void_input() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fileformat_void_input.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fileformat_void_input.q");

      if (qt.shouldBeSkipped("fileformat_void_input.q")) {
        System.out.println("Test fileformat_void_input.q skipped");
        return;
      }

      qt.cliInit("fileformat_void_input.q", false);
      int ecode = qt.executeClient("fileformat_void_input.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fileformat_void_input.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fileformat_void_input.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fileformat_void_input.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fileformat_void_output() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fileformat_void_output.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fileformat_void_output.q");

      if (qt.shouldBeSkipped("fileformat_void_output.q")) {
        System.out.println("Test fileformat_void_output.q skipped");
        return;
      }

      qt.cliInit("fileformat_void_output.q", false);
      int ecode = qt.executeClient("fileformat_void_output.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fileformat_void_output.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fileformat_void_output.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fileformat_void_output.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fs_default_name1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fs_default_name1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fs_default_name1.q");

      if (qt.shouldBeSkipped("fs_default_name1.q")) {
        System.out.println("Test fs_default_name1.q skipped");
        return;
      }

      qt.cliInit("fs_default_name1.q", false);
      int ecode = qt.executeClient("fs_default_name1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fs_default_name1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fs_default_name1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fs_default_name1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_fs_default_name2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "fs_default_name2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/fs_default_name2.q");

      if (qt.shouldBeSkipped("fs_default_name2.q")) {
        System.out.println("Test fs_default_name2.q skipped");
        return;
      }

      qt.cliInit("fs_default_name2.q", false);
      int ecode = qt.executeClient("fs_default_name2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("fs_default_name2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "fs_default_name2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "fs_default_name2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_genericFileFormat() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "genericFileFormat.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/genericFileFormat.q");

      if (qt.shouldBeSkipped("genericFileFormat.q")) {
        System.out.println("Test genericFileFormat.q skipped");
        return;
      }

      qt.cliInit("genericFileFormat.q", false);
      int ecode = qt.executeClient("genericFileFormat.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("genericFileFormat.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "genericFileFormat.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "genericFileFormat.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby2_map_skew_multi_distinct() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby2_map_skew_multi_distinct.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby2_map_skew_multi_distinct.q");

      if (qt.shouldBeSkipped("groupby2_map_skew_multi_distinct.q")) {
        System.out.println("Test groupby2_map_skew_multi_distinct.q skipped");
        return;
      }

      qt.cliInit("groupby2_map_skew_multi_distinct.q", false);
      int ecode = qt.executeClient("groupby2_map_skew_multi_distinct.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby2_map_skew_multi_distinct.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby2_map_skew_multi_distinct.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby2_map_skew_multi_distinct.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby2_multi_distinct() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby2_multi_distinct.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby2_multi_distinct.q");

      if (qt.shouldBeSkipped("groupby2_multi_distinct.q")) {
        System.out.println("Test groupby2_multi_distinct.q skipped");
        return;
      }

      qt.cliInit("groupby2_multi_distinct.q", false);
      int ecode = qt.executeClient("groupby2_multi_distinct.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby2_multi_distinct.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby2_multi_distinct.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby2_multi_distinct.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby3_map_skew_multi_distinct() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby3_map_skew_multi_distinct.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby3_map_skew_multi_distinct.q");

      if (qt.shouldBeSkipped("groupby3_map_skew_multi_distinct.q")) {
        System.out.println("Test groupby3_map_skew_multi_distinct.q skipped");
        return;
      }

      qt.cliInit("groupby3_map_skew_multi_distinct.q", false);
      int ecode = qt.executeClient("groupby3_map_skew_multi_distinct.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby3_map_skew_multi_distinct.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby3_map_skew_multi_distinct.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby3_map_skew_multi_distinct.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby3_multi_distinct() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby3_multi_distinct.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby3_multi_distinct.q");

      if (qt.shouldBeSkipped("groupby3_multi_distinct.q")) {
        System.out.println("Test groupby3_multi_distinct.q skipped");
        return;
      }

      qt.cliInit("groupby3_multi_distinct.q", false);
      int ecode = qt.executeClient("groupby3_multi_distinct.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby3_multi_distinct.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby3_multi_distinct.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby3_multi_distinct.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_cube1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_cube1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_cube1.q");

      if (qt.shouldBeSkipped("groupby_cube1.q")) {
        System.out.println("Test groupby_cube1.q skipped");
        return;
      }

      qt.cliInit("groupby_cube1.q", false);
      int ecode = qt.executeClient("groupby_cube1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_cube1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_cube1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_cube1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_cube2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_cube2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_cube2.q");

      if (qt.shouldBeSkipped("groupby_cube2.q")) {
        System.out.println("Test groupby_cube2.q skipped");
        return;
      }

      qt.cliInit("groupby_cube2.q", false);
      int ecode = qt.executeClient("groupby_cube2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_cube2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_cube2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_cube2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_id1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_id1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_id1.q");

      if (qt.shouldBeSkipped("groupby_grouping_id1.q")) {
        System.out.println("Test groupby_grouping_id1.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_id1.q", false);
      int ecode = qt.executeClient("groupby_grouping_id1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_id1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_id1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_id1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_sets1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_sets1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_sets1.q");

      if (qt.shouldBeSkipped("groupby_grouping_sets1.q")) {
        System.out.println("Test groupby_grouping_sets1.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_sets1.q", false);
      int ecode = qt.executeClient("groupby_grouping_sets1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_sets1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_sets1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_sets1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_sets2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_sets2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_sets2.q");

      if (qt.shouldBeSkipped("groupby_grouping_sets2.q")) {
        System.out.println("Test groupby_grouping_sets2.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_sets2.q", false);
      int ecode = qt.executeClient("groupby_grouping_sets2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_sets2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_sets2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_sets2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_sets3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_sets3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_sets3.q");

      if (qt.shouldBeSkipped("groupby_grouping_sets3.q")) {
        System.out.println("Test groupby_grouping_sets3.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_sets3.q", false);
      int ecode = qt.executeClient("groupby_grouping_sets3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_sets3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_sets3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_sets3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_sets4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_sets4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_sets4.q");

      if (qt.shouldBeSkipped("groupby_grouping_sets4.q")) {
        System.out.println("Test groupby_grouping_sets4.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_sets4.q", false);
      int ecode = qt.executeClient("groupby_grouping_sets4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_sets4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_sets4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_sets4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_grouping_sets5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_grouping_sets5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_grouping_sets5.q");

      if (qt.shouldBeSkipped("groupby_grouping_sets5.q")) {
        System.out.println("Test groupby_grouping_sets5.q skipped");
        return;
      }

      qt.cliInit("groupby_grouping_sets5.q", false);
      int ecode = qt.executeClient("groupby_grouping_sets5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_grouping_sets5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_grouping_sets5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_grouping_sets5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_key() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_key.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_key.q");

      if (qt.shouldBeSkipped("groupby_key.q")) {
        System.out.println("Test groupby_key.q skipped");
        return;
      }

      qt.cliInit("groupby_key.q", false);
      int ecode = qt.executeClient("groupby_key.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_key.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_key.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_key.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_rollup1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_rollup1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_rollup1.q");

      if (qt.shouldBeSkipped("groupby_rollup1.q")) {
        System.out.println("Test groupby_rollup1.q skipped");
        return;
      }

      qt.cliInit("groupby_rollup1.q", false);
      int ecode = qt.executeClient("groupby_rollup1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_rollup1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_rollup1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_rollup1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_groupby_rollup2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "groupby_rollup2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/groupby_rollup2.q");

      if (qt.shouldBeSkipped("groupby_rollup2.q")) {
        System.out.println("Test groupby_rollup2.q skipped");
        return;
      }

      qt.cliInit("groupby_rollup2.q", false);
      int ecode = qt.executeClient("groupby_rollup2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("groupby_rollup2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "groupby_rollup2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "groupby_rollup2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_having1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "having1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/having1.q");

      if (qt.shouldBeSkipped("having1.q")) {
        System.out.println("Test having1.q skipped");
        return;
      }

      qt.cliInit("having1.q", false);
      int ecode = qt.executeClient("having1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("having1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "having1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "having1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_index_bitmap_no_map_aggr() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "index_bitmap_no_map_aggr.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/index_bitmap_no_map_aggr.q");

      if (qt.shouldBeSkipped("index_bitmap_no_map_aggr.q")) {
        System.out.println("Test index_bitmap_no_map_aggr.q skipped");
        return;
      }

      qt.cliInit("index_bitmap_no_map_aggr.q", false);
      int ecode = qt.executeClient("index_bitmap_no_map_aggr.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("index_bitmap_no_map_aggr.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "index_bitmap_no_map_aggr.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "index_bitmap_no_map_aggr.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_index_compact_entry_limit() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "index_compact_entry_limit.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/index_compact_entry_limit.q");

      if (qt.shouldBeSkipped("index_compact_entry_limit.q")) {
        System.out.println("Test index_compact_entry_limit.q skipped");
        return;
      }

      qt.cliInit("index_compact_entry_limit.q", false);
      int ecode = qt.executeClient("index_compact_entry_limit.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("index_compact_entry_limit.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "index_compact_entry_limit.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "index_compact_entry_limit.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_index_compact_size_limit() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "index_compact_size_limit.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/index_compact_size_limit.q");

      if (qt.shouldBeSkipped("index_compact_size_limit.q")) {
        System.out.println("Test index_compact_size_limit.q skipped");
        return;
      }

      qt.cliInit("index_compact_size_limit.q", false);
      int ecode = qt.executeClient("index_compact_size_limit.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("index_compact_size_limit.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "index_compact_size_limit.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "index_compact_size_limit.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_input1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/input1.q");

      if (qt.shouldBeSkipped("input1.q")) {
        System.out.println("Test input1.q skipped");
        return;
      }

      qt.cliInit("input1.q", false);
      int ecode = qt.executeClient("input1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("input1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
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

  public void testNegativeCliDriver_input2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/input2.q");

      if (qt.shouldBeSkipped("input2.q")) {
        System.out.println("Test input2.q skipped");
        return;
      }

      qt.cliInit("input2.q", false);
      int ecode = qt.executeClient("input2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("input2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
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

  public void testNegativeCliDriver_input4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/input4.q");

      if (qt.shouldBeSkipped("input4.q")) {
        System.out.println("Test input4.q skipped");
        return;
      }

      qt.cliInit("input4.q", false);
      int ecode = qt.executeClient("input4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("input4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
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

  public void testNegativeCliDriver_input41() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input41.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/input41.q");

      if (qt.shouldBeSkipped("input41.q")) {
        System.out.println("Test input41.q skipped");
        return;
      }

      qt.cliInit("input41.q", false);
      int ecode = qt.executeClient("input41.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("input41.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input41.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input41.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_input_part0_neg() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "input_part0_neg.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/input_part0_neg.q");

      if (qt.shouldBeSkipped("input_part0_neg.q")) {
        System.out.println("Test input_part0_neg.q skipped");
        return;
      }

      qt.cliInit("input_part0_neg.q", false);
      int ecode = qt.executeClient("input_part0_neg.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("input_part0_neg.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "input_part0_neg.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "input_part0_neg.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insert_into1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_into1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insert_into1.q");

      if (qt.shouldBeSkipped("insert_into1.q")) {
        System.out.println("Test insert_into1.q skipped");
        return;
      }

      qt.cliInit("insert_into1.q", false);
      int ecode = qt.executeClient("insert_into1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insert_into1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_into1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_into1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insert_into2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_into2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insert_into2.q");

      if (qt.shouldBeSkipped("insert_into2.q")) {
        System.out.println("Test insert_into2.q skipped");
        return;
      }

      qt.cliInit("insert_into2.q", false);
      int ecode = qt.executeClient("insert_into2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insert_into2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_into2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_into2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insert_into3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_into3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insert_into3.q");

      if (qt.shouldBeSkipped("insert_into3.q")) {
        System.out.println("Test insert_into3.q skipped");
        return;
      }

      qt.cliInit("insert_into3.q", false);
      int ecode = qt.executeClient("insert_into3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insert_into3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_into3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_into3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insert_into4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_into4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insert_into4.q");

      if (qt.shouldBeSkipped("insert_into4.q")) {
        System.out.println("Test insert_into4.q skipped");
        return;
      }

      qt.cliInit("insert_into4.q", false);
      int ecode = qt.executeClient("insert_into4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insert_into4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_into4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_into4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insert_view_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insert_view_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insert_view_failure.q");

      if (qt.shouldBeSkipped("insert_view_failure.q")) {
        System.out.println("Test insert_view_failure.q skipped");
        return;
      }

      qt.cliInit("insert_view_failure.q", false);
      int ecode = qt.executeClient("insert_view_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insert_view_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insert_view_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insert_view_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insertexternal1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insertexternal1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insertexternal1.q");

      if (qt.shouldBeSkipped("insertexternal1.q")) {
        System.out.println("Test insertexternal1.q skipped");
        return;
      }

      qt.cliInit("insertexternal1.q", false);
      int ecode = qt.executeClient("insertexternal1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insertexternal1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insertexternal1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insertexternal1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_insertover_dynapart_ifnotexists() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "insertover_dynapart_ifnotexists.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/insertover_dynapart_ifnotexists.q");

      if (qt.shouldBeSkipped("insertover_dynapart_ifnotexists.q")) {
        System.out.println("Test insertover_dynapart_ifnotexists.q skipped");
        return;
      }

      qt.cliInit("insertover_dynapart_ifnotexists.q", false);
      int ecode = qt.executeClient("insertover_dynapart_ifnotexists.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("insertover_dynapart_ifnotexists.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "insertover_dynapart_ifnotexists.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "insertover_dynapart_ifnotexists.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_avg_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_avg_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_avg_syntax.q");

      if (qt.shouldBeSkipped("invalid_avg_syntax.q")) {
        System.out.println("Test invalid_avg_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_avg_syntax.q", false);
      int ecode = qt.executeClient("invalid_avg_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_avg_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_avg_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_avg_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_1.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_1.q")) {
        System.out.println("Test invalid_cast_from_binary_1.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_1.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_2.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_2.q")) {
        System.out.println("Test invalid_cast_from_binary_2.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_2.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_3.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_3.q")) {
        System.out.println("Test invalid_cast_from_binary_3.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_3.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_4.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_4.q")) {
        System.out.println("Test invalid_cast_from_binary_4.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_4.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_5.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_5.q")) {
        System.out.println("Test invalid_cast_from_binary_5.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_5.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_from_binary_6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_from_binary_6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_from_binary_6.q");

      if (qt.shouldBeSkipped("invalid_cast_from_binary_6.q")) {
        System.out.println("Test invalid_cast_from_binary_6.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_from_binary_6.q", false);
      int ecode = qt.executeClient("invalid_cast_from_binary_6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_from_binary_6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_from_binary_6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_from_binary_6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_1.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_1.q")) {
        System.out.println("Test invalid_cast_to_binary_1.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_1.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_2.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_2.q")) {
        System.out.println("Test invalid_cast_to_binary_2.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_2.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_3.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_3.q")) {
        System.out.println("Test invalid_cast_to_binary_3.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_3.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_4.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_4.q")) {
        System.out.println("Test invalid_cast_to_binary_4.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_4.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_5.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_5.q")) {
        System.out.println("Test invalid_cast_to_binary_5.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_5.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_cast_to_binary_6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_cast_to_binary_6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_cast_to_binary_6.q");

      if (qt.shouldBeSkipped("invalid_cast_to_binary_6.q")) {
        System.out.println("Test invalid_cast_to_binary_6.q skipped");
        return;
      }

      qt.cliInit("invalid_cast_to_binary_6.q", false);
      int ecode = qt.executeClient("invalid_cast_to_binary_6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_cast_to_binary_6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_cast_to_binary_6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_cast_to_binary_6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_config1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_config1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_config1.q");

      if (qt.shouldBeSkipped("invalid_config1.q")) {
        System.out.println("Test invalid_config1.q skipped");
        return;
      }

      qt.cliInit("invalid_config1.q", false);
      int ecode = qt.executeClient("invalid_config1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_config1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_config1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_config1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_create_tbl1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_create_tbl1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_create_tbl1.q");

      if (qt.shouldBeSkipped("invalid_create_tbl1.q")) {
        System.out.println("Test invalid_create_tbl1.q skipped");
        return;
      }

      qt.cliInit("invalid_create_tbl1.q", false);
      int ecode = qt.executeClient("invalid_create_tbl1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_create_tbl1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_create_tbl1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_create_tbl1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_create_tbl2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_create_tbl2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_create_tbl2.q");

      if (qt.shouldBeSkipped("invalid_create_tbl2.q")) {
        System.out.println("Test invalid_create_tbl2.q skipped");
        return;
      }

      qt.cliInit("invalid_create_tbl2.q", false);
      int ecode = qt.executeClient("invalid_create_tbl2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_create_tbl2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_create_tbl2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_create_tbl2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_max_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_max_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_max_syntax.q");

      if (qt.shouldBeSkipped("invalid_max_syntax.q")) {
        System.out.println("Test invalid_max_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_max_syntax.q", false);
      int ecode = qt.executeClient("invalid_max_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_max_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_max_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_max_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_min_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_min_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_min_syntax.q");

      if (qt.shouldBeSkipped("invalid_min_syntax.q")) {
        System.out.println("Test invalid_min_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_min_syntax.q", false);
      int ecode = qt.executeClient("invalid_min_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_min_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_min_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_min_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_select_column() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_select_column.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_select_column.q");

      if (qt.shouldBeSkipped("invalid_select_column.q")) {
        System.out.println("Test invalid_select_column.q skipped");
        return;
      }

      qt.cliInit("invalid_select_column.q", false);
      int ecode = qt.executeClient("invalid_select_column.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_select_column.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_select_column.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_select_column.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_select_column_with_subquery() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_select_column_with_subquery.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_select_column_with_subquery.q");

      if (qt.shouldBeSkipped("invalid_select_column_with_subquery.q")) {
        System.out.println("Test invalid_select_column_with_subquery.q skipped");
        return;
      }

      qt.cliInit("invalid_select_column_with_subquery.q", false);
      int ecode = qt.executeClient("invalid_select_column_with_subquery.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_select_column_with_subquery.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_select_column_with_subquery.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_select_column_with_subquery.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_select_column_with_tablename() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_select_column_with_tablename.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_select_column_with_tablename.q");

      if (qt.shouldBeSkipped("invalid_select_column_with_tablename.q")) {
        System.out.println("Test invalid_select_column_with_tablename.q skipped");
        return;
      }

      qt.cliInit("invalid_select_column_with_tablename.q", false);
      int ecode = qt.executeClient("invalid_select_column_with_tablename.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_select_column_with_tablename.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_select_column_with_tablename.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_select_column_with_tablename.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_select_expression() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_select_expression.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_select_expression.q");

      if (qt.shouldBeSkipped("invalid_select_expression.q")) {
        System.out.println("Test invalid_select_expression.q skipped");
        return;
      }

      qt.cliInit("invalid_select_expression.q", false);
      int ecode = qt.executeClient("invalid_select_expression.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_select_expression.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_select_expression.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_select_expression.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_std_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_std_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_std_syntax.q");

      if (qt.shouldBeSkipped("invalid_std_syntax.q")) {
        System.out.println("Test invalid_std_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_std_syntax.q", false);
      int ecode = qt.executeClient("invalid_std_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_std_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_std_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_std_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_stddev_samp_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_stddev_samp_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_stddev_samp_syntax.q");

      if (qt.shouldBeSkipped("invalid_stddev_samp_syntax.q")) {
        System.out.println("Test invalid_stddev_samp_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_stddev_samp_syntax.q", false);
      int ecode = qt.executeClient("invalid_stddev_samp_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_stddev_samp_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_stddev_samp_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_stddev_samp_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_sum_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_sum_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_sum_syntax.q");

      if (qt.shouldBeSkipped("invalid_sum_syntax.q")) {
        System.out.println("Test invalid_sum_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_sum_syntax.q", false);
      int ecode = qt.executeClient("invalid_sum_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_sum_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_sum_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_sum_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_t_alter1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_t_alter1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_t_alter1.q");

      if (qt.shouldBeSkipped("invalid_t_alter1.q")) {
        System.out.println("Test invalid_t_alter1.q skipped");
        return;
      }

      qt.cliInit("invalid_t_alter1.q", false);
      int ecode = qt.executeClient("invalid_t_alter1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_t_alter1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_t_alter1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_t_alter1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_t_alter2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_t_alter2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_t_alter2.q");

      if (qt.shouldBeSkipped("invalid_t_alter2.q")) {
        System.out.println("Test invalid_t_alter2.q skipped");
        return;
      }

      qt.cliInit("invalid_t_alter2.q", false);
      int ecode = qt.executeClient("invalid_t_alter2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_t_alter2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_t_alter2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_t_alter2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_t_create1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_t_create1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_t_create1.q");

      if (qt.shouldBeSkipped("invalid_t_create1.q")) {
        System.out.println("Test invalid_t_create1.q skipped");
        return;
      }

      qt.cliInit("invalid_t_create1.q", false);
      int ecode = qt.executeClient("invalid_t_create1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_t_create1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_t_create1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_t_create1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_t_create2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_t_create2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_t_create2.q");

      if (qt.shouldBeSkipped("invalid_t_create2.q")) {
        System.out.println("Test invalid_t_create2.q skipped");
        return;
      }

      qt.cliInit("invalid_t_create2.q", false);
      int ecode = qt.executeClient("invalid_t_create2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_t_create2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_t_create2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_t_create2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_t_transform() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_t_transform.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_t_transform.q");

      if (qt.shouldBeSkipped("invalid_t_transform.q")) {
        System.out.println("Test invalid_t_transform.q skipped");
        return;
      }

      qt.cliInit("invalid_t_transform.q", false);
      int ecode = qt.executeClient("invalid_t_transform.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_t_transform.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_t_transform.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_t_transform.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_tbl_name() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_tbl_name.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_tbl_name.q");

      if (qt.shouldBeSkipped("invalid_tbl_name.q")) {
        System.out.println("Test invalid_tbl_name.q skipped");
        return;
      }

      qt.cliInit("invalid_tbl_name.q", false);
      int ecode = qt.executeClient("invalid_tbl_name.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_tbl_name.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_tbl_name.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_tbl_name.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_var_samp_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_var_samp_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_var_samp_syntax.q");

      if (qt.shouldBeSkipped("invalid_var_samp_syntax.q")) {
        System.out.println("Test invalid_var_samp_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_var_samp_syntax.q", false);
      int ecode = qt.executeClient("invalid_var_samp_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_var_samp_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_var_samp_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_var_samp_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalid_variance_syntax() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalid_variance_syntax.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalid_variance_syntax.q");

      if (qt.shouldBeSkipped("invalid_variance_syntax.q")) {
        System.out.println("Test invalid_variance_syntax.q skipped");
        return;
      }

      qt.cliInit("invalid_variance_syntax.q", false);
      int ecode = qt.executeClient("invalid_variance_syntax.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalid_variance_syntax.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalid_variance_syntax.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalid_variance_syntax.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_invalidate_view1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "invalidate_view1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/invalidate_view1.q");

      if (qt.shouldBeSkipped("invalidate_view1.q")) {
        System.out.println("Test invalidate_view1.q skipped");
        return;
      }

      qt.cliInit("invalidate_view1.q", false);
      int ecode = qt.executeClient("invalidate_view1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("invalidate_view1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "invalidate_view1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "invalidate_view1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_join2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/join2.q");

      if (qt.shouldBeSkipped("join2.q")) {
        System.out.println("Test join2.q skipped");
        return;
      }

      qt.cliInit("join2.q", false);
      int ecode = qt.executeClient("join2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("join2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
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

  public void testNegativeCliDriver_join_nonexistent_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "join_nonexistent_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/join_nonexistent_part.q");

      if (qt.shouldBeSkipped("join_nonexistent_part.q")) {
        System.out.println("Test join_nonexistent_part.q skipped");
        return;
      }

      qt.cliInit("join_nonexistent_part.q", false);
      int ecode = qt.executeClient("join_nonexistent_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("join_nonexistent_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "join_nonexistent_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "join_nonexistent_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_joinneg() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "joinneg.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/joinneg.q");

      if (qt.shouldBeSkipped("joinneg.q")) {
        System.out.println("Test joinneg.q skipped");
        return;
      }

      qt.cliInit("joinneg.q", false);
      int ecode = qt.executeClient("joinneg.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("joinneg.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "joinneg.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "joinneg.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lateral_view_alias() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lateral_view_alias.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lateral_view_alias.q");

      if (qt.shouldBeSkipped("lateral_view_alias.q")) {
        System.out.println("Test lateral_view_alias.q skipped");
        return;
      }

      qt.cliInit("lateral_view_alias.q", false);
      int ecode = qt.executeClient("lateral_view_alias.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lateral_view_alias.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lateral_view_alias.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lateral_view_alias.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lateral_view_join() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lateral_view_join.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lateral_view_join.q");

      if (qt.shouldBeSkipped("lateral_view_join.q")) {
        System.out.println("Test lateral_view_join.q skipped");
        return;
      }

      qt.cliInit("lateral_view_join.q", false);
      int ecode = qt.executeClient("lateral_view_join.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lateral_view_join.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lateral_view_join.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lateral_view_join.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_line_terminator() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "line_terminator.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/line_terminator.q");

      if (qt.shouldBeSkipped("line_terminator.q")) {
        System.out.println("Test line_terminator.q skipped");
        return;
      }

      qt.cliInit("line_terminator.q", false);
      int ecode = qt.executeClient("line_terminator.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("line_terminator.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "line_terminator.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "line_terminator.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_exist_part_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_exist_part_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_exist_part_authfail.q");

      if (qt.shouldBeSkipped("load_exist_part_authfail.q")) {
        System.out.println("Test load_exist_part_authfail.q skipped");
        return;
      }

      qt.cliInit("load_exist_part_authfail.q", false);
      int ecode = qt.executeClient("load_exist_part_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_exist_part_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_exist_part_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_exist_part_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_non_native() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_non_native.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_non_native.q");

      if (qt.shouldBeSkipped("load_non_native.q")) {
        System.out.println("Test load_non_native.q skipped");
        return;
      }

      qt.cliInit("load_non_native.q", false);
      int ecode = qt.executeClient("load_non_native.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_non_native.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_non_native.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_non_native.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_nonpart_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_nonpart_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_nonpart_authfail.q");

      if (qt.shouldBeSkipped("load_nonpart_authfail.q")) {
        System.out.println("Test load_nonpart_authfail.q skipped");
        return;
      }

      qt.cliInit("load_nonpart_authfail.q", false);
      int ecode = qt.executeClient("load_nonpart_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_nonpart_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_nonpart_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_nonpart_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_part_authfail() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_part_authfail.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_part_authfail.q");

      if (qt.shouldBeSkipped("load_part_authfail.q")) {
        System.out.println("Test load_part_authfail.q skipped");
        return;
      }

      qt.cliInit("load_part_authfail.q", false);
      int ecode = qt.executeClient("load_part_authfail.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_part_authfail.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_part_authfail.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_part_authfail.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_part_nospec() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_part_nospec.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_part_nospec.q");

      if (qt.shouldBeSkipped("load_part_nospec.q")) {
        System.out.println("Test load_part_nospec.q skipped");
        return;
      }

      qt.cliInit("load_part_nospec.q", false);
      int ecode = qt.executeClient("load_part_nospec.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_part_nospec.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_part_nospec.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_part_nospec.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_stored_as_dirs() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_stored_as_dirs.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_stored_as_dirs.q");

      if (qt.shouldBeSkipped("load_stored_as_dirs.q")) {
        System.out.println("Test load_stored_as_dirs.q skipped");
        return;
      }

      qt.cliInit("load_stored_as_dirs.q", false);
      int ecode = qt.executeClient("load_stored_as_dirs.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_stored_as_dirs.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_stored_as_dirs.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_stored_as_dirs.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_view_failure() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_view_failure.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_view_failure.q");

      if (qt.shouldBeSkipped("load_view_failure.q")) {
        System.out.println("Test load_view_failure.q skipped");
        return;
      }

      qt.cliInit("load_view_failure.q", false);
      int ecode = qt.executeClient("load_view_failure.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_view_failure.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_view_failure.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_view_failure.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_wrong_fileformat() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_wrong_fileformat.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_wrong_fileformat.q");

      if (qt.shouldBeSkipped("load_wrong_fileformat.q")) {
        System.out.println("Test load_wrong_fileformat.q skipped");
        return;
      }

      qt.cliInit("load_wrong_fileformat.q", false);
      int ecode = qt.executeClient("load_wrong_fileformat.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_wrong_fileformat.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_wrong_fileformat.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_wrong_fileformat.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_wrong_fileformat_rc_seq() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_wrong_fileformat_rc_seq.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_wrong_fileformat_rc_seq.q");

      if (qt.shouldBeSkipped("load_wrong_fileformat_rc_seq.q")) {
        System.out.println("Test load_wrong_fileformat_rc_seq.q skipped");
        return;
      }

      qt.cliInit("load_wrong_fileformat_rc_seq.q", false);
      int ecode = qt.executeClient("load_wrong_fileformat_rc_seq.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_wrong_fileformat_rc_seq.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_wrong_fileformat_rc_seq.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_wrong_fileformat_rc_seq.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_wrong_fileformat_txt_seq() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_wrong_fileformat_txt_seq.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_wrong_fileformat_txt_seq.q");

      if (qt.shouldBeSkipped("load_wrong_fileformat_txt_seq.q")) {
        System.out.println("Test load_wrong_fileformat_txt_seq.q skipped");
        return;
      }

      qt.cliInit("load_wrong_fileformat_txt_seq.q", false);
      int ecode = qt.executeClient("load_wrong_fileformat_txt_seq.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_wrong_fileformat_txt_seq.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_wrong_fileformat_txt_seq.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_wrong_fileformat_txt_seq.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_load_wrong_noof_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "load_wrong_noof_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/load_wrong_noof_part.q");

      if (qt.shouldBeSkipped("load_wrong_noof_part.q")) {
        System.out.println("Test load_wrong_noof_part.q skipped");
        return;
      }

      qt.cliInit("load_wrong_noof_part.q", false);
      int ecode = qt.executeClient("load_wrong_noof_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("load_wrong_noof_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "load_wrong_noof_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "load_wrong_noof_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_local_mapred_error_cache() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "local_mapred_error_cache.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/local_mapred_error_cache.q");

      if (qt.shouldBeSkipped("local_mapred_error_cache.q")) {
        System.out.println("Test local_mapred_error_cache.q skipped");
        return;
      }

      qt.cliInit("local_mapred_error_cache.q", false);
      int ecode = qt.executeClient("local_mapred_error_cache.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("local_mapred_error_cache.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "local_mapred_error_cache.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "local_mapred_error_cache.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lockneg1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lockneg1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lockneg1.q");

      if (qt.shouldBeSkipped("lockneg1.q")) {
        System.out.println("Test lockneg1.q skipped");
        return;
      }

      qt.cliInit("lockneg1.q", false);
      int ecode = qt.executeClient("lockneg1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lockneg1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lockneg1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lockneg1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lockneg2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lockneg2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lockneg2.q");

      if (qt.shouldBeSkipped("lockneg2.q")) {
        System.out.println("Test lockneg2.q skipped");
        return;
      }

      qt.cliInit("lockneg2.q", false);
      int ecode = qt.executeClient("lockneg2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lockneg2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lockneg2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lockneg2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lockneg3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lockneg3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lockneg3.q");

      if (qt.shouldBeSkipped("lockneg3.q")) {
        System.out.println("Test lockneg3.q skipped");
        return;
      }

      qt.cliInit("lockneg3.q", false);
      int ecode = qt.executeClient("lockneg3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lockneg3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lockneg3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lockneg3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lockneg4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lockneg4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lockneg4.q");

      if (qt.shouldBeSkipped("lockneg4.q")) {
        System.out.println("Test lockneg4.q skipped");
        return;
      }

      qt.cliInit("lockneg4.q", false);
      int ecode = qt.executeClient("lockneg4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lockneg4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lockneg4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lockneg4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_lockneg5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "lockneg5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/lockneg5.q");

      if (qt.shouldBeSkipped("lockneg5.q")) {
        System.out.println("Test lockneg5.q skipped");
        return;
      }

      qt.cliInit("lockneg5.q", false);
      int ecode = qt.executeClient("lockneg5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("lockneg5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "lockneg5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "lockneg5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_merge_negative_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "merge_negative_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/merge_negative_1.q");

      if (qt.shouldBeSkipped("merge_negative_1.q")) {
        System.out.println("Test merge_negative_1.q skipped");
        return;
      }

      qt.cliInit("merge_negative_1.q", false);
      int ecode = qt.executeClient("merge_negative_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("merge_negative_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "merge_negative_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "merge_negative_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_merge_negative_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "merge_negative_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/merge_negative_2.q");

      if (qt.shouldBeSkipped("merge_negative_2.q")) {
        System.out.println("Test merge_negative_2.q skipped");
        return;
      }

      qt.cliInit("merge_negative_2.q", false);
      int ecode = qt.executeClient("merge_negative_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("merge_negative_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "merge_negative_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "merge_negative_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_no_matching_udf() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "no_matching_udf.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/no_matching_udf.q");

      if (qt.shouldBeSkipped("no_matching_udf.q")) {
        System.out.println("Test no_matching_udf.q skipped");
        return;
      }

      qt.cliInit("no_matching_udf.q", false);
      int ecode = qt.executeClient("no_matching_udf.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("no_matching_udf.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "no_matching_udf.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "no_matching_udf.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_nonkey_groupby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "nonkey_groupby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/nonkey_groupby.q");

      if (qt.shouldBeSkipped("nonkey_groupby.q")) {
        System.out.println("Test nonkey_groupby.q skipped");
        return;
      }

      qt.cliInit("nonkey_groupby.q", false);
      int ecode = qt.executeClient("nonkey_groupby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("nonkey_groupby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
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

  public void testNegativeCliDriver_nopart_insert() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "nopart_insert.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/nopart_insert.q");

      if (qt.shouldBeSkipped("nopart_insert.q")) {
        System.out.println("Test nopart_insert.q skipped");
        return;
      }

      qt.cliInit("nopart_insert.q", false);
      int ecode = qt.executeClient("nopart_insert.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("nopart_insert.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "nopart_insert.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "nopart_insert.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_nopart_load() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "nopart_load.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/nopart_load.q");

      if (qt.shouldBeSkipped("nopart_load.q")) {
        System.out.println("Test nopart_load.q skipped");
        return;
      }

      qt.cliInit("nopart_load.q", false);
      int ecode = qt.executeClient("nopart_load.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("nopart_load.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "nopart_load.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "nopart_load.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_notable_alias3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "notable_alias3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/notable_alias3.q");

      if (qt.shouldBeSkipped("notable_alias3.q")) {
        System.out.println("Test notable_alias3.q skipped");
        return;
      }

      qt.cliInit("notable_alias3.q", false);
      int ecode = qt.executeClient("notable_alias3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("notable_alias3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "notable_alias3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "notable_alias3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_notable_alias4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "notable_alias4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/notable_alias4.q");

      if (qt.shouldBeSkipped("notable_alias4.q")) {
        System.out.println("Test notable_alias4.q skipped");
        return;
      }

      qt.cliInit("notable_alias4.q", false);
      int ecode = qt.executeClient("notable_alias4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("notable_alias4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "notable_alias4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "notable_alias4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_orderbysortby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "orderbysortby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/orderbysortby.q");

      if (qt.shouldBeSkipped("orderbysortby.q")) {
        System.out.println("Test orderbysortby.q skipped");
        return;
      }

      qt.cliInit("orderbysortby.q", false);
      int ecode = qt.executeClient("orderbysortby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("orderbysortby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "orderbysortby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "orderbysortby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_part_col_complex_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "part_col_complex_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/part_col_complex_type.q");

      if (qt.shouldBeSkipped("part_col_complex_type.q")) {
        System.out.println("Test part_col_complex_type.q skipped");
        return;
      }

      qt.cliInit("part_col_complex_type.q", false);
      int ecode = qt.executeClient("part_col_complex_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("part_col_complex_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "part_col_complex_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "part_col_complex_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_part.q");

      if (qt.shouldBeSkipped("protectmode_part.q")) {
        System.out.println("Test protectmode_part.q skipped");
        return;
      }

      qt.cliInit("protectmode_part.q", false);
      int ecode = qt.executeClient("protectmode_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_part1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_part1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_part1.q");

      if (qt.shouldBeSkipped("protectmode_part1.q")) {
        System.out.println("Test protectmode_part1.q skipped");
        return;
      }

      qt.cliInit("protectmode_part1.q", false);
      int ecode = qt.executeClient("protectmode_part1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_part1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_part1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_part1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_part2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_part2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_part2.q");

      if (qt.shouldBeSkipped("protectmode_part2.q")) {
        System.out.println("Test protectmode_part2.q skipped");
        return;
      }

      qt.cliInit("protectmode_part2.q", false);
      int ecode = qt.executeClient("protectmode_part2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_part2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_part2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_part2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_part_no_drop() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_part_no_drop.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_part_no_drop.q");

      if (qt.shouldBeSkipped("protectmode_part_no_drop.q")) {
        System.out.println("Test protectmode_part_no_drop.q skipped");
        return;
      }

      qt.cliInit("protectmode_part_no_drop.q", false);
      int ecode = qt.executeClient("protectmode_part_no_drop.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_part_no_drop.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_part_no_drop.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_part_no_drop.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl1.q");

      if (qt.shouldBeSkipped("protectmode_tbl1.q")) {
        System.out.println("Test protectmode_tbl1.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl1.q", false);
      int ecode = qt.executeClient("protectmode_tbl1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl2.q");

      if (qt.shouldBeSkipped("protectmode_tbl2.q")) {
        System.out.println("Test protectmode_tbl2.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl2.q", false);
      int ecode = qt.executeClient("protectmode_tbl2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl3.q");

      if (qt.shouldBeSkipped("protectmode_tbl3.q")) {
        System.out.println("Test protectmode_tbl3.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl3.q", false);
      int ecode = qt.executeClient("protectmode_tbl3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl4.q");

      if (qt.shouldBeSkipped("protectmode_tbl4.q")) {
        System.out.println("Test protectmode_tbl4.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl4.q", false);
      int ecode = qt.executeClient("protectmode_tbl4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl5.q");

      if (qt.shouldBeSkipped("protectmode_tbl5.q")) {
        System.out.println("Test protectmode_tbl5.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl5.q", false);
      int ecode = qt.executeClient("protectmode_tbl5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl6.q");

      if (qt.shouldBeSkipped("protectmode_tbl6.q")) {
        System.out.println("Test protectmode_tbl6.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl6.q", false);
      int ecode = qt.executeClient("protectmode_tbl6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl7.q");

      if (qt.shouldBeSkipped("protectmode_tbl7.q")) {
        System.out.println("Test protectmode_tbl7.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl7.q", false);
      int ecode = qt.executeClient("protectmode_tbl7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl8() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl8.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl8.q");

      if (qt.shouldBeSkipped("protectmode_tbl8.q")) {
        System.out.println("Test protectmode_tbl8.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl8.q", false);
      int ecode = qt.executeClient("protectmode_tbl8.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl8.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl8.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl8.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_protectmode_tbl_no_drop() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "protectmode_tbl_no_drop.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/protectmode_tbl_no_drop.q");

      if (qt.shouldBeSkipped("protectmode_tbl_no_drop.q")) {
        System.out.println("Test protectmode_tbl_no_drop.q skipped");
        return;
      }

      qt.cliInit("protectmode_tbl_no_drop.q", false);
      int ecode = qt.executeClient("protectmode_tbl_no_drop.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("protectmode_tbl_no_drop.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "protectmode_tbl_no_drop.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "protectmode_tbl_no_drop.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_recursive_view() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "recursive_view.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/recursive_view.q");

      if (qt.shouldBeSkipped("recursive_view.q")) {
        System.out.println("Test recursive_view.q skipped");
        return;
      }

      qt.cliInit("recursive_view.q", false);
      int ecode = qt.executeClient("recursive_view.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("recursive_view.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "recursive_view.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "recursive_view.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_regex_col_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "regex_col_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/regex_col_1.q");

      if (qt.shouldBeSkipped("regex_col_1.q")) {
        System.out.println("Test regex_col_1.q skipped");
        return;
      }

      qt.cliInit("regex_col_1.q", false);
      int ecode = qt.executeClient("regex_col_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("regex_col_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "regex_col_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "regex_col_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_regex_col_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "regex_col_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/regex_col_2.q");

      if (qt.shouldBeSkipped("regex_col_2.q")) {
        System.out.println("Test regex_col_2.q skipped");
        return;
      }

      qt.cliInit("regex_col_2.q", false);
      int ecode = qt.executeClient("regex_col_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("regex_col_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "regex_col_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "regex_col_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_regex_col_groupby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "regex_col_groupby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/regex_col_groupby.q");

      if (qt.shouldBeSkipped("regex_col_groupby.q")) {
        System.out.println("Test regex_col_groupby.q skipped");
        return;
      }

      qt.cliInit("regex_col_groupby.q", false);
      int ecode = qt.executeClient("regex_col_groupby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("regex_col_groupby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "regex_col_groupby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "regex_col_groupby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_sample() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sample.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/sample.q");

      if (qt.shouldBeSkipped("sample.q")) {
        System.out.println("Test sample.q skipped");
        return;
      }

      qt.cliInit("sample.q", false);
      int ecode = qt.executeClient("sample.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("sample.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sample.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sample.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_script_broken_pipe1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "script_broken_pipe1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/script_broken_pipe1.q");

      if (qt.shouldBeSkipped("script_broken_pipe1.q")) {
        System.out.println("Test script_broken_pipe1.q skipped");
        return;
      }

      qt.cliInit("script_broken_pipe1.q", false);
      int ecode = qt.executeClient("script_broken_pipe1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("script_broken_pipe1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "script_broken_pipe1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "script_broken_pipe1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_script_broken_pipe2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "script_broken_pipe2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/script_broken_pipe2.q");

      if (qt.shouldBeSkipped("script_broken_pipe2.q")) {
        System.out.println("Test script_broken_pipe2.q skipped");
        return;
      }

      qt.cliInit("script_broken_pipe2.q", false);
      int ecode = qt.executeClient("script_broken_pipe2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("script_broken_pipe2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "script_broken_pipe2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "script_broken_pipe2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_script_broken_pipe3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "script_broken_pipe3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/script_broken_pipe3.q");

      if (qt.shouldBeSkipped("script_broken_pipe3.q")) {
        System.out.println("Test script_broken_pipe3.q skipped");
        return;
      }

      qt.cliInit("script_broken_pipe3.q", false);
      int ecode = qt.executeClient("script_broken_pipe3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("script_broken_pipe3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "script_broken_pipe3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "script_broken_pipe3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_script_error() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "script_error.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/script_error.q");

      if (qt.shouldBeSkipped("script_error.q")) {
        System.out.println("Test script_error.q skipped");
        return;
      }

      qt.cliInit("script_error.q", false);
      int ecode = qt.executeClient("script_error.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("script_error.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "script_error.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "script_error.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_select_charliteral() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "select_charliteral.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/select_charliteral.q");

      if (qt.shouldBeSkipped("select_charliteral.q")) {
        System.out.println("Test select_charliteral.q skipped");
        return;
      }

      qt.cliInit("select_charliteral.q", false);
      int ecode = qt.executeClient("select_charliteral.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("select_charliteral.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "select_charliteral.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "select_charliteral.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_select_udtf_alias() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "select_udtf_alias.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/select_udtf_alias.q");

      if (qt.shouldBeSkipped("select_udtf_alias.q")) {
        System.out.println("Test select_udtf_alias.q skipped");
        return;
      }

      qt.cliInit("select_udtf_alias.q", false);
      int ecode = qt.executeClient("select_udtf_alias.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("select_udtf_alias.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "select_udtf_alias.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "select_udtf_alias.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_semijoin1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "semijoin1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/semijoin1.q");

      if (qt.shouldBeSkipped("semijoin1.q")) {
        System.out.println("Test semijoin1.q skipped");
        return;
      }

      qt.cliInit("semijoin1.q", false);
      int ecode = qt.executeClient("semijoin1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("semijoin1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "semijoin1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "semijoin1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_semijoin2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "semijoin2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/semijoin2.q");

      if (qt.shouldBeSkipped("semijoin2.q")) {
        System.out.println("Test semijoin2.q skipped");
        return;
      }

      qt.cliInit("semijoin2.q", false);
      int ecode = qt.executeClient("semijoin2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("semijoin2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "semijoin2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "semijoin2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_semijoin3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "semijoin3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/semijoin3.q");

      if (qt.shouldBeSkipped("semijoin3.q")) {
        System.out.println("Test semijoin3.q skipped");
        return;
      }

      qt.cliInit("semijoin3.q", false);
      int ecode = qt.executeClient("semijoin3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("semijoin3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "semijoin3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "semijoin3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_semijoin4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "semijoin4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/semijoin4.q");

      if (qt.shouldBeSkipped("semijoin4.q")) {
        System.out.println("Test semijoin4.q skipped");
        return;
      }

      qt.cliInit("semijoin4.q", false);
      int ecode = qt.executeClient("semijoin4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("semijoin4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "semijoin4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "semijoin4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_serde_regex() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_regex.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/serde_regex.q");

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

  public void testNegativeCliDriver_serde_regex2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_regex2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/serde_regex2.q");

      if (qt.shouldBeSkipped("serde_regex2.q")) {
        System.out.println("Test serde_regex2.q skipped");
        return;
      }

      qt.cliInit("serde_regex2.q", false);
      int ecode = qt.executeClient("serde_regex2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("serde_regex2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_regex2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_regex2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_serde_regex3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "serde_regex3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/serde_regex3.q");

      if (qt.shouldBeSkipped("serde_regex3.q")) {
        System.out.println("Test serde_regex3.q skipped");
        return;
      }

      qt.cliInit("serde_regex3.q", false);
      int ecode = qt.executeClient("serde_regex3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("serde_regex3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "serde_regex3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "serde_regex3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_set_hiveconf_validation0() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "set_hiveconf_validation0.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/set_hiveconf_validation0.q");

      if (qt.shouldBeSkipped("set_hiveconf_validation0.q")) {
        System.out.println("Test set_hiveconf_validation0.q skipped");
        return;
      }

      qt.cliInit("set_hiveconf_validation0.q", false);
      int ecode = qt.executeClient("set_hiveconf_validation0.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("set_hiveconf_validation0.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "set_hiveconf_validation0.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "set_hiveconf_validation0.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_set_hiveconf_validation1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "set_hiveconf_validation1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/set_hiveconf_validation1.q");

      if (qt.shouldBeSkipped("set_hiveconf_validation1.q")) {
        System.out.println("Test set_hiveconf_validation1.q skipped");
        return;
      }

      qt.cliInit("set_hiveconf_validation1.q", false);
      int ecode = qt.executeClient("set_hiveconf_validation1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("set_hiveconf_validation1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "set_hiveconf_validation1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "set_hiveconf_validation1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_columns1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_columns1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_columns1.q");

      if (qt.shouldBeSkipped("show_columns1.q")) {
        System.out.println("Test show_columns1.q skipped");
        return;
      }

      qt.cliInit("show_columns1.q", false);
      int ecode = qt.executeClient("show_columns1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_columns1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_columns1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_columns1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_columns2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_columns2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_columns2.q");

      if (qt.shouldBeSkipped("show_columns2.q")) {
        System.out.println("Test show_columns2.q skipped");
        return;
      }

      qt.cliInit("show_columns2.q", false);
      int ecode = qt.executeClient("show_columns2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_columns2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_columns2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_columns2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_columns3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_columns3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_columns3.q");

      if (qt.shouldBeSkipped("show_columns3.q")) {
        System.out.println("Test show_columns3.q skipped");
        return;
      }

      qt.cliInit("show_columns3.q", false);
      int ecode = qt.executeClient("show_columns3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_columns3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_columns3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_columns3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_create_table_does_not_exist() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_create_table_does_not_exist.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_create_table_does_not_exist.q");

      if (qt.shouldBeSkipped("show_create_table_does_not_exist.q")) {
        System.out.println("Test show_create_table_does_not_exist.q skipped");
        return;
      }

      qt.cliInit("show_create_table_does_not_exist.q", false);
      int ecode = qt.executeClient("show_create_table_does_not_exist.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_create_table_does_not_exist.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_create_table_does_not_exist.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_create_table_does_not_exist.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_create_table_index() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_create_table_index.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_create_table_index.q");

      if (qt.shouldBeSkipped("show_create_table_index.q")) {
        System.out.println("Test show_create_table_index.q skipped");
        return;
      }

      qt.cliInit("show_create_table_index.q", false);
      int ecode = qt.executeClient("show_create_table_index.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_create_table_index.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_create_table_index.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_create_table_index.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_partitions1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_partitions1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_partitions1.q");

      if (qt.shouldBeSkipped("show_partitions1.q")) {
        System.out.println("Test show_partitions1.q skipped");
        return;
      }

      qt.cliInit("show_partitions1.q", false);
      int ecode = qt.executeClient("show_partitions1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_partitions1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_partitions1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_partitions1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tableproperties1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tableproperties1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tableproperties1.q");

      if (qt.shouldBeSkipped("show_tableproperties1.q")) {
        System.out.println("Test show_tableproperties1.q skipped");
        return;
      }

      qt.cliInit("show_tableproperties1.q", false);
      int ecode = qt.executeClient("show_tableproperties1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tableproperties1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tableproperties1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tableproperties1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tables_bad1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tables_bad1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tables_bad1.q");

      if (qt.shouldBeSkipped("show_tables_bad1.q")) {
        System.out.println("Test show_tables_bad1.q skipped");
        return;
      }

      qt.cliInit("show_tables_bad1.q", false);
      int ecode = qt.executeClient("show_tables_bad1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tables_bad1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tables_bad1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tables_bad1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tables_bad2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tables_bad2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tables_bad2.q");

      if (qt.shouldBeSkipped("show_tables_bad2.q")) {
        System.out.println("Test show_tables_bad2.q skipped");
        return;
      }

      qt.cliInit("show_tables_bad2.q", false);
      int ecode = qt.executeClient("show_tables_bad2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tables_bad2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tables_bad2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tables_bad2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tables_bad_db1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tables_bad_db1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tables_bad_db1.q");

      if (qt.shouldBeSkipped("show_tables_bad_db1.q")) {
        System.out.println("Test show_tables_bad_db1.q skipped");
        return;
      }

      qt.cliInit("show_tables_bad_db1.q", false);
      int ecode = qt.executeClient("show_tables_bad_db1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tables_bad_db1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tables_bad_db1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tables_bad_db1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tables_bad_db2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tables_bad_db2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tables_bad_db2.q");

      if (qt.shouldBeSkipped("show_tables_bad_db2.q")) {
        System.out.println("Test show_tables_bad_db2.q skipped");
        return;
      }

      qt.cliInit("show_tables_bad_db2.q", false);
      int ecode = qt.executeClient("show_tables_bad_db2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tables_bad_db2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tables_bad_db2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tables_bad_db2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tablestatus() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tablestatus.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tablestatus.q");

      if (qt.shouldBeSkipped("show_tablestatus.q")) {
        System.out.println("Test show_tablestatus.q skipped");
        return;
      }

      qt.cliInit("show_tablestatus.q", false);
      int ecode = qt.executeClient("show_tablestatus.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tablestatus.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tablestatus.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tablestatus.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_show_tablestatus_not_existing_part() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "show_tablestatus_not_existing_part.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/show_tablestatus_not_existing_part.q");

      if (qt.shouldBeSkipped("show_tablestatus_not_existing_part.q")) {
        System.out.println("Test show_tablestatus_not_existing_part.q skipped");
        return;
      }

      qt.cliInit("show_tablestatus_not_existing_part.q", false);
      int ecode = qt.executeClient("show_tablestatus_not_existing_part.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("show_tablestatus_not_existing_part.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "show_tablestatus_not_existing_part.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "show_tablestatus_not_existing_part.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_smb_bucketmapjoin() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "smb_bucketmapjoin.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/smb_bucketmapjoin.q");

      if (qt.shouldBeSkipped("smb_bucketmapjoin.q")) {
        System.out.println("Test smb_bucketmapjoin.q skipped");
        return;
      }

      qt.cliInit("smb_bucketmapjoin.q", false);
      int ecode = qt.executeClient("smb_bucketmapjoin.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("smb_bucketmapjoin.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "smb_bucketmapjoin.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "smb_bucketmapjoin.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_sortmerge_mapjoin_mismatch_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "sortmerge_mapjoin_mismatch_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/sortmerge_mapjoin_mismatch_1.q");

      if (qt.shouldBeSkipped("sortmerge_mapjoin_mismatch_1.q")) {
        System.out.println("Test sortmerge_mapjoin_mismatch_1.q skipped");
        return;
      }

      qt.cliInit("sortmerge_mapjoin_mismatch_1.q", false);
      int ecode = qt.executeClient("sortmerge_mapjoin_mismatch_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("sortmerge_mapjoin_mismatch_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "sortmerge_mapjoin_mismatch_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "sortmerge_mapjoin_mismatch_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_split_sample_out_of_range() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "split_sample_out_of_range.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/split_sample_out_of_range.q");

      if (qt.shouldBeSkipped("split_sample_out_of_range.q")) {
        System.out.println("Test split_sample_out_of_range.q skipped");
        return;
      }

      qt.cliInit("split_sample_out_of_range.q", false);
      int ecode = qt.executeClient("split_sample_out_of_range.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("split_sample_out_of_range.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "split_sample_out_of_range.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "split_sample_out_of_range.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_split_sample_wrong_format() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "split_sample_wrong_format.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/split_sample_wrong_format.q");

      if (qt.shouldBeSkipped("split_sample_wrong_format.q")) {
        System.out.println("Test split_sample_wrong_format.q skipped");
        return;
      }

      qt.cliInit("split_sample_wrong_format.q", false);
      int ecode = qt.executeClient("split_sample_wrong_format.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("split_sample_wrong_format.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "split_sample_wrong_format.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "split_sample_wrong_format.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_stats_aggregator_error_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "stats_aggregator_error_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/stats_aggregator_error_1.q");

      if (qt.shouldBeSkipped("stats_aggregator_error_1.q")) {
        System.out.println("Test stats_aggregator_error_1.q skipped");
        return;
      }

      qt.cliInit("stats_aggregator_error_1.q", false);
      int ecode = qt.executeClient("stats_aggregator_error_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("stats_aggregator_error_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "stats_aggregator_error_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "stats_aggregator_error_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_stats_aggregator_error_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "stats_aggregator_error_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/stats_aggregator_error_2.q");

      if (qt.shouldBeSkipped("stats_aggregator_error_2.q")) {
        System.out.println("Test stats_aggregator_error_2.q skipped");
        return;
      }

      qt.cliInit("stats_aggregator_error_2.q", false);
      int ecode = qt.executeClient("stats_aggregator_error_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("stats_aggregator_error_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "stats_aggregator_error_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "stats_aggregator_error_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_stats_publisher_error_1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "stats_publisher_error_1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/stats_publisher_error_1.q");

      if (qt.shouldBeSkipped("stats_publisher_error_1.q")) {
        System.out.println("Test stats_publisher_error_1.q skipped");
        return;
      }

      qt.cliInit("stats_publisher_error_1.q", false);
      int ecode = qt.executeClient("stats_publisher_error_1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("stats_publisher_error_1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "stats_publisher_error_1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "stats_publisher_error_1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_stats_publisher_error_2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "stats_publisher_error_2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/stats_publisher_error_2.q");

      if (qt.shouldBeSkipped("stats_publisher_error_2.q")) {
        System.out.println("Test stats_publisher_error_2.q skipped");
        return;
      }

      qt.cliInit("stats_publisher_error_2.q", false);
      int ecode = qt.executeClient("stats_publisher_error_2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("stats_publisher_error_2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "stats_publisher_error_2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "stats_publisher_error_2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_strict_join() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "strict_join.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/strict_join.q");

      if (qt.shouldBeSkipped("strict_join.q")) {
        System.out.println("Test strict_join.q skipped");
        return;
      }

      qt.cliInit("strict_join.q", false);
      int ecode = qt.executeClient("strict_join.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("strict_join.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "strict_join.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "strict_join.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_strict_orderby() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "strict_orderby.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/strict_orderby.q");

      if (qt.shouldBeSkipped("strict_orderby.q")) {
        System.out.println("Test strict_orderby.q skipped");
        return;
      }

      qt.cliInit("strict_orderby.q", false);
      int ecode = qt.executeClient("strict_orderby.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("strict_orderby.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "strict_orderby.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "strict_orderby.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_strict_pruning() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "strict_pruning.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/strict_pruning.q");

      if (qt.shouldBeSkipped("strict_pruning.q")) {
        System.out.println("Test strict_pruning.q skipped");
        return;
      }

      qt.cliInit("strict_pruning.q", false);
      int ecode = qt.executeClient("strict_pruning.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("strict_pruning.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "strict_pruning.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "strict_pruning.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_subq_insert() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "subq_insert.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/subq_insert.q");

      if (qt.shouldBeSkipped("subq_insert.q")) {
        System.out.println("Test subq_insert.q skipped");
        return;
      }

      qt.cliInit("subq_insert.q", false);
      int ecode = qt.executeClient("subq_insert.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("subq_insert.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "subq_insert.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "subq_insert.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_touch1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "touch1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/touch1.q");

      if (qt.shouldBeSkipped("touch1.q")) {
        System.out.println("Test touch1.q skipped");
        return;
      }

      qt.cliInit("touch1.q", false);
      int ecode = qt.executeClient("touch1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("touch1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "touch1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "touch1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_touch2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "touch2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/touch2.q");

      if (qt.shouldBeSkipped("touch2.q")) {
        System.out.println("Test touch2.q skipped");
        return;
      }

      qt.cliInit("touch2.q", false);
      int ecode = qt.executeClient("touch2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("touch2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "touch2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "touch2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udaf_invalid_place() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udaf_invalid_place.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udaf_invalid_place.q");

      if (qt.shouldBeSkipped("udaf_invalid_place.q")) {
        System.out.println("Test udaf_invalid_place.q skipped");
        return;
      }

      qt.cliInit("udaf_invalid_place.q", false);
      int ecode = qt.executeClient("udaf_invalid_place.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udaf_invalid_place.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udaf_invalid_place.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udaf_invalid_place.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_array_contains_wrong1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_array_contains_wrong1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_array_contains_wrong1.q");

      if (qt.shouldBeSkipped("udf_array_contains_wrong1.q")) {
        System.out.println("Test udf_array_contains_wrong1.q skipped");
        return;
      }

      qt.cliInit("udf_array_contains_wrong1.q", false);
      int ecode = qt.executeClient("udf_array_contains_wrong1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_array_contains_wrong1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_array_contains_wrong1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_array_contains_wrong1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_array_contains_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_array_contains_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_array_contains_wrong2.q");

      if (qt.shouldBeSkipped("udf_array_contains_wrong2.q")) {
        System.out.println("Test udf_array_contains_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_array_contains_wrong2.q", false);
      int ecode = qt.executeClient("udf_array_contains_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_array_contains_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_array_contains_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_array_contains_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_assert_true() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_assert_true.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_assert_true.q");

      if (qt.shouldBeSkipped("udf_assert_true.q")) {
        System.out.println("Test udf_assert_true.q skipped");
        return;
      }

      qt.cliInit("udf_assert_true.q", false);
      int ecode = qt.executeClient("udf_assert_true.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_assert_true.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_assert_true.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_assert_true.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_assert_true2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_assert_true2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_assert_true2.q");

      if (qt.shouldBeSkipped("udf_assert_true2.q")) {
        System.out.println("Test udf_assert_true2.q skipped");
        return;
      }

      qt.cliInit("udf_assert_true2.q", false);
      int ecode = qt.executeClient("udf_assert_true2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_assert_true2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_assert_true2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_assert_true2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_case_type_wrong() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_case_type_wrong.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_case_type_wrong.q");

      if (qt.shouldBeSkipped("udf_case_type_wrong.q")) {
        System.out.println("Test udf_case_type_wrong.q skipped");
        return;
      }

      qt.cliInit("udf_case_type_wrong.q", false);
      int ecode = qt.executeClient("udf_case_type_wrong.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_case_type_wrong.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_case_type_wrong.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_case_type_wrong.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_case_type_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_case_type_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_case_type_wrong2.q");

      if (qt.shouldBeSkipped("udf_case_type_wrong2.q")) {
        System.out.println("Test udf_case_type_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_case_type_wrong2.q", false);
      int ecode = qt.executeClient("udf_case_type_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_case_type_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_case_type_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_case_type_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_case_type_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_case_type_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_case_type_wrong3.q");

      if (qt.shouldBeSkipped("udf_case_type_wrong3.q")) {
        System.out.println("Test udf_case_type_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_case_type_wrong3.q", false);
      int ecode = qt.executeClient("udf_case_type_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_case_type_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_case_type_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_case_type_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_coalesce() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_coalesce.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_coalesce.q");

      if (qt.shouldBeSkipped("udf_coalesce.q")) {
        System.out.println("Test udf_coalesce.q skipped");
        return;
      }

      qt.cliInit("udf_coalesce.q", false);
      int ecode = qt.executeClient("udf_coalesce.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_coalesce.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_coalesce.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_coalesce.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_concat_ws_wrong1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_concat_ws_wrong1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_concat_ws_wrong1.q");

      if (qt.shouldBeSkipped("udf_concat_ws_wrong1.q")) {
        System.out.println("Test udf_concat_ws_wrong1.q skipped");
        return;
      }

      qt.cliInit("udf_concat_ws_wrong1.q", false);
      int ecode = qt.executeClient("udf_concat_ws_wrong1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_concat_ws_wrong1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_concat_ws_wrong1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_concat_ws_wrong1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_concat_ws_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_concat_ws_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_concat_ws_wrong2.q");

      if (qt.shouldBeSkipped("udf_concat_ws_wrong2.q")) {
        System.out.println("Test udf_concat_ws_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_concat_ws_wrong2.q", false);
      int ecode = qt.executeClient("udf_concat_ws_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_concat_ws_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_concat_ws_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_concat_ws_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_concat_ws_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_concat_ws_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_concat_ws_wrong3.q");

      if (qt.shouldBeSkipped("udf_concat_ws_wrong3.q")) {
        System.out.println("Test udf_concat_ws_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_concat_ws_wrong3.q", false);
      int ecode = qt.executeClient("udf_concat_ws_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_concat_ws_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_concat_ws_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_concat_ws_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_elt_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_elt_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_elt_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_elt_wrong_args_len.q")) {
        System.out.println("Test udf_elt_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_elt_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_elt_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_elt_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_elt_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_elt_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_elt_wrong_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_elt_wrong_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_elt_wrong_type.q");

      if (qt.shouldBeSkipped("udf_elt_wrong_type.q")) {
        System.out.println("Test udf_elt_wrong_type.q skipped");
        return;
      }

      qt.cliInit("udf_elt_wrong_type.q", false);
      int ecode = qt.executeClient("udf_elt_wrong_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_elt_wrong_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_elt_wrong_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_elt_wrong_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_field_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_field_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_field_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_field_wrong_args_len.q")) {
        System.out.println("Test udf_field_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_field_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_field_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_field_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_field_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_field_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_field_wrong_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_field_wrong_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_field_wrong_type.q");

      if (qt.shouldBeSkipped("udf_field_wrong_type.q")) {
        System.out.println("Test udf_field_wrong_type.q skipped");
        return;
      }

      qt.cliInit("udf_field_wrong_type.q", false);
      int ecode = qt.executeClient("udf_field_wrong_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_field_wrong_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_field_wrong_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_field_wrong_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong1.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong1.q")) {
        System.out.println("Test udf_format_number_wrong1.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong1.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong2.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong2.q")) {
        System.out.println("Test udf_format_number_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong2.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong3.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong3.q")) {
        System.out.println("Test udf_format_number_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong3.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong4.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong4.q")) {
        System.out.println("Test udf_format_number_wrong4.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong4.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong5() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong5.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong5.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong5.q")) {
        System.out.println("Test udf_format_number_wrong5.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong5.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong5.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong5.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong5.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong5.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong6() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong6.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong6.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong6.q")) {
        System.out.println("Test udf_format_number_wrong6.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong6.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong6.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong6.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong6.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong6.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_format_number_wrong7() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_format_number_wrong7.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_format_number_wrong7.q");

      if (qt.shouldBeSkipped("udf_format_number_wrong7.q")) {
        System.out.println("Test udf_format_number_wrong7.q skipped");
        return;
      }

      qt.cliInit("udf_format_number_wrong7.q", false);
      int ecode = qt.executeClient("udf_format_number_wrong7.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_format_number_wrong7.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_format_number_wrong7.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_format_number_wrong7.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_function_does_not_implement_udf() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_function_does_not_implement_udf.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_function_does_not_implement_udf.q");

      if (qt.shouldBeSkipped("udf_function_does_not_implement_udf.q")) {
        System.out.println("Test udf_function_does_not_implement_udf.q skipped");
        return;
      }

      qt.cliInit("udf_function_does_not_implement_udf.q", false);
      int ecode = qt.executeClient("udf_function_does_not_implement_udf.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_function_does_not_implement_udf.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_function_does_not_implement_udf.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_function_does_not_implement_udf.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_if_not_bool() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_if_not_bool.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_if_not_bool.q");

      if (qt.shouldBeSkipped("udf_if_not_bool.q")) {
        System.out.println("Test udf_if_not_bool.q skipped");
        return;
      }

      qt.cliInit("udf_if_not_bool.q", false);
      int ecode = qt.executeClient("udf_if_not_bool.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_if_not_bool.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_if_not_bool.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_if_not_bool.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_if_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_if_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_if_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_if_wrong_args_len.q")) {
        System.out.println("Test udf_if_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_if_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_if_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_if_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_if_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_if_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_in() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_in.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_in.q");

      if (qt.shouldBeSkipped("udf_in.q")) {
        System.out.println("Test udf_in.q skipped");
        return;
      }

      qt.cliInit("udf_in.q", false);
      int ecode = qt.executeClient("udf_in.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_in.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_in.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_in.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_instr_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_instr_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_instr_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_instr_wrong_args_len.q")) {
        System.out.println("Test udf_instr_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_instr_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_instr_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_instr_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_instr_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_instr_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_instr_wrong_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_instr_wrong_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_instr_wrong_type.q");

      if (qt.shouldBeSkipped("udf_instr_wrong_type.q")) {
        System.out.println("Test udf_instr_wrong_type.q skipped");
        return;
      }

      qt.cliInit("udf_instr_wrong_type.q", false);
      int ecode = qt.executeClient("udf_instr_wrong_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_instr_wrong_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_instr_wrong_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_instr_wrong_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_locate_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_locate_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_locate_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_locate_wrong_args_len.q")) {
        System.out.println("Test udf_locate_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_locate_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_locate_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_locate_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_locate_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_locate_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_locate_wrong_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_locate_wrong_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_locate_wrong_type.q");

      if (qt.shouldBeSkipped("udf_locate_wrong_type.q")) {
        System.out.println("Test udf_locate_wrong_type.q skipped");
        return;
      }

      qt.cliInit("udf_locate_wrong_type.q", false);
      int ecode = qt.executeClient("udf_locate_wrong_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_locate_wrong_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_locate_wrong_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_locate_wrong_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_map_keys_arg_num() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_map_keys_arg_num.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_map_keys_arg_num.q");

      if (qt.shouldBeSkipped("udf_map_keys_arg_num.q")) {
        System.out.println("Test udf_map_keys_arg_num.q skipped");
        return;
      }

      qt.cliInit("udf_map_keys_arg_num.q", false);
      int ecode = qt.executeClient("udf_map_keys_arg_num.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_map_keys_arg_num.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_map_keys_arg_num.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_map_keys_arg_num.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_map_keys_arg_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_map_keys_arg_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_map_keys_arg_type.q");

      if (qt.shouldBeSkipped("udf_map_keys_arg_type.q")) {
        System.out.println("Test udf_map_keys_arg_type.q skipped");
        return;
      }

      qt.cliInit("udf_map_keys_arg_type.q", false);
      int ecode = qt.executeClient("udf_map_keys_arg_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_map_keys_arg_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_map_keys_arg_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_map_keys_arg_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_map_values_arg_num() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_map_values_arg_num.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_map_values_arg_num.q");

      if (qt.shouldBeSkipped("udf_map_values_arg_num.q")) {
        System.out.println("Test udf_map_values_arg_num.q skipped");
        return;
      }

      qt.cliInit("udf_map_values_arg_num.q", false);
      int ecode = qt.executeClient("udf_map_values_arg_num.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_map_values_arg_num.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_map_values_arg_num.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_map_values_arg_num.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_map_values_arg_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_map_values_arg_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_map_values_arg_type.q");

      if (qt.shouldBeSkipped("udf_map_values_arg_type.q")) {
        System.out.println("Test udf_map_values_arg_type.q skipped");
        return;
      }

      qt.cliInit("udf_map_values_arg_type.q", false);
      int ecode = qt.executeClient("udf_map_values_arg_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_map_values_arg_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_map_values_arg_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_map_values_arg_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_max() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_max.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_max.q");

      if (qt.shouldBeSkipped("udf_max.q")) {
        System.out.println("Test udf_max.q skipped");
        return;
      }

      qt.cliInit("udf_max.q", false);
      int ecode = qt.executeClient("udf_max.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_max.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_max.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_max.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_min() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_min.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_min.q");

      if (qt.shouldBeSkipped("udf_min.q")) {
        System.out.println("Test udf_min.q skipped");
        return;
      }

      qt.cliInit("udf_min.q", false);
      int ecode = qt.executeClient("udf_min.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_min.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_min.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_min.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_printf_wrong1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_printf_wrong1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_printf_wrong1.q");

      if (qt.shouldBeSkipped("udf_printf_wrong1.q")) {
        System.out.println("Test udf_printf_wrong1.q skipped");
        return;
      }

      qt.cliInit("udf_printf_wrong1.q", false);
      int ecode = qt.executeClient("udf_printf_wrong1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_printf_wrong1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_printf_wrong1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_printf_wrong1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_printf_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_printf_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_printf_wrong2.q");

      if (qt.shouldBeSkipped("udf_printf_wrong2.q")) {
        System.out.println("Test udf_printf_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_printf_wrong2.q", false);
      int ecode = qt.executeClient("udf_printf_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_printf_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_printf_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_printf_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_printf_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_printf_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_printf_wrong3.q");

      if (qt.shouldBeSkipped("udf_printf_wrong3.q")) {
        System.out.println("Test udf_printf_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_printf_wrong3.q", false);
      int ecode = qt.executeClient("udf_printf_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_printf_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_printf_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_printf_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_printf_wrong4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_printf_wrong4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_printf_wrong4.q");

      if (qt.shouldBeSkipped("udf_printf_wrong4.q")) {
        System.out.println("Test udf_printf_wrong4.q skipped");
        return;
      }

      qt.cliInit("udf_printf_wrong4.q", false);
      int ecode = qt.executeClient("udf_printf_wrong4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_printf_wrong4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_printf_wrong4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_printf_wrong4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_reflect_neg() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_reflect_neg.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_reflect_neg.q");

      if (qt.shouldBeSkipped("udf_reflect_neg.q")) {
        System.out.println("Test udf_reflect_neg.q skipped");
        return;
      }

      qt.cliInit("udf_reflect_neg.q", false);
      int ecode = qt.executeClient("udf_reflect_neg.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_reflect_neg.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_reflect_neg.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_reflect_neg.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_size_wrong_args_len() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_size_wrong_args_len.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_size_wrong_args_len.q");

      if (qt.shouldBeSkipped("udf_size_wrong_args_len.q")) {
        System.out.println("Test udf_size_wrong_args_len.q skipped");
        return;
      }

      qt.cliInit("udf_size_wrong_args_len.q", false);
      int ecode = qt.executeClient("udf_size_wrong_args_len.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_size_wrong_args_len.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_size_wrong_args_len.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_size_wrong_args_len.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_size_wrong_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_size_wrong_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_size_wrong_type.q");

      if (qt.shouldBeSkipped("udf_size_wrong_type.q")) {
        System.out.println("Test udf_size_wrong_type.q skipped");
        return;
      }

      qt.cliInit("udf_size_wrong_type.q", false);
      int ecode = qt.executeClient("udf_size_wrong_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_size_wrong_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_size_wrong_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_size_wrong_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_sort_array_wrong1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_sort_array_wrong1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_sort_array_wrong1.q");

      if (qt.shouldBeSkipped("udf_sort_array_wrong1.q")) {
        System.out.println("Test udf_sort_array_wrong1.q skipped");
        return;
      }

      qt.cliInit("udf_sort_array_wrong1.q", false);
      int ecode = qt.executeClient("udf_sort_array_wrong1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_sort_array_wrong1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_sort_array_wrong1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_sort_array_wrong1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_sort_array_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_sort_array_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_sort_array_wrong2.q");

      if (qt.shouldBeSkipped("udf_sort_array_wrong2.q")) {
        System.out.println("Test udf_sort_array_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_sort_array_wrong2.q", false);
      int ecode = qt.executeClient("udf_sort_array_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_sort_array_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_sort_array_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_sort_array_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_sort_array_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_sort_array_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_sort_array_wrong3.q");

      if (qt.shouldBeSkipped("udf_sort_array_wrong3.q")) {
        System.out.println("Test udf_sort_array_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_sort_array_wrong3.q", false);
      int ecode = qt.executeClient("udf_sort_array_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_sort_array_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_sort_array_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_sort_array_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_test_error() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_test_error.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_test_error.q");

      if (qt.shouldBeSkipped("udf_test_error.q")) {
        System.out.println("Test udf_test_error.q skipped");
        return;
      }

      qt.cliInit("udf_test_error.q", false);
      int ecode = qt.executeClient("udf_test_error.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_test_error.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_test_error.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_test_error.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_test_error_reduce() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_test_error_reduce.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_test_error_reduce.q");

      if (qt.shouldBeSkipped("udf_test_error_reduce.q")) {
        System.out.println("Test udf_test_error_reduce.q skipped");
        return;
      }

      qt.cliInit("udf_test_error_reduce.q", false);
      int ecode = qt.executeClient("udf_test_error_reduce.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_test_error_reduce.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_test_error_reduce.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_test_error_reduce.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_when_type_wrong() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_when_type_wrong.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_when_type_wrong.q");

      if (qt.shouldBeSkipped("udf_when_type_wrong.q")) {
        System.out.println("Test udf_when_type_wrong.q skipped");
        return;
      }

      qt.cliInit("udf_when_type_wrong.q", false);
      int ecode = qt.executeClient("udf_when_type_wrong.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_when_type_wrong.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_when_type_wrong.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_when_type_wrong.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_when_type_wrong2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_when_type_wrong2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_when_type_wrong2.q");

      if (qt.shouldBeSkipped("udf_when_type_wrong2.q")) {
        System.out.println("Test udf_when_type_wrong2.q skipped");
        return;
      }

      qt.cliInit("udf_when_type_wrong2.q", false);
      int ecode = qt.executeClient("udf_when_type_wrong2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_when_type_wrong2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_when_type_wrong2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_when_type_wrong2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udf_when_type_wrong3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udf_when_type_wrong3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udf_when_type_wrong3.q");

      if (qt.shouldBeSkipped("udf_when_type_wrong3.q")) {
        System.out.println("Test udf_when_type_wrong3.q skipped");
        return;
      }

      qt.cliInit("udf_when_type_wrong3.q", false);
      int ecode = qt.executeClient("udf_when_type_wrong3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udf_when_type_wrong3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udf_when_type_wrong3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udf_when_type_wrong3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udfnull() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udfnull.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udfnull.q");

      if (qt.shouldBeSkipped("udfnull.q")) {
        System.out.println("Test udfnull.q skipped");
        return;
      }

      qt.cliInit("udfnull.q", false);
      int ecode = qt.executeClient("udfnull.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udfnull.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udfnull.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udfnull.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_explode_not_supported1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode_not_supported1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_explode_not_supported1.q");

      if (qt.shouldBeSkipped("udtf_explode_not_supported1.q")) {
        System.out.println("Test udtf_explode_not_supported1.q skipped");
        return;
      }

      qt.cliInit("udtf_explode_not_supported1.q", false);
      int ecode = qt.executeClient("udtf_explode_not_supported1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_explode_not_supported1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_explode_not_supported1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_explode_not_supported1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_explode_not_supported2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode_not_supported2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_explode_not_supported2.q");

      if (qt.shouldBeSkipped("udtf_explode_not_supported2.q")) {
        System.out.println("Test udtf_explode_not_supported2.q skipped");
        return;
      }

      qt.cliInit("udtf_explode_not_supported2.q", false);
      int ecode = qt.executeClient("udtf_explode_not_supported2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_explode_not_supported2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_explode_not_supported2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_explode_not_supported2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_explode_not_supported3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode_not_supported3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_explode_not_supported3.q");

      if (qt.shouldBeSkipped("udtf_explode_not_supported3.q")) {
        System.out.println("Test udtf_explode_not_supported3.q skipped");
        return;
      }

      qt.cliInit("udtf_explode_not_supported3.q", false);
      int ecode = qt.executeClient("udtf_explode_not_supported3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_explode_not_supported3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_explode_not_supported3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_explode_not_supported3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_explode_not_supported4() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_explode_not_supported4.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_explode_not_supported4.q");

      if (qt.shouldBeSkipped("udtf_explode_not_supported4.q")) {
        System.out.println("Test udtf_explode_not_supported4.q skipped");
        return;
      }

      qt.cliInit("udtf_explode_not_supported4.q", false);
      int ecode = qt.executeClient("udtf_explode_not_supported4.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_explode_not_supported4.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_explode_not_supported4.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_explode_not_supported4.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_invalid_place() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_invalid_place.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_invalid_place.q");

      if (qt.shouldBeSkipped("udtf_invalid_place.q")) {
        System.out.println("Test udtf_invalid_place.q skipped");
        return;
      }

      qt.cliInit("udtf_invalid_place.q", false);
      int ecode = qt.executeClient("udtf_invalid_place.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_invalid_place.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_invalid_place.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_invalid_place.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_not_supported1() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_not_supported1.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_not_supported1.q");

      if (qt.shouldBeSkipped("udtf_not_supported1.q")) {
        System.out.println("Test udtf_not_supported1.q skipped");
        return;
      }

      qt.cliInit("udtf_not_supported1.q", false);
      int ecode = qt.executeClient("udtf_not_supported1.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_not_supported1.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_not_supported1.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_not_supported1.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_not_supported2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_not_supported2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_not_supported2.q");

      if (qt.shouldBeSkipped("udtf_not_supported2.q")) {
        System.out.println("Test udtf_not_supported2.q skipped");
        return;
      }

      qt.cliInit("udtf_not_supported2.q", false);
      int ecode = qt.executeClient("udtf_not_supported2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_not_supported2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_not_supported2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_not_supported2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_udtf_not_supported3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "udtf_not_supported3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/udtf_not_supported3.q");

      if (qt.shouldBeSkipped("udtf_not_supported3.q")) {
        System.out.println("Test udtf_not_supported3.q skipped");
        return;
      }

      qt.cliInit("udtf_not_supported3.q", false);
      int ecode = qt.executeClient("udtf_not_supported3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("udtf_not_supported3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "udtf_not_supported3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "udtf_not_supported3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_union() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "union.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/union.q");

      if (qt.shouldBeSkipped("union.q")) {
        System.out.println("Test union.q skipped");
        return;
      }

      qt.cliInit("union.q", false);
      int ecode = qt.executeClient("union.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("union.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
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

  public void testNegativeCliDriver_union2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "union2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/union2.q");

      if (qt.shouldBeSkipped("union2.q")) {
        System.out.println("Test union2.q skipped");
        return;
      }

      qt.cliInit("union2.q", false);
      int ecode = qt.executeClient("union2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("union2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "union2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "union2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_union3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "union3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/union3.q");

      if (qt.shouldBeSkipped("union3.q")) {
        System.out.println("Test union3.q skipped");
        return;
      }

      qt.cliInit("union3.q", false);
      int ecode = qt.executeClient("union3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("union3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "union3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "union3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_uniquejoin() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "uniquejoin.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/uniquejoin.q");

      if (qt.shouldBeSkipped("uniquejoin.q")) {
        System.out.println("Test uniquejoin.q skipped");
        return;
      }

      qt.cliInit("uniquejoin.q", false);
      int ecode = qt.executeClient("uniquejoin.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("uniquejoin.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "uniquejoin.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "uniquejoin.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_uniquejoin2() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "uniquejoin2.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/uniquejoin2.q");

      if (qt.shouldBeSkipped("uniquejoin2.q")) {
        System.out.println("Test uniquejoin2.q skipped");
        return;
      }

      qt.cliInit("uniquejoin2.q", false);
      int ecode = qt.executeClient("uniquejoin2.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("uniquejoin2.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "uniquejoin2.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "uniquejoin2.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_uniquejoin3() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "uniquejoin3.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/uniquejoin3.q");

      if (qt.shouldBeSkipped("uniquejoin3.q")) {
        System.out.println("Test uniquejoin3.q skipped");
        return;
      }

      qt.cliInit("uniquejoin3.q", false);
      int ecode = qt.executeClient("uniquejoin3.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("uniquejoin3.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "uniquejoin3.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "uniquejoin3.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

  public void testNegativeCliDriver_wrong_column_type() throws Exception {
    long startTime = System.currentTimeMillis();
    try {
      System.out.println("Begin query: " + "wrong_column_type.q");

      qt.addFile("/home/leejy/work/hive-0.10.0-cdh4.2.0/src/ql/src/test/queries/clientnegative/wrong_column_type.q");

      if (qt.shouldBeSkipped("wrong_column_type.q")) {
        System.out.println("Test wrong_column_type.q skipped");
        return;
      }

      qt.cliInit("wrong_column_type.q", false);
      int ecode = qt.executeClient("wrong_column_type.q");
      if (ecode == 0) {
        fail("Client Execution failed with error code = " + ecode
            + debugHint);
      }

      ecode = qt.checkCliDriverResults("wrong_column_type.q");
      if (ecode != 0) {
        fail("Client execution results failed with error code = " + ecode
            + debugHint);
      }
    }
    catch (Throwable e) {
      System.out.println("Exception: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Failed query: " + "wrong_column_type.q");
      System.out.flush();
      fail("Unexpected exception" + debugHint);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Done query: " + "wrong_column_type.q" + " elapsedTime=" + elapsedTime/1000 + "s");
    assertTrue("Test passed", true);
  }

}


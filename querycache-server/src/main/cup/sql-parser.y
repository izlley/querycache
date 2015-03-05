package com.skplanet.querycache.server.sqlcompiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap;
import java_cup.runtime.Symbol;
import com.google.common.collect.Lists;
import com.skplanet.querycache.server.auth.Privilege;
import com.skplanet.querycache.server.auth.AuthorizeableTable;
import com.skplanet.querycache.server.auth.AuthorizeableFunction;

parser code {:
  QueryStmt queryStmt = new QueryStmt();
  String datastore;

  private Symbol errorToken;

  // list of expected tokens ids from current parsing state
  // for generating syntax error message
  private final List<Integer> expectedTokenIds = new ArrayList<Integer>();
 
  public String transToHdfsFilePath(String path) {
    String str = path.trim();
    if ( str.charAt(0) == 'h' &&
         str.charAt(1) == 'd' &&
         str.charAt(2) == 'f' &&
         str.charAt(3) == 's' &&
         str.charAt(4) == ':' &&
         str.charAt(5) == '/' &&
         str.charAt(6) == '/' ) {
      return str;
    }
    if ( str.charAt(0) == 'f' &&
         str.charAt(1) == 'i' &&
         str.charAt(2) == 'l' &&
         str.charAt(3) == 'e' &&
         str.charAt(4) == ':' &&
         str.charAt(5) == '/' &&
         str.charAt(6) == '/' ) {
      return str;
    }
    return "hdfs://" + str;
  }

  // to avoid reporting trivial tokens as expected tokens in error messages
  private boolean reportExpectedToken(Integer tokenId) {
    if (SqlScanner.isKeyword(tokenId) ||
        tokenId.intValue() == SqlParserSymbols.COMMA ||
        tokenId.intValue() == SqlParserSymbols.IDENT) {
      return true;
    } else {
      return false;
    }
  }

  private String getErrorTypeMessage(int lastTokenId) {
    String msg = null;
    switch(lastTokenId) {
      case SqlParserSymbols.UNMATCHED_STRING_LITERAL:
        msg = "Unmatched string literal";
        break;
      case SqlParserSymbols.NUMERIC_OVERFLOW:
        msg = "Numeric overflow";
        break;
      default:
        msg = "Syntax error";
        break;
    }
    return msg;
  }

  // override to save error token
  public void syntax_error(java_cup.runtime.Symbol token) {
    errorToken = token;

    // derive expected tokens from current parsing state
    expectedTokenIds.clear();
    int state = ((Symbol)stack.peek()).parse_state;
    // get row of actions table corresponding to current parsing state
    // the row consists of pairs of <tokenId, actionId>
    // a pair is stored as row[i] (tokenId) and row[i+1] (actionId)
    // the last pair is a special error action
    short[] row = action_tab[state];
    short tokenId;
    // the expected tokens are all the symbols with a
    // corresponding action from the current parsing state
    for (int i = 0; i < row.length-2; ++i) {
      // get tokenId and skip actionId
      tokenId = row[i++];
      expectedTokenIds.add(Integer.valueOf(tokenId));
    }
  }

  // override to keep it from calling report_fatal_error()
  @Override
  public void unrecovered_syntax_error(Symbol cur_token)
      throws Exception {
    throw new Exception(getErrorTypeMessage(cur_token.sym));
  }

  /**
   * Manually throw a parse error on a given symbol for special circumstances.
   *
   * @symbolName
   *   name of symbol on which to fail parsing
   * @symbolId
   *   id of symbol from SqlParserSymbols on which to fail parsing
   */
  public void parseError(String symbolName, int symbolId) throws Exception {
    Symbol errorToken = getSymbolFactory().newSymbol(symbolName, symbolId,
        ((Symbol) stack.peek()), ((Symbol) stack.peek()), null);
    // Call syntax error to gather information about expected tokens, etc.
    // syntax_error does not throw an exception
    syntax_error(errorToken);
    // Unrecovered_syntax_error throws an exception and will terminate parsing
    unrecovered_syntax_error(errorToken);
  }

  // Returns error string, consisting of a shortened offending line
  // with a '^' under the offending token. Assumes
  // that parse() has been called and threw an exception
  public String getErrorMsg(String stmt) {
    if (errorToken == null || stmt == null) return null;
    String[] lines = stmt.split("\n");
    StringBuffer result = new StringBuffer();
    result.append(getErrorTypeMessage(errorToken.sym) + " in line ");
    result.append(errorToken.left);
    result.append(":\n");

    // errorToken.left is the line number of error.
    // errorToken.right is the column number of the error.
    String errorLine = lines[errorToken.left - 1];
    // If the error is that additional tokens are expected past the end, errorToken.right
    // will be past the end of the string.
    int lastCharIndex = Math.min(errorLine.length(), errorToken.right);
    int maxPrintLength = 60;
    int errorLoc = 0;
    if (errorLine.length() <= maxPrintLength) {
      // The line is short. Print the entire line.
      result.append(errorLine);
      result.append('\n');
      errorLoc = errorToken.right;
    } else {
      // The line is too long. Print maxPrintLength/2 characters before the error and
      // after the error.
      int contextLength = maxPrintLength / 2 - 3;
      String leftSubStr;
      if (errorToken.right > maxPrintLength / 2) {
        leftSubStr = "..." + errorLine.substring(errorToken.right - contextLength,
            lastCharIndex);
      } else {
        leftSubStr = errorLine.substring(0, errorToken.right);
      }
      errorLoc = leftSubStr.length();
      result.append(leftSubStr);
      if (errorLine.length() - errorToken.right > maxPrintLength / 2) {
        result.append(errorLine.substring(errorToken.right,
           errorToken.right + contextLength) + "...");
      } else {
        result.append(errorLine.substring(lastCharIndex));
      }
      result.append("\n");
    }

    // print error indicator
    for (int i = 0; i < errorLoc - 1; ++i) {
      result.append(' ');
    }
    result.append("^\n");

    // only report encountered and expected tokens for syntax errors
    if (errorToken.sym == SqlParserSymbols.UNMATCHED_STRING_LITERAL ||
        errorToken.sym == SqlParserSymbols.NUMERIC_OVERFLOW) {
      return result.toString();
    }

    // append last encountered token
    result.append("Encountered: ");
    String lastToken =
      SqlScanner.tokenIdMap.get(Integer.valueOf(errorToken.sym));
    if (lastToken != null) {
      result.append(lastToken);
    } else {
      result.append("Unknown last token with id: " + errorToken.sym);
    }

    // append expected tokens
    result.append('\n');
    result.append("Expected: ");
    String expectedToken = null;
    Integer tokenId = null;
    for (int i = 0; i < expectedTokenIds.size(); ++i) {
      tokenId = expectedTokenIds.get(i);
      if (reportExpectedToken(tokenId)) {
       expectedToken = SqlScanner.tokenIdMap.get(tokenId);
         result.append(expectedToken + ", ");
      }
    }
    // remove trailing ", "
    result.delete(result.length()-2, result.length());
    result.append('\n');

    return result.toString();
  }
:};

// List of keywords. Please keep them sorted alphabetically.
terminal
  KW_ADD, KW_AGGREGATE, KW_ALL, KW_ALTER, KW_AND, KW_API_VERSION, KW_AS, KW_ASC, KW_AVRO,
  KW_BETWEEN, KW_BIGINT, KW_BINARY, KW_BOOLEAN, KW_BY, KW_CACHED, KW_CASE, KW_CAST,
  KW_CHANGE, KW_CHAR, KW_CLASS, KW_CLOSE_FN, KW_COLUMN, KW_COLUMNS, KW_COMMENT,
  KW_COMPUTE, KW_CREATE, KW_CROSS, KW_DATA, KW_DATABASE, KW_DATABASES, KW_DATASOURCE,
  KW_DATASOURCES, KW_DATE, KW_DATETIME, KW_DECIMAL, KW_DELIMITED, KW_DESC, KW_DESCRIBE,
  KW_DISTINCT, KW_DIV, KW_DOUBLE, KW_DROP, KW_ELSE, KW_END, KW_ESCAPED, KW_EXISTS,
  KW_EXPLAIN, KW_EXTERNAL, KW_FALSE, KW_FIELDS, KW_FILEFORMAT, KW_FINALIZE_FN, KW_FIRST,
  KW_FLOAT, KW_FORMAT, KW_FORMATTED, KW_FROM, KW_FULL, KW_FUNCTION, KW_FUNCTIONS,
  KW_GROUP, KW_HAVING, KW_IF, KW_IN, KW_INIT_FN, KW_INNER, KW_INPATH, KW_INSERT, KW_INT,
  KW_INTERMEDIATE, KW_INTERVAL, KW_INTO, KW_INVALIDATE, KW_IS, KW_JOIN, KW_LAST, KW_LEFT,
  KW_LIKE, KW_LIMIT, KW_LINES, KW_LOAD, KW_LOCATION, KW_MERGE_FN, KW_METADATA, KW_NOT,
  KW_NULL, KW_NULLS, KW_OFFSET, KW_ON, KW_OR, KW_ORDER, KW_OUTER, KW_OVERWRITE,
  KW_PARQUET, KW_PARQUETFILE, KW_PARTITION, KW_PARTITIONED, KW_PARTITIONS, KW_PREPARE_FN,
  KW_PRODUCED, KW_RCFILE, KW_REFRESH, KW_REGEXP, KW_RENAME, KW_REPLACE, KW_RETURNS,
  KW_RIGHT, KW_RLIKE, KW_ROW, KW_SCHEMA, KW_SCHEMAS, KW_SELECT, KW_SEMI, KW_SEQUENCEFILE,
  KW_SERDEPROPERTIES, KW_SERIALIZE_FN, KW_SET, KW_SHOW, KW_SMALLINT, KW_STORED,
  KW_STRAIGHT_JOIN, KW_STRING, KW_SYMBOL, KW_TABLE, KW_TABLES, KW_TBLPROPERTIES,
  KW_TERMINATED, KW_TEXTFILE, KW_THEN, KW_TIMESTAMP, KW_TINYINT, KW_STATS, KW_TO,
  KW_TRUE,KW_UNCACHED, KW_UNION, KW_UPDATE_FN, KW_USE, KW_USING, KW_VALUES, KW_VIEW,
  KW_WHEN, KW_WHERE, KW_WITH;

terminal COMMA, DOT, DOTDOTDOT, STAR, LPAREN, RPAREN, LBRACKET, RBRACKET,
  DIVIDE, MOD, ADD, SUBTRACT;
terminal BITAND, BITOR, BITXOR, BITNOT;
terminal EQUAL, NOT, LESSTHAN, GREATERTHAN;
terminal String IDENT;
terminal String NUMERIC_OVERFLOW;
terminal BigInteger INTEGER_LITERAL;
terminal BigDecimal DECIMAL_LITERAL;
terminal String STRING_LITERAL;
terminal String UNMATCHED_STRING_LITERAL;

nonterminal QueryStmt stmt;
// Single select statement.
nonterminal select_stmt;
// Single values statement.
nonterminal values_stmt;
// Select or union statement.
nonterminal query_stmt;
nonterminal opt_query_stmt;
// Single select_stmt or parenthesized query_stmt.
nonterminal union_operand;
// List of select or union blocks connected by UNION operators or a single select block.
nonterminal union_operand_list;
// List of union operands consisting of constant selects.
nonterminal values_operand_list;
// USE stmt
nonterminal use_stmt;
nonterminal show_tables_stmt;
nonterminal show_dbs_stmt;
nonterminal show_partitions_stmt;
nonterminal show_stats_stmt;
nonterminal show_pattern;
nonterminal describe_stmt;
nonterminal show_create_tbl_stmt;
nonterminal describe_output_style;
nonterminal load_stmt;
nonterminal reset_metadata_stmt;
// List of select blocks connected by UNION operators, with order by or limit.
nonterminal union_with_order_by_or_limit;
nonterminal select_clause;
nonterminal select_list;
nonterminal select_list_item;
nonterminal star_expr;
nonterminal opt_straight_join;
nonterminal expr, non_pred_expr, arithmetic_expr, timestamp_arithmetic_expr;
nonterminal expr_list;
nonterminal alias_clause;
nonterminal ident_list;
nonterminal opt_ident_list;
nonterminal TableName table_name;
nonterminal TableName view_name;
nonterminal FuncName function_name;
nonterminal where_clause;
nonterminal predicate, between_predicate, comparison_predicate,
compound_predicate, in_predicate, like_predicate;
nonterminal group_by_clause;
nonterminal having_clause;
nonterminal order_by_elements, order_by_clause;
nonterminal order_by_element;
nonterminal opt_order_param;
nonterminal opt_nulls_order_param;
nonterminal opt_offset_param;
nonterminal opt_limit_offset_clause;
nonterminal opt_limit_clause, opt_offset_clause;
nonterminal cast_expr, case_else_clause;
nonterminal literal;
nonterminal case_expr;
nonterminal case_when_clause_list;
nonterminal function_params;
nonterminal column_ref;
nonterminal from_clause, fromhead_clause, table_ref_list;
nonterminal with_table_ref_list;
nonterminal with_clause;
nonterminal TableName table_ref;
nonterminal TableName base_table_ref;
nonterminal inline_view_ref;
nonterminal with_table_ref;
nonterminal join_operator;
nonterminal opt_inner, opt_outer;
nonterminal opt_plan_hints;
nonterminal column_type;
nonterminal sign_chain_expr;
nonterminal insert_stmt;
nonterminal explain_stmt;
// Optional partition spec
nonterminal opt_partition_spec;
// Required partition spec
nonterminal partition_spec;
nonterminal partition_clause;
nonterminal static_partition_key_value_list;
nonterminal partition_key_value_list;
nonterminal partition_key_value;
nonterminal static_partition_key_value;
nonterminal union_op;

nonterminal alter_tbl_stmt;
nonterminal alter_view_stmt;
nonterminal compute_stats_stmt;
nonterminal drop_db_stmt;
nonterminal drop_tbl_or_view_stmt;
nonterminal create_db_stmt;
nonterminal create_tbl_as_select_stmt;
nonterminal create_tbl_like_stmt;
nonterminal create_tbl_stmt;
nonterminal create_view_stmt;
nonterminal create_data_src_stmt;
nonterminal drop_data_src_stmt;
nonterminal show_data_srcs_stmt;
nonterminal column_def, view_column_def;
nonterminal column_def_list, view_column_def_list;
nonterminal partition_column_defs, view_column_defs;
// Options for DDL commands - CREATE/DROP/ALTER
nonterminal Boolean cache_op_val;
nonterminal comment_val;
nonterminal Boolean external_val;
nonterminal opt_init_string_val;
nonterminal file_format_val;
nonterminal file_format_create_table_val;
nonterminal if_exists_val;
nonterminal if_not_exists_val;
nonterminal replace_existing_cols_val;
nonterminal String location_val;
nonterminal row_format_val;
nonterminal field_terminator_val;
nonterminal line_terminator_val;
nonterminal escaped_by_val;
nonterminal terminator_val;
nonterminal table_property_type;
nonterminal serde_properties;
nonterminal tbl_properties;
nonterminal properties_map;
// Used to simplify commands that accept either KW_DATABASE(S) or KW_SCHEMA(S)
nonterminal db_or_schema_kw;
nonterminal dbs_or_schemas_kw;
// Used to simplify commands where KW_COLUMN is optional
nonterminal optional_kw_column;
// Used to simplify commands where KW_TABLE is optional
nonterminal opt_kw_table;
nonterminal overwrite_val;

// For Create/Drop/Show function ddl
nonterminal function_def_args;
nonterminal function_def_arg_list;
nonterminal opt_is_aggregate_fn;
nonterminal opt_is_varargs;
nonterminal opt_aggregate_fn_intermediate_type;
nonterminal create_udf_stmt;
nonterminal create_uda_stmt;
nonterminal show_functions_stmt;
nonterminal drop_function_stmt;
// Accepts space separated key='v' arguments.
nonterminal create_function_args_map;
nonterminal create_function_arg_key;

precedence left KW_OR;
precedence left KW_AND;
precedence left KW_NOT, NOT;
precedence left KW_BETWEEN, KW_IN, KW_IS;
precedence left KW_LIKE, KW_RLIKE, KW_REGEXP;
precedence left EQUAL, LESSTHAN, GREATERTHAN;
precedence left ADD, SUBTRACT;
precedence left STAR, DIVIDE, MOD, KW_DIV;
precedence left BITAND, BITOR, BITXOR, BITNOT;
precedence left KW_ORDER, KW_BY, KW_LIMIT;
precedence left LPAREN, RPAREN;
// Support chaining of timestamp arithmetic exprs.
precedence left KW_INTERVAL;

// These tokens need to be at the end for create_function_args_map to accept
// no keys. Otherwise, the grammar has shift/reduce conflicts.
precedence left KW_COMMENT;
precedence left KW_SYMBOL;
precedence left KW_PREPARE_FN;
precedence left KW_CLOSE_FN;
precedence left KW_UPDATE_FN;
precedence left KW_FINALIZE_FN;
precedence left KW_INIT_FN;
precedence left KW_MERGE_FN;
precedence left KW_SERIALIZE_FN;

start with stmt;

stmt ::=
  query_stmt
  {: RESULT = parser.queryStmt; :}
  | fromhead_clause query_stmt
  {: RESULT = parser.queryStmt; :}
  | insert_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.INSERT;
    RESULT = parser.queryStmt;
  :}
  | fromhead_clause insert_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.INSERT;
    RESULT = parser.queryStmt;
  :}
  | use_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.USE;
    RESULT = parser.queryStmt;
  :}
  | show_tables_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_dbs_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_partitions_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_stats_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_functions_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_data_srcs_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | show_create_tbl_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.SHOW;
    RESULT = parser.queryStmt;
  :}
  | describe_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.DESCRIBE;
    RESULT = parser.queryStmt;
  :}
  | alter_tbl_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.ALTER;
    RESULT = parser.queryStmt;
  :}
  | alter_view_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.ALTER;
    RESULT = parser.queryStmt;
  :}
  | compute_stats_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.COMPUTE_STATS;
    RESULT = parser.queryStmt;
  :}
  | create_tbl_as_select_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_tbl_like_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_tbl_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_view_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_data_src_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_db_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_udf_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | create_uda_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.CREATE;
    RESULT = parser.queryStmt;
  :}
  | drop_db_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.DROP;
    RESULT = parser.queryStmt;
  :}
  | drop_tbl_or_view_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.DROP;
    RESULT = parser.queryStmt;
  :}
  | drop_function_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.DROP;
    RESULT = parser.queryStmt;
  :}
  | drop_data_src_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.DROP;
    RESULT = parser.queryStmt;
  :}
  | explain_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.EXPLAIN;
    RESULT = parser.queryStmt;
  :}
  | load_stmt
  {:
    parser.queryStmt.type = QueryStmt.QueryType.LOAD;
    RESULT = parser.queryStmt;
  :}
  | reset_metadata_stmt
  {: RESULT = parser.queryStmt; :}
  ;

load_stmt ::=
  KW_LOAD KW_DATA KW_INPATH STRING_LITERAL:loc overwrite_val KW_INTO KW_TABLE
  table_name:tbl partition_spec
  {:
    parser.queryStmt.uriPathRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.INSERT));
  :}
  ;

overwrite_val ::=
  KW_OVERWRITE
  | /* empty */
  ;

reset_metadata_stmt ::=
  KW_INVALIDATE KW_METADATA
  {:
    parser.queryStmt.dataStoreRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(
        parser.datastore, Privilege.ALL));
    parser.queryStmt.type = QueryStmt.QueryType.INVALIDATE;
    parser.queryStmt.isInvalidateAll = true;
  :}
  | KW_INVALIDATE KW_METADATA table_name:tbl
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl.getDb()), Privilege.ALL));
    parser.queryStmt.type = QueryStmt.QueryType.INVALIDATE;
  :}
  | KW_REFRESH table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALL));
    parser.queryStmt.type = QueryStmt.QueryType.REFRESH;
  :}
  ;

explain_stmt ::=
  KW_EXPLAIN query_stmt
  | KW_EXPLAIN insert_stmt
  | KW_EXPLAIN create_tbl_as_select_stmt
  ;

// Insert statements have two optional clauses: the column permutation (INSERT into
// tbl(col1,...) etc) and the PARTITION clause. If the column permutation is present, the
// query statement clause is optional as well.
insert_stmt ::=
  with_clause KW_INSERT KW_OVERWRITE opt_kw_table table_name:tbl LPAREN
  opt_ident_list RPAREN partition_clause opt_plan_hints
  opt_query_stmt
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.INSERT));
  :}
  | with_clause KW_INSERT KW_OVERWRITE opt_kw_table table_name:tbl
  partition_clause opt_plan_hints query_stmt
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.INSERT));
  :}
  | with_clause KW_INSERT KW_INTO opt_kw_table table_name:tbl LPAREN
  opt_ident_list RPAREN partition_clause opt_plan_hints
  opt_query_stmt
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.INSERT));
  :}
  | with_clause KW_INSERT KW_INTO opt_kw_table table_name:tbl
  partition_clause opt_plan_hints query_stmt
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.INSERT));
  :}
  ;

opt_query_stmt ::=
  query_stmt
  | /* empty */
  ;

opt_ident_list ::=
  /* TODO: support the column level authorization*/
  ident_list
  | /* empty */
  ;

opt_kw_table ::=
  KW_TABLE
  | /* empty */
  ;

alter_tbl_stmt ::=
  KW_ALTER KW_TABLE table_name:tbl replace_existing_cols_val KW_COLUMNS
  LPAREN column_def_list RPAREN
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl KW_ADD if_not_exists_val
    partition_spec location_val:loc cache_op_val
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALTER));
    }
  :}
  | KW_ALTER KW_TABLE table_name:tbl KW_DROP optional_kw_column IDENT
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl KW_CHANGE optional_kw_column IDENT
    column_def
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl KW_DROP if_exists_val
    partition_spec
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl opt_partition_spec KW_SET KW_FILEFORMAT
    file_format_val
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl opt_partition_spec KW_SET
    KW_LOCATION STRING_LITERAL:loc
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
    parser.queryStmt.uriPathRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl1 KW_RENAME KW_TO table_name:tbl2
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl1, Privilege.ALTER));
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl2, Privilege.CREATE));
  :}
  | KW_ALTER KW_TABLE table_name:tbl opt_partition_spec KW_SET
    table_property_type LPAREN properties_map RPAREN
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  | KW_ALTER KW_TABLE table_name:tbl opt_partition_spec KW_SET
    cache_op_val:cache_op
  {:
    // Ensure a parser error is thrown for ALTER statements if no cache op is specified.
    if (cache_op == false) {
      parser.parseError("set", SqlParserSymbols.KW_SET);
    }
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALTER));
  :}
  ;
  
table_property_type ::=
  KW_TBLPROPERTIES
  | KW_SERDEPROPERTIES
  ;

optional_kw_column ::=
  KW_COLUMN
  | /* empty */
  ;

replace_existing_cols_val ::=
  KW_REPLACE
  | KW_ADD
  ;

create_db_stmt ::=
  KW_CREATE db_or_schema_kw if_not_exists_val IDENT:db_name
  comment_val location_val:loc
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,db_name), Privilege.CREATE));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    }
  :}
  ;

create_tbl_like_stmt ::=
  KW_CREATE external_val KW_TABLE if_not_exists_val
  table_name:tbl1 KW_LIKE table_name:tbl2 comment_val
  KW_STORED KW_AS file_format_val location_val:loc
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl1.getDb()), Privilege.ALL));
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl2, Privilege.ANY));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    }
  :}
  | KW_CREATE external_val KW_TABLE if_not_exists_val
    table_name:tbl1 KW_LIKE table_name:tbl2 comment_val
    location_val:loc
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl1.getDb()), Privilege.ALL));
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl2, Privilege.ANY));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    }
  :}
  ;

create_tbl_as_select_stmt ::=
  KW_CREATE external_val KW_TABLE if_not_exists_val
  table_name:tbl comment_val row_format_val
  serde_properties file_format_create_table_val
  location_val:loc tbl_properties
  KW_AS query_stmt
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl.getDb()), Privilege.ALL));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    }
  :}
  ;

create_tbl_stmt ::=
  KW_CREATE external_val KW_TABLE if_not_exists_val
  table_name:tbl LPAREN column_def_list RPAREN
  partition_column_defs comment_val
  row_format_val serde_properties
  file_format_create_table_val location_val:loc tbl_properties
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl.getDb()), Privilege.ALL));
    if (loc != null) {
      parser.queryStmt.uriPathRefs.add(
        new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ALL));
    }
  :}
  | KW_CREATE external_val:external KW_TABLE if_not_exists_val
    table_name:tbl LPAREN column_def_list RPAREN
    KW_PRODUCED KW_BY KW_DATASOURCE IDENT:data_src_name
    opt_init_string_val comment_val
  {:
    if (external) {
      parser.parseError("external", SqlParserSymbols.KW_EXTERNAL);
    }
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,tbl.getDb()), Privilege.ALL));
  :}
  ;

create_udf_stmt ::=
  KW_CREATE KW_FUNCTION if_not_exists_val
  function_name:func function_def_args
  KW_RETURNS column_type
  KW_LOCATION STRING_LITERAL:binary_path
  create_function_args_map
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,func.getDb()), Privilege.ALL));
    parser.queryStmt.uriPathRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(
        parser.transToHdfsFilePath(binary_path), Privilege.SELECT));
  :}
  ;

create_uda_stmt ::=
  KW_CREATE KW_AGGREGATE KW_FUNCTION if_not_exists_val
  function_name:func function_def_args
  KW_RETURNS column_type
  opt_aggregate_fn_intermediate_type
  KW_LOCATION STRING_LITERAL:binary_path
  create_function_args_map
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName, Privilege>(
        new DatabaseName(parser.datastore,func.getDb()), Privilege.ALL));
    parser.queryStmt.uriPathRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(
        parser.transToHdfsFilePath(binary_path), Privilege.SELECT));
  :}
  ;

cache_op_val ::=
  KW_CACHED KW_IN STRING_LITERAL
  {: RESULT = true; :}
  | KW_UNCACHED
  {: RESULT = true; :}
  | /* empty */
  {: RESULT = false; :}
  ;

comment_val ::=
  KW_COMMENT STRING_LITERAL
  | /* empty */
  ;

location_val ::=
  KW_LOCATION STRING_LITERAL:location
  {: RESULT = location; :}
  | /* empty */
  ;

opt_init_string_val ::=
  LPAREN STRING_LITERAL RPAREN
  | /* empty */
  ;

external_val ::=
  KW_EXTERNAL
  {: RESULT = true; :}
  |
  {: RESULT = false; :}
  ;

if_not_exists_val ::=
  KW_IF KW_NOT KW_EXISTS
  |
  ;

row_format_val ::=
  KW_ROW KW_FORMAT KW_DELIMITED field_terminator_val
  escaped_by_val line_terminator_val
  |/* empty */
  ;

escaped_by_val ::=
  KW_ESCAPED KW_BY STRING_LITERAL
  | /* empty */
  ;

line_terminator_val ::=
  KW_LINES terminator_val
  | /* empty */
  ;

field_terminator_val ::=
  KW_FIELDS terminator_val
  | /* empty */
  ;

terminator_val ::=
  KW_TERMINATED KW_BY STRING_LITERAL
  ;

file_format_create_table_val ::=
  KW_STORED KW_AS file_format_val
  | /* empty - default to TEXT */
  ;

file_format_val ::=
  KW_PARQUET
  | KW_PARQUETFILE
  | KW_TEXTFILE
  | KW_SEQUENCEFILE
  | KW_RCFILE
  | KW_AVRO
  ;

tbl_properties ::=
  KW_TBLPROPERTIES LPAREN properties_map RPAREN
  | /* empty */
  ;

serde_properties ::=
  KW_WITH KW_SERDEPROPERTIES LPAREN properties_map RPAREN
  | /* empty */
  ;

properties_map ::=
  STRING_LITERAL EQUAL STRING_LITERAL
  | properties_map COMMA STRING_LITERAL EQUAL STRING_LITERAL
  ;

partition_column_defs ::=
  KW_PARTITIONED KW_BY LPAREN column_def_list RPAREN
  | /* Empty - not a partitioned table */
  ;

column_def_list ::=
  column_def
  | column_def_list COMMA column_def
  ;

column_def ::=
  /* TODO: support the column level authorization*/
  IDENT column_type comment_val
  ;

create_view_stmt ::=
  KW_CREATE KW_VIEW if_not_exists_val view_name:vw
  view_column_defs comment_val KW_AS query_stmt
  {:
    parser.queryStmt.viewRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        vw, Privilege.CREATE));
  :}
  ;

create_data_src_stmt ::=
  KW_CREATE KW_DATASOURCE if_not_exists_val IDENT:data_src_name
  KW_LOCATION STRING_LITERAL:loc
  KW_CLASS STRING_LITERAL
  KW_API_VERSION STRING_LITERAL
  {: 
    parser.queryStmt.uriPathRefs.add(
      new AbstractMap.SimpleEntry<String, Privilege>(parser.transToHdfsFilePath(loc), Privilege.ANY));
  :}
  ;

view_column_defs ::=
  LPAREN view_column_def_list RPAREN
  | /* empty */
  ;

view_column_def_list ::=
  view_column_def
  | view_column_def_list COMMA view_column_def
  ;

view_column_def ::=
  IDENT comment_val
  ;

alter_view_stmt ::=
  KW_ALTER KW_VIEW view_name:vw KW_AS query_stmt
  {:
    parser.queryStmt.viewRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        vw, Privilege.ALTER));
  :}
  | KW_ALTER KW_VIEW view_name:vw1 KW_RENAME KW_TO view_name:vw2
  {:
    parser.queryStmt.viewRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        vw1, Privilege.ALTER));
    parser.queryStmt.viewRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        vw2, Privilege.CREATE));
  :}
  ;

compute_stats_stmt ::=
  KW_COMPUTE KW_STATS table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ALL));
  :}
  ;

drop_db_stmt ::=
  KW_DROP db_or_schema_kw if_exists_val IDENT:db_name
  {:
    parser.queryStmt.databaseRefs.add(
      new AbstractMap.SimpleEntry<DatabaseName,Privilege>(
        new DatabaseName(parser.datastore, db_name), Privilege.DROP));
  :}  
  ;

drop_tbl_or_view_stmt ::=
  KW_DROP KW_TABLE if_exists_val table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.DROP));
  :}
  | KW_DROP KW_VIEW if_exists_val view_name:vw
  {:
    parser.queryStmt.viewRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        vw, Privilege.DROP));
  :}
  ;

drop_function_stmt ::=
  KW_DROP opt_is_aggregate_fn KW_FUNCTION
      if_exists_val function_name:func
  function_def_args
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName, Privilege>(
        func, Privilege.DROP));
  :}
  ;

drop_data_src_stmt ::=
  KW_DROP KW_DATASOURCE if_exists_val IDENT:data_src_name
  ;

db_or_schema_kw ::=
  KW_DATABASE
  | KW_SCHEMA
  ;

dbs_or_schemas_kw ::=
  KW_DATABASES
  | KW_SCHEMAS
  ;

if_exists_val ::=
  KW_IF KW_EXISTS
  |
  ;

partition_clause ::=
  KW_PARTITION LPAREN partition_key_value_list RPAREN
  |
  ;

partition_key_value_list ::=
  partition_key_value
  | partition_key_value_list COMMA partition_key_value
  ;

// A partition spec is a set of static partition key/value pairs. This is a bit
// different than a partition clause in an INSERT statement because that allows
// for dynamic and static partition key/values.
partition_spec ::=
  KW_PARTITION LPAREN static_partition_key_value_list RPAREN
  ;

opt_partition_spec ::=
  partition_spec
  | /* Empty */
  ;

static_partition_key_value_list ::=
  static_partition_key_value
  | static_partition_key_value_list COMMA static_partition_key_value
  ;

partition_key_value ::=
  // Dynamic partition key values.
  IDENT
  | static_partition_key_value
  ;

static_partition_key_value ::=
  // Static partition key values.
  IDENT EQUAL expr
  ;

function_def_args ::=
  LPAREN RPAREN
  | LPAREN function_def_arg_list opt_is_varargs RPAREN
  ;

function_def_arg_list ::=
  column_type
  | function_def_arg_list COMMA column_type
  ;

opt_is_aggregate_fn ::=
  KW_AGGREGATE
  |
  ;

opt_is_varargs ::=
  DOTDOTDOT
  |
  ;

opt_aggregate_fn_intermediate_type ::=
  KW_INTERMEDIATE column_type
  |
  ;

create_function_args_map ::=
  create_function_arg_key EQUAL STRING_LITERAL
  | create_function_args_map create_function_arg_key EQUAL STRING_LITERAL
  |
  ;

// Any keys added here must also be added to the end of the precedence list.
create_function_arg_key ::=
  KW_COMMENT
  | KW_SYMBOL
  | KW_PREPARE_FN
  | KW_CLOSE_FN
  | KW_UPDATE_FN
  | KW_INIT_FN
  | KW_SERIALIZE_FN
  | KW_MERGE_FN
  | KW_FINALIZE_FN
  ;

// Our parsing of UNION is slightly different from MySQL's:
// http://dev.mysql.com/doc/refman/5.5/en/union.html
//
// Imo, MySQL's parsing of union is not very clear.
// For example, MySQL cannot parse this query:
// select 3 order by 1 limit 1 union all select 1;
//
// On the other hand, MySQL does parse this query, but associates
// the order by and limit with the union, not the select:
// select 3 as g union all select 1 order by 1 limit 2;
//
// MySQL also allows some combinations of select blocks
// with and without parenthesis, but also disallows others.
//
// Our parsing:
// Select blocks may or may not be in parenthesis,
// even if the union has order by and limit.
// ORDER BY and LIMIT bind to the preceding select statement by default.
query_stmt ::=
  with_clause union_operand_list
  | with_clause union_with_order_by_or_limit
  ;

with_clause ::=
  KW_WITH with_table_ref_list
  {: parser.queryStmt.isWith = true; :}
  | /* empty */
  ;

with_table_ref ::=
  IDENT:alias KW_AS LPAREN query_stmt RPAREN
  {: parser.queryStmt.withRefs.add(alias); :}
  | STRING_LITERAL:alias KW_AS LPAREN query_stmt RPAREN
  {: parser.queryStmt.withRefs.add(alias); :}
  ;

with_table_ref_list ::=
  with_table_ref
  | with_table_ref_list COMMA with_table_ref
  ;

// We must have a non-empty order by or limit for them to bind to the union.
// We cannot reuse the existing order_by_clause or
// limit_clause because they would introduce conflicts with EOF,
// which, unfortunately, cannot be accessed in the parser as a nonterminal
// making this issue unresolvable.
// We rely on the left precedence of KW_ORDER, KW_BY, and KW_LIMIT,
// to resolve the ambiguity with select_stmt in favor of select_stmt
// (i.e., ORDER BY and LIMIT bind to the select_stmt by default, and not the union).
// There must be at least two union operands for ORDER BY or LIMIT to bind to a union,
// and we manually throw a parse error if we reach this production
// with only a single operand.
union_with_order_by_or_limit ::=
    union_operand_list
    KW_ORDER KW_BY order_by_elements
    opt_offset_param
  |
    union_operand_list
    KW_LIMIT expr
  {: parser.queryStmt.isLimit = true; :}
  |
    union_operand_list
    KW_ORDER KW_BY order_by_elements
    KW_LIMIT expr opt_offset_param
  {: parser.queryStmt.isLimit = true; :}
  ;

union_operand ::=
  select_stmt
  {:
    if (parser.queryStmt.type != QueryStmt.QueryType.UNION) {
      parser.queryStmt.type = QueryStmt.QueryType.SELECT;
    }
  :}
  | values_stmt
  | LPAREN query_stmt RPAREN
  ;

union_operand_list ::=
  union_operand
  | union_operand_list union_op union_operand
  {:
    parser.queryStmt.unionLvl++;
    parser.queryStmt.type = QueryStmt.QueryType.UNION;
  :}
  ;

union_op ::=
  KW_UNION
  | KW_UNION KW_DISTINCT
  | KW_UNION KW_ALL
  ;

values_stmt ::=
  KW_VALUES values_operand_list
  order_by_clause
  opt_limit_offset_clause
  | KW_VALUES LPAREN values_operand_list RPAREN
    order_by_clause
    opt_limit_offset_clause
  ;

values_operand_list ::=
  LPAREN select_list RPAREN
  | values_operand_list COMMA LPAREN select_list RPAREN
  ;

use_stmt ::=
  KW_USE IDENT:db
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName,Privilege>(
        new TableName(db, AuthorizeableTable.ANY_TABLE_NAME), Privilege.ANY));
  :}
  ;

show_tables_stmt ::=
  KW_SHOW KW_TABLES
  | KW_SHOW KW_TABLES show_pattern
  | KW_SHOW KW_TABLES KW_IN IDENT:db
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName,Privilege>(
        new TableName(db, AuthorizeableTable.ANY_TABLE_NAME), Privilege.ANY));
  :}
  | KW_SHOW KW_TABLES KW_IN IDENT:db show_pattern
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName,Privilege>(
        new TableName(db, AuthorizeableTable.ANY_TABLE_NAME), Privilege.ANY));
  :}
  ;

show_dbs_stmt ::=
  KW_SHOW dbs_or_schemas_kw
  | KW_SHOW dbs_or_schemas_kw show_pattern
  ;

show_stats_stmt ::=
  KW_SHOW KW_TABLE KW_STATS table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ANY));
  :}
  | KW_SHOW KW_COLUMN KW_STATS table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ANY));
  :}
  ;

show_partitions_stmt ::=
  KW_SHOW KW_PARTITIONS table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ANY));
  :}
  ;

show_functions_stmt ::=
  KW_SHOW opt_is_aggregate_fn KW_FUNCTIONS
  | KW_SHOW opt_is_aggregate_fn KW_FUNCTIONS show_pattern
  | KW_SHOW opt_is_aggregate_fn KW_FUNCTIONS KW_IN IDENT:db
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName,Privilege>(
        new FuncName(db, AuthorizeableFunction.ANY_FUNCTION_NAME), Privilege.ANY));
  :}
  | KW_SHOW opt_is_aggregate_fn KW_FUNCTIONS KW_IN IDENT:db
      show_pattern
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName,Privilege>(
        new FuncName(db, AuthorizeableFunction.ANY_FUNCTION_NAME), Privilege.ANY));
  :}
  ;

show_data_srcs_stmt ::=
  KW_SHOW KW_DATASOURCES
  | KW_SHOW KW_DATASOURCES show_pattern
  ;

show_pattern ::=
  STRING_LITERAL
  | KW_LIKE STRING_LITERAL
  ;

show_create_tbl_stmt ::=
  KW_SHOW KW_CREATE KW_TABLE table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ANY));
  :}
  ;

describe_stmt ::=
  KW_DESCRIBE describe_output_style table_name:tbl
  {:
    parser.queryStmt.tableRefs.add(
      new AbstractMap.SimpleEntry<TableName, Privilege>(
        tbl, Privilege.ANY));
  :}
  ;

describe_output_style ::=
  KW_FORMATTED
  | /* empty */
  ;

select_stmt ::=
  select_clause
  where_clause
  group_by_clause
  having_clause
  order_by_clause
  opt_limit_offset_clause
  |
  select_clause
  from_clause
  where_clause
  group_by_clause
  having_clause
  order_by_clause
  opt_limit_offset_clause
  ;

select_clause ::=
  KW_SELECT opt_straight_join select_list
  | KW_SELECT KW_ALL opt_straight_join select_list
  | KW_SELECT KW_DISTINCT opt_straight_join select_list
  ;

opt_straight_join ::=
  KW_STRAIGHT_JOIN
  | /* empty */
  ;

select_list ::=
  /* TODO: support the column level authorization*/
  select_list_item
  | select_list COMMA select_list_item
  ;

select_list_item ::=
  expr alias_clause
  | expr
  | star_expr
  ;

alias_clause ::=
  KW_AS IDENT
  | IDENT
  | KW_AS STRING_LITERAL
  | STRING_LITERAL
  ;

star_expr ::=
  STAR
  // table_name DOT STAR doesn't work because of a reduce-reduce conflict
  // on IDENT [DOT]
  | IDENT DOT STAR
  | IDENT DOT IDENT DOT STAR
  ;

table_name ::=
  IDENT:tbl
  {: RESULT = new TableName(null, tbl); :}
  | IDENT:db DOT IDENT:tbl
  {: RESULT = new TableName(db, tbl); :}
  ;

view_name ::=
  IDENT:vw
  {: RESULT = new TableName(null, vw); :}
  | IDENT:db DOT IDENT:vw
  {: RESULT = new TableName(db, vw); :}
  ;

function_name ::=
  IDENT:fn
  {: RESULT = new FuncName(null, fn); :}
  | IDENT:db DOT IDENT:fn
  {: RESULT = new FuncName(db, fn); :}
  ;

from_clause ::=
  KW_FROM table_ref_list
  ;

fromhead_clause ::=
  KW_FROM inline_view_ref
  ;

table_ref_list ::=
  table_ref:tbl
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
  :}
  | table_ref_list COMMA table_ref:tbl
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
    parser.queryStmt.isJoin = true;
  :}
  | table_ref_list KW_CROSS KW_JOIN opt_plan_hints table_ref:tbl
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
    parser.queryStmt.isJoin = true;
  :}
  | table_ref_list join_operator opt_plan_hints table_ref:tbl
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
    parser.queryStmt.isJoin = true;
  :}
  | table_ref_list join_operator opt_plan_hints table_ref:tbl
    KW_ON expr
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
    parser.queryStmt.isJoin = true;
  :}
  | table_ref_list join_operator opt_plan_hints table_ref:tbl
    KW_USING LPAREN ident_list RPAREN
  {:
    if (tbl != null) {
      parser.queryStmt.tableRefs.add(
        new AbstractMap.SimpleEntry<TableName, Privilege>(
          tbl, Privilege.SELECT));
    }
    parser.queryStmt.isJoin = true;
  :}
  ;

table_ref ::=
  base_table_ref:tbl
  {: RESULT = tbl; :}
  | inline_view_ref
  {:
    parser.queryStmt.isNested = true;
    parser.queryStmt.nestedLvl++;
  :}
  ;

inline_view_ref ::=
  LPAREN query_stmt RPAREN alias_clause
  ;

base_table_ref ::=
  table_name:tbl alias_clause
  {: RESULT = tbl; :}
  | table_name:tbl
  {: RESULT = tbl; :}
  ;

join_operator ::=
  opt_inner KW_JOIN
  | KW_LEFT opt_outer KW_JOIN
  | KW_RIGHT opt_outer KW_JOIN
  | KW_FULL opt_outer KW_JOIN
  | KW_LEFT KW_SEMI KW_JOIN
  ;

opt_inner ::=
  KW_INNER
  |
  ;

opt_outer ::=
  KW_OUTER
  |
  ;

opt_plan_hints ::=
  LBRACKET ident_list RBRACKET
  |
  ;

ident_list ::=
  IDENT
  | ident_list COMMA IDENT
  ;

expr_list ::=
  expr
  | expr_list COMMA expr
  ;

where_clause ::=
  KW_WHERE expr
  {: parser.queryStmt.isFilterCond = true; :}
  | /* empty */
  ;

group_by_clause ::=
  KW_GROUP KW_BY expr_list
  | /* empty */
  ;

having_clause ::=
  KW_HAVING expr
  | /* empty */
  ;

order_by_clause ::=
  KW_ORDER KW_BY order_by_elements
  | /* empty */
  ;

order_by_elements ::=
  order_by_element
  | order_by_elements COMMA order_by_element
  ;

order_by_element ::=
  expr opt_order_param opt_nulls_order_param
  ;

opt_order_param ::=
  KW_ASC
  | KW_DESC
  | /* empty */
  ;

opt_nulls_order_param ::=
  KW_NULLS KW_FIRST
  | KW_NULLS KW_LAST
  | /* empty */
  ;

opt_offset_param ::=
  KW_OFFSET expr
  | /* empty */
  ;

opt_limit_offset_clause ::=
  opt_limit_clause opt_offset_clause
  ;

opt_limit_clause ::=
  KW_LIMIT expr
  {: parser.queryStmt.isLimit = true; :}
  | /* empty */
  ;

opt_offset_clause ::=
  KW_OFFSET expr
  | /* empty */
  ;

cast_expr ::=
  KW_CAST LPAREN expr KW_AS column_type RPAREN
  ;

case_expr ::=
  KW_CASE expr
    case_when_clause_list
    case_else_clause
    KW_END
  | KW_CASE
    case_when_clause_list
    case_else_clause
    KW_END
  ;

case_when_clause_list ::=
  KW_WHEN expr KW_THEN expr
  | case_when_clause_list KW_WHEN expr
    KW_THEN expr
  ;

case_else_clause ::=
  KW_ELSE expr
  | /* emtpy */
  ;

sign_chain_expr ::=
  SUBTRACT expr
  | ADD expr
  ;

expr ::=
  non_pred_expr
  | predicate
  ;

non_pred_expr ::=
  sign_chain_expr
  | literal
  | function_name:func LPAREN RPAREN
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName, Privilege>(
        func, Privilege.SELECT));
  :}
  | function_name:func LPAREN function_params RPAREN
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName, Privilege>(
        func, Privilege.SELECT));
  :}
  | function_name:func LPAREN function_params RPAREN LBRACKET INTEGER_LITERAL RBRACKET
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName, Privilege>(
        func, Privilege.SELECT));
  :}
  /* Since "IF" is a keyword, need to special case this function */
  | KW_IF LPAREN expr_list RPAREN
  | cast_expr
  | case_expr
  | column_ref
  | timestamp_arithmetic_expr
  | arithmetic_expr
  | LPAREN non_pred_expr RPAREN
  ;

arithmetic_expr ::=
  expr STAR expr
  | expr DIVIDE expr
  | expr MOD expr
  | expr KW_DIV expr
  | expr ADD expr
  | expr SUBTRACT expr
  | expr BITAND expr
  | expr BITOR expr
  | expr BITXOR expr
  | BITNOT expr
  ;

// We use IDENT for the temporal unit to avoid making DAY, YEAR, etc. keywords.
// This way we do not need to change existing uses of IDENT.
// We chose not to make DATE_ADD and DATE_SUB keywords for the same reason.
timestamp_arithmetic_expr ::=
  KW_INTERVAL expr IDENT ADD expr
  | expr ADD KW_INTERVAL expr IDENT
  // Set precedence to KW_INTERVAL (which is higher than ADD) for chaining.
  %prec KW_INTERVAL
  | expr SUBTRACT KW_INTERVAL expr IDENT
  // Set precedence to KW_INTERVAL (which is higher than ADD) for chaining.
  %prec KW_INTERVAL
  // Timestamp arithmetic expr that looks like a function call.
  // We use func_arg_list instead of expr to avoid a shift/reduce conflict with
  // func_arg_list on COMMA, and report an error if the list contains more than one expr.
  // Although we don't want to accept function names as the expr, we can't parse it
  // it as just an IDENT due to the precedence conflict with function_name.
  | function_name:func LPAREN expr_list COMMA
    KW_INTERVAL expr IDENT RPAREN
  {:
    parser.queryStmt.functionRefs.add(
      new AbstractMap.SimpleEntry<FuncName, Privilege>(
        func, Privilege.SELECT));
  :}
  ;

literal ::=
  INTEGER_LITERAL
  | DECIMAL_LITERAL
  | STRING_LITERAL
  | KW_TRUE
  | KW_FALSE
  | KW_NULL
  | UNMATCHED_STRING_LITERAL expr
  {:
    // we have an unmatched string literal.
    // to correctly report the root cause of this syntax error
    // we must force parsing to fail at this point,
    // and generate an unmatched string literal symbol
    // to be passed as the last seen token in the
    // error handling routine (otherwise some other token could be reported)
    parser.parseError("literal", SqlParserSymbols.UNMATCHED_STRING_LITERAL);
  :}
  | NUMERIC_OVERFLOW
  {:
    // similar to the unmatched string literal case
    // we must terminate parsing at this point
    // and generate a corresponding symbol to be reported
    parser.parseError("literal", SqlParserSymbols.NUMERIC_OVERFLOW);
  :}
  ;

function_params ::=
  STAR
  | KW_ALL STAR
  | expr_list
  | KW_ALL expr_list
  | KW_DISTINCT expr_list
  ;

predicate ::=
  expr KW_IS KW_NULL
  | expr KW_IS KW_NOT KW_NULL
  | between_predicate
  | comparison_predicate
  | compound_predicate
  | in_predicate
  | like_predicate
  | LPAREN predicate RPAREN
  ;

comparison_predicate ::=
  expr EQUAL expr
  | expr NOT EQUAL expr
  | expr LESSTHAN GREATERTHAN expr
  | expr LESSTHAN EQUAL expr
  | expr GREATERTHAN EQUAL expr
  | expr LESSTHAN expr
  | expr GREATERTHAN expr
  ;

like_predicate ::=
  expr KW_LIKE expr
  | expr KW_RLIKE expr
  | expr KW_REGEXP expr
  | expr KW_NOT KW_LIKE expr
  | expr KW_NOT KW_RLIKE expr
  | expr KW_NOT KW_REGEXP expr
  ;

// Avoid a reduce/reduce conflict with compound_predicate by explicitly
// using non_pred_expr and predicate separately instead of expr.
between_predicate ::=
  expr KW_BETWEEN non_pred_expr KW_AND expr
  | expr KW_BETWEEN predicate KW_AND expr
  | expr KW_NOT KW_BETWEEN non_pred_expr KW_AND expr
  | expr KW_NOT KW_BETWEEN predicate KW_AND expr
  ;

in_predicate ::=
  expr KW_IN LPAREN expr_list RPAREN
  | expr KW_NOT KW_IN LPAREN expr_list RPAREN
  ;

compound_predicate ::=
  expr KW_AND expr
  | expr KW_OR expr
  | KW_NOT expr
  | NOT expr
  ;

column_ref ::=
  IDENT
  // table_name:tblName DOT IDENT:col causes reduce/reduce conflicts
  | IDENT DOT IDENT
  | IDENT DOT IDENT DOT IDENT
  ;

column_type ::=
  KW_TINYINT
  | KW_SMALLINT
  | KW_INT
  | KW_BIGINT
  | KW_BOOLEAN
  | KW_FLOAT
  | KW_DOUBLE
  | KW_DATE
  | KW_DATETIME
  | KW_TIMESTAMP
  | KW_STRING
  | KW_BINARY
  | KW_CHAR LPAREN INTEGER_LITERAL:len RPAREN
  | KW_DECIMAL LPAREN INTEGER_LITERAL:precision RPAREN
  | KW_DECIMAL LPAREN INTEGER_LITERAL:precision COMMA INTEGER_LITERAL:scale RPAREN
  | KW_DECIMAL
  ;

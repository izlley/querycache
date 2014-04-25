package com.skplanet.querycache.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * General string utils
 */
public class StringUtils {
  
  final public static String[] emptyStringArray = {};
  final public static char COMMA = ',';
  final public static String COMMA_STR = ",";
  final public static char ESCAPE_CHAR = '\\';
  
  /**
   * Returns an arraylist of strings.
   * @param str the comma seperated string values
   * @return the arraylist of the comma seperated string values
   */
  public static String[] getStrings(String str){
    Collection<String> values = getStringCollection(str);
    if(values.size() == 0) {
      return null;
    }
    return values.toArray(new String[values.size()]);
  }
  
  /**
   * Returns a collection of strings.
   * @param str comma seperated string values
   * @return an <code>ArrayList</code> of string values
   */
  public static Collection<String> getStringCollection(String str){
    String delim = ",";
    return getStringCollection(str, delim);
  }
  
  /**
   * Returns a collection of strings.
   * 
   * @param str
   *          String to parse
   * @param delim
   *          delimiter to separate the values
   * @return Collection of parsed elements.
   */
  public static Collection<String> getStringCollection(String str, String delim) {
    List<String> values = new ArrayList<String>();
    if (str == null)
      return values;
    StringTokenizer tokenizer = new StringTokenizer(str, delim);
    while (tokenizer.hasMoreTokens()) {
      values.add(tokenizer.nextToken());
    }
    return values;
  }
  
  /**
   * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
   * Duplicate and empty values are removed.
   * @param str a comma separated <String> with values
   * @return a <code>Collection</code> of <code>String</code> values
   */
  public static Collection<String> getTrimmedStringCollection(String str){
    Set<String> set = new LinkedHashSet<String>(
      Arrays.asList(getTrimmedStrings(str)));
    set.remove("");
    return set;
  }
  
  /**
   * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
   * @param str a comma separated <String> with values
   * @return an array of <code>String</code> values
   */
  public static String[] getTrimmedStrings(String str){
    if (null == str || str.trim().isEmpty()) {
      return emptyStringArray;
    }

    return str.trim().split("\\s*,\\s*");
  }
  
  /**
   * Given an array of strings, return a comma-separated list of its elements.
   * @param strs Array of strings
   * @return Empty string if strs.length is 0, comma separated list of strings
   * otherwise
   */
  
  public static String arrayToString(String[] strs) {
    if (strs.length == 0) { return ""; }
    StringBuilder sbuf = new StringBuilder();
    sbuf.append(strs[0]);
    for (int idx = 1; idx < strs.length; idx++) {
      sbuf.append(",");
      sbuf.append(strs[idx]);
    }
    return sbuf.toString();
  }
}

package com.skplanet.querycache.server.util;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * Provides equivalent behavior to String.intern() to optimize performance, 
 * whereby does not consume memory in the permanent generation.
 */
public class StringInterner {
  
  /**
   * Retains a strong reference to each string instance it has interned.
   */
  private final static Interner<String> strongInterner;
  
  /**
   * Retains a weak reference to each string instance it has interned. 
   */
  private final static Interner<String> weakInterner;
  
  
  
  static {
    strongInterner = Interners.newStrongInterner();
    weakInterner = Interners.newWeakInterner();
  }
  
  /**
   * Interns and returns a reference to the representative instance 
   * for any of a collection of string instances that are equal to each other.
   * Retains strong reference to the instance, 
   * thus preventing it from being garbage-collected. 
   * 
   * @param sample string instance to be interned
   * @return strong reference to interned string instance
   */
  public static String strongIntern(String sample) {
    if (sample == null) {
      return null;
    }
    return strongInterner.intern(sample);
  }
  
  /**
   * Interns and returns a reference to the representative instance 
   * for any of a collection of string instances that are equal to each other.
   * Retains weak reference to the instance, 
   * and so does not prevent it from being garbage-collected.
   * 
   * @param sample string instance to be interned
   * @return weak reference to interned string instance
   */
  public static String weakIntern(String sample) {
    if (sample == null) {
      return null;
    }
    return weakInterner.intern(sample);
  }

}

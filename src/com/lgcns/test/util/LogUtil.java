package com.lgcns.test.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
  public static boolean DEBUG = (System.getProperties().stringPropertyNames().contains("XDEBUG")) ? true : false;
  
  public static void printLog(String log) {
    if (!DEBUG) {
      return;
    }
    
    SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");    
    System.out.println(String.format("%s %s", dateFormatLocal.format(new Date()), log));
  }

  public static void printLog(String fmt, Object ... args) {
    if (!DEBUG) {
      return;
    }
    
    SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
    fmt = "%s " + fmt;
    System.out.println(String.format(fmt, dateFormatLocal.format(new Date()), args));
  }
  
  
  public static void printLogStart(String log) {
    printLog(String.format("%s [START]", log));
  }
  
  public static void printLogEnd(String log) {
    printLog(String.format("%s [END]", log));
  }

  public static void main(String[] args) {
    // -DXDEBUG 설정: Run Configuration > Arguments > VM Arguments -DXDEBUG 에 지정
    
    System.out.println(String.format("DEBUG:%s", (DEBUG) ? "TRUE" : "FALSE"));
    printLog("Hello, World");
  }
}
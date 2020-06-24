package com.lgcns.test.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
	public static boolean DEBUG = true;

	public static void setDebug(boolean debug) {
		LogUtil.DEBUG = debug;
	}

	public static boolean getDebug() {
		return LogUtil.DEBUG;
	}
	
	public static void printLog(String log) {
		if (!DEBUG) {
			return;
		}
		
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");		
		System.out.println(String.format("%s %s", dateFormatLocal.format(new Date()), log));
	}
	
	public static void printLogStart(String log) {
		printLog(String.format("%s [START]", log));
	}
	
	public static void printLogEnd(String log) {
		printLog(String.format("%s [END]", log));
	}
}

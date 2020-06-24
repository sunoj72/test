package com.lgcns.test.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static SimpleDateFormat formatLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Date addSeconds(Date date, int value) {
		Date result = new Date(date.getTime() + (value * 1000));  
		
		return result;
	}
	
	public static Date addDays(Date date, int value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, value);
		
		return cal.getTime();
	}
	
//	public static Date add(Date date, int value) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//		cal.add(Calendar.SECOND, value);
//		
//		return cal.getTime();
//	}
	
	
//	public static void main(String[] args) throws Exception {
//		String str = "2019-03-31 23:24:00";
//		Date dt = formatDateTime.parse(str); 
//		
//		System.out.println(dt);
//		
//		dt = addSeconds(dt, 50 * 60);
//		System.out.println(dt);
//		
//		dt = addDays(dt, -1);
//		System.out.println(dt);
//	}
	
	
}

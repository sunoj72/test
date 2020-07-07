package com.lgcns.test.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final SimpleDateFormat formatLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
  
	public static synchronized Date addSeconds(Date date, int value) {
		Date result = new Date(date.getTime() + (value * 1000));  
		
		return result;
	}
	
	public static synchronized Date addDays(Date date, int value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, value);
		
		return cal.getTime();
	}
	
	public static synchronized Date convertDate(String fmt, String date) throws ParseException {
	  DateFormat format = new SimpleDateFormat(fmt);
	  return format.parse(date);
	}
  
  public static synchronized Date max(Date dt1, Date dt2) {
    return (dt1.getTime() >= dt2.getTime()) ? dt1 : dt2;
  }

  public static synchronized String getSecondWithDateFormat(long l) {
    long c = l;
    int h = (int)c/ 3600;
    c = c - (h * 3600);
    int m = (int)c/ 60;
    c = c - (m * 60);
    int s = (int)c;
    
    return String.format("%02d:%02d:%02d", h, m, s);
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

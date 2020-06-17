package com.lgcns.test.suno.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class StringUtil {
	public static String getMD5(String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return (new String(md.digest(msg.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String readLineFromConsole() {
		String line;
		Scanner cin = new Scanner(System.in);
		
		line = cin.nextLine().toUpperCase();
		cin.close();
		
		return line;
	}
}

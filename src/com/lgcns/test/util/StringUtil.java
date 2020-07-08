package com.lgcns.test.util;

import java.util.Scanner;

public class StringUtil {	
	public static String readLineFromConsole() {
		String line;
		Scanner cin = new Scanner(System.in);
		
		line = cin.nextLine();
		cin.close();
		
		return line;
	}	
	
}

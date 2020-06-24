package com.lgcns.test.util;

import java.util.Scanner;

public class CLIUtil {
	public static String readLineFromConsole() {
		String line;
		Scanner cin = new Scanner(System.in);
		
		line = cin.nextLine().toUpperCase();
		cin.close();
		
		return line;
	}

	
	
}

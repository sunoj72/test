package com.lgcns.test.util;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ProcessUtil {
	public static String executeWithReturn(List<String> exec) throws IOException, InterruptedException {
		return executeWithReturn(exec, 0, 1);
	}
	
	public static String executeWithReturn(List<String> exec, int skipLines, int waitLines) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		ProcessBuilder pb = new ProcessBuilder(exec);
		Process ps = pb.start();
		
		Scanner scan = new Scanner(ps.getInputStream()); 
		
		int i = 0, skip = 0;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			
			if (skipLines > skip ) {
				skip++;
				continue;
			}
			
			sb.append(String.format("%s\n", line));
			i++;
			
			if (waitLines > 0 && i >= waitLines) {
				break;
			}
		}
		
		ps.waitFor();
		scan.close();
			
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		ArrayList<String> exec = new ArrayList<>();
//		exec.add("cmd.exe");
//		exec.add("/c");
//		exec.add("dir");
//		exec.add("/w");
//		
//		try {
//			System.out.println(ProcessUtil.executeWithReturn(exec, 3, 1));
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		exec.clear();
//		exec.add("tasklist");
//		exec.add("/fo");
//		exec.add("csv");
//		ProcessThread thread = executeWithThread("SHELL", exec);
//	}
}
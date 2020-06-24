package com.lgcns.test.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileUtil {
	public static Scanner getReader(String filename) {
		Scanner reader = null;
		
		try {
			reader = new Scanner(new File(filename));
			return reader;
		} catch (FileNotFoundException e) {
			System.out.println("File could not found. " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static Scanner getReader(InputStream in) {
		Scanner reader = null;
		
		try {
			reader = new Scanner(in);
			return reader;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PrintWriter getWriter(String filename) {
		return getWriter(filename, false);
	}

	public static PrintWriter getWriter(String filename, boolean append) {
		PrintWriter writer = null;
				
		try {
			writer = new PrintWriter(new FileWriter(filename, append));
			return writer;
		} catch (IOException e) {
			System.out.println("File could not created. " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	

	public static PrintWriter getWriter(OutputStream out) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(new OutputStreamWriter(out));
			return writer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	
	
	public static void copyFile(String fileIn, String fileOut) {
		int len;
		int BUFFER_SIZE = 8 * 1024;
		byte[] buff = new byte[BUFFER_SIZE];
		
		try (FileInputStream in = new FileInputStream(fileIn);
			 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));) {
			
			while ((len = in.read(buff)) > 0) {
				out.write(buff, 0, len);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		// Read Text
//		Scanner r = getReader(System.in);
//		
//		// Write Text
//		PrintWriter w = getWriter(System.out);
//		while(r.hasNext()) {
//			String line = r.nextLine();
//
//			w.println(String.format("[OUT] %s", line.toUpperCase()));
//			w.flush();
//
//			if (line.equalsIgnoreCase("exit")) {
//				break;
//			}
//		}
//		
//		r.close();
//		w.close();
//
//		
//		// ByteArray
//		String fileIn = "./INPUT.BIN";
//		String fileOut = "./OUTPUT.BIN";
//		int len, BUFFER_SIZE = 4096;
//		byte[] buff = new byte[BUFFER_SIZE];
//		
//		try (FileInputStream in = new FileInputStream(fileIn);
//			 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));) {
//			
//			while ((len = in.read(buff)) > 0) {
//				out.write(buff, 0, len);
//				out.flush();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}

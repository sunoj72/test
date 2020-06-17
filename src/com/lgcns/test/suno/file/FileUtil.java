package com.lgcns.test.suno.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	public static BufferedReader getLineReader(String filename) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			return reader;
		} catch (FileNotFoundException e) {
			System.out.println("File could not found. " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static BufferedWriter getLineWriter(String filename) {
		return getLineWriter(filename, false);
	}

	public static BufferedWriter getLineWriter(String filename, boolean append) {
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(filename, append));
			return writer;
		} catch (IOException e) {
			System.out.println("File could not created. " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	

	public static BufferedReader getByteReader(String filename) {
		File f = new File(filename);
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			return reader;
		} catch (FileNotFoundException e) {
			System.out.println("File could not fouind. " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	

}

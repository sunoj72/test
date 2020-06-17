package com.lgcns.test.suno.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {
	
	// ex) PathUtil.matchFirst("glob:**" + File.separator + filename, FileSystems.getDefault().getPath(DATA_PATH, "").toString());
	// ex) PathUtil.matchFirst("glob:**" + File.separator + filename, FileSystems.getDefault().getPath(DATA_PATH, "").toString());
	
	public static String matchFirst(String glob, String location) throws IOException {
	    StringBuilder result = new StringBuilder();
	    
	    if (!glob.toLowerCase().startsWith("glob")) {
	    	glob = String.format("glob:**%s%s", File.separator, glob);
	    }
	    
	    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
	    Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

	        @Override
	        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
	            if (pathMatcher.matches(path)) {
	                result.append(path.toString());
	                return FileVisitResult.TERMINATE;
	            }
	            return FileVisitResult.CONTINUE;
	        }
	    });

	    return result.toString();
	}
	
	public static List<String> matchs(String glob, String location) throws IOException {
		List<String> result = new ArrayList<String>();

	    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
	    Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

	        @Override
	        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
	            if (pathMatcher.matches(path)) {
	            	result.add(path.toString());
	            }
	            return FileVisitResult.CONTINUE;
	        }
	    });

	    return result;
	}

	public static BufferedReader getReader(String filename, String location) {
		BufferedReader br = null;
		
		try {
			String file = matchFirst("glob:**" + File.separator + filename, location);
			br = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return br;
	}

	public static BufferedReader getReader(InputStream in) {	
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return br;
	}

	public static BufferedWriter getWriter(String filename, String location) {	
		return getWriter(filename, location, false);
	}

	public static BufferedWriter getWriter(String filename, String location, boolean append) {	
		File outFile = FileSystems.getDefault().getPath(location, filename).toFile();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile, append));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bw;
	}

	public static BufferedWriter getWriter(OutputStream out) {	
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(out));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bw;
	}
	
	public static BufferedInputStream getInputStream(String filename, String location) {
		BufferedInputStream in = null;
		
		try {
			String file = matchFirst("glob:" + File.separator + filename, location);
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return in;
	}	
	
	public static BufferedOutputStream getOutputStream(String filename, String location) {
		BufferedOutputStream out = null;
		
		try {
			File inFile = FileSystems.getDefault().getPath(location, filename).toFile();
			out = new BufferedOutputStream(new FileOutputStream(inFile));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}	
	
}

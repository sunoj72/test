package com.lgcns.test.util;

import java.io.File;
import java.io.IOException;
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
import java.util.Map;

public class PathUtil {
	
	// ex) PathUtil.matchFirst("glob:**" + File.separator + filename, FileSystems.getDefault().getPath(DATA_PATH, "").toString());
	// ex) PathUtil.matchFirst("glob:**" + File.separator + filename, FileSystems.getDefault().getPath(DATA_PATH, "").toString());
	
	public static String findFile(String filename) {
		return findFile(filename, "./");
	}
	
	public static String findFile(String filename, String location) {
		String result = "";
		
		try {
			result = matchFirst(filename, location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> listDirectory() {
		return null;
	}
	
	
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

//	public static void main(String[] args) {
//		System.out.println(findFile("INPUT.TXT", "D:/project"));
//	}
}

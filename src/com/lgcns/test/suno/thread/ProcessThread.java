package com.lgcns.test.suno.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.lgcns.test.suno.util.LogUtil;
import com.lgcns.test.suno.util.PathUtil;

public class ProcessThread extends Thread {
	private String threadName = null;
	private Process process = null;
	private List<String> exec = null;
	private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>() ;
	
//	private Queue<String> messagesEx = new ConcurrentLinkedQueue<>(1000) ;

	public ProcessThread(String name, List<String> exec) {
//		this.threadName = name;
		this.setName(name);
		this.exec = exec;
	}
	
	@Override
	public void run() {
		try {
			ProcessBuilder pb = new ProcessBuilder(exec);
			//pb.redirectErrorStream(true);
			process = pb.start();

			// Buffer Process
//			InputStream is = process.getInputStream();
//			byte[] buffer = new byte[1024];
//	        int n = 0;
//	        while ((n = is.read(buffer)) != -1) {
//	        	String tmp = new String(buffer, 0, n);
//				LogUtil.printLog(String.format("\t%s", tmp));
//	        }

			// Line Process
			String line;
			BufferedReader reader = PathUtil.getReader(process.getInputStream());
	        while ((line = reader.readLine()) != null) {
	        	messages.offer(line);
				LogUtil.printLog(String.format("[%s] %s", this.getName(), line));
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.run();
	}
	
	public synchronized void startThread() {
		this.start();
	}
	
	public synchronized void stopThread() {
		this.interrupt();
		//this.wait();
	}
	
	public InputStream getInputStream() {
		if (process == null) {
			return null;
		}
		
		return process.getInputStream();
	}
	
	public InputStream getErrorStream() {
		if (process == null) {
			return null;
		}
		
		return process.getErrorStream();
	}
	
	public OutputStream getOutputStream() {
		if (process == null) {
			return null;
		}
		
		return process.getOutputStream();
	}

	public BufferedWriter getWriter() {
		if (process == null) {
			return null;
		}
		
		return PathUtil.getWriter(process.getOutputStream());
	}

	public Process getProcess() {
		return process;
	}

	public List<String> getParameters() {
		return exec;
	}

	public ConcurrentLinkedQueue<String> getMessages() {
		return messages;
	}
}

package com.lgcns.suno.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.lgcns.test.util.FileUtil;
import com.lgcns.test.util.LogUtil;

public class ProcessThread extends Thread {
	private Process process = null;
	private List<String> exec = null;
	private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>() ;

	public ProcessThread(String name, List<String> exec) {
		this.setName(name);
		this.exec = exec;
	}
	
	@Override
	public void run() {
		try {
			ProcessBuilder pb = new ProcessBuilder(exec);
			//pb.redirectErrorStream(true);
			process = pb.start();

			// Line Process
			String line;
			Scanner reader = FileUtil.getReader(process.getInputStream());
			while (reader.hasNextLine()) {
				line = reader.nextLine();
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

	public PrintWriter getWriter() {
		if (process == null) {
			return null;
		}
		
		return FileUtil.getWriter(process.getOutputStream());
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

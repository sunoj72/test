package com.lgcns.test.suno.examples;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.lgcns.test.suno.thread.ProcessThread;
import com.lgcns.test.suno.util.LogUtil;
import com.lgcns.test.suno.util.PathUtil;
import com.lgcns.test.suno.util.ProcessUtil;

public class ProcessTest {

	public static void main(String[] args) {
		LogUtil.setDebug(true);

		LogUtil.printLogStart("APP");
		
		// Send arguments and return
		//processExecute();
		
		// Communicate with Processes
		processExecuteWithInput();
		
		LogUtil.printLogEnd("APP");
	}

	public static void processExecute() {
		String execName = "DUMMY.EXE";
		String execPath = ""; // "./EXEC";
		String msg = null;
		String exitCode = "EXIT DUMMY";
		
		try {
			String execFile = PathUtil.matchFirst(execName, execPath);
			ArrayList<String> args = new ArrayList<>();
			args.add(execFile);
			args.add("-p test");
			args.add("param1");
			args.add("param2");
			args.add("param3");
			args.add(exitCode);
			
			msg = ProcessUtil.executeWithReturn(args, 10);
			
			LogUtil.printLog(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void processExecuteWithInput() {
		List<ProcessThread> processes = new ArrayList<>();
		
		
		String execName = "DUMMY.EXE";
		String execPath = ""; // "./EXEC";
		String msg = "PROCESS%d MESSAGE%d";
		String exitCode = "EXIT DUMMY";
		
		try {
			String execFile = PathUtil.matchFirst(execName, execPath);
			ArrayList<String> args = new ArrayList<>();
			args.add(execFile);
			
			// 5개 프로세스 생성
			for (int i = 0; i < 5; i++) {
				processes.add(ProcessUtil.executeWithThread(String.format("Process%d", i), args));
				LogUtil.printLog(String.format("Process%d Created", i));
			}

			for (int i = 0; i < 3; i++) {
				// 프로세스 출력
				for (int j = 0; j < 5; j++) {
					BufferedWriter writer = processes.get(j).getWriter();
					if (writer == null) {
						break;
					}
					writer.write(String.format(msg, j, i));
					writer.newLine();
					writer.flush();
					LogUtil.printLog(String.format("Send a message to Process%d", j));
				}
				
				// 작업 대기
				Thread.sleep(5 * 1000);
			}

			// 프로세스별 출력
			for (int i = 0; i < 5; i++) {
				LogUtil.printLog(String.format("[Process%d]", i));
				ConcurrentLinkedQueue<String> queue = processes.get(i).getMessages();

				while (true) {
					String line = queue.poll();
					if (line == null) {
						break;
					}
					LogUtil.printLog(String.format("\t%s", line));
		        }
			}

			// 작업 종료
			for (int i = 0; i < 5; i++) {
				BufferedWriter writer = processes.get(i).getWriter();
				writer.write(String.format(exitCode));
				writer.newLine();
				writer.flush();
				LogUtil.printLog(String.format("Request exit to Process%d", i));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

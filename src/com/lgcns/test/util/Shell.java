package com.lgcns.test.util;

import java.util.Scanner;

enum ShellMode {
  IGNORE_CASE,
  CASESENSITIVE,
  UPPER_CASE,
  LOWER_CASE
}

class Shell  {
	public Thread thread = null;
	
	public class Console implements Runnable {
		public ShellMode charMode = ShellMode.UPPER_CASE;
		public String exit = "EXIT";
		public boolean appExit = false;
//		public IMessageExecutor executor = null;
		
//    public Console (ShellMode mode, String exitCmd, boolean applicationExit, IMessageExecutor executor) {
		public Console (ShellMode mode, String exitCmd, boolean applicationExit) {
			this.charMode = mode;
			this.exit = exitCmd;
			this.appExit = applicationExit;
//			this.executor = executor;
		}

		@Override
		public void run() {
			String line = "";
			
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					// 문자열 처리
					if (this.charMode == ShellMode.UPPER_CASE) { line = scanner.nextLine().trim().toUpperCase(); }
					if (this.charMode == ShellMode.LOWER_CASE) { line = scanner.nextLine().trim().toLowerCase(); }

					// 종료 처리
					if (this.charMode == ShellMode.CASESENSITIVE) {
						if (this.exit.equals(line)) {
							scanner.close();
							exitIfWant();
							return;
						}
					} else {
						if (this.exit.equalsIgnoreCase(line)) {
							scanner.close();
							exitIfWant();
							return;
						}
					}
					
					// TODO:명령처리
					System.out.println(line);
          //if (this.executor != null) {
          //	this.executor.execute(line);
          //}
					
					// 스레드 인터럽트 처리
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {}
			
		}
		
		private void exitIfWant() {
			if (this.appExit) {
				System.exit(0);
			}
		}
		
	}
	
//  public void start(ShellMode mode, String exitCmd, boolean applicationExit, IMessageExecutor executor) {
	public void start(ShellMode mode, String exitCmd, boolean applicationExit) {
		Console console = new Console(mode, exitCmd, applicationExit);
		thread = new Thread(console);
		thread.start();
	}
	
	public void stop() {
		if (this.thread != null) {
			this.thread.interrupt();
		}
	}
	
//	public static void main(String[] args) {
//		IMessageExecutor executor = new SimpleMessageExecutor();
//		Shell shell = new Shell();
//		shell.start(ShellCharacterMode.UPPER_CASE, "Exit", false, executor);
//	}	
}

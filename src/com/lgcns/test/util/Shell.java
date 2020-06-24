package com.lgcns.test.util;

import java.util.Scanner;

import com.lgcns.test.suno.model.IMessageExecutor;


public class Shell  {
	private Thread thread = null;
	public class Console implements Runnable {
		private ShellMode charMode = ShellMode.UPPER_CASE;
		private String exit = "EXIT";
		private boolean appExit = false;
		private IMessageExecutor executor = null;
		
		public Console (ShellMode mode, String exitCmd, boolean applicationExit, IMessageExecutor executor) {
			this.charMode = mode;
			this.exit = exitCmd;
			this.appExit = applicationExit;
			this.executor = executor;
		}

		@Override
		@SuppressWarnings("resource")
		public void run() {
			String line = "";
			Scanner scanner = null;

			try {
				scanner = new Scanner(System.in);
				
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
					
					// 라인 처리
					if (this.executor != null) {
						this.executor.execute(line);
					}

					
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
	
	public void start(ShellMode mode, String exitCmd, boolean applicationExit, IMessageExecutor executor) {
		Console console = new Console(mode, exitCmd, applicationExit, executor);
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

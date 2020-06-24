package com.lgcns.test.suno.net.client;

import java.io.IOException;

import com.lgcns.test.suno.net.model.Message;
import com.lgcns.test.suno.net.util.MessageBuilder;
import com.lgcns.test.util.Shell;
import com.lgcns.test.util.ShellCharacterMode;

public class ClientApp {
	private NetworkClient client = null;
	private Shell console = null;

	private boolean useThread = false;
	private boolean useShell = false;
	private String serverIP = "localhost";
	private int serverPort = 7777;

	public ClientApp(String addr, int port, boolean useThread, boolean useShell) throws IOException {
		this.serverIP = addr;
		this.serverPort = port;
		this.useThread = useThread;
		this.useShell = useShell;
	}

	public void start() throws IOException {
		client = new NetworkClient(this.serverIP, this.serverPort);
		client.connect();

		if (this.useThread) {
			client.startThread();
		}

		if (this.useShell) {
			console = new Shell();
			//TODO:
			console.start(ShellCharacterMode.IGNORE_CASE, "Exit", false, null);
		}
	}

	public void stop() throws IOException {
		if (this.useThread) {
			client.stopThread();
		}

		client.close();

		if (this.useShell) {
			console.stop();
		}
	}

	public synchronized void sendMessage(Message msg) throws IOException, IllegalArgumentException {
		client.sendMessage(msg);
	}

	public synchronized Message readMessage() throws IOException {
		return client.readMessage();
	}

	public synchronized Message sendMessagewithResponse(String msg) throws IOException, IllegalArgumentException {
		return client.sendMessagewithResponse(msg);
	}

	public synchronized Message sendMessagewithResponse(Message msg) throws IOException, IllegalArgumentException {
		return sendMessagewithResponse(msg);
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		String requestMessage = "CMD1#hello";
		String serverIP = "localhost";
		int serverPort = 7777;
		boolean useThread = true;
		boolean useShell = true;

		if (args.length >= 1) {
			requestMessage = args[0];
		}

		ClientApp app = new ClientApp(serverIP, serverPort, useThread, useShell);
		app.start();

		Message request = MessageBuilder.build(requestMessage);

		try {
			app.sendMessage(request);

			if (!useThread && !useShell) {
				app.readMessage();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!useThread && !useShell) {
			app.stop();
			System.exit(0);
		}
	}
}
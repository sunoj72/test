package com.lgcns.test.suno.net.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

import com.lgcns.test.suno.net.model.Message;
import com.lgcns.test.suno.net.server.NetworkServer;
import com.lgcns.test.suno.net.util.MessageBuilder;

public class ClientHandler implements Runnable {
	private NetworkServer server;
	private Socket client;
	private Scanner inputStream;

	public ClientHandler(Socket client, NetworkServer server) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			inputStream = new Scanner(client.getInputStream());

			while (true) {
				if (!inputStream.hasNext())
					return;

				String line = inputStream.next().trim();
				Message msg = MessageBuilder.build(line);
				this.processMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void processMessage(Message req) throws IOException {
		if (!client.isClosed()) {
			Message resp = server.getMessageHadler().processMessage(req);

			if (req.getCommand().isBroadcast()) {
				sendMessageToAll(resp);
			} else {
				sendMessage(resp);
			}
		}
	}

	public synchronized void sendMessage(Socket client, Message msg) throws IOException {
		if (!client.isClosed()) {
			PrintWriter pw = new PrintWriter(client.getOutputStream());
			pw.println(msg);
			pw.flush();
			System.out.println(String.format("Sent to %s: [%s]", client.getRemoteSocketAddress(), msg));
		}
	}

	public synchronized void sendMessage(Message msg) throws IOException {
		sendMessage(this.client, msg);
	}

	public synchronized void sendMessageToAll(Message msg) {
		for (Iterator<Socket> it = server.getClients().iterator(); it.hasNext();) {
			try {
				Socket client = it.next();
				sendMessage(client, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
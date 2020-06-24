package com.lgcns.test.net.client;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.lgcns.test.suno.net.model.Message;
import com.lgcns.test.suno.net.util.MessageBuilder;

public class Client implements Runnable {
	// private MessageHandler messageHandler;
	private Thread thread = null;
	private Socket client;
	private String addr = "localhost";
	private int port = 7777;

	public Client(String addr, int port) throws IOException {
		// this.messageHandler = new ClientSideMessageHandler();

    this.addr = addr;
    this.port = port;
	}

	public void connect() throws IOException {
		// connect to server
		InetAddress host = null;
		try {
			host = InetAddress.getByName(this.addr);
		} catch (UnknownHostException e1) {
			System.out.println("Host not found");
		}
		System.out.println("You are now connected to: " + host.getHostAddress());

		client = null;
		try {
			client = new Socket(host, port);
			client.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("not found");
		}
	}

	public void close() throws IOException {
		client.close();
		System.out.println("Clients closed..");
	}

	public void startThread() {
		thread = new Thread(this);
		thread.start();
	}

	public void stopThread() {
    if (thread != null) {
      thread.interrupt();
    }
	}

	public synchronized void sendMessage(String msg) throws IOException, IllegalArgumentException {
		if (msg == null || msg.length() == 0) {
			throw new IllegalArgumentException();
		}

		if (!this.client.isClosed()) {
			PrintWriter pw = new PrintWriter(this.client.getOutputStream());
			pw.println(msg);
			pw.flush();
			System.out.println(String.format("Sent to %s: [%s]", this.client.getRemoteSocketAddress(), msg));
		}
	}

	public synchronized void sendMessage(Message msg) throws IOException, IllegalArgumentException {
		if (msg != null) {
			sendMessage(msg.toString());
		} else {
			throw new IllegalArgumentException();
		}
	}

	public synchronized Message readMessage() throws IOException {
		Scanner inputStream = new Scanner(client.getInputStream());

		while(true) {
			if (inputStream.hasNextLine()) {
				String line = inputStream.nextLine();
				Message response = MessageBuilder.build(line);
				System.out.println(String.format("Received: [%s]", line));
				inputStream.close();
				return response;
			}
		}
	}

	public synchronized Message sendMessagewithResponse(String msg) throws IOException, IllegalArgumentException {
		this.sendMessage(msg);
		return this.readMessage();
	}

	public synchronized Message sendMessagewithResponse(Message msg) throws IOException, IllegalArgumentException {
		this.sendMessage(msg);
		return this.readMessage();
	}

	@Override
	public void run() {
		try {
			Scanner inputStream = new Scanner(client.getInputStream());

			while (true) {
				if (inputStream.hasNextLine())
					System.out.println(String.format("Received in thread: [%s]", inputStream.nextLine()));
			}
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
	}
}
package com.lgcns.test.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.lgcns.test.net.model.IMessageHandler;
import com.lgcns.test.net.model.ServerMessageHandler;

public class Server {
	private static final int PORT = 7777;

	private ServerSocket server;
	private ArrayList<Socket> clients;
	private IMessageHandler messageHandler;

	public Server() {
		try {
			messageHandler = new ServerMessageHandler();
			server = new ServerSocket(PORT);
			server.setReuseAddress(true);
			System.out.println("Accepting clients...");
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}

		this.clients = new ArrayList<>();
	}

	public ArrayList<Socket> getClients() {
		return this.clients;
	}

	public IMessageHandler getMessageHadler() {
		return this.messageHandler;
	}

	public void setMessageHadler(IMessageHandler handler) {
		this.messageHandler = handler;
	}

	public void startServer() {
		Socket client;
		
		while (true) {
			// wait for a client
			try {
				client = server.accept();
				clients.add(client);
				System.out.println("New client accepted..." + client.getRemoteSocketAddress());
				System.out.println("Total Users: " + clients.size());

				ClientHandler handler = new ClientHandler(client, this);
				Thread thread = new Thread(handler);
				thread.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopServer() {
	}	

	public static void main(String[] args) throws IOException {
		new Server().startServer();
	}
}
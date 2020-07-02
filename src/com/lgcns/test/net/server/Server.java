package com.lgcns.test.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.lgcns.test.net.model.IMessageHandler;
import com.lgcns.test.net.model.ServerMessageHandler;

public class Server extends Thread {
	public int port = 7777;
	public ServerSocket server;
	public ArrayList<Connection> clients = new ArrayList<>();
	public IMessageHandler messageHandler;

	public Server() {
	}

	@Override
	public void run() {
    try {
      messageHandler = new ServerMessageHandler(this);
      server = new ServerSocket(port);
      server.setReuseAddress(true);
      System.out.println("Accepting clients...");
    } catch (IOException e) {
      System.out.println(e.getStackTrace());
    }

	  Socket client;
		
		while (true) {
			try {
			  if (this.isInterrupted()) {
          return;
			  }

				client = server.accept();
				System.out.println("New client accepted..." + client.getRemoteSocketAddress());
				System.out.println("Total Users: " + clients.size());

				Connection handler = new Connection(client, this);
				clients.add(handler);
				
				Thread thread = new Thread(handler);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
  
  public Thread startServer() {
    this.start();
    
    return this;
  } 
	
	public void stopServer() {
	  this.interrupt();
    System.out.println("Service closed");
	}	

	public static void main(String[] args) throws IOException {
	  Server server = new Server();
	  server.port = 5555;
	  server.startServer();
	  
	  try {
      Thread.sleep(10 * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
	  
	  server.stopServer();
	}
}
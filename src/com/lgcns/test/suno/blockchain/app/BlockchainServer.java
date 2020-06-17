package com.lgcns.suno.blockchain.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BlockchainServer {
  private static final int PORT = 1234;

  private ServerSocket server;
  private ArrayList<Socket> clients;

  public BlockchainServer() {
		try {
			server = new ServerSocket(PORT);
			server.setReuseAddress(true);
		} catch (IOException e)
		{
			System.out.println(e.getStackTrace());
    }

    clients = new ArrayList<>();
  }

  public ArrayList<Socket> getClients() {
    return clients;
  }

	public void startServer() throws IOException {
    System.out.println("Accepting clients...");

		while(true)
		{
			// wait for a client
			Socket client = server.accept();
			clients.add(client);
			System.out.println("New client accepted..." + client.getRemoteSocketAddress());
			System.out.println("Total sessions: " + clients.size());
			BlockchainClientHandler handler = new BlockchainClientHandler(client, this);
			Thread thread = new Thread(handler);
			thread.start();
		}
  }

  public static void main(String[] args) throws IOException {
      new BlockchainServer().startServer();
  }
}
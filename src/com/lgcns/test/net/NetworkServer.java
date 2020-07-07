package com.lgcns.test.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkServer {
  public ServerSocket server;
  public ArrayList<ClientConnection> clients = new ArrayList<>();
  
  public void start(int port) {
    try {
      server = new ServerSocket(port);
      server.setReuseAddress(true);
      System.out.println("NetworkServer listening...");
    } catch (IOException e) {
      System.out.println(e.getStackTrace());
    }

    Socket client;
    
    while (true) {
      try {
//        if (this.isInterrupted()) return;

        client = server.accept();
        System.out.println("New Client connected..." + client.getRemoteSocketAddress());
        System.out.println("Total Clients: " + clients.size());

        ClientConnection handler = new ClientConnection(client);
        clients.add(handler);
        
        Thread thread = new Thread(handler);
        thread.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}

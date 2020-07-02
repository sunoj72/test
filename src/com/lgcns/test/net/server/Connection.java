package com.lgcns.test.net.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements Runnable {
	Server server;
	Socket client;
	Scanner inputStream;

	public Connection(Socket client, Server server) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			inputStream = new Scanner(client.getInputStream());

			while (true) {
        if (Thread.currentThread().isInterrupted())
          break;

        if (!inputStream.hasNext())
					return;
				
				String line = inputStream.next().trim();
				this.processMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    Thread.currentThread().interrupt();
    
	}

	public synchronized void processMessage(String req) throws IOException {
			server.messageHandler.processMessage(this, req);
//			if (req.getCommand().isBroadcast()) {
//				sendMessageToAll(resp);
//			} else {
//				sendMessage(resp);
//			}
	}

  public synchronized void sendMessage(String msg) throws IOException {
    if (!client.isClosed()) {
      PrintWriter pw = new PrintWriter(client.getOutputStream());
      pw.println(msg);
      pw.flush();
      System.out.println(String.format("Sent to %s: [%s]", client.getRemoteSocketAddress(), msg));
    }
  }
}
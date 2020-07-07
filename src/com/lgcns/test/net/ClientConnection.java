package com.lgcns.test.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.lgcns.test.util.FileUtil;

public class ClientConnection implements Runnable {
  Socket client;
  boolean isMobile = false;
  Scanner reader;
  
  public ClientConnection(Socket conn) {
    this.client = conn;
  }
  
  public synchronized void processMessage(String msg) {
    //TODO:기능구현-메시지처리
    if (msg.substring(0, 3).equalsIgnoreCase("BUS")) {
    } else if (msg.substring(0, 3).equalsIgnoreCase("STA")) {
    } else if (msg.equalsIgnoreCase("MOBILE")) {
    } else if (msg.equalsIgnoreCase("PRINT")) {
    } else {
    }
  }

  @Override
  public void run() {
    try {
      reader = new Scanner(client.getInputStream());

      while (true) {
        if (Thread.currentThread().isInterrupted())
          break;

        if (!reader.hasNext())
          return;
        
        String line = reader.next().trim().toUpperCase();
        if (line.length() > 0) {
          processMessage(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void sendMessage(String msg) {
    try {
      PrintWriter writer = FileUtil.getWriter(this.client.getOutputStream());
      writer.print(msg);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void close() {
    try {
      PrintWriter writer = FileUtil.getWriter(this.client.getOutputStream());
      writer.close();
      this.client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

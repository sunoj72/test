package com.lgcns.test.net;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import com.lgcns.test.util.FileUtil;

public class NetworkServer implements Runnable {
  public static final String MSG_BUS = "BUS";
  public static final String MSG_STA = "STA";
  public static final String MSG_MOBILE = "MOBILE";
  public static final String MSG_PRINT = "PRINT";

  public static final String MSG_FILE = ".FILE";
  public static String MSG_FILE_COMPLETED = ".COMPLETED";
  public static String MSG_ALL_COMPLETED = ".ALL_COMPLETED";
  public static final String PATH_SAVE = "./SERVER/";
  
  public ServerSocket server = null;
  public ArrayList<ClientConnection> clients = new ArrayList<>();
  public int port = 5555;
  boolean useBase64 = false; // Plain 전송시, 마지막라인이 추가될 수 있음. OS에 따라 CRLF와 LF 차이도 발생. 
  
  public NetworkServer(int port) {
    this.port = port;
  }
  
  public NetworkServer(int port, boolean useBase64) {
    this.port = port;
    this.useBase64 = useBase64;
  }

  @Override
  public void run() {
    try {
      server = new ServerSocket(this.port);
      server.setReuseAddress(true);
      System.out.println("NetworkServer listening... :" + this.port);
    } catch (IOException e) {
      System.out.println(e.getStackTrace());
    }

    while (!Thread.currentThread().isInterrupted()) {
      try {
        Socket client = server.accept();
        System.out.println("New Client connected..." + client.getRemoteSocketAddress());
        System.out.println("Total Clients: " + clients.size());

        ClientConnection handler = new ClientConnection(client, this.useBase64);
        clients.add(handler);
        
        Thread thread = new Thread(handler);
        thread.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    Thread t = new Thread(new NetworkServer(7777, true));
    t.start();
  }

}

class ClientConnection implements Runnable {
  Socket client;
  boolean isMobile = false;
  boolean isFileReceived = false;
  boolean useBase64 = false;
  
  public ClientConnection(Socket conn, boolean useBase64) {
    this.client = conn;
    this.useBase64 = useBase64;
  }

  @Override
  public void run() {
    try (Scanner reader = new Scanner(client.getInputStream())) {
      while (reader.hasNextLine()) {
        if (Thread.currentThread().isInterrupted())
          break;

        String line = reader.nextLine().trim();
        if (line.length() > 0) {
          if (this.isFileReceived) {
            try {
              receiveFile(reader, line);
            } catch (Exception e) {
              e.printStackTrace();
              this.isFileReceived = false;
            }
          } else {
            processMessage(line);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void processMessage(String msg) {
    //TODO:기능구현-메시지처리
    if (msg.length() >= 3 && msg.substring(0, 3).equalsIgnoreCase(NetworkServer.MSG_BUS)) {
    } else if (msg.length() >= 3 && msg.substring(0, 3).equalsIgnoreCase(NetworkServer.MSG_STA)) {
    } else if (msg.equalsIgnoreCase(NetworkServer.MSG_MOBILE)) {
    } else if (msg.equalsIgnoreCase(NetworkServer.MSG_PRINT)) {
    } else if (msg.equalsIgnoreCase(NetworkServer.MSG_FILE)) {
      this.isFileReceived = true;
      System.out.println("receiving a file");
    } else {
    }
  }
  
  public synchronized void receiveFile(Scanner reader, String filename) throws Exception {
    //String filename = reader.nextLine();
    PrintWriter pw = null;
    BufferedOutputStream os = null;
    
    if (this.useBase64) {
      // 바이너리 파일
      os = new BufferedOutputStream(new FileOutputStream(NetworkServer.PATH_SAVE + filename));
    } else {
      // 텍스트 파일
      pw = new PrintWriter(new FileWriter(NetworkServer.PATH_SAVE + filename));
    }
    
    while(reader.hasNextLine()) {
      String line = reader.nextLine();
      if (!line.equalsIgnoreCase(NetworkServer.MSG_FILE_COMPLETED)) {
        if (this.useBase64) {
          os.write(decodeText(line));
          os.flush();
        } else {
          pw.println(line);
          pw.flush();
        }
      } else {
        if (this.useBase64) {
          os.flush();
          os.close();
        } else {
          pw.flush();
          pw.close();
        }

        // 파일 전송 종료
        this.isFileReceived = false;
        System.out.println(String.format("Server: %s received.", filename));
        break;
      }
    }
  }

  public synchronized byte[] decodeText(String msg) {
    return Base64.getDecoder().decode(msg);
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

package com.lgcns.test.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import com.lgcns.test.util.LogUtil;

public class NetworkClient implements Runnable {
  public static String SERVER = "127.0.0.1";
  public static int PORT = 7777;
  
  public static final String MSG_FILE = ".FILE";
  public static String MSG_FILE_COMPLETED = ".COMPLETED";
  public static String MSG_ALL_COMPLETED = ".ALL_COMPLETED";
  
  Socket client;
  boolean isMobile = false;
  boolean isFileReceived = false;
  boolean useBase64 = false;
  
  public NetworkClient() {
    super();
  }

  @Override
  public void run() {
    try {
      Socket client = connect(NetworkClient.SERVER, NetworkClient.PORT);
      
      //TODO: 구현
      PrintWriter pw = new PrintWriter(client.getOutputStream());
      sendFiles(pw);
      pw.close();

      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Socket connect(String server, int port) throws UnknownHostException, IOException {
    // 서버에 연결
    Socket client = null;
    
    client = new Socket(server, port);
    client.setReuseAddress(true);
    
    return client;
  }
  
  public void sendFiles(PrintWriter pw) {
    // 파일 목록 구하기
    File base = new File("./CLIENT");
    File[] files = base.listFiles();
      
    for (int i = 0; i < files.length; i++) {
      if (this.useBase64) {
        sendFile(pw, files[i]);
      } else {
        sendTextFile(pw, files[i]);
      }
    }
  }

  public synchronized void sendTextFile(PrintWriter pw, File file) {
    Scanner reader = null;
    
    // 파일 전송 시작
    pw.println(MSG_FILE);
    pw.flush();
    
    // 파일 이름
    pw.println(file.getName());
    pw.flush();

    try {
      reader = new Scanner(file);
    } catch (FileNotFoundException e) {
      LogUtil.printLog(String.format("%s file not found", file.getName()));
      return;
    }
    
    // 파일 내용 전송
    while (reader.hasNextLine()) {
      String line = reader.nextLine();
      pw.println(line);
      pw.flush();
    }
    
    reader.close();
    pw.println(MSG_FILE_COMPLETED);
    pw.flush();
    
    try {
      System.out.println(String.format("Client: %s, %d bytes done", file.getName(), Files.size(file.toPath())));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void sendFile(PrintWriter pw, File file) {
    BufferedInputStream bis = null;
    
    // 파일 전송 시작
    pw.println(MSG_FILE);
    pw.flush();
    
    // 파일 이름
    pw.println(file.getName());
    pw.flush();

    try {
      bis = new BufferedInputStream(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      LogUtil.printLog(String.format("%s file not found", file.getName()));
      return;
    }
    
    // 파일 내용 전송
    int len = 0, BUFF_SIZE = 512;
    byte[] buff = new byte[BUFF_SIZE];

    try {
      while ((len = bis.read(buff)) > -1) {
        if (len != BUFF_SIZE) {
          buff = Arrays.copyOfRange(buff, 0, len);
        }
        pw.println(encodeText(buff));
        pw.flush();
      }
      
      bis.close();
      pw.println(MSG_FILE_COMPLETED);
      pw.flush();
      
      System.out.println(String.format("Client: %s done", file.getName()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized String encodeText(byte[] buff) {
    return Base64.getEncoder().encodeToString(buff);
  }
  
  public static void main(String[] args) {
    LogUtil.DEBUG = true;
    NetworkClient client = new NetworkClient();
    client.useBase64 = true;
    Thread t = new Thread(client);
    t.start();
  }
}

package com.lgcns.test.net.model;

import java.io.IOException;
import java.util.Iterator;

import com.lgcns.test.net.server.Connection;
import com.lgcns.test.net.server.Server;

public class ServerMessageHandler implements IMessageHandler {
  Server server;
  
  public ServerMessageHandler(Server server) {
    this.server = server;
  }


  public synchronized String processMessage(Connection conn, String request) {
	  Message req = Message.fromString(request); 
	  Message resp = executeMessage(conn, req);
    return resp.toString();
  }
  
  
  private synchronized Message executeMessage(Connection conn, Message req) {
    //TODO:메시지 처리기 구현
    try {
      conn.sendMessage(req.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
	  return null;
  }
  

  public synchronized void sendMessageToAll(String msg) {
    Iterator<Connection> it = server.clients.iterator();
    while(it.hasNext()) {
      try {
        Connection client = it.next();
        client.sendMessage(msg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
package com.lgcns.test.net.model;

public class ServerMessageHandler implements IMessageHandler {

  public synchronized Message processMessage(Message request) {
    return new Message(request.getCommand(), request.getMessageBody());
  }

  public synchronized String processMessage(String request) {
    return request;
  }
}
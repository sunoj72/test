package com.lgcns.test.net.model;

public class Message {
  public static final String TOKEN_SEPARATOR = "#";

  MessageEnum command;
  String messageBody;

  public Message(MessageEnum cmd, String messageBody) {
    this.command = cmd;
    this.messageBody = messageBody;
  }

  public static Message fromString(String request) {
	  //TODO:구현
	  return null;
  }
  
  @Override
  public String toString() {
	  //TODO:구현
    if (this.messageBody == null || this.messageBody.length() == 0) {
      return String.format("%s", this.command.getCommandString());
    } else {
      return String.format("%s%s%s", this.command.getCommandString(), TOKEN_SEPARATOR, this.messageBody);
    }
  }
}
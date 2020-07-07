package com.lgcns.test.suno.net.model;

public enum MessageEnum {
  COMMAND_1("CMD1", false),
  COMMAND_2("CMD2", false);

  private String command;
  private boolean broadcast;

  MessageEnum(String cmd, boolean broadcast) {
    this.command = cmd;
    this.broadcast = broadcast;
  }

  public String getCommandString() {
    return this.command;
  }

  public boolean isBroadcast() {
    return this.broadcast;
  }

  public static MessageEnum getEnum(String value) {
    for(MessageEnum v : MessageEnum.values())
      if(v.getCommandString().equalsIgnoreCase(value)) return v;

      throw new IllegalArgumentException();
  }
}
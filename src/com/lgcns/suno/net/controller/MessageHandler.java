package com.lgcns.test.suno.net.controller;

import com.lgcns.test.suno.net.model.Message;

public interface MessageHandler {
  public String processMessage(String msg);
  public Message processMessage(Message msg);
}
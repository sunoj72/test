package com.lgcns.test.net.model;

import com.lgcns.test.net.server.Connection;

public interface IMessageHandler {
  public String processMessage(Connection conn, String msg);
}
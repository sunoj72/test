package com.lgcns.test.net.util;

import com.lgcns.test.net.model.Message;
import com.lgcns.test.net.model.MessageEnum;

public class MessageBuilder {
  public static Message build(String msg) {
    if (msg == null || msg.length() == 0) {
      return null;
    }

    try {
      String[] tokens = tokenize(msg);
      MessageEnum cmd =  MessageEnum.getEnum(tokens[0]);
      Message message = null;
      if (tokens.length == 1) {
        message = new Message(cmd, null);
      } else {
        message = new Message(cmd, tokens[1]);
      }

      return message;
    } catch (Exception e) {
      return null;
    }
  }

  private static String[] tokenize(String msg) {
    return tokenize(msg, Message.TOKEN_SEPARATOR);
  }

  private static String[] tokenize(String msg, String tokenSeparator) {
    return msg.split(tokenSeparator);
  }
}
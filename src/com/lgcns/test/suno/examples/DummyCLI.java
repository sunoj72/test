package com.lgcns.test.suno.examples;

import java.util.Scanner;

public class DummyCLI {
  private static final String EXIT = "EXIT";

  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);

    while(true) {
      if (s.hasNext()) {
        String msg = s.nextLine();
        if (msg.equals(EXIT)) {
          break;
        }
        System.out.println(msg);
      }
      try {
        Thread.sleep(0);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    s.close();
  }
}
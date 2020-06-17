package com.lgcns.suno.blockchain.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;

import javax.xml.bind.DatatypeConverter;

public class Pem {
  private Key key;
  private String desc;

  public Pem(Key key, String desc) {
    this.key = key;
    this.desc = desc;
  }

  
  
  @Override
  public String toString() {
    return String.format("Key: %s%sDescription: %s%s", this.key, System.lineSeparator(), this.desc, System.lineSeparator());
  }

  public void write(String filename) throws FileNotFoundException, IOException {
    BufferedOutputStream bos = null;

    try {
      bos = new BufferedOutputStream(new FileOutputStream(filename));
      byte[] decoded = DatatypeConverter.printBase64Binary(key.getEncoded()).getBytes();
      // bos.write(String.format("-----BEGIN %s-----\n", this.desc).getBytes());
      bos.write(decoded);
      // bos.write(String.format("\n-----END %s-----\n", this.desc).getBytes());
    } finally {
      if (bos != null) {
        bos.close();
      }
    }
  }
}
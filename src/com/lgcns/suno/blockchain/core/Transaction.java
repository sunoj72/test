package com.lgcns.suno.blockchain.core;

import java.security.PublicKey;
import java.sql.Timestamp;

import com.lgcns.suno.blockchain.util.Util;

public class Transaction {
  String signature;
  PublicKey sender;
  PublicKey receiver;
  double amount;
  Timestamp timestamp;

  public Transaction (Wallet wallet, PublicKey receiver, double amount, String timestamp) throws Exception {
    this.sender = wallet.getPublicKey();
    this.receiver = receiver;
    this.amount = amount;
    this.timestamp = Timestamp.valueOf(timestamp);
    this.signature = wallet.sign(getData());
  }

  public String getSignature() {
    return signature;
  }
  public void setSignature(String signature) {
    this.signature = signature;
  }
  public PublicKey getSender() {
    return sender;
  }
  public void setSender(PublicKey sender) {
    this.sender = sender;
  }
  public PublicKey getReceiver() {
    return receiver;
  }
  public void setReceiver(PublicKey receiver) {
    this.receiver = receiver;
  }
  public double getAmount() {
    return amount;
  }
  public void setAmount(double amount) {
    this.amount = amount;
  }
  public Timestamp getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public String getData() {
    return String.format("%s -> %s : %f (%s)",
      Util.getHash(sender.toString()),
      Util.getHash(receiver.toString()),
      amount, timestamp);
  }

  public String getInformation() {
    return String.format("<%s>\n%s -> %s : %f (%s)",
      this.signature,
      Util.getHash(sender.toString()),
      Util.getHash(receiver.toString()),
      amount, timestamp);
  }
}
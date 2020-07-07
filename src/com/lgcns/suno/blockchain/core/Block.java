package com.lgcns.suno.blockchain.core;

import java.security.Signature;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import com.lgcns.suno.blockchain.util.Util;

public class Block {
  private static final String ALGORITHM = "SHA1withRSA";

  private int blockID;
  private String prevBlockHash;
  // private String merkleHash;
  private int nonce;
  private ArrayList<Transaction> txList;

  public Block(int blockID, String prevBlockHash, int nonce, ArrayList<Transaction> txList) {
    this.blockID = blockID;
    this.prevBlockHash = prevBlockHash;
    this.nonce = nonce;
    this.txList = txList;
  }

  public int getBlockID() {
    return blockID;
  }
  public void setBlockID(int blockID) {
    this.blockID = blockID;
  }
  public int getNonce() {
    return nonce;
  }
  public void setNonce(int nonce) {
    this.nonce = nonce;
  }
  public String getPrevBlockHash() {
	  return prevBlockHash;
  }
  public void setPrevBlockHash(String prevBlockHash) {
	  this.prevBlockHash = prevBlockHash;
  }
  // public String getMerkleHash() {
  //   return merkleHash;
  // }
  // public void setMerkleHash(String merkleHash) {
  //   this.merkleHash = merkleHash;
  // }

  public boolean verifyTransaction(Transaction tx) throws Exception {
    Signature signature;
    byte[] baText = tx.getData().getBytes();

    signature = Signature.getInstance(ALGORITHM);
    signature.initVerify(tx.getSender());
    signature.update(baText);
    byte[] baSignature = DatatypeConverter.parseHexBinary(tx.getSignature());

    return signature.verify(baSignature);
  }

  public void addTransaction(Transaction tx) throws Exception {
    if (verifyTransaction(tx)) {
      System.out.println("Valid transaction");
      txList.add(tx);
    } else {
      System.out.println("Invalid Transaction");
    }
  }

  public String getBlockHash() {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < txList.size(); i++) {
      sb.append(txList.get(i).getInformation());
    }

    return Util.getHash(nonce + sb.toString() + prevBlockHash);
  }

  public void mine() {
    while(true) {
      if(getBlockHash().substring(0, 4).equals("0000")) {
        System.out.println("The key of the block (" + blockID + ") was found.");
        break;
      }

      nonce++;
    }
  }

  public void showInformation() {
    System.out.println("--------------------------------------");
    System.out.println("Block ID: " + getBlockID());
    System.out.println("Block Hash: " + getBlockHash());
    System.out.println("Previous Block Hash: " + getPrevBlockHash());
    System.out.println("Nonce: " + getNonce());
    System.out.println("Transaction Count: " + txList.size());
    for(int i = 0; i < txList.size(); i++) {
      System.out.println(txList.get(i).getInformation());
    }
    System.out.println("--------------------------------------");
  }
}
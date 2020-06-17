package com.lgcns.suno.blockchain.core;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.xml.bind.DatatypeConverter;

import com.lgcns.suno.blockchain.util.EC;

public class Wallet {
  private static final String ALGORITHM = "SHA1withRSA";

  private PrivateKey privateKey;
  private PublicKey publicKey;

  public PrivateKey getPrivateKey() {
    return privateKey;
  }
  public PublicKey getPublicKey() {
    return publicKey;
  }

  public void setFromFile (String privateKeyFile, String publicKeyFile) throws Exception {
    this.privateKey = (new EC()).readPrivateKeyFromFile(privateKeyFile);
    this.publicKey = (new EC()).readPublicKeyFromFile(publicKeyFile);
  }

  public String sign(String data) throws Exception {
    Signature signature;
    signature = Signature.getInstance(ALGORITHM);
    signature.initSign(privateKey);
    byte[] baText = data.getBytes();
    signature.update(baText);
    byte[] baSignature = signature.sign();
    return DatatypeConverter.printHexBinary(baSignature);
  }
}
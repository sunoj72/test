package com.lgcns.suno.blockchain.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class EC {
  public void generate(String privateKeyName, String publicKeyName) throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);

    KeyPair kp = generator.generateKeyPair();
    System.out.println("One key pair was created.");

    PrivateKey priv = kp.getPrivate();
    PublicKey pub = kp.getPublic();

    writePemFile(priv, "RSA PRIVATE KEY", privateKeyName);
    writePemFile(pub, "RSA PUBLIC KEY", publicKeyName);
  }

  private void writePemFile(Key key, String desc, String filename) throws FileNotFoundException, IOException {
    Pem pemFile = new Pem(key, desc);
    pemFile.write(filename);
    System.out.println(String.format("RSA CryptoKey exported %s -> %s", desc, filename));
  }

  public PrivateKey readPrivateKeyFromFile(String privateKeyFile)
    throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    String data = readString(privateKeyFile);
    System.out.println("RSA PrivateKey loaded: " + privateKeyFile);
    System.out.println(data);
    // data = data.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
    // data = data.replace("\n-----END RSA PRIVATE KEY-----\n", "");

    byte[] decoded = DatatypeConverter.parseBase64Binary(data);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory factory = KeyFactory.getInstance("RSA");
    PrivateKey key = factory.generatePrivate(spec);

    return key;
  }

  public PublicKey readPublicKeyFromFile(String publicKeyFile)
    throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    String data = readString(publicKeyFile);
    System.out.println("RSA PublicKey loaded: " + publicKeyFile);
    System.out.println(data);
    // data = data.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
    // data = data.replace("\n-----END RSA PUBLIC KEY-----\n", "");

    byte[] decoded = DatatypeConverter.parseBase64Binary(data);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory factory = KeyFactory.getInstance("RSA");
    PublicKey key = factory.generatePublic(spec);

    return key;
  }

  public String readString(String filename)
    throws FileNotFoundException, IOException {

    String pem = "", line;
    BufferedReader br = new BufferedReader(new FileReader(filename));

    while ((line = br.readLine()) != null) {
      pem += line;
    }

    br.close();

    pem.trim();
    return pem;
  }
}
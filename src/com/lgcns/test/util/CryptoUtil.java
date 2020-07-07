package com.lgcns.test.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtil {
  public static String encode64(String msg) {
    return new String(Base64.getEncoder().encode(msg.getBytes()));
  }
  
  public static String decode64(String msg) {
    return new String(Base64.getDecoder().decode(msg));
  }

  public static String getMD5(String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return toHexString(md.digest(msg.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String getSHA256(String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return toHexString(md.digest(msg.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String toHexString(byte[] bytes) {
	    StringBuilder hexString = new StringBuilder();

	    for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(0xFF & bytes[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }

	    return hexString.toString();
	}
	
	
//	public static void main(String[] args) {
//		String test = "asdfassdcvbcvbgyy3ert356754hjkhj,8906854t34";
//		
//		System.out.println(getMD5(test));
//		System.out.println(getSHA256(test));
//	}
}

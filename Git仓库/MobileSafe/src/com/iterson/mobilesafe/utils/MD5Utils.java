package com.iterson.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String encode(String password){
		try {
			MessageDigest digest =MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;//获取该字节低八位
				String hexString = Integer.toHexString(i);
				if (hexString.length()<2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// 
			e.printStackTrace();
		}
		
		return "";
	}
}

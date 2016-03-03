package com.project.enjoyit.global;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyAlgorithm {
	
	public static String hashMd5(String str) {
		// 信息摘要器
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String password = str;// 加密字符
		StringBuffer buffer = new StringBuffer();
		byte[] result = digest.digest(password.getBytes());
		for (byte b : result) {
			int number = b & 0xff;// 不按标准加密
			// 转换成16进制
			String numberStr = Integer.toHexString(number);
			if (numberStr.length() == 1) {
				buffer.append("0");
			}
			buffer.append(numberStr);
		}
		// MD5加密结果
		return buffer.toString();
	}
	
}

package com.iterson.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *读取流工具，转化为字符串
 * @author Yang
 *
 */
public class StreamUtils {
	public static String streamToString(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len=in.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		String result = out.toString();
		out.close();
		in.close();
		
		
		return result;
		
	}
}

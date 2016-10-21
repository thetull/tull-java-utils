package net.tullco.tullutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;


public class NetworkUtils {

	public static String getDataFromConnection(URLConnection conn) throws UnsupportedEncodingException, IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String line;
		String data="";
		while((line=reader.readLine())!=null){
			data+=line;
		}
		reader.close();
		return data;
	}
}

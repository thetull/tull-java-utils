package net.tullco.tullutils;

import java.util.HashMap;

public class Configuration {

	private static HashMap<String,String> configurationMap = new HashMap<String,String>();
	
	public static void addConfiguration(String key, String value){
		configurationMap.put(key, value);
	}
	public static String getConfiguration(String key){
		if(configurationMap.containsKey(key))
			return configurationMap.get(key);
		else
			return System.getenv(key);
	}
}

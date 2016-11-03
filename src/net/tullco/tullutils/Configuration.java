package net.tullco.tullutils;

import java.util.HashMap;

import net.tullco.tullutils.exceptions.UnconfiguredException;

public class Configuration {

	private static HashMap<String,String> configurationMap = new HashMap<String,String>();
	
	public static void addConfiguration(String key, String value){
		configurationMap.put(key, value);
	}
	public static String getConfiguration(String key) throws UnconfiguredException{
		if(configurationMap.containsKey(key))
			return configurationMap.get(key);
		else if(System.getenv().containsKey(key))
			return System.getenv(key);
		else
			throw new UnconfiguredException("The value "+key+" is not yet configured.");
	}
}

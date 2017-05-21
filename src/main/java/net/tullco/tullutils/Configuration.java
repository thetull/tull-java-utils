package net.tullco.tullutils;

import java.util.HashMap;

import net.tullco.tullutils.exceptions.UnconfiguredException;

public final class Configuration {

	private static HashMap<String,String> configurationMap = new HashMap<String,String>();
	
	/**
	 * Configures the package with certain keys.
	 * @param key The configuration key
	 * @param value The configuration value.
	 */
	public static void addConfiguration(String key, String value){
		configurationMap.put(key, value);
	}
	/**
	 * Gets a configuration option matching the key. Looks it first from the internal configuration
	 * and, if it doesn't find it, checks the system environment variables.
	 * @param key The key you want to look up.
	 * @return The value corresponding to the key.
	 * @throws UnconfiguredException If the key doesn't exist
	 */
	public static String getConfiguration(String key) throws UnconfiguredException{
		if(configurationMap.containsKey(key))
			return configurationMap.get(key);
		else if(System.getenv().containsKey(key))
			return System.getenv(key);
		else
			throw new UnconfiguredException("The value "+key+" is not configured.");
	}
	/**
	 * Checks if a given key has a configuration value attached.
	 * @param key The key for your configuration value.
	 * @return True if the key has been set. False otherwise.
	 */
	public static boolean isConfigured(String key){
		if(configurationMap.containsKey(key))
			return true;
		else if(System.getenv().containsKey(key))
			return true;
		else
			return false;
	}
}

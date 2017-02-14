package net.tullco.tullutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONArray;
import org.json.JSONObject;

import net.tullco.tullutils.exceptions.UnconfiguredException;

public final class GardenUtils {

	private static HashMap<String,Connection> connectionCache = new HashMap<String,Connection>();
	
	private static final String GARDEN_URL = "https://garden2.ds.avant.com/";
	private static final String KEYRING_LOCATION = "q?model=keyrings&name=e%s";
	private static final String KEY_LOCATION = "keys?ids=%s";
	
	private final static JSONObject getKeyring(String keyring) throws IOException,UnconfiguredException{
		try{
			String apiKey = Configuration.getConfiguration("GARDEN_API_KEY");
			String keyringURL = String.format(GARDEN_URL+KEYRING_LOCATION,keyring);
			String keyringResponse = NetworkUtils.getDataFromURL(keyringURL, true, NetworkUtils.GET, new ImmutablePair<String,String>("Authorization",apiKey));
			
			JSONObject keyringResponseJson = new JSONObject(keyringResponse);
			JSONArray keyIds = keyringResponseJson.getJSONArray("result").getJSONObject(0).getJSONArray("keys");
			String keyIdString = keyIds.toString().replace("[", "").replace("]", "");
	
			String keyURL = String.format(GARDEN_URL+KEY_LOCATION,keyIdString);
			String keysResponse = NetworkUtils.getDataFromURL(keyURL, true, NetworkUtils.GET, new ImmutablePair<String,String>("Authorization",apiKey));
			
			JSONObject keysResponseJson = new JSONObject(keysResponse);

			return keysResponseJson;
		}catch(MalformedURLException e){
			return null;
		}
	}
	
	/**
	 * Gets a JDBC Connection object for a garden keyring. Currently only postgres connections are supported.
	 * To use this, you'll need to set the configuration value for GARDEN_API_KEY.
	 * @param keyring The name of the keyring of the given resource.
	 * @return A connection to the given resource
	 * @throws IOException If a network problem happens
	 * @throws SQLException If an SQL problem happens
	 * @throws UnconfiguredException If the value GARDEN_API_KEY is not configured in the Util configuration class.
	 */
	public final static Connection getPgConnectionFromGarden(String keyring) throws IOException, SQLException, UnconfiguredException {
		if(connectionCache.containsKey(keyring) && !connectionCache.get(keyring).isClosed())
			return connectionCache.get(keyring);
		JSONObject keys = getKeyring(keyring);
		JSONArray keyValueArray = keys.getJSONArray("result");
		
		String username=null;
		String password=null;
		String database=null;
		String type=null;
		String host=null;
		String port=null;
		
		
		for(Object o : keyValueArray){
			JSONObject item = (JSONObject) o;
			String name = item.getString("name");
			if(name.equals("user"))
				username=item.getString("value");
			if(name.equals("password"))
				password=item.getString("value");
			if(name.equals("database"))
				database=item.getString("value");
			if(name.equals("type"))
				type=item.getString("value");
			if(name.equals("host"))
				host=item.getString("value");
			if(name.equals("port"))
				port=item.getString("value");
		}
		if (!type.equals("postgres"))
			return null;
		
		String jdbcURL="jdbc:postgresql://"+host+"/"+database+":"+port+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		Connection c = DriverManager.getConnection(jdbcURL, username, password);
		connectionCache.put(keyring, c);
		return c;
	}
}
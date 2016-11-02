package net.tullco.tullutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;

public class GardenUtils {

	private static HashMap<String,Connection> connectionCache = new HashMap<String,Connection>();
	
	private static final String GARDEN_URL = "https://garden2.ds.avant.com/";
	private static final String KEYRING_LOCATION = "q?model=keyrings&name=e%s";
	private static final String KEY_LOCATION = "keys?ids=%s";
	
	private static JSONObject getKeyring(String keyring) throws IOException{
		try{
			String apiKey = Configuration.getConfiguration("GARDEN_API_KEY");
			String keyringURL = String.format(GARDEN_URL+KEYRING_LOCATION,keyring);
			String keyringResponse = NetworkUtils.getDataFromURL(keyringURL, true, "GET", new Pair<String,String>("Authorization",apiKey));
			
			JSONObject keyringResponseJson = new JSONObject(keyringResponse);
			JSONArray keyIds = keyringResponseJson.getJSONArray("result").getJSONObject(0).getJSONArray("keys");
			String keyIdString = keyIds.toString().replace("[", "").replace("]", "");
	
			String keyURL = String.format(GARDEN_URL+KEY_LOCATION,keyIdString);
			String keysResponse = NetworkUtils.getDataFromURL(keyURL, true, "GET", new Pair<String,String>("Authorization",apiKey));
			
			JSONObject keysResponseJson = new JSONObject(keysResponse);

			return keysResponseJson;
		}catch(MalformedURLException e){
			return null;
		}
	}
	private static Connection getPgConnectionFromGarden(String keyring) throws IOException, SQLException {
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
	
	/**
	 * Get's a JDBC statement object for the given keyring that you can execute queries against.
	 * @param keyring The keyring you'd like to query against.
	 * @return A statement that you can execute queries against.
	 * @throws SQLException If something goes wrong with the SQL Connection.
	 * @throws IOException If something goes wrong pulling the data from Garden.
	 */
	public static Statement getStatementFromGarden(String keyring) throws SQLException, IOException{
		return getPgConnectionFromGarden(keyring).createStatement();
	}
}

package net.tullco.tullutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import net.tullco.tullutils.exceptions.UnconfiguredException;

public final class GardenUtils {

	private static HashMap<String,JSONObject> keyringCache = new HashMap<String,JSONObject>();
	
	private static final String KEYRING_LOCATION = "api/resources/%s";
	
	/**
	 * Gets the JSON keyring of the connection.
	 * @param keyring The name of the keyring you want to fetch.
	 * @return A JSON representation of they keyring from the garden server.
	 * @throws IOException If a network problem happens
	 * @throws UnconfiguredException If the value GARDEN_API_KEY or GARDEN_URL is not configured in the Configuration class.
	 */
	public final static JSONObject getKeyring(String keyring) throws IOException,UnconfiguredException{
		if (keyringCache.containsKey(keyring))
			return keyringCache.get(keyring);
		try{
			String apiKey = Configuration.getConfiguration("GARDEN_API_KEY");
			String gardenURL = Configuration.getConfiguration("GARDEN_URL");
			String keyringURL = String.format(gardenURL+KEYRING_LOCATION,keyring);
			String keyringResponse = NetworkUtils.getDataFromURL(
					keyringURL, true, NetworkUtils.GET, Pair.<String,String>of("Authorization","Token token="+apiKey));
			JSONObject keyringResponseJson = new JSONObject(keyringResponse);
			return keyringResponseJson;
		}catch(MalformedURLException e){
			return null;
		}
	}
	
	/**
	 * Gets the keys on a given keyring in a map with the keys being the name field and the values being the value field.
	 * @param keyring The name of the keyring you want to fetch.
	 * @return A map containing the keys and values
	 * @throws IOException If a network problem happens
	 * @throws UnconfiguredException If the value GARDEN_API_KEY or GARDEN_URL is not configured in the Configuration class.
	 */
	public final static Map<String,String> getKeyMap(String keyring) throws IOException, UnconfiguredException{
		JSONArray keyValues = GardenUtils.getKeyring(keyring).getJSONArray("keys");
		Map<String,String> keyMap = new HashMap<String,String>();
		for (int i=0; i < keyValues.length(); i++){
			String name = keyValues.getJSONObject(i).optString("name");
			String value = keyValues.getJSONObject(i).optString("value");
			keyMap.put(name, value);
		}
		return keyMap;
	}
	
	/**
	 * Gets a JDBC Connection object for a garden keyring. Currently only postgres connections are supported.
	 * To use this, you'll need to set the configuration value for GARDEN_API_KEY.
	 * @param keyring The name of the keyring of the given resource.
	 * @return A connection to the given resource
	 * @throws IOException If a network problem happens
	 * @throws SQLException If an SQL problem happens
	 * @throws UnconfiguredException If the value GARDEN_API_KEY or GARDEN_URL is not configured in the Configuration class.
	 */
	public final static Connection getPgConnectionFromGarden(String keyring) throws IOException, SQLException, UnconfiguredException {
		Map<String,String> keyValues = getKeyMap(keyring);
		
		String username = keyValues.get("user");
		String password = keyValues.get("password");
		String database = keyValues.get("database");
		String type = keyValues.get("type");
		String host = keyValues.get("host");
		String port = keyValues.get("port");

		if (!type.equals("postgres"))
			return null;
		
		String jdbcURL="jdbc:postgresql://"+host+"/"+database+":"+port+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		Connection c = DriverManager.getConnection(jdbcURL, username, password);
		return c;
	}
	
	/**
	 * Connects to a presto keyring.
	 * @param keyring The name of the keyring of the given resource.
	 * @param trustStoreLocation The location of the truststore containing the ssl certificate
	 * @param trustStorePassword The password to the truststore containing the ssl certificate
	 * @return A JDBC connection to the Presto database at the keyring specified
	 * @throws IOException If a network problem happens
	 * @throws SQLException If an SQL problem happens
	 * @throws UnconfiguredException If the value GARDEN_API_KEY or GARDEN_URL is not configured in the Configuration class.
	 */
	public final static Connection getPrestoConnection(String keyring, String trustStoreLocation, String trustStorePassword) throws UnconfiguredException, IOException, SQLException{
		Map<String,String> prestoKeys = getKeyMap(keyring);
		String jdbcURL = String.format("jdbc:presto://%s:%s/%s/dw?SSL=true&user=%s&SSLTrustStorePath=%s&SSLTrustStorePassword=%s"
				,prestoKeys.get("host")
				,prestoKeys.get("port")
				,prestoKeys.get("database")
				,"username"
				,trustStoreLocation
				,trustStorePassword);
		Connection c = DriverManager.getConnection(jdbcURL);
		return c;
	}
	
	public final static Connection getDremioConnection() throws UnconfiguredException, IOException, SQLException{
		String jdbcURL = String.format("jdbc:dremio:zk=%s;ssl"
				,Configuration.getConfiguration("DREMIO_ZK_QUORUM")
				);
        Properties props = new Properties();
        props.setProperty("user",Configuration.getConfiguration("DREMIO_USERNAME"));
        props.setProperty("password",Configuration.getConfiguration("DREMIO_PAT"));
		Connection c = DriverManager.getConnection(jdbcURL, props);
		return c;
	}
}
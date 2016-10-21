package net.tullco.tullutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class GardenUtils {

	private static HashMap<String,Connection> connectionCache = new HashMap<String,Connection>();
	
	private static final String GARDEN_URL = "https://garden2.ds.avant.com/";
	private static final String KEYRING_LOCATION = "q?model=keyrings&name=e%s";
	private static final String KEY_LOCATION = "keys?ids=%s";
	private static final String GARDEN_API_KEY = "UC6VI4759MNH9SHOIVELSUF19LHNCB8TIBHNADQ3052QL5TVPTDG====";
	
	public static void main(String[] args) throws IOException, SQLException{
		ResultSet rs = getStatementFromGarden("uk_prod.application_user_ro").executeQuery("SELECT * FROM loans LIMIT 10");
		while (rs.next()){
			System.out.println(rs.getLong("id"));
		}
		rs.close();
		rs = getStatementFromGarden("uk_prod.application_user_ro").executeQuery("SELECT * FROM loans LIMIT 10");
		while (rs.next()){
			System.out.println(rs.getLong("amount_cents"));
		}
	}
	private static JSONObject getKeyring(String keyring) throws IOException{
		try{
			String keyringURL = String.format(GARDEN_URL+KEYRING_LOCATION,keyring);
			HttpsURLConnection conn = (HttpsURLConnection) new URL(keyringURL).openConnection();
			conn.setRequestProperty("Authorization", GARDEN_API_KEY);
			String keyringResponse = NetworkUtils.getDataFromConnection(conn);
			
			JSONObject keyringResponseJson = new JSONObject(keyringResponse);
			JSONArray keyIds = keyringResponseJson.getJSONArray("result").getJSONObject(0).getJSONArray("keys");
			String keyIdString = keyIds.toString().replace("[", "").replace("]", "");
	
			String keyURL = String.format(GARDEN_URL+KEY_LOCATION,keyIdString);
			conn = (HttpsURLConnection) new URL(keyURL).openConnection();
			conn.setRequestProperty("Authorization", GARDEN_API_KEY);
			String keysResponse = NetworkUtils.getDataFromConnection(conn);
			
			JSONObject keysResponseJson = new JSONObject(keysResponse);
			//System.out.println(keysResponseJson);
			return keysResponseJson;
		}catch(MalformedURLException e){
			return null;
		}
	}
	public static Connection getPgConnectionFromGarden(String keyring) throws IOException, SQLException {
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
	public static Statement getStatementFromGarden(String keyring) throws SQLException, IOException{
		return getPgConnectionFromGarden(keyring).createStatement();
	}
}

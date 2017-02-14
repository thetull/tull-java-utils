package net.tullco.tullutils.lookerutils;

import java.io.IOException;


import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.exceptions.InvalidHTTPMethodException;
import net.tullco.tullutils.exceptions.LookerException;

public class Authenticator {
	private static final String LOGIN_URL="login";
	private static final String LOGOUT_URL="logout";
	private String apiEndpoint;
	private String accessToken;
	private String clientId;
	private String clientSecret;

	/**
	 * Creates an authenticator to the Looker System. The LOOKER_API_ENDPOINT configuration must be set with the URL of the API system.
	 * @param clientId The client id for authentication.
	 * @param clientSecret The client secret for authentication.
	 */
	public Authenticator(String clientId, String clientSecret, String endpointLocation){
		this.clientId=clientId;
		this.clientSecret=clientSecret;
		this.apiEndpoint = endpointLocation;
	}
	public String getAccessToken() throws LookerException{
		if (this.clientId == null || this.clientSecret == null){
			throw new LookerException("Authentication details not set. Cannot connect.");
		}
		if(this.accessToken != null)
			return this.accessToken;
		String url=this.apiEndpoint+LOGIN_URL+"?client_id="+this.clientId+"&client_secret="+this.clientSecret;
		try {
			String result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.POST);
			JSONObject jsonResult=new JSONObject(result);
			accessToken = jsonResult.getString("access_token");
			return accessToken;
			
		} catch (InvalidHTTPMethodException | IOException e1) {
			throw new LookerException("Could not authenticate.\n"+e1.getMessage(),e1);
		}
	}

	public void logout() throws LookerException{
		String url=this.apiEndpoint+LOGOUT_URL;
		try {
			NetworkUtils.getDataFromURL(url, true, NetworkUtils.DELETE, Pair.of("Authorization", "Bearer "+getAccessToken()));
		} catch (Exception e) {
			throw new LookerException("Logout Failed.\n"+e.getMessage(),e);
		} finally{
			this.accessToken = null;
		}
	}
}

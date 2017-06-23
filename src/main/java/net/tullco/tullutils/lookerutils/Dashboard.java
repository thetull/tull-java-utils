package net.tullco.tullutils.lookerutils;

import java.io.IOException;

import org.json.JSONObject;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;
import net.tullco.tullutils.exceptions.LookerException;

public class Dashboard {
	
	private static final String GET_LOOK_URL="looks/%d";
	private final String apiEndpoint;
	private final String accessToken;
	
	private int id;
	private int contentMedatadataId;

	public Dashboard(String accessToken, String apiEndpoint){
		this.apiEndpoint=apiEndpoint;
		this.accessToken=accessToken;
	}
	public Dashboard(JSONObject json, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
	}

	public JSONObject toJSON(){
		return null;
	}
	protected static Dashboard getLookById(String accessToken, String apiEndpoint, int id) throws LookerException{
		String url = String.format(apiEndpoint+GET_LOOK_URL, id);
		try {
			String result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.GET, Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			System.out.println(result);
		} catch (IOException e) {
			throw new LookerException(e);
		}
		return null;
	}
}

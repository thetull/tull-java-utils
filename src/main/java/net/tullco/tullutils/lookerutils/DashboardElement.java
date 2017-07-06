package net.tullco.tullutils.lookerutils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;
import net.tullco.tullutils.exceptions.LookerException;

public class DashboardElement {
	
	private final static String CREATE_ELEMENT_URL="dashboard_elements";
	private final static String UPDATE_ELEMENT_URL="dashboard_elements/%d";
	
	private int id=0;
	private int dashboardId;
	private int lookId;
	private String type;
	
	private final String accessToken;
	private final String apiEndpoint;

	protected DashboardElement(String accessToken, String apiEndpoint){
		this.accessToken=accessToken;
		this.apiEndpoint=apiEndpoint;
	}
	protected DashboardElement(JSONObject json, String accessToken, String apiEndpoint) {
		this(accessToken, apiEndpoint);
		fromJSON(json);
	}
	protected DashboardElement(int dashboardId, int lookId, String type, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		this.dashboardId=dashboardId;
		this.lookId=lookId;
		this.type=type;
	}
	/**
	 * Gets the ID of the dashboard element.
	 * @return The ID of the dashboard element. 
	 */
	public int getId(){
		return this.id;
	}
	/**
	 * Gets the dashboard id.
	 * @return The id of the dashboard.
	 */
	public int getDashboardId(){
		return this.dashboardId;
	}
	/**
	 * Gets the id of the relevant look.
	 * @return The id of the look.
	 */
	public int getLookId(){
		return this.lookId;
	}
	/**
	 * Set's the dashboard that the look is attached to.
	 * @param dashboardId The id of the dashboard.
	 */
	public void setDashboardId(int dashboardId){
		this.dashboardId=dashboardId;
	}
	/**
	 * Set's the id of the look that this dashboard is using.
	 * @param lookId The id of the look.
	 */
	public void setLookId(int lookId){
		this.lookId=lookId;
	}
	private void fromJSON(JSONObject json){
		id = json.getInt("id");
		dashboardId=json.getInt("dashboard_id");
		lookId=json.getInt("look_id");
		type=json.getString("type");
	}
	/**
	 * Saves the dashboard element to Looker.
	 * @throws LookerException If the saving was unsuccessful.
	 */
	public void save() throws LookerException{
		try{
			if(this.id==0)
				create();
			else
				update();
		}catch(IOException e){
			throw new LookerException(e);
		}
	}
	private void create() throws IOException{
		String url = this.apiEndpoint+CREATE_ELEMENT_URL;
		String response = NetworkUtils.sendDataToURL(url, true, NetworkUtils.POST, this.toCreateJSON().toString(), Pair.<String,String>of("Authorization", "Bearer "+accessToken));
		this.fromJSON(new JSONObject(response));
	}
	private void update() throws IOException{
		String url = String.format(this.apiEndpoint+UPDATE_ELEMENT_URL,this.id);
		HttpPatch patchRequest = new HttpPatch(url);
		patchRequest.addHeader("Authorization", "Bearer "+this.accessToken);
		JSONObject dashboardJSON = this.toEditJSON();
		patchRequest.setEntity(new StringEntity(dashboardJSON.toString(),ContentType.APPLICATION_JSON));
		HttpClient c = HttpClientBuilder.create().disableCookieManagement().build();
		HttpResponse response = c.execute(patchRequest);
		JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
		this.fromJSON(json);
	}
	protected JSONObject toCreateJSON(){
		JSONObject json = new JSONObject();
		json.put("look_id", lookId);
		json.put("dashboard_id",dashboardId);
		json.put("type", this.type);
		return json;
	}
	protected JSONObject toEditJSON(){
		JSONObject json = toCreateJSON();
		json.put("id", this.id);
		return json;
	}
	/**
	 * Converts the element to JSON.
	 * @return A JSONObject containing the element data.
	 */
	public JSONObject toJSON() {
		JSONObject json = toEditJSON();
		return json;		
	}
}

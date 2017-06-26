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

	private DashboardElement(String accessToken, String apiEndpoint){
		this.accessToken=accessToken;
		this.apiEndpoint=apiEndpoint;
	}
	public DashboardElement(JSONObject json, String accessToken, String apiEndpoint) {
		this(accessToken, apiEndpoint);
		fromJSON(json);
	}
	public DashboardElement(int dashboardId, int lookId, String type, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		this.dashboardId=dashboardId;
		this.lookId=lookId;
		this.type=type;
	}
	public int getId(){
		return this.id;
	}
	public int getDashboardId(){
		return this.dashboardId;
	}
	public int getLookId(){
		return this.lookId;
	}
	public void setDashboardId(int dashboardId){
		this.dashboardId=dashboardId;
	}
	public void setLookId(int lookId){
		this.lookId=lookId;
	}
	public void fromJSON(JSONObject json){
		id = json.getInt("id");
		dashboardId=json.getInt("dashboard_id");
		lookId=json.getInt("look_id");
		type=json.getString("type");
	}

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
	public JSONObject toCreateJSON(){
		JSONObject json = new JSONObject();
		json.put("look_id", lookId);
		json.put("dashboard_id",dashboardId);
		json.put("type", this.type);
		return json;
	}
	public JSONObject toEditJSON(){
		JSONObject json = toCreateJSON();
		json.put("id", this.id);
		return json;
	}
	public JSONObject toJSON() {
		JSONObject json = toEditJSON();
		return json;		
	}
}

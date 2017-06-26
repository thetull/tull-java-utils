package net.tullco.tullutils.lookerutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;
import net.tullco.tullutils.exceptions.LookerException;

public class Dashboard {
	
	private static final String GET_DASHBOARD_URL="dashboards/%d";
	private static final String CREATE_DASHBOARD_URL="dashboards";
	private static final String UPDATE_DASHBOARD_URL="dashboards/%d";
	private final String apiEndpoint;
	private final String accessToken;
	
	private int id;
	private int contentMetadataId;
	private int userId;
	private int spaceId;
	private int viewCount;
	private int favoriteCount;
	private String description;
	private String title;
	private Space space;
	private List<DashboardElement> elements;

	protected Dashboard(String accessToken, String apiEndpoint){
		this.apiEndpoint=apiEndpoint;
		this.accessToken=accessToken;
		this.elements = new ArrayList<DashboardElement>();
	}
	protected Dashboard(JSONObject json, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		fromJSON(json);
	}
	protected Dashboard(String title, int spaceId, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		this.title=title;
		this.spaceId=spaceId;
	}
	public Dashboard(String title, Space space, String accessToken, String apiEndpoint){
		this(title,space.getId(),accessToken,apiEndpoint);
	}
	public List<DashboardElement> getElements(){
		return this.elements;
	}
	public int getId(){
		return id;
	}
	public void addLook(Look look) throws LookerException {
		this.addLook(look.getId());
	}
	public void addLook(int lookId) throws LookerException{
		DashboardElement e = new DashboardElement(this.id, lookId, "vis", this.accessToken, this.apiEndpoint);
		e.save();
		this.elements.add(e);
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
		String url = this.apiEndpoint+CREATE_DASHBOARD_URL;
		String response = NetworkUtils.sendDataToURL(url, true, NetworkUtils.POST, this.toCreateJSON().toString(), Pair.<String,String>of("Authorization", "Bearer "+accessToken));
		this.fromJSON(new JSONObject(response));
	}
	private void update() throws IOException{
		String url = String.format(this.apiEndpoint+UPDATE_DASHBOARD_URL,this.id);
		HttpPatch patchRequest = new HttpPatch(url);
		patchRequest.addHeader("Authorization", "Bearer "+this.accessToken);
		JSONObject dashboardJSON = this.toEditJSON();
		patchRequest.setEntity(new StringEntity(dashboardJSON.toString(),ContentType.APPLICATION_JSON));
		HttpClient c = HttpClientBuilder.create().disableCookieManagement().build();
		HttpResponse response = c.execute(patchRequest);
		JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
		this.fromJSON(json);
	}
	private void fromJSON(JSONObject json){
		if(!json.isNull("id"))
			this.id=json.getInt("id");
		if(!json.isNull("content_metadata_id"))
			this.contentMetadataId=json.getInt("content_metadata_id");
		if(!json.isNull("view_count"))
			this.viewCount=json.getInt("view_count");
		if(!json.isNull("favorite_count"))
			this.favoriteCount=json.getInt("favorite_count");
		if(!json.isNull("user_id"))
			this.userId=json.getInt("user_id");
		if(!json.isNull("title"))
			this.title=json.getString("title");
		if(!json.isNull("description"))
			this.description=json.getString("description");
		if(!json.isNull("space_id"))
			this.spaceId=json.getInt("space_id");
		if(!json.isNull("space"))
			this.space=new Space(json.getJSONObject("space"),this.accessToken, this.apiEndpoint);
		if(!json.isNull("dashboard_elements")){
			JSONArray elements=json.getJSONArray("dashboard_elements");
			for(int i=0; i< elements.length(); i++){
				this.elements.add(new DashboardElement(elements.getJSONObject(i), this.accessToken, this.apiEndpoint));
			}
		}
	}
	public JSONObject toJSON(){
		JSONObject json = toEditJSON();
		json.put("content_metadata_id", this.contentMetadataId);
		json.put("view_count", this.viewCount);
		json.put("favorite_count", this.favoriteCount);
		json.put("user_id", this.userId);
		json.put("space", space.toJSON());
		json.remove("dashboard_elements");
		for(DashboardElement e: this.elements){
			if(e.getId()==0)
				json.append("dashboard_elements", e.toCreateJSON());
			else
				json.append("dashboard_elements", e.toJSON());
		}
		return json;
	}
	private JSONObject toCreateJSON(){
		JSONObject json = new JSONObject();
		json.put("title",this.title);
		json.put("description", this.description);
		json.put("space_id", this.spaceId);
		for(DashboardElement e: this.elements){
			json.append("dashboard_elements", e.toCreateJSON());
		}
		return json;
	}
	private JSONObject toEditJSON(){
		JSONObject json = this.toCreateJSON();
		json.put("id", this.id);
		json.remove("dashboard_elements");
		for(DashboardElement e: this.elements){
			if(e.getId()==0)
				json.append("dashboard_elements", e.toCreateJSON());
			else
				json.append("dashboard_elements", e.toEditJSON());
		}
		return json;
	}
	protected static Dashboard getNewDashboard(int spaceId, String title, String accessToken, String apiEndpoint) throws LookerException{
		Dashboard d = new Dashboard(title,spaceId,accessToken, apiEndpoint);
		d.save();
		return d;
	}
	protected static Dashboard getDashboardById(int id, String accessToken, String apiEndpoint) throws LookerException{
		String url = String.format(apiEndpoint+GET_DASHBOARD_URL, id);
		try {
			String result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.GET, Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			return new Dashboard(new JSONObject(result),accessToken, apiEndpoint);
		} catch (IOException e) {
			throw new LookerException(e);
		}
	}
}

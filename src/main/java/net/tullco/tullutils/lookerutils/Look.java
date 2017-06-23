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

public class Look {
	
	private static final String GET_LOOK_URL="looks/%d";
	private final static String CREATE_LOOK_URL="looks";
	private final static String UPDATE_LOOK_URL="looks/%d";
	
	private final String apiEndpoint;
	private final String accessToken;
	
	private int id;
	private int contentMetadataId;
	private int viewCount;
	private int favoriteCount;
	private int userId;
	private int queryId;
	private String title;
	private String description;
	private String shortURL;
	private int spaceId;
	private String longURL;
	private Query query;

	public Look(String accessToken, String apiEndpoint){
		this.apiEndpoint=apiEndpoint;
		this.accessToken=accessToken;
		this.id=0;
	}
	public Look(JSONObject json, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		this.fromJSON(json);
	}
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public int getSpaceId(){
		return this.spaceId;
	}
	public void setSpaceId(int id){
		this.spaceId=id;
	}
	public void setSpace(Space s){
		this.spaceId=s.getId();
	}
	public int getQueryId(){
		return this.queryId;
	}
	public Query getQuery(){
		return this.query;
	}
	public void setQueryId(int id) throws LookerException{
		this.queryId=id;
		this.query = Query.getQueryById(this.accessToken, this.apiEndpoint, id);
	}
	public void setQuery(Query q){
		this.queryId=q.getId();
		this.query=q;
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
		String url = this.apiEndpoint+CREATE_LOOK_URL;
		String response = NetworkUtils.sendDataToURL(url, true, NetworkUtils.POST, this.toCreateJSON().toString(), Pair.<String,String>of("Authorization", "Bearer "+accessToken));
		this.fromJSON(new JSONObject(response));
	}
	private void update() throws IOException{
		String url = String.format(this.apiEndpoint+UPDATE_LOOK_URL,this.id);
		HttpPatch patchRequest = new HttpPatch(url);
		patchRequest.addHeader("Authorization", "Bearer "+this.accessToken);
		patchRequest.setEntity(new StringEntity(this.toEditJSON().toString(),ContentType.APPLICATION_JSON));
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
		if(!json.isNull("query_id"))
			this.queryId=json.getInt("query_id");
		if(!json.isNull("title"))
			this.title=json.getString("title");
		if(!json.isNull("description"))
			this.description=json.getString("description");
		if(!json.isNull("short_url"))
			this.shortURL=json.getString("short_url");
		if(!json.isNull("space_id"))
			this.spaceId=json.getInt("space_id");
		if(!json.isNull("url"))
			this.longURL=json.getString("url");
		if(!json.isNull("query"))
			this.query=new Query(json.getJSONObject("query"),this.accessToken,this.apiEndpoint);
	}
	public JSONObject toJSON(){
		JSONObject json = toEditJSON();
		json.put("content_metadata_id", this.contentMetadataId);
		json.put("view_count", this.viewCount);
		json.put("favorite_count", this.favoriteCount);
		json.put("user_id", this.userId);
		json.put("short_url", this.shortURL);
		json.put("url", this.longURL);
		json.put("query",this.query.toJSON());
		return json;
	}
	private JSONObject toCreateJSON(){
		JSONObject json = new JSONObject();
		json.put("title",this.title);
		json.put("description", this.description);
		json.put("space_id", this.spaceId);
		json.put("query_id", this.queryId);
		return json;
	}
	private JSONObject toEditJSON(){
		JSONObject json = this.toCreateJSON();
		json.put("id", this.id);
		return json;
	}
	protected static Look getNewLook(String accessToken, String apiEndpoint, String title, int spaceId, int queryId) throws LookerException{
		Look l = new Look(accessToken,apiEndpoint);
		l.setQueryId(queryId);
		l.setSpaceId(spaceId);
		l.setTitle(title);
		l.save();
		return l;
	}
	protected static Look getLookById(String accessToken, String apiEndpoint, int id) throws LookerException{
		String url = String.format(apiEndpoint+GET_LOOK_URL, id);
		try {
			String result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.GET, Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			JSONObject json = new JSONObject(result);
			return new Look(json,accessToken,apiEndpoint);
		} catch (IOException e) {
			throw new LookerException(e);
		}
	}
}

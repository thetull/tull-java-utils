package net.tullco.tullutils.lookerutils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

public class Space {
	private final static String GET_SPACE_URL="spaces/%d";
	private final static String CREATE_SPACE_URL="spaces";
	private final static String UPDATE_SPACE_URL="spaces/%d";
	private final static String SEARCH_SPACE_URL="spaces/search";
	
	private final String apiEndpoint;
	private final String accessToken;
	
	private int id;
	private int creatorId;
	private String name;
	private int contentMetadataId;
	private boolean isPersonal;
	private boolean isSharedRoot;
	private boolean isPersonalDescendant;
	private boolean isEmbed;
	private int externalId;
	private int parentId;
	private boolean isUserRoot;
	private boolean isRoot;
	private Set<Look> looks;
	private Set<Dashboard> dashboards;
	
	protected Space(String accessToken, String apiEndpoint){
		this.apiEndpoint=apiEndpoint;
		this.accessToken=accessToken;
		this.looks=new HashSet<Look>();
		this.dashboards=new HashSet<Dashboard>();
		this.id=0;
	}
	protected Space(JSONObject json, String accessToken, String apiEndpoint){
		this(accessToken, apiEndpoint);
		this.fromJSON(json);
	}
	/**
	 * Gets the id of the parent space.
	 * @return The id of the parent space.
	 */
	public int getParentId(){
		return this.parentId;
	}
	/**
	 * Sets the parent ID.
	 * @param parentId The ID of the new parent space.
	 */
	public void setParentId(int parentId){
		this.parentId=parentId;
	}
	/**
	 * Sets the parent name.
	 * @param name The new name for the space.
	 */
	public void setName(String name){
		this.name=name;
	}
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId(){
		return this.id;
	}
	/**
	 * Return all the looks in the space.
	 * @return A set containing the looks in the space.
	 */
	public Set<Look> getLooks(){
		return this.looks;
	}
	/**
	 * Get all spaces in this space.
	 * @return A set containing the subspaces.
	 * @throws LookerException If there was a problem fetching the child spaces
	 */
	public Set<Space> getSubspaces() throws LookerException{
		Set<Space> subspaces = new HashSet<Space>();
		String url = this.apiEndpoint + SEARCH_SPACE_URL+"?parent_id="+this.id;
		try{
			String response = NetworkUtils.getDataFromURL(url, true, NetworkUtils.GET, Pair.<String,String>of("parent_id",Integer.toString(this.id)), Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			JSONArray json = new JSONArray(response);
			for(int i=0;i<json.length();i++){
				subspaces.add(new Space(json.getJSONObject(i),this.accessToken,this.apiEndpoint));
			}
		}catch(IOException e){
			throw new LookerException(e);
		}
		return subspaces;
	}
	/**
	 * Creates a dashboard in the space. Also saves the space.
	 * @param title The dashboard Title
	 * @return The newly created dashboard. It will be saved.
	 * @throws LookerException If the dashboard could not be created or the space couldn't be saved.
	 */
	public Dashboard createDashboard(String title) throws LookerException{
		this.save();
		Dashboard d = Dashboard.getNewDashboard(id, title, accessToken, apiEndpoint);
		refresh();
		return d;
	}
	/**
	 * Creates a look in the space. Also saves the space.
	 * @param title The look title.
	 * @param queryId The id of the query backing the look
	 * @return The look that was created.
	 * @throws LookerException If the look couldn't be created or the space couldn't be saved.
	 */
	public Look createLook(String title, int queryId) throws LookerException{
		this.save();
		Look l = Look.getNewLook(accessToken, apiEndpoint, title, this, queryId);
		refresh();
		return l;
	}
	/**
	 * Creates a look in the space. Also saves the space.
	 * @param title The look title.
	 * @param q The query backing the look
	 * @return The look that was created.
	 * @throws LookerException If the look couldn't be created or the space couldn't be saved.
	 */
	public Look createLook(String title, Query q) throws LookerException{
		return this.createLook(title, q.getId());
	}
	private void fromJSON(JSONObject json){
		if(json.has("id"))
			this.id=json.getInt("id");
		if(json.has("creator_id"))
			this.creatorId=json.getInt("creator_id");
		if(json.has("name"))
			this.name=json.getString("name");
		if(json.has("content_metadata_id"))
			this.contentMetadataId=json.getInt("content_metadata_id");
		if(json.has("is_personal"))
			this.isPersonal=json.getBoolean("is_personal");
		if(json.has("is_shared_root"))
			this.isSharedRoot=json.getBoolean("is_shared_root");
		if(json.has("is_personal_descendant"))
			this.isPersonalDescendant=json.getBoolean("is_personal_descendant");
		if(json.has("is_embed"))
			this.isEmbed=json.getBoolean("is_embed");
		if(json.has("external_id") && !json.isNull("external_id"))
			this.externalId=json.getInt("external_id");
		if(json.has("parent_id"))
			this.parentId=json.getInt("parent_id");
		if(json.has("is_user_root"))
			this.isUserRoot=json.getBoolean("is_user_root");
		if(json.has("is_root"))
			this.isRoot=json.getBoolean("is_root");
		if(json.has("looks")){
			JSONArray looks = json.getJSONArray("looks");
			for(int i = 0; i < looks.length();i++){
				this.looks.add(new Look(looks.getJSONObject(i),this.accessToken,this.apiEndpoint));
			}
		}
		if(json.has("dashboards")){
			JSONArray dashboards = json.getJSONArray("dashboards");
			for(int i = 0; i < dashboards.length();i++){
				this.dashboards.add(new Dashboard(dashboards.getJSONObject(i),this.accessToken,this.apiEndpoint));
			}
		}
	}
	/**
	 * Refreshes the space to the configuration on the server.
	 * @throws LookerException If the new configuration could not be fetched.
	 */
	public void refresh() throws LookerException{
		this.fromJSON(getSpaceJSONById(accessToken, apiEndpoint, this.id));
	}
	/**
	 * Saves changes to the space.
	 * @throws LookerException If the space could not be saved.
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
		String url = this.apiEndpoint+CREATE_SPACE_URL;
		String response = NetworkUtils.sendDataToURL(url, true, NetworkUtils.POST, this.toCreateJSON().toString(), Pair.<String,String>of("Authorization", "Bearer "+accessToken));
		this.fromJSON(new JSONObject(response));
	}
	private void update() throws IOException{
		String url = String.format(this.apiEndpoint+UPDATE_SPACE_URL,this.id);
		HttpPatch patchRequest = new HttpPatch(url);
		patchRequest.addHeader("Authorization", "Bearer "+this.accessToken);
		patchRequest.setEntity(new StringEntity(this.toEditJSON().toString(),ContentType.APPLICATION_JSON));
		HttpClient c = HttpClientBuilder.create().disableCookieManagement().build();
		HttpResponse response = c.execute(patchRequest);
		JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
		this.fromJSON(json);
	}
	private JSONObject toCreateJSON(){
		JSONObject json = new JSONObject();
		json.put("name", this.name);
		json.put("parent_id", this.parentId);
		return json;
	}
	private JSONObject toEditJSON(){
		JSONObject json = toCreateJSON();
		json.put("id", this.id);
		return json;
	}
	/**
	 * Converts the space to JSON.
	 * @return A JSONObject containing the space data.
	 */
	public JSONObject toJSON(){
		JSONObject json = toEditJSON();
		json.put("creator_id", this.creatorId);
		json.put("content_metadata_id", this.contentMetadataId);
		json.put("is_personal", this.isPersonal);
		json.put("is_shared_root", this.isSharedRoot);
		json.put("is_personal_descendant", this.isPersonalDescendant);
		json.put("is_embed", this.isEmbed);
		json.put("external_id", this.externalId);
		json.put("is_user_root", this.isUserRoot);
		json.put("is_root", this.isRoot);
		for(Look l: this.looks){
			json.append("looks", l.toJSON());
		}
		for(Dashboard d: this.dashboards){
			json.append("dashboards", d.toJSON());
		}
		return json;
	}
	protected static Space createNewSpace(String accessToken, String apiEndpoint, String name, int parentSpaceId) throws LookerException{
		Space s = new Space(accessToken, apiEndpoint);
		s.setParentId(parentSpaceId);
		s.setName(name);
		s.save();
		return s;
	}
	private static JSONObject getSpaceJSONById(String accessToken, String apiEndpoint, int id) throws LookerException{
		String url = String.format(apiEndpoint+GET_SPACE_URL, id);
		try {
			String result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.GET, Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			JSONObject json = new JSONObject(result);
			return json;
		} catch (IOException e) {
			throw new LookerException(e);
		}
	}
	protected static Space getSpaceById(String accessToken, String apiEndpoint, int id) throws LookerException{
		return new Space(
				getSpaceJSONById(accessToken, apiEndpoint,id),
				accessToken,
				apiEndpoint);
	}
}

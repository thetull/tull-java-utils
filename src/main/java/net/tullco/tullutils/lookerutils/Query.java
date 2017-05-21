package net.tullco.tullutils.lookerutils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;
import net.tullco.tullutils.exceptions.LookerException;

public class Query implements Closeable {
	private String accessToken;
	private String endpointLocation;
	
	private boolean closed=false;
	
	private int id=0;
	private String slug="";
	private ArrayList<JSONObject> jsonResults;
	private File csvResults;
	private String model;
	private String view;
	private int limit = 500;
	private ArrayList<String> fields;
	private ArrayList<String> pivots;
	private ArrayList<String> sorts;
	private JSONObject filters;
	private final static String GET_QUERY_ID_URL="queries/%d";
	private final static String GET_QUERY_SLUG_URL="queries/slug/%s";
	private final static String CREATE_QUERY_URL="queries";
	private final static String RUN_QUERY_URL="queries/%d/run/%s";

	private static enum OutputType { CSV, JSON };
	public static OutputType CSV = OutputType.CSV;
	public static OutputType JSON = OutputType.JSON;
	
	public Query(String accessToken, String endpointLocation){
		this.accessToken = accessToken;
		this.endpointLocation = endpointLocation;
		this.fields=new ArrayList<String>();
		this.pivots=new ArrayList<String>();
		this.sorts=new ArrayList<String>();
		this.filters=new JSONObject();
	}
	
	/**
	 * Saves the query, as it is currently designed to the attached Looker instance.
	 * @throws LookerException If the query can't be saved
	 */
	public void saveQuery() throws LookerException{
		if (model != null && !fields.isEmpty() && view !=null){
			try {
				String response = NetworkUtils.sendDataToURL(
						this.endpointLocation+CREATE_QUERY_URL
						,true
						,NetworkUtils.POST
						,this.toString()
						,Pair.<String,String>of("Authorization","Bearer "+this.accessToken));
				JSONObject jsonResponse=new JSONObject(response);
				this.id=jsonResponse.getInt("id");
				this.slug=jsonResponse.getString("slug");
			} catch (Exception e) {
				throw new LookerException(e.getMessage(),e);
			}
		}
	}
	private String runQuery(OutputType outputType) throws LookerException{
		try {
			this.saveQuery();
			String outputCode=convertOutputTypeToString(outputType);
			String response = NetworkUtils.getDataFromURL(
					String.format(this.endpointLocation+RUN_QUERY_URL, this.id,outputCode)
					,true
					,NetworkUtils.GET
					,Pair.<String,String>of("Authorization","Bearer "+this.accessToken));
			return response;
		} catch (Exception e) {
			throw new LookerException(e.getMessage(),e);
		}
	}
	/**
	 * Gets the results of the query in a JSON format. Each row of output is put into an item in a list of JSON Objects in the order they were retrieved.
	 * @return A list of JSON results
	 * @throws LookerException If there was a problem getting the results
	 */
	public List<JSONObject> getJSONResults() throws LookerException {
		throwIfClosed();
		if (this.jsonResults!=null)
			return this.jsonResults;
		String rawResults = runQuery(OutputType.JSON);
		JSONArray resultsArray=new JSONArray(rawResults);
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		for(Object json :resultsArray){
			jsonList.add(new JSONObject(json.toString()));
		}
		this.jsonResults=jsonList;
		return jsonList;
	}
	/**
	 * Gets the results of the query from the API in CSV format. Returns a file handle to a temp file containing the results.
	 * The file will be deleted at the conclusion of the program, so be sure to copy it somewhere else if you want it to persist.
	 * @return A File object pointing to a file containing the results in CSV format.
	 * @throws LookerException If there is a problem with Looker
	 * @throws IOException If there is a problem creating or writing to the file.
	 */
	public File getCSVResults() throws LookerException, IOException{
		throwIfClosed();
		if (this.csvResults!=null)
			return this.csvResults;
		File tempFile = File.createTempFile("looker_cache_", ".csv");
		tempFile.deleteOnExit();
		String output=runQuery(OutputType.CSV);
		System.out.println(output);
		FileUtils.writeStringToFile(output, tempFile);
		return tempFile;
	}
	/**
	 * Returns the slug associated with the query.
	 * @return A string representing the slug.
	 */
	public String getSlug(){
		return this.slug;
	}
	/**
	 * Gets the query ID. A unique integer identifier.
	 * @return The query ID.
	 */
	public int getId(){
		return this.id;
	}
	/**
	 * Sets the model for the query
	 * @param model The model
	 */
	public void setModel(String model){
		this.model=model;
		clearCachedResults();
	}
	/**
	 * Sets the view for the query.
	 * @param view The view
	 */
	public void setView(String view){
		this.view=view;
		clearCachedResults();
	}
	/**
	 * Sets the limit. Default is 50
	 * @param limit The new limit for the query.
	 */
	public void setLimit(int limit){
		this.limit=limit;
		clearCachedResults();
	}
	/**
	 * Adds a field to the look. Should be in the form "view.field".
	 * @param field The field name
	 */
	public void addField(String field){
		this.fields.add(field);
		clearCachedResults();
	}
	/**
	 * Adds a pivot to the look. Should be in the form "view.field".
	 * @param pivot The field to pivot by
	 */
	public void addPivot(String pivot){
		this.pivots.add(pivot);
		clearCachedResults();
	}
	/**
	 * Adds a sort to the look. Should be in the form "view.field".
	 * @param filter The field you are filtering by
	 * @param value The value you are filtering the field to
	 */
	public void addFilter(String filter,String value){
		filters.put(filter, value);
		clearCachedResults();
	}
	/**
	 * Adds a sort to the look. Should be in the form "view.field".
	 * @param sort The field to sort by
	 */
	public void addSort(String sort){
		sorts.add(sort);
		clearCachedResults();
	}
	private void fromJSON(JSONObject json){
		this.model=json.getString("model");
		this.view=json.getString("view");
		if(!json.isNull("filters")) {
			this.filters = json.getJSONObject("filters");
		}
		if(!json.isNull("fields")){
			for(Object o: json.getJSONArray("fields")){
				this.addField((String) o);
			}
		}
		if(!json.isNull("sorts")){
			for(Object o: json.getJSONArray("sorts")){
				this.addSort((String) o);
			}
		}
		if(!json.isNull("pivots")){
			for(Object o: json.getJSONArray("pivots")){
				this.addPivot((String) o);
			}
		}
	}
	/**
	 * Returns the JSON representation of the Query for submission to the API.
	 * @return JSON representing the query.
	 */
	public JSONObject toJSON(){
		JSONObject json=new JSONObject();
		json.put("model", this.model);
		json.put("view", this.view);
		json.put("fields", this.fields);
		json.put("pivots", this.pivots);
		json.put("filters", this.filters);
		json.put("sorts", this.sorts);
		json.put("limit", this.limit);
		return json;
	}
	public String toString(){
		return this.toJSON().toString();
	}
	private String convertOutputTypeToString(OutputType ot){
		switch(ot){
			case JSON:
				return "json";
			case CSV:
				return "csv";
			default:
				return "json";
		}
	}
	private void clearCachedResults(){
		this.jsonResults=null;
		this.csvResults=null;
	}

	@Override
	public void close() throws IOException {
		this.closed=true;
	}
	private void throwIfClosed() throws LookerException {
		if (this.closed)
			throw new LookerException("This object has been closed.");
	}
	
	protected static Query getQueryBySlug(String accessToken, String endpointLocation, String slug) throws LookerException {
		try{
			String queryString = NetworkUtils.getDataFromURL(
					String.format(endpointLocation+GET_QUERY_SLUG_URL,slug)
					,true
					,NetworkUtils.GET
					,Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			JSONObject json = new JSONObject(queryString);
			Query q = new Query(accessToken,endpointLocation);
			q.fromJSON(json);
			return q;

		}catch(IOException e){
			throw new LookerException("Error fetching Query",e);
		}
	}
	
	protected static Query getQueryById(String accessToken, String endpointLocation, int id) throws LookerException{
		try{
			System.out.println(String.format(endpointLocation+GET_QUERY_ID_URL,id));
			String queryString = NetworkUtils.getDataFromURL(
					String.format(endpointLocation+GET_QUERY_ID_URL,id)
					,true
					,NetworkUtils.GET
					,Pair.<String,String>of("Authorization", "Bearer "+accessToken));
			System.out.println(queryString);
			JSONObject json = new JSONObject(queryString);
			Query q = new Query(accessToken,endpointLocation);
			q.fromJSON(json);
			System.out.println(q);
			return q;
		}catch(IOException e){
			throw new LookerException("Error fetching Query",e);
		}
	}
}

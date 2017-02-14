package net.tullco.tullutils.lookerutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.exceptions.LookerException;

public class Query {
	private String accessToken;
	private String endpointLocation;
	
	private int id=0;
	private String slug="";
	private ArrayList<JSONObject> jsonResults;
	private File csvResults;
	private String model;
	private String view;
	private int limit = 500;
	private ArrayList<String> fields=new ArrayList<String>();
	private ArrayList<String> pivots=new ArrayList<String>();
	private ArrayList<String> sorts=new ArrayList<String>();
	private JSONObject filters=new JSONObject();
	//private final static String GET_QUERY_URL="queries/slug/%s";
	private final static String CREATE_QUERY_URL="queries";
	private final static String RUN_QUERY_URL="queries/%d/run/%s";

	private static enum OutputType { CSV, JSON };
	public static OutputType CSV = OutputType.CSV;
	public static OutputType JSON = OutputType.JSON;
	
	public Query(String accessToken, String endpointLocation){
		this.accessToken = accessToken;
		this.endpointLocation = endpointLocation;
	}
	
	public void saveQuery() throws LookerException{
		if (model != null && !fields.isEmpty() && view !=null){
			try {
				String response = NetworkUtils.sendDataToURL(
						this.endpointLocation+CREATE_QUERY_URL
						,true
						,NetworkUtils.POST
						,this.toString()
						,Pair.of("Authorization","Bearer "+this.accessToken));
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
					,Pair.of("Authorization","Bearer "+this.accessToken));
			return response;
		} catch (Exception e) {
			throw new LookerException(e.getMessage(),e);
		}
	}
	public ArrayList<JSONObject> getJSONResults() throws LookerException{
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
	public File getCSVResults() throws LookerException, IOException{
		if (this.csvResults!=null)
			return this.csvResults;
		File tempFile = File.createTempFile("looker_cache", ".csv");
		tempFile.deleteOnExit();
		String output=runQuery(OutputType.CSV);
		System.out.println(output);
		FileUtils.writeStringToFile(output, tempFile);
		return tempFile;
	}
	public String getSlug(){
		return this.slug;
	}
	public void setModel(String model){
		this.model=model;
		clearCachedResults();
	}
	public void setView(String view){
		this.view=view;
		clearCachedResults();
	}
	public void setLimit(int limit){
		this.limit=limit;
		clearCachedResults();
	}
	public void addField(String field){
		this.fields.add(field);
		clearCachedResults();
	}
	public void addPivot(String pivot){
		this.pivots.add(pivot);
		clearCachedResults();
	}
	public void addFilter(String filter,String value){
		filters.put(filter, value);
		clearCachedResults();
	}
	public void addSort(String sort){
		sorts.add(sort);
		clearCachedResults();
	}
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
}

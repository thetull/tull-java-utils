package net.tullco.tullutils.lookerutils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import net.tullco.tullutils.Configuration;
import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;
import net.tullco.tullutils.exceptions.LookerException;
import net.tullco.tullutils.exceptions.UnconfiguredException;

public class Looker implements Closeable {
	
	private Authenticator auth;
	private List<Query> queries;
	private String endpointLocation;
	private String clientId;
	private String clientSecret;
	
	private boolean closed;
	
	/**
	 * Constructs a looker API connection from environment variables or the configuration class.
	 * @throws UnconfiguredException If there are no values for LOOKER_CLIENT_ID, LOOKER_CLIENT_SECRET, or LOOKER_API_ENDPOINT in either the configuration class or the environment variables
	 * @throws LookerException If the provided details don't allow the API to authenticate.
	 */
	public Looker() throws UnconfiguredException, LookerException {
		this.clientId=Configuration.getConfiguration("LOOKER_CLIENT_ID");
		this.clientSecret=Configuration.getConfiguration("LOOKER_CLIENT_SECRET");
		this.endpointLocation = Configuration.getConfiguration("LOOKER_API_ENDPOINT");
		init();
	}
	/**
	 * Constructs a looker API connection from environment variables and the provided endpoint.
	 * @param endpointLocation The URL to the Looker endpoint.
	 * @throws UnconfiguredException If there are no values for LOOKER_CLIENT_ID or LOOKER_CLIENT_SECRET in either the configuration class or the environment variables
	 * @throws LookerException If the provided details don't allow the API to authenticate.
	 */
	public Looker(String endpointLocation) throws UnconfiguredException, LookerException {
		this.clientId=Configuration.getConfiguration("LOOKER_CLIENT_ID");
		this.clientSecret=Configuration.getConfiguration("LOOKER_CLIENT_SECRET");
		this.endpointLocation = endpointLocation;
	}
	/**
	 * Constructs a looker API connection from environment variables and the provided credentials.
	 * @param clientId The client ID for authentication to the API.
	 * @param clientSecret The client secret for authentication to the API.
	 * @throws UnconfiguredException If there is no value for LOOKER_API_ENDPOINT in either the configuration class or the environment variables
	 * @throws LookerException If the provided details don't allow the API to authenticate.
	 */
	public Looker(String clientId, String clientSecret) throws UnconfiguredException,LookerException {
		this.clientId=clientId;
		this.clientSecret=clientSecret;
		this.endpointLocation = Configuration.getConfiguration("LOOKER_API_ENDPOINT");
		init();
	}
	/**
	 * Constructs a looker API connection from environment variables and the provided credentials.
	 * @param clientId The client ID for authentication to the API.
	 * @param clientSecret The client secret for authentication to the API.
	 * @param endpointLocation The endpoint location for the API.
	 * @throws LookerException If the provided details don't allow the API to authenticate.
	 */
	public Looker(String clientId, String clientSecret, String endpointLocation) throws LookerException {
		this.clientId=clientId;
		this.clientSecret=clientSecret;
		this.endpointLocation = endpointLocation;
		init();
	}
	private void init() throws LookerException{
		this.auth = new Authenticator(this.clientId,this.clientSecret,this.endpointLocation);
		this.closed=false;
		this.queries = new ArrayList<Query>();
		this.getAccessToken();
	}
	/**
	 * Runs a query on the given connection, and writes it out to the given file.
	 * @param query The query to run.
	 * @param connection The name of the connection to run the query on.
	 * @param outputLocation A file containing the outputs.
	 * @throws LookerException Thrown if anything goes wrong with the connection to Looker.
	 * @throws IOException Thrown if there is a problem writing the output file.
	 */
	public void writeSqlRunnerQueryToFile(String query, String connection, File outputLocation) throws LookerException, IOException{
		throwIfClosed();
		String url = String.format("%s%s", this.endpointLocation, "sql_queries");
		JSONObject payload = new JSONObject();
		payload.put("connection_name", connection);
		payload.put("sql", query);
		String result;
		try {
			result = NetworkUtils.sendDataToURL(url, true, NetworkUtils.POST, payload.toString(), Pair.<String,String>of("Authorization", "Bearer "+ this.getAccessToken()));
		} catch (IOException e) {
			throw new LookerException(e);
		}
		JSONObject json = new JSONObject(result);
		String slug = json.getString("slug");
		url = String.format("%s%s/%s/run/csv", this.endpointLocation, "sql_queries", slug);
		try{
			result = NetworkUtils.getDataFromURL(url, true, NetworkUtils.POST, Pair.<String,String>of("Authorization", "Bearer "+ this.getAccessToken()));
		} catch (IOException e) {
			throw new LookerException(e);
		}
		FileUtils.writeStringToFile(result, outputLocation);
	}
	
	/**
	 * Gets a new, empty query against the looker API defined by the object.
	 * @return A new, empty query.
	 * @throws LookerException If the object is closed.
	 */
	public Query newQuery() throws LookerException{
		throwIfClosed();
		Query q = new Query(this.getAccessToken(),this.endpointLocation);
		q.setLimit(500);
		return q;
	}
	/**
	 * Fetches the query matching the specified slug from the API.
	 * @param slug The slug corresponding to the desired query.
	 * @return The query matching the specified slug.
	 * @throws LookerException If the object is closed.
	 */
	public Query getQueryBySlug(String slug) throws LookerException {
		throwIfClosed();
		for (Query q: queries){
			if(q.getSlug().equals(slug))
				return q;
		}
		return Query.getQueryBySlug(this.getAccessToken(), this.endpointLocation, slug);
	}
	/**
	 * Doesn't actually work properly yet. DO NOT USE! Fetches the query matching the specified id from the API. 
	 * @param id The id corresponding to the desired query.
	 * @return The query matching the specified id.
	 * @throws LookerException If the object is closed.
	 */
	public Query getQueryById(int id) throws LookerException {
		throwIfClosed();
		for (Query q: queries){
			if(q.getId() == id)
				return q;
		}
		return Query.getQueryById(this.getAccessToken(), this.endpointLocation, id);
	}
	/**
	 * Gets a new query attached to this endpoint.
	 * @return A new, empty query object
	 * @throws LookerException If a new query could not be obtained.
	 */
	public Query getNewQuery() throws LookerException{
		return new Query(this.getAccessToken(), this.endpointLocation);
	}
	/**
	 * Gets the look with the given ID.
	 * @param id The ID of the desired look.
	 * @return The look with the given ID.
	 * @throws LookerException If the look could not be fetched.
	 */
	public Look getLookById(int id) throws LookerException {
		return Look.getLookById(this.getAccessToken(), this.endpointLocation, id);
	}
	/**
	 * Creates a new look with the given information. It will be unsaved when returned.
	 * @param title The title of the new look.
	 * @param space The space the look will be contained in.
	 * @param queryId The query id.
	 * @return The created look.
	 * @throws LookerException If the look could not be created.
	 */
	public Look createNewLook(String title, Space space, int queryId) throws LookerException{
		return Look.getNewLook(getAccessToken(), this.endpointLocation, title, space, queryId);
	}
	/**
	 * Gets the dashboard with the given ID.
	 * @param id The ID of the desired look.
	 * @return The dashboard.
	 * @throws LookerException If the dashboard could not be fetched.
	 */
	public Dashboard getDashboardById(int id) throws LookerException {
		return Dashboard.getDashboardById(id, getAccessToken(), this.endpointLocation);
	}
	/**
	 * Gets the space with the given ID.
	 * @param id The ID of the space.
	 * @return The space.
	 * @throws LookerException If the space could not be fetched.
	 */
	public Space getSpaceById(int id) throws LookerException {
		return Space.getSpaceById(this.getAccessToken(), this.endpointLocation, id);
	}
	/**
	 * Creates a new space. It will be saved.
	 * @param name The name of the new space
	 * @param parentSpaceId The id of the containing space.
	 * @return The space.
	 * @throws LookerException If the space could not be created.
	 */
	public Space createNewSpace(String name, int parentSpaceId) throws LookerException{
		return Space.createNewSpace(getAccessToken(), this.endpointLocation, name, parentSpaceId);
	}
	protected String getAccessToken() throws LookerException {
		return this.auth.getAccessToken();
	}
	
	protected String getEndpointLocation() { 
		return this.endpointLocation;
	}
	
	@Override
	public void close() throws IOException {
		this.closed=false;
		for (Query q: this.queries){
			q.close();
		}
		try{
			this.auth.logout();
		}catch(LookerException e){
			throw new IOException(e);
		}
	}
	private void throwIfClosed() throws LookerException {
		if (this.closed)
			throw new LookerException("LookerAPI closed. Please instatiate a new API.");
	}
}

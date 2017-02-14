package net.tullco.tullutils.lookerutils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tullco.tullutils.Configuration;
import net.tullco.tullutils.exceptions.LookerException;
import net.tullco.tullutils.exceptions.UnconfiguredException;

public class Looker implements Closeable {
	
	private Authenticator auth;
	private List<Query> queries;
	private String endpointLocation;
	private String clientId;
	private String clientSecret;
	
	private boolean closed;
	
	public Looker() throws UnconfiguredException, LookerException {
		this.clientId=Configuration.getConfiguration("LOOKER_CLIENT_ID");
		this.clientSecret=Configuration.getConfiguration("LOOKER_CLIENT_SECRET");
		this.endpointLocation = Configuration.getConfiguration("LOOKER_API_ENDPOINT");
		init();
	}
	public Looker(String endpointLocation) throws UnconfiguredException, LookerException {
		this.clientId=Configuration.getConfiguration("LOOKER_CLIENT_ID");
		this.clientSecret=Configuration.getConfiguration("LOOKER_CLIENT_SECRET");
		this.endpointLocation = endpointLocation;
	}
	public Looker(String clientId, String clientSecret) throws UnconfiguredException,LookerException {
		this.clientId=clientId;
		this.clientSecret=clientSecret;
		this.endpointLocation = Configuration.getConfiguration("LOOKER_API_ENDPOINT");
		init();
	}
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
	
	public Query newQuery() throws IOException, LookerException{
		throwIfClosed();
		Query q = new Query(this.getAccessToken(),this.endpointLocation);
		q.setLimit(500);
		return q;
	}
	public Query getQueryBySlug(String slug) throws IOException{
		throwIfClosed();
		for (Query q: queries){
			if(q.getSlug().equals(slug))
				return q;
		}
		return null;
	}
	public Query getQueryById(int id) throws IOException{
		throwIfClosed();
		for (Query q: queries){
			return q;
		}
		return null;
	}
	
	protected String getAccessToken() throws LookerException {
		return this.auth.getAccessToken();
	}
	
	protected String getEndpointLocation() { 
		return this.endpointLocation;
	}
	@Override
	public void close() throws IOException {
		try{
			this.auth.logout();
		}catch(LookerException e){
			throw new IOException(e);
		}
		this.closed=false;
	}
	private void throwIfClosed() throws IOException {
		if (this.closed)
			throw new IOException("LookerAPI closed. Please instatiate a new API.");
	}
}

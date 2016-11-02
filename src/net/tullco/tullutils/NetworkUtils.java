package net.tullco.tullutils;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import javafx.util.Pair;
import net.tullco.tullutils.exceptions.InvalidHTTPMethodException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;


public class NetworkUtils {

	/**
	 * The allowed HTTP methods.
	 */
	public static final String[] ALLOWED_METHODS = {"PUT","GET","POST","DELETE","HEAD"};
	public static final HashSet<String> METHOD_SET = new HashSet<String>(Arrays.asList(ALLOWED_METHODS));

	/**
	 * Sends data to a URL using the method provided, and return the response.
	 * @param url The URL to connect to.
	 * @param https True if https. False if http.
	 * @param method Probably either post or put here.
	 * @param data The data to send.
	 * @param headers JavaFX key value pairs of headers.
	 * @return A String containing the response.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws InvalidHTTPMethodException If the method given was invalid
	 */
	@SafeVarargs
	public static String sendDataToURL(String url
			,boolean https
			,String method
			,String data
			,Pair<String,String>... headers) throws MalformedURLException, IOException, InvalidHTTPMethodException{
		HttpURLConnection conn = getUrlConnection(url,https);
		conn.setDoOutput(true);
		for(Pair<String,String> h : headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(method);
		return sendDataToConnection(conn,data);
	}
	
	/**
	 * Sends data across a connection. This method assumes that the connection has been preconfigured with headers and methods.
	 * @param conn The connection to send the data across.
	 * @param data The data to send.
	 * @return The String response.
	 * @throws IOException
	 */
	public static String sendDataToConnection(HttpURLConnection conn,String data) throws IOException{
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();
		return getDataFromConnection(conn);
	}
	
	/**
	 * Gets the data from a URLConnection object.
	 * @param conn The connection to get data from
	 * @return The data at the endpoint.
	 * @throws UnsupportedEncodingException If the UTF-8 encoding is not supported.
	 * @throws IOException If an exception occurred getting the data.
	 */
	public static String getDataFromConnection(HttpURLConnection conn) throws UnsupportedEncodingException, IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String line;
		String data="";
		while((line=reader.readLine())!=null){
			data+=line;
		}
		reader.close();
		return data;
	}
	
	/**
	 * Gets the data at the URL using http or https.
	 * @param url The String representing the location of the resource you're trying to get.
	 * @param https True if this is an https connection. False if this is an http connection.
	 * @param method The HTTP method to use in this connection. Valid options include GET, HEAD, POST, PUT, and DELETE.
	 * @param headers A JavaFx pairs that will be the headers.
	 * @return The data at the location as a String.
	 * @throws MalformedURLException If the URL wasn't valid.
	 * @throws IOException If something went wrong getting the information.
	 * @throws InvalidHTTPMethodException 
	 */
	@SafeVarargs
	public static String getDataFromURL(String url
			,boolean https
			,String method
			,Pair<String,String>... headers) throws MalformedURLException, IOException, InvalidHTTPMethodException{
		if(!METHOD_SET.contains(method))
			throw new InvalidHTTPMethodException(method+ " is not a supported method.");
		HttpURLConnection conn = getUrlConnection(url,https);
		for(Pair<String,String> h:headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(method);
		return getDataFromConnection(conn);
	}
	
	/**
	 * Gets a URL Connection from a given String representing a url.
	 * @param url The String URL to make the connection for.
	 * @param https True if this is an https string, false if it is http
	 * @return A URLConnection object to this endpoint.
	 * @throws MalformedURLException If the URL is not well formed.
	 * @throws IOException If a communication error occurs.
	 */
	public static HttpURLConnection getUrlConnection(String url, boolean https) throws MalformedURLException, IOException{
		HttpURLConnection conn;
		if(https)
			conn = (HttpsURLConnection) new URL(url).openConnection();
		else
			conn = (HttpURLConnection) new URL(url).openConnection();
		return conn;
	}
	
	/**
	 * Gets a URL Connection form a given string. Attempts to dynamically detect http vs https,
	 * but the better choice is definitely using getUrlConnection(String url, boolean https) and
	 * declaring explicitly.
	 * @param url The URL to connect to.
	 * @return A URLConnection object to this endpoint.
	 * @throws MalformedURLException If the URL is not well formed.
	 * @throws IOException If a communication error occurs.
	 */
	public static HttpURLConnection getUrlConnection(String url) throws MalformedURLException, IOException{
		if(url.startsWith("https://"))
			return getUrlConnection(url,true);
		else
			return getUrlConnection(url,false);
	}
}

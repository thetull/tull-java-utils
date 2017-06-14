package net.tullco.tullutils;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is a static class that handles various network tasks, including sending and receiving data.
 * @author Tull Gearreald
 */
public final class NetworkUtils {

	/**
	 * The allowed HTTP methods. Currently PUT, GET, POST, DELETE, and HEAD.
	 */
	private static enum HttpMethods {PUT, GET, POST, DELETE, HEAD};
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;
	public static final HttpMethods PUT = HttpMethods.PUT;
	public static final HttpMethods GET = HttpMethods.GET;
	public static final HttpMethods POST = HttpMethods.POST;
	public static final HttpMethods DELETE = HttpMethods.DELETE;
	public static final HttpMethods HEAD = HttpMethods.HEAD;

	private boolean useHttps;
	private String url;
	private Set<Pair<String,String>> headers;
	private String dataToSend;
	private String contentType;
	private HttpMethods method;
	
	/**
	 * Creates a network connection object. The default settings are: GET, and HTTPS.
	 * @param url The url for this object. It can be changed later.
	 */
	public NetworkUtils(String url){
		this.headers=new HashSet<Pair<String,String>>();
		this.setMethod(HttpMethods.GET);
		this.setHttps(true);
		this.setUrl(url);
		this.setDataToSend(null);
		this.setContentType(null);
	}
	
	/**
	 * Uses the configured settings to get the data from the url.
	 * @return The data retrieved.
	 * @throws MalformedURLException If the URL is malformed.
	 * @throws IOException If there is an IOException
	 */
	public String getResponse() throws MalformedURLException, IOException{
		HttpURLConnection c = this.getConfiguredConnection();
		String response;
		if(this.getDataToSend()!=null){
			response = NetworkUtils.sendDataToConnection(c, this.getDataToSend());
		}else{
			response = NetworkUtils.getDataFromConnection(c);
		}
		return response;
	}
	
	private HttpURLConnection getConfiguredConnection() throws MalformedURLException, IOException{
		String urlToUse;
		if(isHttps()) {
			urlToUse = StringUtils.assureStartsWith(StringUtils.assureNotStartsWith(this.getUrl(), "http://"), "https://");
		}else{
			urlToUse = StringUtils.assureStartsWith(StringUtils.assureNotStartsWith(this.getUrl(), "https://"), "http://");
		}	
		HttpURLConnection c = NetworkUtils.getUrlConnection(urlToUse, isHttps());
		c.setRequestMethod(NetworkUtils.httpMethodToString(this.getMethod()));
		if(this.getContentType()!=null){
			c.setRequestProperty("Content-Type", this.getContentType());
		}
		for(Pair<String,String> p: this.getHeaders()){
			if(!p.left.equals("Content-Type")) {
				c.setRequestProperty(p.left,p.right);
			}
		}
		return c;
	}
	
	/**
	 * Checks whether or not this method will use HTTPS.
	 * @return Yes if the connection is using https. No otherwise.
	 */
	public boolean isHttps() {
		return useHttps;
	}
	/**
	 * Set if you want the connection to use HTTPS.
	 * @param useHttps True or false depending on if you want the connection to use HTTPS or HTTP
	 */
	public void setHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	/**
	 * Gets the current URL for the request.
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url for the request.
	 * @param url The url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get the currently attached headers.
	 * @return A set of pairs of headers.
	 */
	public Set<Pair<String,String>> getHeaders() {
		return headers;
	}

	/**
	 * Add the given key value pair to the headers.
	 * @param key The key of the header 
	 * @param value The value of the header.
	 */
	public void addHeaders(String key, String value) {
		this.headers.add(Pair.<String,String>of(key, value));
	}
	
	/**
	 * Remove all headers. After this call, no headers will be sent with the request until more are added. 
	 */
	public void clearHeaders(){
		this.headers.clear();
	}

	/**
	 * Get the data that will be sent across the network.
	 * @return the data that was sent.
	 */
	public String getDataToSend() {
		return dataToSend;
	}

	/**
	 * The data to send with the next request. Set to null if you don't want to send any data.
	 * @param dataToSend The data you want to send.
	 */
	public void setDataToSend(String dataToSend) {
		this.dataToSend = dataToSend;
	}

	/**
	 * Get the content type of the request
	 * @return The Content Type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Set the content type of the request
	 * @param contentType The content type to use
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Get the HTTP Method used in the request.
	 * @return The HTTP Method being used.
	 */
	public HttpMethods getMethod() {
		return method;
	}

	/**
	 * Set the HTTP Method to be used in the request.
	 * @param method The method to be used.
	 */
	public void setMethod(HttpMethods method) {
		this.method = method;
	}
	
	/**
	 * Sends data to a URL using the method provided, and return the response.
	 * @param url The URL to connect to.
	 * @param https True if https. False if http.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param data The data to send.
	 * @param headers JavaFX key value pairs of headers.
	 * @return A String containing the response.
	 * @throws MalformedURLException Throws if the URL is invalid
	 * @throws IOException If a network exception occurred when sending the data
	 */
	@SafeVarargs
	public final static String sendDataToURL(String url
			,boolean https
			,HttpMethods method
			,String data
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		HttpURLConnection conn = getUrlConnection(url,https);
		conn.setDoOutput(true);
		for(Pair<String,String> h : headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(httpMethodToString(method));
		return sendDataToConnection(conn,data);
	}

	/**
	 * Sends a byte array to a URL using the method provided, and return the response.
	 * @param url The URL to connect to.
	 * @param https True if https. False if http.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param binaryData A byte array containing the data you want to send.
	 * @param headers JavaFX key value pairs of headers.
	 * @return A String containing the response.
	 * @throws MalformedURLException Throws if the URL is invalid
	 * @throws IOException If a network exception occurred when sending the data
	 */
	@SafeVarargs
	public final static String sendBinaryDataToURL(String url
			,boolean https
			,HttpMethods method
			,byte[] binaryData
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		HttpURLConnection conn = getUrlConnection(url,https);
		conn.setDoOutput(true);
		for(Pair<String,String> h : headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(httpMethodToString(method));
		return sendBinaryDataToConnection(conn, binaryData, binaryData.length);
	}	
	
	
	/**
	 * Sends a byte array to a URL using the method provided, and return the response.
	 * @param url The URL to connect to.
	 * @param https True if https. False if http.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param binaryData A byte array containing the data you want to send.
	 * @param dataBytes The number of bytes to read out of the array
	 * @param headers JavaFX key value pairs of headers.
	 * @return A String containing the response.
	 * @throws MalformedURLException Throws if the URL is invalid
	 * @throws IOException If a network exception occurred when sending the data
	 */
	@SafeVarargs
	public final static String sendBinaryDataToURL(String url
			,boolean https
			,HttpMethods method
			,byte[] binaryData
			,int dataBytes
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		HttpURLConnection conn = getUrlConnection(url,https);
		conn.setDoOutput(true);
		for(Pair<String,String> h : headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(httpMethodToString(method));
		return sendBinaryDataToConnection(conn, binaryData, dataBytes);
	}	
	
	/**
	 * Sends data across a connection. This method assumes that the connection has been preconfigured with headers and methods.
	 * @param conn The connection to send the data across.
	 * @param data The data to send.
	 * @return The String response.
	 * @throws IOException If a network exception occurred when sending the data
	 */
	public final static String sendDataToConnection(HttpURLConnection conn,String data) throws IOException{
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();
		return getDataFromConnection(conn);
	}

	/**
	 * Sends data across a connection. This method assumes that the connection has been preconfigured with headers and methods.
	 * @param conn The connection to send the data across.
	 * @param binaryData A byte array containing the data you want to send.
	 * @param dataBytes The number of bytes in the data array.
	 * @return The String response.
	 * @throws IOException If a network exception occurred when sending the data
	 */
	public final static String sendBinaryDataToConnection(HttpURLConnection conn, byte[] binaryData, int dataBytes) throws IOException{
		conn.setDoOutput(true);
		conn.getOutputStream().write(binaryData, 0, dataBytes);
		return getDataFromConnection(conn);
	}
	
	/**
	 * Gets the data from a URLConnection object.
	 * @param conn The connection to get data from
	 * @return The data at the endpoint.
	 * @throws UnsupportedEncodingException If the UTF-8 encoding is not supported.
	 * @throws IOException If an exception occurred getting the data.
	 */
	public final static String getDataFromConnection(HttpURLConnection conn) throws UnsupportedEncodingException, IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String line;
		String data="";
		while((line=reader.readLine())!=null){
			data+=line;
			data+="\n";
		}
		reader.close();
		return data;
	}
	
	/**
	 * Gets the data as a byte array from a URLConnection object.
	 * @param conn The connection to get data from
	 * @return A byte array containing binary data from the endpoint.
	 * @throws UnsupportedEncodingException If the UTF-8 encoding is not supported.
	 * @throws IOException If an exception occurred getting the data.
	 */
	public final static byte[] getBinaryDataFromConnection(HttpURLConnection conn) throws UnsupportedEncodingException, IOException{
		byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
		byte[] output = new byte[0];
		InputStream input = conn.getInputStream();
		int bytesRead;
		while((bytesRead = input.read(buffer)) != -1){
			if(bytesRead < DOWNLOAD_BUFFER_SIZE){
				byte[] temp = new byte[bytesRead];
				for(int i = 0; i < temp.length; i++)
					temp[i]=buffer[i];
				output = MergeUtils.mergeByteArrays(output, temp);
			}else {
				output = MergeUtils.mergeByteArrays(output, buffer);
			}
		}
		return output;
	}
	
	/**
	 * Gets the data at the URL using http or https.
	 * @param url The String representing the location of the resource you're trying to get.
	 * @param https True if this is an https connection. False if this is an http connection.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param headers Apache pairs that will be the headers.
	 * @return The data at the url as a String.
	 * @throws MalformedURLException If the URL wasn't valid.
	 * @throws IOException If something went wrong getting the information.
	 */
	@SafeVarargs
	public final static String getDataFromURL(String url
			,boolean https
			,HttpMethods method
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		HttpURLConnection conn = getUrlConnection(url,https);
		for(Pair<String,String> h:headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(httpMethodToString(method));
		return getDataFromConnection(conn);
	}
	
	/**
	 * Gets the data at the URL as a byte array using http or https.
	 * @param url The String representing the location of the resource you're trying to get.
	 * @param https True if this is an https connection. False if this is an http connection.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param headers Apache pairs that will be the headers.
	 * @return The data at the url as a byte array.
	 * @throws MalformedURLException If the URL wasn't valid.
	 * @throws IOException If something went wrong getting the information.
	 */
	@SafeVarargs
	public final static byte[] getBinaryDataFromURL(String url
			,boolean https
			,HttpMethods method
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		HttpURLConnection conn = getUrlConnection(url,https);
		for(Pair<String,String> h:headers){
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setRequestMethod(httpMethodToString(method));
		return getBinaryDataFromConnection(conn);
	}
	
	/**
	 * Gets the data at the URL using http or https with retries on IOExceptions
	 * @param url The String representing the location of the resource you're trying to get.
	 * @param https True if this is an https connection. False if this is an http connection.
	 * @param method Use NetworkUtils.PUT/GET/POST/DELETE/HEAD for your use case.
	 * @param retries The number of times to retry the request.
	 * @param headers A JavaFx pairs that will be the headers.
	 * @return The data at the url as a String.
	 * @throws MalformedURLException If the URL wasn't valid.
	 * @throws IOException If something went wrong getting the information over all the retries.
	 */
	@SafeVarargs
	public final static String getDataFromURL(String url
			,boolean https
			,HttpMethods method
			,int retries
			,Pair<String,String>... headers) throws MalformedURLException, IOException {
		int i=0;
		while(true){
			try{
				return getDataFromURL(url,https,method,headers);
			}catch(IOException e){
				if(i<retries){
					try{
						Thread.sleep(5000);
					}catch(InterruptedException e2){
						throw e;
					}
					i++;
				}
				else{
					throw e;
				}
			}
		}
	}
	
	/**
	 * Gets a URL Connection from a given String representing a url.
	 * @param url The String URL to make the connection for.
	 * @param https True if this is an https string, false if it is http
	 * @return A URLConnection object to this endpoint.
	 * @throws MalformedURLException If the URL is not well formed.
	 * @throws IOException If a communication error occurs.
	 */
	public final static HttpURLConnection getUrlConnection(String url, boolean https) throws MalformedURLException, IOException{
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
	public final static HttpURLConnection getUrlConnection(String url) throws MalformedURLException, IOException{
		if(url.startsWith("https://"))
			return getUrlConnection(url,true);
		else
			return getUrlConnection(url,false);
	}
	private final static String httpMethodToString(HttpMethods hm){
		switch (hm){
			case PUT:
				return "PUT";
			case GET:
				return "GET";
			case POST:
				return "POST";
			case DELETE:
				return "DELETE";
			case HEAD:
				return "HEAD";
			default:
				return "GET";
		}
		
	}
}

package httpCommand;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import utils.HttpUtils;


public class HttpRequest {
	
	// request
	boolean forceHttps;
	String serverName;
	String method;
	String resourcePath;
	HashMap<String, String> headers;
	String body = "";
	
	// response
	int responseCode;
	String responseMessage;
	long elapsedTime;
	
	{
		headers = new HashMap<String, String>();
		body = new String("");
	}
	
	public boolean doPost() {
		String protocol = forceHttps? "Https" : "Http";
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader in = null;
		DataOutputStream out = null;
		DataOutputStream redirectOut = null;
		
		try {
			url = new URL(protocol + "://" + serverName + resourcePath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			stuffHttpHeader(conn);
			conn.setRequestMethod(method.toUpperCase());
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			long startTime = System.currentTimeMillis();
			
			// send request to server
			out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(body);

			// send another request if server implements redirect
			boolean redirect = false;
			setResponseCode(conn.getResponseCode());
			if(getResponseCode() != HttpURLConnection.HTTP_OK) {
				if(getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
					redirect = true;
			}
			if(redirect) {
				String newURL = conn.getHeaderField("Location");
				
				// open new connection
				conn = (HttpURLConnection) new URL(newURL).openConnection();
				stuffHttpHeader(conn);
				conn.setDoOutput(true);
				
				// send request to server
				redirectOut = new DataOutputStream(conn.getOutputStream());
				redirectOut.writeBytes(body);
				
				setResponseCode(conn.getResponseCode());
			}
			
//			System.out.println("\n-------------------Start-------------------\nRequest: " + httpMethod.toUpperCase() + " " + "to " + httpResourcePath + ";\nResult: " + conn.getResponseCode() + "\nBody:");
//			for(String key : conn.getHeaderFields().keySet()){
//				System.out.println(key + ":" + conn.getHeaderFields().get(key));
//			}
//			System.out.println("--------------------End--------------------");
//			
			// read response from server
			if(getResponseCode() > 399)
				in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			else
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			setElapsedTime(System.currentTimeMillis() - startTime);
			setResponseMessage(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			HttpUtils.closeQuietly(out);
			HttpUtils.closeQuietly(redirectOut);
			HttpUtils.closeQuietly(in);

			if(conn != null)
				conn.disconnect();
		}
		
		return true;
	}

	public boolean doGet() {
		String protocol = forceHttps? "Https" : "Http";
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader in = null;
		
		try {
			url = new URL(protocol + "://" + serverName + resourcePath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			stuffHttpHeader(conn);
			conn.setRequestMethod(method.toUpperCase());
			conn.setUseCaches(false);
			conn.setDoInput(true);

			long startTime = System.currentTimeMillis();
			
			// send another request if server implements redirect
			boolean redirect = false;
			setResponseCode(conn.getResponseCode());
			if(getResponseCode() != HttpURLConnection.HTTP_OK) {
				if(getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
					redirect = true;
			}
			if(redirect) {
				String newURL = conn.getHeaderField("Location");
				
				// open new connection
				conn = (HttpURLConnection) new URL(newURL).openConnection();
				stuffHttpHeader(conn);
				
				setResponseCode(conn.getResponseCode());
			}
			
//			System.out.println("\n-------------------Start-------------------\nRequest: " + httpMethod.toUpperCase() + " " + "to " + httpResourcePath + ";\nResult: " + conn.getResponseCode() + "\nBody:");
//			for(String key : conn.getHeaderFields().keySet()){
//				System.out.println(key + ":" + conn.getHeaderFields().get(key));
//			}
//			System.out.println("--------------------End--------------------");
			
			// read response from server
			if(getResponseCode() > 399)
				in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			else
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			setElapsedTime(System.currentTimeMillis() - startTime);
			setResponseMessage(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			HttpUtils.closeQuietly(in);

			if(conn != null)
				conn.disconnect();
		}
		
		return true;
	}
	
	private void stuffHttpHeader(HttpURLConnection connection) {
		for(String key : headers.keySet()) {
			connection.setRequestProperty(key, headers.get(key));
		}
	}
	
	public boolean getForceHttps() {
		return this.forceHttps;
	}
	
	public HttpRequest setForceHttps(boolean enable) {
		forceHttps = enable;
		return this;
	}
	
	public String getServerName() {
		return serverName;
	}

	public HttpRequest setServerName(String httpServerName) {
		this.serverName = httpServerName;
		return this;
	}

	public String getMethod() {
		return method;
	}

	public HttpRequest setMethod(String httpMethod) {
		this.method = httpMethod;
		return this;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public HttpRequest setResourcePath(String httpResourcePath) {
		this.resourcePath = httpResourcePath;
		return this;
	}

	public HashMap<String, String> getHeader() {
		return headers;
	}

	public HttpRequest setHeader(String httpHeader) {
		String[] headers = httpHeader.split("\\r?\\n");
		for(String header : headers) {
			String[] keyValuePair = header.split(":");
			this.headers.put(keyValuePair[0], keyValuePair[1]);
		}
		
		return this;
	}
	
	public String getData() {
		return body;
	}

	public HttpRequest setData(String httpData) {
		this.body = httpData;
		return this;
	}

	private void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	private void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	public String getResponseMessage() {
		return this.responseMessage;
	}
	
	private void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}
}

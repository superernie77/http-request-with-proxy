package com.jot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestExample {
	
	static {
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		System.out.println("Property set");
		System.out.println(System.getProperty("jdk.http.auth.tunneling.disabledSchemes"));
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		RequestExample reqex = new RequestExample();
		
		HttpResponse resp = reqex.request(new URL("http://www.google.de"), "", "GET");
		
		System.out.println(resp.getBody());
	}

	private void setup() {
		Authenticator.setDefault(new BasicAuthenticator());
	}

	private final class BasicAuthenticator extends Authenticator {
		protected PasswordAuthentication getPasswordAuthentication() {

			System.out.println("Authenticator method called!");

			return new PasswordAuthentication("", "1234".toCharArray());
		}
	}

	public HttpResponse request(URL urlToCall, String requestBody, String method) throws IOException {

		setup();

		final HttpURLConnection conn = openConnection(urlToCall);
		conn.setRequestMethod(method.toUpperCase());
		conn.setDoOutput(true);

		if (method.toUpperCase().equals("POST")) {
			DataOutputStream wr = null;
			try {
				wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(requestBody);
				wr.flush();
			} finally {
				if (wr != null) {
					wr.close();
				}
			}
		}
		return readResponseFromConnection(conn);

	}

	private HttpURLConnection openConnection(URL finalURL) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) finalURL.openConnection(proxy());
		//HttpURLConnection connection = (HttpURLConnection) finalURL.openConnection();
		connection.setConnectTimeout(10_000);
		connection.setReadTimeout(60_000);
		//connection.addRequestProperty(null, null);
		connection.setAuthenticator(new BasicAuthenticator());
		return connection;
	}

	private Proxy proxy() {
		return new Proxy(Type.HTTP, new InetSocketAddress("85.214.250.48", 3128));
	}

	private HttpResponse readResponseFromConnection(final HttpURLConnection conn) throws IOException {
		InputStream is = null;
		try {
			HttpResponse httpResponse = new HttpResponse();
			int responseCode = conn.getResponseCode();
			httpResponse.setStatusCode(responseCode);
			if (responseCode != HttpURLConnection.HTTP_OK) {
				is = conn.getErrorStream();
				if (is != null) {
					httpResponse.addHeaders(conn.getHeaderFields());
					httpResponse.setBody(inputStreamToString(is));
				}
				return httpResponse;
			}

			is = conn.getInputStream();
			httpResponse.addHeaders(conn.getHeaderFields());
			httpResponse.setBody(inputStreamToString(is));
			return httpResponse;
		} catch (SocketTimeoutException readException) {

			throw readException;
		} catch (ConnectException timeoutException) {

			throw timeoutException;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private String inputStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");) {	
			String result = s.hasNext() ? s.next() : "";
			return result;
		}
	}

}

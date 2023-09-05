package com.jot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

	/**
	 * HTTP response status code
	 */
	private int statusCode;

	/**
	 * HTTP response headers
	 */
	private Map<String, List<String>> headers = new HashMap<>();

	/**
	 * HTTP response body
	 */
	private String body;

	
	/**
	 * @param responseHeaders Map of HTTP headers returned from HTTP client
	 */
	public void addHeaders(Map<String, List<String>> responseHeaders) {
		for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
			if (entry.getKey() == null) {
				continue;
			}

			List<String> values = entry.getValue();
			if (values == null || values.isEmpty() || values.get(0) == null) {
				continue;
			}

			addHeader(entry.getKey(), values.toArray(new String[] {}));
		}
	}

	private void addHeader(final String name, final String... values) {
		if (values != null && values.length > 0) {
			headers.put(name, Arrays.asList(values));
		} else {
			headers.remove(name);
		}
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}

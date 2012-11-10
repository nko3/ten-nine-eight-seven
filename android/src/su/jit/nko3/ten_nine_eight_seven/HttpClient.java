package su.jit.nko3.ten_nine_eight_seven;

/***
 Copyright (c) 2009 
 Author: Stefan Klumpp <stefan.klumpp@gmail.com>
 Web: http://stefanklumpp.com

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class HttpClient {
	private static final String TAG = "HttpClient";

	public static JSONObject post(String URL, JSONObject jsonObjSend) {

		try {
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			setHeader(httpPostRequest);
			httpPostRequest.setHeader("Content-type", "application/json");
			return sendRequest(httpPostRequest);

		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static JSONObject delete(String URL) {
		try {
			
			HttpDelete httpDeleteRequest = new HttpDelete(URL);

			// Set HTTP parameters
			setHeader(httpDeleteRequest);
			
			return sendRequest(httpDeleteRequest);

		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return null;
	}
	
	private static void setHeader(HttpRequestBase httpRequest) {
		// Set HTTP parameters
		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Accept-Encoding", "gzip"); // only set
																// this
																// parameter
																// if you
																// would
																// like to
																// use gzip
																// compression
	}
	
	private static JSONObject sendRequest(HttpRequestBase httpRequest)
			throws ClientProtocolException, IOException, JSONException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		long t = System.currentTimeMillis();
		HttpResponse response = (HttpResponse) httpclient
				.execute(httpRequest);
		Log.i(TAG,
				"HTTPResponse received in ["
						+ (System.currentTimeMillis() - t) + "ms]");

		// Get hold of the response entity (-> the data):
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			// Read the content stream
			InputStream instream = entity.getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			// convert content stream to a String
			String resultString = convertStreamToString(instream);
			instream.close();
			
			Log.d("HTTPClient", "Received: " + resultString);

			// Transform the String into a JSONObject
			JSONObject jsonObjRecv = new JSONObject(resultString);
			// Raw DEBUG output of our received JSON object:
			Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString()
					+ "\n</JSONObject>");

			return jsonObjRecv;
		}
		return null;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 * 
		 * (c) public domain:
		 * http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/
		 * 11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}

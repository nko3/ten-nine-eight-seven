package su.jit.nko3.ten_nine_eight_seven;

import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.SERVER_URL;
import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.TAG;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class used to communicate with the node server.
 */
public final class ServerProxy {

	/**
	 * Register this account/user pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	static JSONObject register(final Location location, final String name) {
		Log.i(TAG, "Register user.");
		
		String serverUrl = SERVER_URL + "users/new";
		
		JSONObject json = new JSONObject();

		try {
			json.put("location", locationToJson(location));
			json.put("name", name);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return HttpClient.post(serverUrl, json);
	}
	
	static int update(final int uid, final Location location) {
		Log.i(TAG, "Register user.");
		
		String serverUrl = SERVER_URL + "users/" + uid;
		
		JSONObject json = new JSONObject();
		
		try {
			json.put("location", locationToJson(location));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		HttpClient.post(serverUrl, json);
		
		return uid;
	}

	/**
	 * Unregister the user
	 */
	static void unregister(final int uid) {
		Log.i(TAG, "Remove user (uid = " + uid + ")");

		String serverUrl = SERVER_URL + "users/" + uid;
		
		HttpClient.delete(serverUrl);
	}
	
	
	
	private static JSONObject locationToJson(Location location) {
		JSONObject json = new JSONObject();
		try {
			json.put("lat", location.getLatitude());
			json.put("lon", location.getLongitude());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}



	

	

}


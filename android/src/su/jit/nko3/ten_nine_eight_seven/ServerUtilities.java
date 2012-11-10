package su.jit.nko3.ten_nine_eight_seven;

import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.SERVER_URL;
import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.TAG;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	/**
	 * Register this account/user pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	static int register(final Context context, final Location location) {
		Log.i(TAG, "Register user.");
		
		String serverUrl = SERVER_URL + "users/new";
		
		JSONObject json = new JSONObject();
		
		try {
			json.put("location", locationToJson(location));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		JSONObject resp = HttpClient.post(serverUrl, json);
		
		int uid = -1;
		try {
			uid = resp.getInt("uid");
			Log.i(TAG, "Registered with uid = " + uid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
		return uid;
	}
	
	static int update(final Context context, final int uid, final Location location) {
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
	static void unregister(final Context context, final int uid) {
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


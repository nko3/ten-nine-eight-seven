package su.jit.nko3.ten_nine_eight_seven;

import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.TAG;
import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.SERVER_NAME;

import java.net.InetSocketAddress;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

public class Connection {

	private AsyncTask<Void, Void, Void> httpRequestsTask;
	private int _userId;
	private int _port;
	private Location _location;
	private boolean _isStarted = false;
	private Activity _activity;

	public Connection(Activity activity) {
		_activity = activity;
	}

	public void start() {

		httpRequestsTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Log.d(TAG, "Connection started.");

				String name = android.os.Build.MODEL;

				JSONObject resp = ServerProxy.register(_location, name, getRotation());

				try {
					_userId = resp.getInt("uid");
					_port = resp.getInt("port");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				sendSocket();

				_isStarted = true;
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				httpRequestsTask = null;
			}
		};
		httpRequestsTask.execute(null, null, null);

	}

	public void update(Location location) {
		_location = location;
		sendUpdate();
	}
	
	public void sendUpdate() {

		if (!_isStarted) {
			return;
		}
		httpRequestsTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				_userId = ServerProxy.update(_userId, _location, getRotation());
				_isStarted = true;

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				httpRequestsTask = null;
			}
		};
		httpRequestsTask.execute(null, null, null);
	}

	public void destroy() {
		if (httpRequestsTask != null) {
			httpRequestsTask.cancel(true);
		}

		httpRequestsTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Log.d(TAG, "Connection closed.");
				ServerProxy.unregister(_userId);
				_isStarted = false;
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				httpRequestsTask = null;
			}
		};
		httpRequestsTask.execute(null, null, null);
	}

	public int getUserId() {
		return _userId;
	}

	public void attached() {
		if (!_isStarted) {
			return;
		}
		sendSocket();
	}

	private void sendSocket() {
		Message socketMessage = new Message();
		socketMessage.obj = new InetSocketAddress(SERVER_NAME, _port);
		Main.socketHandler.sendMessage(socketMessage);

		Message statusMessage = new Message();
		statusMessage.obj = "User: " + _userId;
		Main.statusHandler.sendMessage(statusMessage);
	}
	
	private int getRotation() {
		int rotation = _activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		return degrees;
	}
}

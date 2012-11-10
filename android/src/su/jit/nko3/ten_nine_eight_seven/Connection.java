package su.jit.nko3.ten_nine_eight_seven;

import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.TAG;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class Connection {

	private AsyncTask<Void, Void, Void> httpRequestsTask;
	private int _userId;
	private Location _location;
	private boolean _isStarted = false;

	public void start() {
		httpRequestsTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Log.d(TAG, "Connection started.");
				_userId = ServerProxy.register(_location);
				// TODO: Update UI
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

		if (!_isStarted) {
			return;
		}
		httpRequestsTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				_userId = ServerProxy.update(_userId, _location);
				// TODO: Update UI
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
}

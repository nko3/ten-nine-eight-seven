package su.jit.nko3.ten_nine_eight_seven;

import static su.jit.nko3.ten_nine_eight_seven.CommonUtilities.TAG;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class Client {
	
	private AsyncTask<Void, Void, Void> registerTask;
	private AsyncTask<Void, Void, Void> unregisterTask;
	private Context _context;
	private int _userId;
	private Location _location;
	private AsyncTask<Void, Void, Void> updateTask;
	
	public Client(Context context) {
		_context = context;
		new ClientLocation(context, this);
	}

	public void updateLocation(Location location) {
		_location = location;
		
		updateTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				_userId = ServerUtilities.update(_context, _userId, _location);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				updateTask = null;
			}
		};
		updateTask.execute(null, null, null);
	}
	
	public void onResume() {
		Log.i(TAG, "onResume");
		registerTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				_userId = ServerUtilities.register(_context, _location);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				registerTask = null;
			}
		};
		registerTask.execute(null, null, null);
	}
	
	public void onPause() {
		Log.i(TAG, "onPause");
		unregisterTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				ServerUtilities.unregister(_context, _userId);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				unregisterTask = null;
			}
		};
		unregisterTask.execute(null, null, null);
	}
	
	public void onDestroy() {
		if (registerTask != null) {
            registerTask.cancel(true);
        }
        if (unregisterTask != null) {
            unregisterTask.cancel(true);
        }
        
        if (updateTask != null) {
        	updateTask.cancel(true);
        }
	}

}

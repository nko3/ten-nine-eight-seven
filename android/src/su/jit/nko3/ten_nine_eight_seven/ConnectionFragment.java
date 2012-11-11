package su.jit.nko3.ten_nine_eight_seven;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public class ConnectionFragment extends Fragment {

	private Connection _connection;
	private LocationService _locationService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("Fragment", "onCreate");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		_connection = new Connection();
		_locationService = new LocationService(this.getActivity(), _connection);
		_locationService.start();

		_connection.start();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_connection.attached();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		Log.d("Fragment", "onDestroy");
		_locationService.stop();
		_connection.destroy();
		super.onDestroy();
	}
}

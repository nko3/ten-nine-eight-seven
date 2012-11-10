package su.jit.nko3.ten_nine_eight_seven;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationService {

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int THIRTY_SECONDS = 1000 * 30;
	private Location _location;
	private Connection _connection;
	private LocationManager _locationManager;
	private Listener _locationListener;

	/**
	 * Location service for receiving location updates
	 * 
	 * @param context
	 * @param connection
	 */
	public LocationService(Context context, Connection connection) {

		_connection = connection;
		_locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		_locationListener = new Listener();
	}

	/**
	 * Start receiving locations
	 */
	public void start() {
		setLocation(_locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		setLocation(_locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));

		// Register the listener with the Location Manager to receive location
		// updates
		_locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, THIRTY_SECONDS, 0,
				_locationListener);
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				THIRTY_SECONDS, 0, _locationListener);
	}

	/**
	 * Stop receiving locations
	 */
	public void stop() {
		_locationManager.removeUpdates(_locationListener);
	}

	/**
	 * Update a location
	 * 
	 * @param location
	 */
	protected void setLocation(Location location) {
		if (location == null) {
			return;
		}
		if (isBetterLocation(location)) {
			_location = location;
			_connection.update(location);
		}
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location) {
		if (_location == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - _location.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - _location
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				_location.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Listener for location updates
	 */
	class Listener implements LocationListener {
		public void onLocationChanged(Location location) {
			setLocation(_location);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	}

}

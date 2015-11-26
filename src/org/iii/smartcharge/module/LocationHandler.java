package org.iii.smartcharge.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import org.iii.smartcharge.common.Logs;

public class LocationHandler
{
	private Activity					theActivity					= null;
	private LocationManager				locationManager				= null;
	private Criteria					criteria					= null;
	private OnLocationChangeListener	onLocationChangeListener	= null;
	private String						provider					= null;
	private Location					location					= null;

	public static interface OnLocationChangeListener
	{
		public void onLocation(double fla, double flo);
	}

	public void setOnLocationChangeListener(OnLocationChangeListener listener)
	{
		onLocationChangeListener = listener;
	}

	public LocationHandler(Activity activity)
	{
		theActivity = activity;
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
	}

	public void start()
	{
		provider = locationManager.getBestProvider(criteria, false);
		Logs.showTrace("Location Provider:" + provider);

		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled)
		{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			theActivity.startActivity(intent);
		}

		location = locationManager.getLastKnownLocation(provider);
		update();
	}

	public void LocationUpdates()
	{
		locationManager.requestLocationUpdates(provider, 400, 1, locationListener);
	}

	public void removeUpdates()
	{
		locationManager.removeUpdates(locationListener);
	}

	private final LocationListener locationListener = new LocationListener()
	{
		@Override
		public void onLocationChanged(Location location)
		{
			update(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Logs.showTrace("Provider now is enabled..");
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			update();
			Logs.showTrace("Location Provider now is disabled..");
			final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			theActivity.startActivity(intent);
		}

	};

	public void update(Location lt)
	{
		String latLng;

		if (lt != null)
		{
			double lat = lt.getLatitude();
			double lng = lt.getLongitude();
			if (null != onLocationChangeListener)
			{
				onLocationChangeListener.onLocation(lat, lng);
			}
			latLng = "Latitude:" + lat + " Longitude:" + lng;
		}
		else
		{
			latLng = "Can't access your location";
		}

		Logs.showTrace("Location:" + latLng);
	}

	public void update()
	{
		String latLng;

		if (location != null)
		{
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			if (null != onLocationChangeListener)
			{
				onLocationChangeListener.onLocation(lat, lng);
			}
			latLng = "Latitude:" + lat + " Longitude:" + lng;
		}
		else
		{
			latLng = "Can't access your location";
		}

		Logs.showTrace("Location:" + latLng);
	}

}

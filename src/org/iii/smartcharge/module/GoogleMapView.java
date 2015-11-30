package org.iii.smartcharge.module;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Global;
import org.iii.smartcharge.common.Logs;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition.Builder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class GoogleMapView extends RelativeLayout
{
	private final int			PADING				= 10;
	private GoogleMap			googleMap;
	private GoogleMapFragment	googleMapFragment	= null;
	private String				mstrMarker			= null;
	private double				mdLatitude			= 0;
	private double				mdLongitude			= 0;

	public GoogleMapView(Context context)
	{
		super(context);
		init(context);
	}

	public GoogleMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public GoogleMapView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		googleMapFragment = null;
		super.finalize();
	}

	private void init(Context context)
	{
		this.setOnTouchListener(mapViewTouchListener);
	}

	public void init(String strTag, Activity activity, int nMapType, double dLatitude, double dLongitude,
			int nZoomLevel, String strMarker)
	{

		this.setId(Global.getUserId());
		this.setTag(strTag);
		setPadding(PADING, PADING, PADING, PADING);

		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();

		googleMapFragment = new GoogleMapFragment();
		GoogleMapOptions options = new GoogleMapOptions();
		Builder cp = new CameraPosition.Builder();
		cp.target(new LatLng(dLatitude, dLongitude));
		cp.zoom(nZoomLevel);
		options.camera(cp.build()).mapType(nMapType);
		options.compassEnabled(false);

		Bundle localBundle = new Bundle();
		localBundle.putParcelable("MapOptions", options);

		googleMapFragment.setArguments(localBundle);
		googleMapFragment.setContainer(this);
		googleMapFragment.initHandler(mapHandler);

		Fragment fragment = activity.getFragmentManager().findFragmentByTag(strTag);
		if (null != fragment)
		{
			ft.remove(fragment);
		}
		ft.add(0, googleMapFragment, strTag);
		ft.commitAllowingStateLoss();

		mdLatitude = dLatitude;
		mdLongitude = dLongitude;
		mstrMarker = strMarker;
	}

	private boolean isValidMap()
	{
		if (null == googleMap)
		{
			return false;
		}
		return true;
	}

	public void setMarker(double latitude, double longitude, String title, String snippet)
	{
		if (!isValidMap())
		{
			this.mstrMarker = title;
			Logs.showError("Google Map Set Marker Fail!!");
			return;
		}
		MarkerOptions markerOpt = new MarkerOptions();
		LatLng latLng = new LatLng(latitude, longitude);
		markerOpt.position(latLng);
		markerOpt.title(title);
		markerOpt.snippet(snippet);
		markerOpt.draggable(false);
		markerOpt.visible(true);
		markerOpt.anchor(0.5f, 0.5f);
		markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.btn_location_blue));
		googleMap.addMarker(markerOpt);
		latLng = null;

	}

	public void drawPolyline(SparseArray<LatLng> listLatLng)
	{
		if (!isValidMap())
		{
			return;
		}

		PolylineOptions polylineOpt = new PolylineOptions();

		for (int i = 0; i < listLatLng.size(); ++i)
		{
			polylineOpt.add(listLatLng.get(i));
		}

		polylineOpt.color(Color.BLUE);

		Polyline polyline = googleMap.addPolyline(polylineOpt);
		polyline.setWidth(10);

		polylineOpt = null;
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new RelativeLayout.LayoutParams(nWidth, nHeight));
	}

	private void initOption()
	{
		MarkerOptions markerOpt = new MarkerOptions();
		LatLng latLng = new LatLng(mdLatitude, mdLongitude);
		markerOpt.position(latLng);
		markerOpt.draggable(false);
		markerOpt.visible(true);
		markerOpt.anchor(0.5f, 0.5f);
		markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		// markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
		if (null != mstrMarker && mstrMarker.length() > 0)
		{
			markerOpt.title(mstrMarker);
			markerOpt.snippet(mstrMarker);
		}
		googleMapFragment.getMap().addMarker(markerOpt);
		markerOpt = null;
		latLng = null;
	}

	private Handler mapHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case GoogleMapFragment.NOTIFY_VIEW_CREATED:
				googleMap = googleMapFragment.getGoogleMap();
				if (isValidMap())
				{
					initOption();
				}
				else
				{
					Logs.showError("googleMap invalid");
				}
				break;
			}
		}
	};

	OnTouchListener mapViewTouchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				// EventHandler.notify(Global.handlerActivity,
				// EventMessage.MSG_LOCK_PAGE, 0, 0, null);
				break;
			case MotionEvent.ACTION_UP:
				// EventHandler.notify(Global.handlerActivity,
				// EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				break;
			}

			return true;
		}
	};

}

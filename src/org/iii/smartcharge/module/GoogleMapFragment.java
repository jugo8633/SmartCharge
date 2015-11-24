package org.iii.smartcharge.module;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class GoogleMapFragment extends MapFragment
{
	public static final int		NOTIFY_VIEW_CREATED	= 0;
	private static GoogleMap	googleMap			= null;
	private RelativeLayout		relativeMain		= null;
	private Handler				theHandler			= null;

	public GoogleMapFragment()
	{
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (null != v)
		{
			if (null != relativeMain)
			{
				relativeMain.addView(v);
			}
			googleMap = getMap();
			if (null != theHandler)
			{
				//EventHandler.notify(theHandler, NOTIFY_VIEW_CREATED, 0, 0, null);
			}
		}

		v.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return true;
			}
		});
		return v;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onDestroyView()
	{
		if (null != relativeMain)
		{
			relativeMain.removeAllViews();
		}

		super.onDestroyView();
	}

	public GoogleMap getGoogleMap()
	{
		return googleMap;
	}

	public void setContainer(RelativeLayout rlMain)
	{
		relativeMain = rlMain;
	}

	public void initHandler(Handler handler)
	{
		theHandler = handler;
	}
}

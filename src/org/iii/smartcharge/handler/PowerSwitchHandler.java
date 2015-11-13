package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Logs;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PowerSwitchHandler extends BaseHandler
{

	private GridView			gridView	= null;
	private PowerSwitchAdapter	ppAdapter	= null;

	public PowerSwitchHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
		ppAdapter = new PowerSwitchAdapter();
	}

	public void init(View viewPowerSwitch)
	{
		if (null == viewPowerSwitch)
			return;

		gridView = (GridView) viewPowerSwitch.findViewById(R.id.gridViewPowerSwitch);
		if (null == gridView)
		{
			Logs.showTrace("Get Mission Grid View Fail");
		}

	}

	private class PowerSwitchAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

}

package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Logs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PowerSwitchHandler extends BaseHandler
{

	private GridView			gridView	= null;
	private PowerSwitchAdapter	ppAdapter	= null;

	public static class PortData
	{
		public int		mnWire;
		public int		mnPort;
		public int		mnPortNum;
		public boolean	mbState;

		public PortData(final int nWire, final int nPort, final int nPortNum, boolean bState)
		{
			mnWire = nWire;
			mnPort = nPort;
			mnPortNum = nPortNum;
			mbState = bState;
		}
	}

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

	public boolean setData(String strJSON)
	{
		ppAdapter.clear();
		try
		{
			JSONObject jsonMain = new JSONObject(strJSON);
			JSONArray jsonWires = jsonMain.getJSONArray("wires");
			JSONObject jsonWire = null;
			String strState = null;
			int nWire = 0;
			boolean bPort1 = false;
			boolean bPort2 = false;
			boolean bPort3 = false;
			boolean bPort4 = false;

			for (int i = 0; i < jsonWires.length(); ++i)
			{
				jsonWire = jsonWires.getJSONObject(i);
				nWire = jsonWire.getInt("wire");
				strState = jsonWire.getString("state");

				if (0 >= nWire || 4 != strState.length())
				{
					continue;
				}

				bPort1 = strState.substring(0, 1).contains("1") ? true : false;
				bPort2 = strState.substring(1, 2).contains("1") ? true : false;
				bPort3 = strState.substring(2, 3).contains("1") ? true : false;
				bPort4 = strState.substring(3).contains("1") ? true : false;
				Logs.showTrace("Port1: " + strState.substring(0, 1) + " Port2: " + strState.substring(1, 2) + " Port3: "
						+ strState.substring(2, 3) + " Port4: " + strState.substring(3));
				ppAdapter.addItem(nWire, bPort1, bPort2, bPort3, bPort4);
				Logs.showTrace("wire:" + String.valueOf(nWire) + " state:" + strState);
			}

		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return false;
		}

		ppAdapter.init(theActivity);
		gridView.setAdapter(ppAdapter);
		return true;
	}

	private class PowerSwitchAdapter extends BaseAdapter
	{

		private SparseArray<PortData>	listData;
		private SparseArray<View>		listView;

		public PowerSwitchAdapter()
		{
			listData = new SparseArray<PortData>();
			listView = new SparseArray<View>();
		}

		public void init(Context context)
		{

			TextView tvPortNum = null;
			ImageView ivSwitch = null;
			View itemView = null;

			for (int i = 0; i < listData.size(); ++i)
			{
				itemView = createView(R.layout.power_switch_item);
				if (null != itemView)
				{
					itemView.setTag(i);
					tvPortNum = (TextView) itemView.findViewById(R.id.textViewPowerSwitchItemNum);
					ivSwitch = (ImageView) itemView.findViewById(R.id.imageViewPowerSwitch);
					if (null != tvPortNum)
					{
						tvPortNum.setText(String.valueOf(listData.get(i).mnPortNum));
					}

					if (null != ivSwitch)
					{
						if (listData.get(i).mbState)
						{
							ivSwitch.setImageResource(R.drawable.btn_switch_on);
						}
						else
						{
							ivSwitch.setImageResource(R.drawable.btn_switch);
							ivSwitch.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									if (v instanceof ImageView)
									{
										((ImageView) v).setImageResource(R.drawable.btn_switch_on);
									}
								}
							});
						}
					}
					listView.append(i, itemView);
				}
			}
		}

		@Override
		public int getCount()
		{
			return listView.size();
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return listView.get(position);
		}

		public void clear()
		{
			listData.clear();
			listView.clear();
		}

		public void addItem(int nWire, boolean bPort1State, boolean bPort2State, boolean bPort3State,
				boolean bPort4State)
		{
			int nPort1Num = (4 * nWire) - 3;
			int nPort2Num = (4 * nWire) - 2;
			int nPort3Num = (4 * nWire) - 1;
			int nPort4Num = 4 * nWire;

			listData.append(listData.size(), new PortData(nWire, 1, nPort1Num, false));
			listData.append(listData.size(), new PortData(nWire, 2, nPort2Num, bPort2State));
			listData.append(listData.size(), new PortData(nWire, 3, nPort3Num, bPort3State));
			listData.append(listData.size(), new PortData(nWire, 4, nPort4Num, bPort4State));

			Logs.showTrace("Add Port: " + String.valueOf(nPort1Num) + " State: " + String.valueOf(bPort1State));
			Logs.showTrace("Add Port: " + String.valueOf(nPort2Num) + " State: " + String.valueOf(bPort2State));
			Logs.showTrace("Add Port: " + String.valueOf(nPort3Num) + " State: " + String.valueOf(bPort3State));
			Logs.showTrace("Add Port: " + String.valueOf(nPort4Num) + " State: " + String.valueOf(bPort4State));
		}

	}

}

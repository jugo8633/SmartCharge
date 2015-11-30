package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.MSG;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FootMenuHandler extends BaseHandler
{

	public final static int	ITEM_LEVEL			= 0;
	public final static int	ITEM_TEMPERATURE	= 1;
	public final static int	ITEM_HEALTH			= 2;
	public final static int	ITEM_VOLTAGE		= 3;
	public final static int	ITEM_CHARGE			= 4;
	public final static int	ITEM_STATION		= 5;
	private int				mnIndexSelected		= -1;

	private class ViewItem
	{
		private LinearLayout	mLayout			= null;
		private ImageView		mImage			= null;
		private TextView		mText			= null;
		private int				mnImgNormal		= 0;
		private int				mnImgSelected	= 0;
		private int				mnTextNormal	= Color.argb(255, 167, 173, 216);
		private int				mnTextSelected	= Color.GREEN;

		public ViewItem(LinearLayout layout, ImageView image, TextView text, int nResImgNormal, int nResImgSelected,
				int nResTxtColorNormal, int nResTxtColorSelected)
		{
			mLayout = layout;
			mImage = image;
			mText = text;
			mnImgNormal = nResImgNormal;
			mnImgSelected = nResImgSelected;
			mnTextNormal = nResTxtColorNormal;
			mnTextSelected = nResTxtColorSelected;
		}

		public void setSelect(boolean bSelect)
		{
			if (bSelect)
			{
				mImage.setImageResource(mnImgSelected);
				mText.setTextColor(mnTextSelected);
			}
			else
			{
				mImage.setImageResource(mnImgNormal);
				mText.setTextColor(mnTextNormal);
			}
		}
	}

	private SparseArray<ViewItem> listView = null;

	public FootMenuHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
		listView = new SparseArray<ViewItem>();
	}

	public void init()
	{
		int nColorNormal = theActivity.getResources().getColor(R.color.Gray_Word);

		listView.put(ITEM_LEVEL,
				new ViewItem(getItemLayout(R.id.linearLayoutPowerLevel), getItemImage(R.id.imageViewPowerLevel),
						getItemText(R.id.textViewPowerLevel), R.drawable.btn_ammeter_white,
						R.drawable.btn_ammetert_pink, nColorNormal, Color.RED));

		listView.put(ITEM_TEMPERATURE,
				new ViewItem(getItemLayout(R.id.linearLayoutTemperature),
						getItemImage(R.id.imageViewBatteryTemperature), getItemText(R.id.textViewBatteryTemperature),
						R.drawable.btn_temperature_white, R.drawable.btn_temperature_pink, nColorNormal, Color.RED));

		listView.put(ITEM_HEALTH,
				new ViewItem(getItemLayout(R.id.linearLayoutHealth), getItemImage(R.id.imageViewBatteryHealth),
						getItemText(R.id.textViewBatteryHealth), R.drawable.btn_health_white,
						R.drawable.btn_health_pink, nColorNormal, Color.RED));

		listView.put(ITEM_VOLTAGE,
				new ViewItem(getItemLayout(R.id.linearLayoutVoltage), getItemImage(R.id.imageViewBatteryVoltage),
						getItemText(R.id.textViewBatteryVoltage), R.drawable.btn_voltage_white,
						R.drawable.btn_voltage_pink, nColorNormal, Color.RED));

		listView.put(ITEM_CHARGE,
				new ViewItem(getItemLayout(R.id.linearLayoutCharge), getItemImage(R.id.imageViewBatteryCharge),
						getItemText(R.id.textViewBatteryCharge), R.drawable.btn_charge_white,
						R.drawable.btn_charge_pink, nColorNormal, Color.RED));

		listView.put(ITEM_STATION, new ViewItem(getItemLayout(R.id.linearLayoutStationLocation),
				getItemImage(R.id.imageViewBatteryStationLocation), getItemText(R.id.textViewBatteryStationLocation),
				R.drawable.btn_station_location, R.drawable.btn_station_location, nColorNormal, nColorNormal));

		for (int i = 0; i < listView.size(); ++i)
		{
			listView.get(i).mLayout.setTag(i);
			listView.get(i).mLayout.setOnClickListener(itemClick);
		}
	}

	private LinearLayout getItemLayout(int nResId)
	{
		LinearLayout lyItem = (LinearLayout) theActivity.findViewById(nResId);
		return lyItem;
	}

	private ImageView getItemImage(int nResId)
	{
		ImageView image = (ImageView) theActivity.findViewById(nResId);
		return image;
	}

	private TextView getItemText(int nResId)
	{
		TextView text = (TextView) theActivity.findViewById(nResId);
		return text;
	}

	public void setClicked(int nIndex)
	{
		if (ITEM_STATION == nIndex)
		{
			postMsg(MSG.FOOT_MENU_SELECT, nIndex, 0, null);
			return;
		}

		if (mnIndexSelected != nIndex)
		{
			if (-1 != mnIndexSelected)
			{
				listView.get(mnIndexSelected).setSelect(false);
			}
			listView.get(nIndex).setSelect(true);
			mnIndexSelected = nIndex;
			postMsg(MSG.FOOT_MENU_SELECT, mnIndexSelected, 0, null);
		}
	}

	private OnClickListener itemClick = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (v instanceof LinearLayout)
			{
				setClicked((Integer) v.getTag());
			}
		}
	};
}

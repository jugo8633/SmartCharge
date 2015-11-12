package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Global;
import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.common.MSG;
import org.iii.smartcharge.common.Utility;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ListMenuHandler extends BaseHandler
{

	public final static int	ITEM_CHARGE				= 0;
	public final static int	ITEM_SELECT_STATION		= 1;
	public final static int	ITEM_LOCATION_STATION	= 2;
	public final static int	ITEM_COPY_RIGHT			= 3;

	private ListView		listView				= null;
	private MenuAdapter		menuAdapter				= null;

	public static class MenuData
	{
		public int		nId;
		public String	strTitle;
		public int		nIconResource;

		public MenuData(final int id, final String title, final int icon)
		{
			nId = id;
			strTitle = title;
			nIconResource = icon;
		}
	}

	public ListMenuHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	@Override
	protected void finalize() throws Throwable
	{
		menuAdapter = null;
		super.finalize();
	}

	public boolean init()
	{
		listView = (ListView) theActivity.findViewById(R.id.ListView_drawer_menu_list);
		if (null == listView)
		{
			return false;
		}

		menuAdapter = new MenuAdapter();

		addItem(ITEM_CHARGE, theActivity.getString(R.string.start_charge), R.drawable.btn_charge);
		addItem(ITEM_SELECT_STATION, theActivity.getString(R.string.select_station), R.drawable.btn_select_station);
		addItem(ITEM_LOCATION_STATION, theActivity.getString(R.string.location_station),
				R.drawable.btn_station_location);
		addItem(ITEM_COPY_RIGHT, theActivity.getString(R.string.copy_right), R.drawable.copy_right);

		start();

		LinearLayout llLeftDrawer = (LinearLayout) theActivity.findViewById(R.id.left_drawer_main);
		llLeftDrawer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

		if (null != Global.fb_picture)
		{
			ImageView fbImg = (ImageView) theActivity.findViewById(R.id.imageViewFbPicture);
			fbImg.setImageBitmap(Utility.getRoundedCornerBitmap(Global.fb_picture, 100.0f));
		}

		if (null != Global.fb_nickname)
		{
			TextView tvFB = (TextView) theActivity.findViewById(R.id.textViewFbNickname);
			tvFB.setText(Global.fb_nickname);
		}

		return true;
	}

	public void addItem(final int nId, final String strTitle, final int nIcon)
	{
		if (null == menuAdapter)
			return;
		menuAdapter.addMenuItem(nId, strTitle, nIcon);
	}

	public void start()
	{
		if (null == menuAdapter)
			return;
		menuAdapter.init(theActivity);
		listView.setAdapter(menuAdapter);
	}

	private class MenuAdapter extends BaseAdapter
	{
		SparseArray<MenuData>	menuData	= null;
		SparseArray<View>		mItems		= null;

		public MenuAdapter()
		{
			menuData = new SparseArray<MenuData>();
			mItems = new SparseArray<View>();
		}

		public void init(Context context)
		{
			TextView tvTitle = null;
			ImageView ivIcon = null;
			for (int i = 0; i < menuData.size(); ++i)
			{
				View itemView = createView(R.layout.drawer_menu_item);
				if (null != itemView)
				{
					itemView.setTag(menuData.get(i).nId);
					tvTitle = (TextView) itemView.findViewById(R.id.drawer_menu_item_text);
					tvTitle.setText(menuData.get(i).strTitle);
					ivIcon = (ImageView) itemView.findViewById(R.id.drawer_menu_item_image);
					ivIcon.setImageResource(menuData.get(i).nIconResource);
					itemView.setOnTouchListener(itemTouchListener);
					mItems.append(i, itemView);
				}
			}
		}

		@Override
		protected void finalize() throws Throwable
		{
			if (null != menuData)
			{
				menuData.clear();
				menuData = null;
			}
			super.finalize();
		}

		@Override
		public int getCount()
		{
			return menuData.size();
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
			return mItems.get(position);
		}

		public void addMenuItem(final int nId, final String strTitle, final int nIconResource)
		{
			if (null != menuData)
			{
				MenuData itemData = new MenuData(nId, strTitle, nIconResource);
				menuData.append(menuData.size(), itemData);
			}
		}

		private OnTouchListener itemTouchListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (MotionEvent.ACTION_DOWN == event.getAction())
				{
					switch (event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						Logs.showTrace("list item click id:" + String.valueOf((Integer) v.getTag()));
						ListMenuHandler.this.postMsg(MSG.MENU_SELECTED, (Integer) v.getTag(), 0, null);
						break;
					}
				}
				return true;
			}
		};
	}
}

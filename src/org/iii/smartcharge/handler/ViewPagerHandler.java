package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.MSG;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class ViewPagerHandler extends BaseHandler
{
	private ViewPager			viewPager		= null;
	private ViewPagerAdapter	pagerAdapter	= null;
	public static int			PAGE_POWER;
	public static int			PAGE_TEMPERATURE;
	public static int			PAGE_HEALTH;
	public static int			PAGE_VOLTAGE;

	public ViewPagerHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	public boolean init()
	{
		viewPager = (ViewPager) theActivity.findViewById(R.id.ViewPager);

		if (null == viewPager)
		{
			return false;
		}

		viewPager.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return true;
			}
		});

		viewPager.addOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				if (arg0 == 2)
				{
					ViewPagerHandler.this.postMsg(MSG.PAGE_SELECTED, viewPager.getCurrentItem(), 0, null);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageSelected(int arg0)
			{

			}
		});

		LayoutInflater Inflater = LayoutInflater.from(theActivity);
		pagerAdapter = new ViewPagerAdapter();

		PAGE_POWER = pagerAdapter.addPage(Inflater.inflate(R.layout.power_level, null),
				theActivity.getString(R.string.power_level));
		PAGE_TEMPERATURE = pagerAdapter.addPage(Inflater.inflate(R.layout.battery_temperature, null),
				theActivity.getString(R.string.temperature));
		PAGE_HEALTH = pagerAdapter.addPage(Inflater.inflate(R.layout.battery_health, null),
				theActivity.getString(R.string.health));
		PAGE_VOLTAGE = pagerAdapter.addPage(Inflater.inflate(R.layout.battery_voltage, null),
				theActivity.getString(R.string.voltage));

		viewPager.setAdapter(pagerAdapter);
		return true;
	}

	public View getView(final int nId)
	{
		return pagerAdapter.getView(nId);
	}

	public void showPage(final int nPageIndex)
	{
		if (0 > nPageIndex || pagerAdapter.getCount() <= nPageIndex)
			return;

		viewPager.setCurrentItem(nPageIndex, true);
	}

	private class ViewPagerAdapter extends PagerAdapter
	{

		private class Page
		{
			public View		view		= null;
			public String	strTitle	= null;
		}

		private SparseArray<Page> Pages = null;

		public ViewPagerAdapter()
		{
			Pages = new SparseArray<Page>();
		}

		@Override
		public int getCount()
		{
			return Pages.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return (arg0 == arg1);
		}

		@Override
		public int getItemPosition(Object object)
		{
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return Pages.get(position).strTitle;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView(Pages.get(position).view);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			((ViewPager) container).addView(Pages.get(position).view, 0);
			return Pages.get(position).view;
		}

		public int addPage(final View view, final String strTitle)
		{
			Page page = new Page();
			page.view = view;
			page.strTitle = strTitle;
			Pages.put(Pages.size(), page);
			return (Pages.size() - 1);
		}

		public View getView(final int nId)
		{
			return Pages.get(nId).view;
		}

	}
}

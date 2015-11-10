package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.MSG;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;

public class DrawerMenuHandler extends BaseHandler
{
	private DrawerLayout	layDrawer	= null;

	public DrawerMenuHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	public void init(int nResource)
	{
		layDrawer = (DrawerLayout) theActivity.findViewById(nResource);

		if (null != layDrawer)
		{
			layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			layDrawer.setDrawerListener(drawerListener);
		}

	}

	public void switchDisplay()
	{
		if (null == layDrawer)
			return;

		if (layDrawer.isDrawerOpen(Gravity.START))
		{
			closeDrawer();
		}
		else
		{
			openDrawer();
		}
	}

	public void openDrawer()
	{
		if (null != layDrawer && !layDrawer.isDrawerOpen(Gravity.START))
		{
			layDrawer.openDrawer(Gravity.START);
		}
	}

	public void closeDrawer()
	{
		if (null != layDrawer && layDrawer.isDrawerOpen(Gravity.START))
		{
			layDrawer.closeDrawers();
			
		}
	}

	DrawerListener	drawerListener	= new DrawerListener()
									{

										@Override
										public void onDrawerClosed(View arg0)
										{
											postMsg(MSG.DRAWER_CLOSE, 0, 0, null);
										}

										@Override
										public void onDrawerOpened(View arg0)
										{
											postMsg(MSG.DRAWER_OPEN, 0, 0, null);
										}

										@Override
										public void onDrawerSlide(View arg0, float arg1)
										{
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onDrawerStateChanged(int arg0)
										{
											// TODO Auto-generated method stub
											
										}

									};
}

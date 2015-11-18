package org.iii.smartcharge.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public abstract class Device
{
	public static int getSdkVer()
	{
		int sdkInt;
		try
		{
			sdkInt = android.os.Build.VERSION.SDK_INT;
		}
		catch (NumberFormatException nfe)
		{
			sdkInt = 10000;
		}
		Logs.showTrace("Android SDK: " + String.valueOf(sdkInt));
		return sdkInt;
	}

	public static int getWidth(Context context)
	{
		int width = 0;
		width = getScreenResolution(context).x;
		return width;
	}

	public static int getHeight(Context context)
	{
		int height = 0;
		height = getScreenResolution(context).y;
		return height;
	}

	public static Point getScreenResolution(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static int getDisplayWidth(Context context)
	{
		int nWidth;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		nWidth = metrics.widthPixels;
		metrics = null;
		return nWidth;
	}

	public int getDisplayHeight(Context context)
	{
		int nHeight;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		nHeight = metrics.heightPixels;
		metrics = null;
		return nHeight;
	}

	public static int getDeviceWidth()
	{
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density + 0.5);
		return dpWidth;
	}

	public static int getDeviceHeight()
	{
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density + 0.5);
		return dpHeight;
	}

	public static float getScaleSize(Context context)
	{
		float nDeviceWidth = (float) getDeviceWidth();
		float nDisplayWidth = (float) getDisplayWidth(context);
		float nScale = nDisplayWidth / nDeviceWidth;
		if (0 >= nScale)
		{
			nScale = 1;
		}
		return nScale;
	}

	public boolean checkInstallation(String packageName, Context context)
	{
		try
		{
			context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		}
		catch (NameNotFoundException e)
		{
			return false;
		}
	}

	public void installApp(Activity activity, String strAppPackageName)
	{
		Uri uri = Uri.parse("market://details?id=" + strAppPackageName);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(it);
	}

	public int getAndroidSdkVersion()
	{
		return android.os.Build.VERSION.SDK_INT;
	}

	public String getAndroidReleaseVersion()
	{
		return android.os.Build.VERSION.RELEASE;
	}
}

package org.iii.smartcharge.common;

import android.content.Context;
import android.graphics.Point;
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
}

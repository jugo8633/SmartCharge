package org.iii.smartcharge.common;

import android.graphics.Bitmap;
import android.os.Handler;

public abstract class Global
{
	public static String	fb_nickname	= null;
	public static Bitmap	fb_picture	= null;
	public static Handler	mainHandler	= null;
	public static int		mnUserId	= 2048;
	public static double	Latitude	= 0.0;
	public static double	Longitude	= 0.0;

	public static int getUserId()
	{
		return (++mnUserId);
	}

}

package org.iii.smartcharge.module;

import java.io.File;

import org.iii.smartcharge.common.Logs;

import android.content.Context;

public class ClearCache
{

	private static boolean stmCleanCache = true;

	public ClearCache()
	{
		super();
	}

	public static void setCacheClean(boolean bClean)
	{
		stmCleanCache = bClean;
	}

	public void clearApplicationData(Context context)
	{
		if (!stmCleanCache)
		{
			return;
		}
		File cache = context.getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists())
		{
			String[] children = appDir.list();
			for (String s : children)
			{
				if (!s.equals("lib"))
				{
					deleteDir(new File(appDir, s));
				}
			}
		}
	}

	public void trimCache(Context context)
	{
		if (!stmCleanCache)
		{
			return;
		}
		try
		{
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory())
			{
				deleteDir(dir);
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	private boolean deleteDir(File dir)
	{
		if (dir != null && dir.isDirectory())
		{
			Logs.showTrace("Delete cache directory:" + dir.toString());
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}

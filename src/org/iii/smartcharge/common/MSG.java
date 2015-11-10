package org.iii.smartcharge.common;

public abstract class MSG
{
	public static final int	ID				= 1024;
	public static final int	SUCCESS			= 1;
	public static final int	FAIL			= 0;
	public static final int	MENU_CLICK		= ID + 1;
	public static final int	DRAWER_OPEN		= ID + 2;
	public static final int	DRAWER_CLOSE	= ID + 3;
	public static final int	BACK_CLICK		= ID + 4;
	public static final int	FLIPPER_CLOSE	= ID + 5;
	public static final int	MENU_SELECTED	= ID + 6;
}

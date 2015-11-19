package org.iii.smartcharge.common;

public abstract class MSG
{
	public static final int	ID					= 1024;
	public static final int	SUCCESS				= 1;
	public static final int	FAIL				= 0;
	public static final int	MENU_CLICK			= ID + 1;
	public static final int	DRAWER_OPEN			= ID + 2;
	public static final int	DRAWER_CLOSE		= ID + 3;
	public static final int	BACK_CLICK			= ID + 4;
	public static final int	FLIPPER_CLOSE		= ID + 5;
	public static final int	MENU_SELECTED		= ID + 6;
	public static final int	CHARGE_STATE		= ID + 7;
	public static final int	CHARGING			= ID + 8;
	public static final int	CHARGING_NOT		= ID + 9;
	public static final int	CHARGE_USB			= ID + 10;
	public static final int	CHARGE_AC			= ID + 11;
	public static final int	QR_CODE				= ID + 12;
	public static final int	FINISH				= ID + 13;
	public static final int	DIALOG_CLICKED		= ID + 14;
	public static final int	POWER_STATE			= ID + 15;
	public static final int	FB_LOGIN			= ID + 16;
	public static final int	FOOT_MENU_SELECT	= ID + 17;

}

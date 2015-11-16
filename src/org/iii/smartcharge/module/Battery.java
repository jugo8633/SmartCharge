package org.iii.smartcharge.module;

import org.iii.smartcharge.common.Common;
import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.common.MSG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery
{
	private IntentFilter	filterBattery	= null;
	private Intent			itbattery		= null;

	public class BatteryState
	{
		public boolean	bCharging;
		public int		nLevel;
		public boolean	bUsbCharge;
		public boolean	bAcCharge;

		public BatteryState()
		{
			bCharging = false;
			nLevel = 0;
			bUsbCharge = false;
			bAcCharge = false;
		}
	}

	public Battery()
	{
		filterBattery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	}

	public void analysis(Context context, BatteryState batteryState)
	{
		itbattery = context.registerReceiver(null, filterBattery);

		/** Get Battery level **/
		int level = itbattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = itbattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		batteryState.nLevel = (int) ((level / (float) scale) * 100);

		/** Get Charge State **/
		int status = itbattery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		batteryState.bCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;

		if (batteryState.bCharging)
		{
			int chargePlug = itbattery.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			batteryState.bUsbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			batteryState.bAcCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		}

	}
}

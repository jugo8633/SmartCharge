package org.iii.smartcharge.module;

import org.iii.smartcharge.common.Logs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class PowerConnectionReceiver extends BroadcastReceiver
{

	public PowerConnectionReceiver()
	{

	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;

		if (isCharging)
		{
			int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			if (usbCharge)
			{
				// Common.postMessage(Global.mainHandler, MSG.CHARGE_STATE,
				// MSG.CHARGING, MSG.CHARGE_USB, null);
				Logs.showTrace("Battery Charging Use USB");
			}
			else if (acCharge)
			{
				// Common.postMessage(Global.mainHandler, MSG.CHARGE_STATE,
				// MSG.CHARGING, MSG.CHARGE_AC, null);
				Logs.showTrace("Battery Charging Use AC");
			}
			else
			{
				// Common.postMessage(Global.mainHandler, MSG.CHARGE_STATE,
				// MSG.CHARGING, 0, null);
			}
		}
		else
		{
			// Common.postMessage(Global.mainHandler, MSG.CHARGE_STATE,
			// MSG.CHARGING_NOT, 0, null);
			Logs.showTrace("Battery Not Charging");
		}
	}

}

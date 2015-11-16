package org.iii.smartcharge.module;

import org.iii.smartcharge.common.Logs;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery
{
	private IntentFilter	filterBattery	= null;
	private Intent			itbattery		= null;
	private boolean			mbCharging		= false;
	private int				mnBatteryLevel	= 0;
	private float			mfTemperature	= 0.0f;

	public static class BatteryState
	{
		public boolean	bCharging;
		public int		nLevel;
		public boolean	bUsbCharge;
		public boolean	bAcCharge;
		public boolean	bChanged;
		public float	fTemperature;
		public String	strHealth;
		public int		nHealth;
		public int		nVoltage;

		public BatteryState()
		{
			init();
		}

		public void init()
		{
			bCharging = false;
			nLevel = 0;
			bUsbCharge = false;
			bAcCharge = false;
			bChanged = false;
			fTemperature = 0.0f;
			strHealth = "未知錯誤";
			nHealth = -1;
			nVoltage = 0;
		}
	}

	public Battery()
	{
		filterBattery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	}

	public void analysis(Context context, BatteryState batteryState)
	{
		itbattery = context.registerReceiver(null, filterBattery);

		batteryState.init();

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

		/** Get Battery Templature **/
		batteryState.fTemperature = ((float) itbattery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;

		if (mbCharging != batteryState.bCharging || mnBatteryLevel != batteryState.nLevel
				|| mfTemperature != batteryState.fTemperature)
		{
			batteryState.bChanged = true;
			mbCharging = batteryState.bCharging;
			mnBatteryLevel = batteryState.nLevel;
			mfTemperature = batteryState.fTemperature;

			Logs.showTrace("Battery level:" + String.valueOf(mnBatteryLevel));
			Logs.showTrace("Battery Temperature:" + String.valueOf(mfTemperature));
		}

		batteryState.nHealth = itbattery.getIntExtra(BatteryManager.EXTRA_HEALTH,
				BatteryManager.BATTERY_HEALTH_UNKNOWN);
		switch(batteryState.nHealth)
		{
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			batteryState.strHealth = "未知錯誤";
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			batteryState.strHealth = "狀態良好";
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			batteryState.strHealth = "電池沒有電";
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			batteryState.strHealth = "電池電壓過高";
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			batteryState.strHealth = "電池過熱";
			break;
		}

		batteryState.nVoltage = itbattery.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0); // 電池電壓

	}
}

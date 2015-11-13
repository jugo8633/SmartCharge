package org.iii.smartcharge;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.iii.smartcharge.common.Common;
import org.iii.smartcharge.common.Global;
import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.common.MSG;
import org.iii.smartcharge.common.Utility;
import org.iii.smartcharge.handler.ActionbarHandler;
import org.iii.smartcharge.handler.DialogHandler;
import org.iii.smartcharge.handler.DrawerMenuHandler;
import org.iii.smartcharge.handler.FlipperHandler;
import org.iii.smartcharge.handler.ListMenuHandler;
import org.iii.smartcharge.handler.PowerSwitchHandler;
import org.iii.smartcharge.module.CmpHandler;
import org.iii.smartcharge.module.FacebookHandler;
import org.iii.smartcharge.view.GaugeView;
import com.facebook.appevents.AppEventsLogger;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private ActionbarHandler	actionbarHandler	= null;
	private DrawerMenuHandler	drawerMenu			= null;
	private ListMenuHandler		listMenuHandler		= null;
	private GaugeView			powerGauge			= null;
	private Timer				chkChargeTimer		= null;
	private final int			LAYOUT_MAIN			= 2;
	private final int			LAYOUT_LOGIN		= 1;
	private Intent				batteryStatus		= null;
	private TextView			tvBatteryState		= null;
	private boolean				mbCharging			= false;
	private IntentFilter		filterCharge		= null;
	private int					mnBatteryLevel		= 0;
	private TextView			btnFacebook			= null;
	private TextView			btnSkip				= null;
	private FacebookHandler		facebook			= null;
	private CmpHandler			cmpHandler			= null;
	private PowerSwitchHandler	powerSwitchHandler	= null;
	private FlipperHandler		flipperHandler		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.getActionBar().hide();
		showStartUp();

		filterCharge = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

		String strHashKey = Utility.getHashKey(this);
		Logs.showTrace("Hash Key: " + strHashKey);

		cmpHandler = new CmpHandler();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (QrScanerActivity.ACTIVITY_REQUEST_CODE == requestCode)
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("barcode");
				if (Utility.isValidStr(scanResult))
				{
					Common.postMessage(selfHandler, MSG.QR_CODE, 0, 0, scanResult);
				}
				Logs.showTrace("QR Code:" + scanResult);
			}
		}

		FacebookHandler.callbackManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void showStartUp()
	{
		setContentView(R.layout.start_up);
		selfHandler.sendEmptyMessageDelayed(LAYOUT_LOGIN, 3000);
	}

	private void showLogin()
	{
		setContentView(R.layout.login);
		btnFacebook = (TextView) findViewById(R.id.textViewLoginFacebook);
		if (null != btnFacebook)
		{
			btnFacebook.setOnClickListener(itemClickListener);
		}
		btnSkip = (TextView) findViewById(R.id.textViewLoginSkip);
		if (null != btnSkip)
		{
			btnSkip.setOnClickListener(itemClickListener);
		}
	}

	private void showMainLayout()
	{
		Global.mainHandler = selfHandler;

		setContentView(R.layout.activity_main);
		actionbarHandler = new ActionbarHandler(this, selfHandler);
		actionbarHandler.init();
		drawerMenu = new DrawerMenuHandler(this, selfHandler);
		drawerMenu.init(R.id.drawer_layout);
		listMenuHandler = new ListMenuHandler(this, selfHandler);
		listMenuHandler.init();
		powerGauge = (GaugeView) findViewById(R.id.gaugeViewPower);
		powerGauge.setBackgroundResource(R.drawable.gauge_background);
		powerGauge.setAmount(-90);
		this.getActionBar().show();

		tvBatteryState = (TextView) findViewById(R.id.textViewBatteryState);

		if (null == chkChargeTimer)
		{
			chkChargeTimer = new Timer();
			setChargeTimerTask();
		}

		flipperHandler = new FlipperHandler(this, selfHandler);
		if (flipperHandler.init())
		{
			powerSwitchHandler = new PowerSwitchHandler(this, selfHandler);
			powerSwitchHandler.init(flipperHandler.getView(FlipperHandler.VIEW_ID_POWER_SWITCH));

		}

	}

	private void showQrScanner()
	{
		if (!Utility.checkInternet(MainActivity.this))
		{
			DialogHandler.showNetworkError(MainActivity.this, false);
			return;
		}
		Intent openCameraIntent = new Intent(MainActivity.this, QrScanerActivity.class);
		startActivityForResult(openCameraIntent, QrScanerActivity.ACTIVITY_REQUEST_CODE);
	}

	private void showLayout(final int nLayout)
	{
		switch(nLayout)
		{
		case ListMenuHandler.ITEM_CHARGE:
			showQrScanner();
			break;
		case ListMenuHandler.ITEM_SELECT_STATION:
			break;
		case ListMenuHandler.ITEM_LOCATION_STATION:
			showStationLocation();
			break;
		case ListMenuHandler.ITEM_COPY_RIGHT:
			showAbout();
			break;
		}
	}

	private void setChargeTimerTask()
	{
		chkChargeTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				batteryStatus = registerReceiver(null, filterCharge);

				/** Get Battery State **/
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int state = (int) ((level / (float) scale) * 100);

				/** Get Charge State **/
				int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
						|| status == BatteryManager.BATTERY_STATUS_FULL;

				if (mbCharging != isCharging || mnBatteryLevel != state)
				{
					mbCharging = isCharging;
					mnBatteryLevel = state;
					Logs.showTrace("Battery level:" + String.valueOf(mnBatteryLevel));

					if (mbCharging)
					{
						int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
						boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
						boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
						if (usbCharge)
						{
							Common.postMessage(selfHandler, MSG.CHARGE_STATE, MSG.CHARGING, MSG.CHARGE_USB, null);
						}
						else if (acCharge)
						{
							Common.postMessage(selfHandler, MSG.CHARGE_STATE, MSG.CHARGING, MSG.CHARGE_AC, null);
						}
						else
						{
							Common.postMessage(selfHandler, MSG.CHARGE_STATE, MSG.CHARGING, 0, null);
						}
					}
					else
					{
						Common.postMessage(selfHandler, MSG.CHARGE_STATE, MSG.CHARGING_NOT, 0, null);
					}
				}

			}
		}, 500, 1000);
	}

	private void setChargeState(int nIsCharge, int nChargeType)
	{
		if (null == tvBatteryState)
			return;
		String strState = null;
		boolean bCharging = nIsCharge == MSG.CHARGING ? true : false;
		if (bCharging)
		{
			switch(nChargeType)
			{
			case MSG.CHARGE_USB:
				strState = String.format(Locale.CHINESE, "%d%% - 充電中(USB)", mnBatteryLevel);
				break;
			case MSG.CHARGE_AC:
				strState = String.format(Locale.CHINESE, "%d%% - 充電中(AC)", mnBatteryLevel);
				break;
			default:
				strState = String.format(Locale.CHINESE, "%d%% - 充電中", mnBatteryLevel);
				break;
			}
		}
		else
		{
			strState = String.format(Locale.CHINESE, "%d%% - 非充電中", mnBatteryLevel);
		}
		tvBatteryState.setText(strState);
		int nDegress = (int) ((mnBatteryLevel * 1.8) - 90);
		powerGauge.setAmount(nDegress);
	}

	private OnClickListener itemClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
			case R.id.textViewLoginFacebook:
				if (Utility.checkInternet(MainActivity.this))
				{
					showFacebookLogin();
				}
				else
				{
					DialogHandler.showNetworkError(MainActivity.this, false);
				}
				break;
			case R.id.textViewLoginSkip:
				showMainLayout();
				break;
			}
		}
	};

	private void showFacebookLogin()
	{
		Logs.showTrace("Facebook Login Start");
		facebook = new FacebookHandler(this);
		facebook.init();
		facebook.setOnFacebookLoginResultListener(new FacebookHandler.OnFacebookLoginResult()
		{
			@Override
			public void onLoginResult(String strFBID, String strName, String strEmail, String strError)
			{
				Common.postMessage(selfHandler, LAYOUT_MAIN, 0, 0, null);
				Logs.showTrace("Login Facebook: " + strFBID + " " + strName + " " + strEmail + " " + strError);
			}
		});
		facebook.login();
	}

	private void powerPortSet(String strController, String strWire, String strPort, String strState)
	{
		cmpHandler.powerPortSet(strController, strWire, strPort, strState);
	}

	private void showPowerSwitch()
	{
		flipperHandler.showView(FlipperHandler.VIEW_ID_POWER_SWITCH);
		actionbarHandler.showBackBtn(true);
	}

	private void showStationLocation()
	{
		flipperHandler.showView(FlipperHandler.VIEW_ID_POWER_MAP);
		actionbarHandler.showBackBtn(true);
	}

	private void showAbout()
	{
		flipperHandler.showView(FlipperHandler.VIEW_ID_COPY_RIGHT);
		actionbarHandler.showBackBtn(true);
	}

	private Handler selfHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case LAYOUT_LOGIN:
				showLogin();
				break;
			case LAYOUT_MAIN:
				showMainLayout();
				break;
			case MSG.MENU_CLICK:
				drawerMenu.switchDisplay();
				break;
			case MSG.BACK_CLICK:
				flipperHandler.close();
				actionbarHandler.showBackBtn(false);
				break;
			case MSG.DRAWER_OPEN:
				actionbarHandler.setMenuState(true);
				break;
			case MSG.DRAWER_CLOSE:
				actionbarHandler.setMenuState(false);
				break;
			case MSG.MENU_SELECTED:
				drawerMenu.switchDisplay();
				showLayout(msg.arg1);
				break;
			case MSG.CHARGE_STATE:
				setChargeState(msg.arg1, msg.arg2);
				break;
			case MSG.QR_CODE:
				showPowerSwitch();
				// powerPortSet((String) msg.obj, "1", "2", "1");
				break;
			case MSG.FINISH:
				MainActivity.this.finish();
				break;
			}
		}

	};
}

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
import org.iii.smartcharge.module.Battery;
import org.iii.smartcharge.module.Battery.BatteryState;
import org.iii.smartcharge.module.CmpHandler;
import org.iii.smartcharge.module.FacebookHandler;
import org.iii.smartcharge.view.GaugeView;
import com.facebook.appevents.AppEventsLogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private ActionbarHandler	actionbarHandler		= null;
	private DrawerMenuHandler	drawerMenu				= null;
	private ListMenuHandler		listMenuHandler			= null;
	private GaugeView			powerGauge				= null;
	private Timer				chkChargeTimer			= null;
	private final int			LAYOUT_MAIN				= 2;
	private final int			LAYOUT_LOGIN			= 1;
	private TextView			tvBatteryState			= null;
	private TextView			btnFacebook				= null;
	private TextView			btnSkip					= null;
	private FacebookHandler		facebook				= null;
	private CmpHandler			cmpHandler				= null;
	private PowerSwitchHandler	powerSwitchHandler		= null;
	private FlipperHandler		flipperHandler			= null;
	private static Battery		battery					= null;
	private static BatteryState	batteryState			= null;
	private final int			BATTERY_STATE_BATTERY	= 0;
	private final int			BATTERY_STATE_AC		= 1;
	private final int			BATTERY_STATE_USB		= 2;
	private final int			BATTERY_STATE_WARNING	= 3;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.getActionBar().hide();
		showStartUp();

		battery = new Battery();
		batteryState = new Battery.BatteryState();

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
		Logs.showTrace("onActivityResult:" + String.valueOf(requestCode) + " " + String.valueOf(resultCode));
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
				battery.analysis(MainActivity.this, batteryState);

				if (batteryState.bChanged)
				{
					Common.postMessage(selfHandler, MSG.CHARGE_STATE, 0, 0, null);
				}
			}
		}, 500, 1000);
	}

	private void setChargeState()
	{
		if (null == tvBatteryState)
			return;
		String strState = String.format(Locale.CHINESE, "%d%%", batteryState.nLevel);

		if (batteryState.bCharging)
		{
			if (batteryState.bUsbCharge)
			{
				setBatteryState(BATTERY_STATE_USB);
			}
			else if (batteryState.bAcCharge)
			{
				setBatteryState(BATTERY_STATE_AC);
			}
			else
			{
				setBatteryState(BATTERY_STATE_BATTERY);
			}
		}
		else
		{
			setBatteryState(BATTERY_STATE_BATTERY);
		}

		tvBatteryState.setText(strState);
		int nDegress = (int) ((0 - (batteryState.nLevel * 1.8)) + 90);
		powerGauge.setAmount(nDegress);
	}

	private void setBatteryState(int nState)
	{
		ImageView ivPic = null;

		ivPic = (ImageView) findViewById(R.id.imageViewBSBattery);
		if (nState == BATTERY_STATE_BATTERY)
			ivPic.setImageResource(R.drawable.ic_battery_80_white_48dp);
		else
			ivPic.setImageResource(R.drawable.ic_battery_80_black_48dp);

		ivPic = (ImageView) findViewById(R.id.imageViewBSPowerAC);
		if (nState == BATTERY_STATE_AC)
			ivPic.setImageResource(R.drawable.ic_power_white_48dp);
		else
			ivPic.setImageResource(R.drawable.ic_power_black_48dp);

		ivPic = (ImageView) findViewById(R.id.imageViewBSUsb);
		if (nState == BATTERY_STATE_USB)
			ivPic.setImageResource(R.drawable.ic_usb_white_48dp);
		else
			ivPic.setImageResource(R.drawable.ic_usb_black_48dp);

		ivPic = (ImageView) findViewById(R.id.imageViewBSWarning);
		if (nState == BATTERY_STATE_WARNING)
			ivPic.setImageResource(R.drawable.ic_warning_white_48dp);
		else
			ivPic.setImageResource(R.drawable.ic_warning_black_48dp);

		TextView tvBattery = null;

		/** Battery Temperature **/
		tvBattery = (TextView) findViewById(R.id.textViewBatteryTemperature);
		tvBattery.setText(String.valueOf(batteryState.fTemperature) + "℃");

		/** Battery Health **/
		tvBattery = (TextView) findViewById(R.id.textViewBatteryHealth);
		tvBattery.setText(batteryState.strHealth);

		/** Battery Voltage **/
		tvBattery = (TextView) findViewById(R.id.textViewBatteryVoltage);
		tvBattery.setText(String.valueOf(batteryState.nVoltage) + "mV");
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
				setChargeState();
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

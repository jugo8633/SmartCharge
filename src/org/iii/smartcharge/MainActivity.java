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
// import org.iii.smartcharge.handler.DrawerMenuHandler;
import org.iii.smartcharge.handler.FlipperHandler;
import org.iii.smartcharge.handler.FootMenuHandler;
import org.iii.smartcharge.handler.ListMenuHandler;
import org.iii.smartcharge.handler.PowerSwitchHandler;
import org.iii.smartcharge.handler.ViewPagerHandler;
import org.iii.smartcharge.module.Battery;
import org.iii.smartcharge.module.Battery.BatteryState;
import org.iii.smartcharge.module.BitmapHandler;
import org.iii.smartcharge.module.CmpHandler;
import org.iii.smartcharge.module.FacebookHandler;
import org.iii.smartcharge.module.LocationHandler;
import org.iii.smartcharge.module.LocationHandler.OnLocationChangeListener;
import org.iii.smartcharge.view.ArcProgressBarView;
import org.iii.smartcharge.view.GaugeView;
import org.iii.smartcharge.view.HealthView;
import org.iii.smartcharge.view.TemperatureGaugeView;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private ActionbarHandler		actionbarHandler		= null;
	private DrawerMenuHandler		drawerMenu				= null;
	private ListMenuHandler			listMenuHandler			= null;
	private GaugeView				powerGauge				= null;
	private TemperatureGaugeView	temperatureGauge		= null;
	private HealthView				healthBar				= null;
	private ArcProgressBarView		voltageBar				= null;
	private Timer					chkChargeTimer			= null;
	private final int				LAYOUT_MAIN				= 2;
	private final int				LAYOUT_LOGIN			= 1;
	private TextView				tvBatteryState			= null;
	private TextView				btnFacebook				= null;
	private TextView				btnSkip					= null;
	private FacebookHandler			facebook				= null;
	private CmpHandler				cmpHandler				= null;
	private PowerSwitchHandler		powerSwitchHandler		= null;
	private FlipperHandler			flipperHandler			= null;
	private static Battery			battery					= null;
	private static BatteryState		batteryState			= null;
	private final int				BATTERY_STATE_BATTERY	= 0;
	private final int				BATTERY_STATE_AC		= 1;
	private final int				BATTERY_STATE_USB		= 2;
	private final int				BATTERY_STATE_WARNING	= 3;
	private Dialog					dialogLoading			= null;
	private ViewPagerHandler		viewPage				= null;
	private FootMenuHandler			footMenu				= null;
	private boolean					mbPowerStateUpdate		= true;
	private LocationHandler			locationHandler			= null;
	private GoogleMap				mMap					= null;

	final LatLng					III						= new LatLng(25.058577, 121.5548295);

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
		cmpHandler.setOnPowerStateResponseListener(powerStatelistener);

		Global.mainHandler = selfHandler;

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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (KeyEvent.KEYCODE_BACK == keyCode)
		{
			Logs.showTrace("Smart Charge Terminate!!");
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("onActivityResult:" + String.valueOf(requestCode) + " " + String.valueOf(resultCode));
		if (QrScanerActivity.ACTIVITY_REQUEST_CODE == requestCode)
		{
			footMenu.setClicked(FootMenuHandler.ITEM_LEVEL);
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("barcode");
				if (Utility.isValidStr(scanResult))
				{
					Common.postMessage(selfHandler, MSG.QR_CODE, 0, 0, scanResult);
				}
				else
				{
					DialogHandler.showAlert(this, this.getString(R.string.qr_fail), false);
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
		setContentView(R.layout.activity_main);
		actionbarHandler = new ActionbarHandler(this, selfHandler);
		actionbarHandler.init();
		drawerMenu = new DrawerMenuHandler(this, selfHandler);
		drawerMenu.init(R.id.drawer_layout);
		listMenuHandler = new ListMenuHandler(this, selfHandler);
		listMenuHandler.init();
		viewPage = new ViewPagerHandler(this, selfHandler);
		viewPage.init();
		powerGauge = (GaugeView) viewPage.getView(ViewPagerHandler.PAGE_POWER).findViewById(R.id.gaugeViewPower);
		powerGauge.setBackgroundResource(R.drawable.electricmeter);
		powerGauge.setAmount(-90);

		temperatureGauge = (TemperatureGaugeView) viewPage.getView(ViewPagerHandler.PAGE_TEMPERATURE)
				.findViewById(R.id.gaugeViewTemperature);
		temperatureGauge.setBackgroundResource(R.drawable.gauge_temperature);
		temperatureGauge.setAmount(-135);

		healthBar = (HealthView) viewPage.getView(ViewPagerHandler.PAGE_HEALTH).findViewById(R.id.healthViewBattery);
		healthBar.setAmount(0);

		voltageBar = (ArcProgressBarView) viewPage.getView(ViewPagerHandler.PAGE_VOLTAGE)
				.findViewById(R.id.arcProgressBarViewVoltage);
		voltageBar.setAmount(0);

		this.getActionBar().show();

		tvBatteryState = (TextView) viewPage.getView(ViewPagerHandler.PAGE_POWER)
				.findViewById(R.id.textViewBatteryState);

		footMenu = new FootMenuHandler(this, selfHandler);
		footMenu.init();

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

			locationHandler = new LocationHandler(this);
			locationHandler.setOnLocationChangeListener(new OnLocationChangeListener()
			{
				@Override
				public void onLocation(double fla, double flo)
				{
					Global.Latitude = fla;
					Global.Longitude = flo;
				}
			});
			locationHandler.start();

			initMap();
		}

		footMenu.setClicked(FootMenuHandler.ITEM_LEVEL);

	}

	@SuppressWarnings("deprecation")
	private void initMap()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null)
			{
				Bitmap bmpIcon = BitmapHandler.getResizedBitmap(this, "btn_location_blue", 80, 100);

				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				MarkerOptions markerOpt = new MarkerOptions();
				markerOpt.position(III);
				markerOpt.title("資策會");
				markerOpt.snippet("資策會智慧網路");
				markerOpt.draggable(false);
				markerOpt.visible(true);
				markerOpt.anchor(0.5f, 0.5f);
				markerOpt.icon(BitmapDescriptorFactory.fromBitmap(bmpIcon));
				mMap.addMarker(markerOpt);
			}
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
				if (batteryState.bChanged || mbPowerStateUpdate)
				{
					mbPowerStateUpdate = false;
					Common.postMessage(selfHandler, MSG.CHARGE_STATE, 0, 0, null);
					Logs.showTrace("Update Power State " + String.valueOf(batteryState.bChanged) + " "
							+ String.valueOf(mbPowerStateUpdate));
				}
			}
		}, 1000, 1000);
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
			ivPic.setImageResource(R.drawable.battery_green);
		else
			ivPic.setImageResource(R.drawable.battery_black);

		ivPic = (ImageView) findViewById(R.id.imageViewBSPowerAC);
		if (nState == BATTERY_STATE_AC)
			ivPic.setImageResource(R.drawable.plug_green);
		else
			ivPic.setImageResource(R.drawable.plug_black);

		ivPic = (ImageView) findViewById(R.id.imageViewBSUsb);
		if (nState == BATTERY_STATE_USB)
			ivPic.setImageResource(R.drawable.usb_green);
		else
			ivPic.setImageResource(R.drawable.usb_black);

		ivPic = (ImageView) findViewById(R.id.imageViewBSWarning);
		if (nState == BATTERY_STATE_WARNING)
			ivPic.setImageResource(R.drawable.warning_green);
		else
			ivPic.setImageResource(R.drawable.warning_black);

		TextView tvBattery = null;

		/** Battery Temperature **/
		int nDegress = (int) ((0 - (batteryState.fTemperature * 2.7)) + 135);
		temperatureGauge.setAmount(nDegress);
		tvBattery = (TextView) viewPage.getView(ViewPagerHandler.PAGE_TEMPERATURE)
				.findViewById(R.id.textViewTemperature);
		tvBattery.setText(String.valueOf(batteryState.fTemperature) + "℃");

		/** Battery Health **/
		healthBar.setAmount(batteryState.nHealth);
		tvBattery = (TextView) viewPage.getView(ViewPagerHandler.PAGE_HEALTH).findViewById(R.id.textViewBatteryHealth);
		switch(batteryState.nHealth)
		{
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			tvBattery.setText(getString(R.string.battery_health_unknow));
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			tvBattery.setText(getString(R.string.battery_health_good));
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			tvBattery.setText(getString(R.string.battery_health_dead));
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			tvBattery.setText(getString(R.string.battery_health_over_voltage));
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			tvBattery.setText(getString(R.string.battery_health_over_head));
			break;
		}

		/** Battery Voltage **/
		voltageBar.setAmount((int) (((batteryState.nVoltage * 0.1) - 320) * 2));
		tvBattery = (TextView) viewPage.getView(ViewPagerHandler.PAGE_VOLTAGE)
				.findViewById(R.id.textViewBatteryVoltage);
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

	// private void powerPortSet(String strController, String strWire, String
	// strPort, String strState)
	// {
	// cmpHandler.powerPortSet(strController, strWire, strPort, strState);
	// }

	private void showPowerSwitch(String strJSON)
	{
		boolean bSuccess = powerSwitchHandler.setData(strJSON);

		if (null != dialogLoading)
			dialogLoading.dismiss();

		if (!bSuccess)
		{
			DialogHandler.showAlert(MainActivity.this, MainActivity.this.getString(R.string.power_state_fail), false);
			return;
		}

		flipperHandler.showView(FlipperHandler.VIEW_ID_POWER_SWITCH);
		actionbarHandler.showBackBtn(true);
	}

	private void getPowerPortState(String strController)
	{
		dialogLoading = DialogHandler.showLoading(this);
		cmpHandler.powerPortState(strController, "0");
	}

	private void powerPortStateResp(int nResult, String strData)
	{
		if (CmpHandler.SUCCESS != nResult)
		{
			if (null != dialogLoading)
				dialogLoading.dismiss();
			DialogHandler.showAlert(MainActivity.this, MainActivity.this.getString(R.string.power_state_fail), false);
			return;
		}

		showPowerSwitch(strData);
	}

	private void showStationLocation()
	{
		locationHandler.LocationUpdates();
		locationHandler.update();
		flipperHandler.showView(FlipperHandler.VIEW_ID_POWER_MAP);
		actionbarHandler.showBackBtn(true);
	}

	private void showAbout()
	{
		flipperHandler.showView(FlipperHandler.VIEW_ID_COPY_RIGHT);
		actionbarHandler.showBackBtn(true);
	}

	private CmpHandler.OnPowerStateResponseListener powerStatelistener = new CmpHandler.OnPowerStateResponseListener()
	{
		@Override
		public void onResponse(int nResult, String strData)
		{
			Common.postMessage(selfHandler, MSG.POWER_STATE, nResult, 0, strData);
		}
	};

	private void switchViewPage(int nIndex)
	{
		if (FootMenuHandler.ITEM_CHARGE == nIndex)
		{
			showQrScanner();
			return;
		}

		switch(nIndex)
		{
		case FootMenuHandler.ITEM_LEVEL:
			powerGauge.setAmount(-90);
			break;
		case FootMenuHandler.ITEM_TEMPERATURE:
			temperatureGauge.setAmount(-135);
			break;
		case FootMenuHandler.ITEM_HEALTH:
			healthBar.setAmount(0);
			break;
		case FootMenuHandler.ITEM_VOLTAGE:
			voltageBar.setAmount(0);
			break;
		case FootMenuHandler.ITEM_STATION:
			showStationLocation();
			return;
		}
		viewPage.showPage(nIndex);
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
				// drawerMenu.switchDisplay();
				break;
			case MSG.BACK_CLICK:
				locationHandler.removeUpdates();
				flipperHandler.close();
				actionbarHandler.showBackBtn(false);
				mbPowerStateUpdate = true;
				break;
			case MSG.DRAWER_OPEN:
				actionbarHandler.setMenuState(true);
				break;
			case MSG.DRAWER_CLOSE:
				actionbarHandler.setMenuState(false);
				break;
			case MSG.MENU_SELECTED:
				// drawerMenu.switchDisplay();
				showLayout(msg.arg1);
				break;
			case MSG.CHARGE_STATE:
				setChargeState();
				break;
			case MSG.QR_CODE:
				// flipperHandler.showView(FlipperHandler.VIEW_ID_POWER_SWITCH);
				// actionbarHandler.showBackBtn(true);
				getPowerPortState((String) msg.obj);
				break;
			case MSG.FINISH:
				MainActivity.this.finish();
				break;
			case MSG.POWER_STATE:
				powerPortStateResp(msg.arg1, (String) msg.obj);
				break;
			case MSG.FB_LOGIN:
				Logs.showTrace("Facebook Relogin");
				facebook.login();
				break;
			case MSG.FOOT_MENU_SELECT:
				mbPowerStateUpdate = false;
				switchViewPage(msg.arg1);
				break;
			case MSG.PAGE_SELECTED:
				mbPowerStateUpdate = true;
				break;
			}
		}

	};
}

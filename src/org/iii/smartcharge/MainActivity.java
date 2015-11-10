package org.iii.smartcharge;

import org.iii.smartcharge.common.MSG;
import org.iii.smartcharge.handler.ActionbarHandler;
import org.iii.smartcharge.handler.DrawerMenuHandler;
import org.iii.smartcharge.handler.ListMenuHandler;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity
{
	private ActionbarHandler	actionbarHandler	= null;
	private DrawerMenuHandler	drawerMenu			= null;
	private ListMenuHandler		listMenuHandler		= null;
	private final int			LAYOUT_MAIN			= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.getActionBar().hide();
		showStartUp();
	}

	private void showStartUp()
	{
		setContentView(R.layout.start_up);
		selfHandler.sendEmptyMessageDelayed(LAYOUT_MAIN, 3000);
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
		this.getActionBar().show();
	}

	private void switchPage(final int nPage)
	{
		/*
		 * if (missionHandler.isShowMissionEnter()) {
		 * missionHandler.closeMissionEnter(); }
		 * 
		 * flipperHandler.close(); actionbarHandler.showBackBtn(false);
		 * 
		 * drawerMenu.switchDisplay();
		 * 
		 * switch (nPage) { case ListMenuHandler.PAGE_GIFT:
		 * viewPagerHandler.showPage(ViewPagerHandler.PAGE_GIFT);
		 * Global.theApplication.submitLog(AppSensor.TYPE_VIEW, null, null,
		 * "Gift Page", "兌獎", "Draw Menu"); break; case
		 * ListMenuHandler.PAGE_STORE:
		 * viewPagerHandler.showPage(ViewPagerHandler.PAGE_FIELD);
		 * Global.theApplication.submitLog(AppSensor.TYPE_VIEW, null, null,
		 * "Field Page", "合作商店", "Draw Menu"); break; case
		 * ListMenuHandler.PAGE_ACCOUNT:
		 * viewPagerHandler.showPage(ViewPagerHandler.PAGE_ACCOUNT);
		 * Global.theApplication.submitLog(AppSensor.TYPE_VIEW, null, null,
		 * "Account Page", "帳戶", "Draw Menu"); break; case
		 * ListMenuHandler.PAGE_SUPPORT: break; case
		 * ListMenuHandler.PAGE_ABOUNT:
		 * viewPagerHandler.showPage(ViewPagerHandler.PAGE_ABOUT);
		 * Global.theApplication.submitLog(AppSensor.TYPE_VIEW, null, null,
		 * "About Page", "關於", "Draw Menu"); break; }
		 */
	}

	private Handler selfHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case LAYOUT_MAIN:
					showMainLayout();
					break;
				case MSG.MENU_CLICK:
					drawerMenu.switchDisplay();
					break;
				case MSG.DRAWER_OPEN:
					actionbarHandler.setMenuState(true);
					break;
				case MSG.DRAWER_CLOSE:
					actionbarHandler.setMenuState(false);
					break;
				case MSG.MENU_SELECTED:
					switchPage(msg.arg1);
					break;
			}
		}

	};
}

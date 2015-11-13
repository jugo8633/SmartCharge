package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.view.FlipperView;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

public class FlipperHandler extends BaseHandler
{
	private final int	RESOURCE_FLIPPER		= R.id.flipperViewOption;
	private FlipperView	flipper					= null;
	public static int	VIEW_ID_POWER_SWITCH	= 0;
	public static int	VIEW_ID_POWER_MAP		= 0;
	public static int	VIEW_ID_COPY_RIGHT		= 0;

	public FlipperHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	public boolean init()
	{
		flipper = (FlipperView) theActivity.findViewById(RESOURCE_FLIPPER);
		if (null == flipper)
		{
			Logs.showTrace("Flipper View Init Fail");
			return false;
		}

		flipper.setNotifyHandler(theHandler);
		VIEW_ID_POWER_SWITCH = flipper.addChild(R.layout.power_switch);
		VIEW_ID_POWER_MAP = flipper.addChild(R.layout.station_location);
		VIEW_ID_COPY_RIGHT = flipper.addChild(R.layout.copy_right);

		return true;
	}

	public View getView(final int nId)
	{
		return flipper.getChildView(nId);
	}

	public void showView(final int nViewId)
	{
		if (null != flipper)
		{
			flipper.showView(nViewId);
		}
	}

	public void close()
	{
		if (null != flipper)
		{
			flipper.close();
		}
	}
}

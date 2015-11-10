package org.iii.smartcharge;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity
{

	private final int LAYOUT_MAIN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
			}
		}

	};
}

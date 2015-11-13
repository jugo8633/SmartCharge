package org.iii.smartcharge.handler;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.MSG;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionbarHandler extends BaseHandler
{
	private ImageView	listMenuBtn	= null;
	private TextView	tvPoint		= null;
	private ImageView	ibtnBack	= null;

	public ActionbarHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	public boolean init()
	{
		if (null == theActivity)
		{
			return false;
		}

		initMainActionbar();

		listMenuBtn = (ImageView) theActivity.findViewById(R.id.imageViewMenu);
		if (null != listMenuBtn)
		{
			listMenuBtn.setOnClickListener(buttonClick);
		}

		ibtnBack = (ImageView) theActivity.findViewById(R.id.imageViewActionbarBack);
		if (null != ibtnBack)
		{
			ibtnBack.setOnClickListener(buttonClick);
		}

		return true;
	}

	public void showBackBtn(boolean bShow)
	{
		if (null != ibtnBack)
		{
			if (bShow)
				ibtnBack.setVisibility(View.VISIBLE);
			else
				ibtnBack.setVisibility(View.INVISIBLE);
		}
	}

	private void initMainActionbar()
	{
		if (null == theActivity)
			return;
		ActionBar actionBar = theActivity.getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		// actionBar.setBackgroundDrawable(ContextCompat.getDrawable(theActivity,
		// R.drawable.action_bar_2));

		LayoutInflater inflater = LayoutInflater.from(theActivity);
		View vActionbar = inflater.inflate(R.layout.actionbar, null);

		// option handler
		actionBar.setCustomView(vActionbar);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		tvPoint = (TextView) theActivity.findViewById(R.id.TextViewPoint);
	}

	private OnClickListener buttonClick = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			handleButtonClick(view);
		}
	};

	private void handleButtonClick(View view)
	{
		if (view.getId() == R.id.imageViewMenu)
		{
			postMsg(MSG.MENU_CLICK, 0, 0, null);
		}

		if (view.getId() == R.id.imageViewActionbarBack)
		{
			postMsg(MSG.BACK_CLICK, 0, 0, null);
		}
	}

	public void setMenuState(boolean bOpen)
	{
		if (null == listMenuBtn)
			return;

		if (bOpen)
		{
			listMenuBtn.setImageResource(R.drawable.act_br_menu_open);
		}
		else
		{
			listMenuBtn.setImageResource(R.drawable.btn_menu);
		}
	}

	public void setPoint(int nPoint)
	{
		if (null != tvPoint)
		{
			tvPoint.setText(String.valueOf(nPoint));
		}
	}
}

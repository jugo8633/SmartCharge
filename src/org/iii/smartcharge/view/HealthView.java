package org.iii.smartcharge.view;

import org.iii.smartcharge.R;
import org.iii.smartcharge.common.Device;
import org.iii.smartcharge.common.Logs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class HealthView extends RelativeLayout
{
	private ImageView	ivPointer	= null;
	private int			fscal		= 0;

	public HealthView(Context context)
	{
		super(context);
		init(context);
	}

	public HealthView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public HealthView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		fscal = (int) Device.getScaleSize(context);
		ivPointer = new ImageView(context);
		ivPointer.setScaleType(ScaleType.CENTER_INSIDE);
		ivPointer.setAdjustViewBounds(false);
		ivPointer.setImageResource(R.drawable.btn_number_bg);
		RelativeLayout.LayoutParams ivPointerlayoutParams = new RelativeLayout.LayoutParams(80 * fscal, 80 * fscal);
		ivPointerlayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		ivPointer.setLayoutParams(ivPointerlayoutParams);
		addView(ivPointer);
	}

	public void setAmount(int nAmount)
	{
		ivPointer.clearAnimation();
		int nX = 0;

		switch(nAmount)
		{
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			ivPointer.setImageResource(R.drawable.btn_number_bg);
			nX = 0;
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			ivPointer.setImageResource(R.drawable.smile);
			nX = 23;
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			ivPointer.setImageResource(R.drawable.sad);
			nX = 360;
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			ivPointer.setImageResource(R.drawable.anger);
			nX = 250;
			break;
		}

		ivPointer.animate().translationX(nX * fscal).setDuration(200)
				.setInterpolator(new AccelerateDecelerateInterpolator()).setListener(listener);
	}

	private AnimatorListener listener = new AnimatorListener()
	{

		@Override
		public void onAnimationStart(Animator animation)
		{

		}

		@Override
		public void onAnimationEnd(Animator animation)
		{

		}

		@Override
		public void onAnimationCancel(Animator animation)
		{

		}

		@Override
		public void onAnimationRepeat(Animator animation)
		{

		}

	};

}

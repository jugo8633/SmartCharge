package org.iii.smartcharge.view;

import org.iii.smartcharge.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class TemperatureGaugeView extends RelativeLayout
{

	private int	mnStartAmount	= -135;
	private int	mnEndAmount		= 135;
	ImageView	ivPointer		= null;
	ImageView	ivCenter		= null;

	public TemperatureGaugeView(Context context)
	{
		super(context);
		init(context);
	}

	public TemperatureGaugeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public TemperatureGaugeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		ivPointer = new ImageView(context);
		ivPointer.setScaleType(ScaleType.CENTER_INSIDE);
		ivPointer.setAdjustViewBounds(false);
		//ivPointer.setImageResource(R.drawable.pointer);
		ivPointer.setBackgroundColor(Color.argb(155, 255, 50, 50));
		RelativeLayout.LayoutParams ivPointerlayoutParams = new RelativeLayout.LayoutParams(12,
				LayoutParams.MATCH_PARENT);
		ivPointerlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivPointerlayoutParams.setMargins(8, 80, 0, 200);
		ivPointer.setLayoutParams(ivPointerlayoutParams);
		addView(ivPointer);

		ivCenter = new ImageView(context);
		ivCenter.setScaleType(ScaleType.CENTER_INSIDE);
		ivCenter.setAdjustViewBounds(false);
		ivCenter.setImageResource(R.drawable.circle);
		RelativeLayout.LayoutParams ivCenterlayoutParams = new RelativeLayout.LayoutParams(60, 60);
		ivCenterlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		ivCenter.setLayoutParams(ivCenterlayoutParams);
		addView(ivCenter);
	}

	public void setAmount(int nAmount)
	{
		ivPointer.clearAnimation();
		mnEndAmount = nAmount;
		RotateAnimation rotate = new RotateAnimation(mnStartAmount, mnEndAmount, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		rotate.setInterpolator(new AccelerateDecelerateInterpolator());
		if (-135 == nAmount)
		{
			rotate.setDuration(0);
		}
		else
		{
			rotate.setDuration(1000);
		}

		rotate.setAnimationListener(listener);
		rotate.setFillAfter(true);
		ivPointer.setAnimation(rotate);
	}

	private AnimationListener listener = new AnimationListener()
	{

		@Override
		public void onAnimationStart(Animation animation)
		{

		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			mnStartAmount = mnEndAmount;
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{

		}
	};
}

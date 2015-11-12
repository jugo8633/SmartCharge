package org.iii.smartcharge.view;

import org.iii.smartcharge.common.Logs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class GaugeView extends RelativeLayout
{
	private int	mnStartAmount	= -90;
	private int	mnEndAmount		= 90;
	ImageView	ivPointer		= null;

	public GaugeView(Context context)
	{
		super(context);
		init(context);
	}

	public GaugeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public GaugeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		ivPointer = new ImageView(context);
		ivPointer.setScaleType(ScaleType.FIT_XY);
		ivPointer.setAdjustViewBounds(false);
		ivPointer.setBackgroundColor(Color.argb(155, 0, 255, 0));
		RelativeLayout.LayoutParams ivPointerlayoutParams = new RelativeLayout.LayoutParams(15,
				LayoutParams.MATCH_PARENT);
		ivPointerlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivPointerlayoutParams.setMargins(0, 80, 0, 20);
		ivPointer.setLayoutParams(ivPointerlayoutParams);

		addView(ivPointer);
	}

	public void setAmount(int nAmount)
	{
		ivPointer.clearAnimation();
		mnEndAmount = nAmount;
		RotateAnimation rotate = new RotateAnimation(mnStartAmount, mnEndAmount, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.9f);
		rotate.setInterpolator(new AccelerateDecelerateInterpolator());
		rotate.setDuration(1000);
		rotate.setAnimationListener(listener);
		rotate.setFillAfter(true);
		ivPointer.setAnimation(rotate);
//		Logs.showTrace("set amount:" + String.valueOf(mnEndAmount));
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

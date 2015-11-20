package org.iii.smartcharge.view;

import org.iii.smartcharge.R;

import android.content.Context;
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
	ImageView	ivCover			= null;

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
		ivPointer.setScaleType(ScaleType.CENTER_INSIDE);
		ivPointer.setAdjustViewBounds(false);
		ivPointer.setImageResource(R.drawable.pointer);
		RelativeLayout.LayoutParams ivPointerlayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ivPointerlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivPointerlayoutParams.setMargins(0, 75, 0, 5);
		ivPointer.setLayoutParams(ivPointerlayoutParams);
		addView(ivPointer);

		ivCover = new ImageView(context);
		ivCover.setScaleType(ScaleType.FIT_XY);
		ivCover.setAdjustViewBounds(false);
		ivCover.setImageResource(R.drawable.cover);
		RelativeLayout.LayoutParams ivCoverlayoutParams = new RelativeLayout.LayoutParams(200, 120);
		ivCoverlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivCoverlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		ivCover.setLayoutParams(ivCoverlayoutParams);
		addView(ivCover);
	}

	public void setAmount(int nAmount)
	{
		ivPointer.clearAnimation();
		mnEndAmount = nAmount;
		RotateAnimation rotate = new RotateAnimation(mnStartAmount, mnEndAmount, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.9f);
		rotate.setInterpolator(new AccelerateDecelerateInterpolator());
		if (-90 == nAmount)
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

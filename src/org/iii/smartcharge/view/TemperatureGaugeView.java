package org.iii.smartcharge.view;

import org.iii.smartcharge.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TemperatureGaugeView extends RelativeLayout
{

	private int	mnStartAmount	= -135;
	private int	mnEndAmount		= 135;
	ImageView	ivPointer		= null;

	public TemperatureGaugeView(Context context)
	{
		super(context);
	}

	public TemperatureGaugeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public TemperatureGaugeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		ivPointer = (ImageView) this.findViewById(R.id.imageViewBatteryTemperaturePointer);
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
		// ivPointer.setAnimation(rotate);
		ivPointer.startAnimation(rotate);
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

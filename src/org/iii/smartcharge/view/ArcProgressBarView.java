package org.iii.smartcharge.view;

import org.iii.smartcharge.common.Logs;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgressBarView extends View
{

	private int		bgStrokeWidth		= 80;
	private int		barStrokeWidth		= 60;
	private int		bgColor				= Color.DKGRAY;
	private int		barColor			= Color.GREEN;
	private int		smallBgColor		= Color.LTGRAY;
	private int		progress			= 0;
	private int		angleOfMoveCircle	= 140;			// 移動小園的起始角度。
	private int		startAngle			= 140;
	private int		endAngle			= 260;
	private Paint	mPaintBar			= null;
	private Paint	mPaintSmallBg		= null;
	private Paint	mPaintBg			= null;
	private Paint	mPaintCircle		= null;
	private RectF	rectBg				= null;
	private int		diameter			= 350;			// 直徑
	private boolean	showSmallBg			= true;			// 是否顯示小背景。
	private boolean	showMoveCircle		= false;		// 是否顯示移動的小園。
	private int		cx1					= 0;
	private int		cy1					= 0;
	private int		arcRadius			= 0;
	private int		mnCurrent			= 0;

	public ArcProgressBarView(Context context)
	{
		super(context);
		init(context);
	}

	public ArcProgressBarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ArcProgressBarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		DrawBar(canvas);
		calcSpeed();
	}

	private void init(Context context)
	{
		// 畫弧形的矩陣區域。
		rectBg = new RectF(bgStrokeWidth, bgStrokeWidth, diameter, diameter);

		// 計算弧形的圓心和半徑。
		cx1 = (diameter + bgStrokeWidth) / 2;
		cy1 = (diameter + bgStrokeWidth) / 2;
		arcRadius = (diameter - bgStrokeWidth) / 2;

	}

	private void DrawBar(Canvas canvas)
	{
		// ProgressBar結尾和開始畫2個圓，實現ProgressBar的圓角。
		mPaintCircle = new Paint();
		mPaintCircle.setAntiAlias(true);
		mPaintCircle.setColor(bgColor);

		canvas.drawCircle((float) (cx1 + arcRadius * Math.cos(startAngle * 3.14 / 180)),
				(float) (cy1 + arcRadius * Math.sin(startAngle * 3.14 / 180)), bgStrokeWidth / 2, mPaintCircle);// 小圓

		canvas.drawCircle((float) (cx1 + arcRadius * Math.cos((180 - startAngle) * 3.14 / 180)),
				(float) (cy1 + arcRadius * Math.sin((180 - startAngle) * 3.14 / 180)), bgStrokeWidth / 2, mPaintCircle);// 小圓

		// 弧形背景。
		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStyle(Style.STROKE);
		mPaintBg.setStrokeWidth(bgStrokeWidth);
		mPaintBg.setColor(bgColor);
		canvas.drawArc(rectBg, startAngle, endAngle, false, mPaintBg);

		// 弧形小背景。
		if (showSmallBg)
		{
			mPaintSmallBg = new Paint();
			mPaintSmallBg.setAntiAlias(true);
			mPaintSmallBg.setStyle(Style.STROKE);
			mPaintSmallBg.setStrokeWidth(barStrokeWidth);
			mPaintSmallBg.setColor(smallBgColor);
			canvas.drawArc(rectBg, startAngle, endAngle, false, mPaintSmallBg);
		}

		// 弧形ProgressBar。
		mPaintBar = new Paint();
		mPaintBar.setAntiAlias(true);
		mPaintBar.setStyle(Style.STROKE);
		mPaintBar.setStrokeWidth(barStrokeWidth);
		mPaintBar.setColor(barColor);

		BlurMaskFilter mBGBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.INNER);
		mPaintBar.setMaskFilter(mBGBlur);

		canvas.drawArc(rectBg, startAngle, progress, false, mPaintBar);

		// 隨ProgressBar移動的圓。
		if (showMoveCircle)
		{
			mPaintCircle.setColor(barColor);
			canvas.drawCircle((float) (cx1 + arcRadius * Math.cos(angleOfMoveCircle * 3.14 / 180)),
					(float) (cy1 + arcRadius * Math.sin(angleOfMoveCircle * 3.14 / 180)), bgStrokeWidth / 2,
					mPaintCircle);// 小圓
		}

		// invalidate();
	}

	/**
	 * 
	 * @param progress
	 */
	public void addProgress(int _progress)
	{
		mnCurrent = _progress;
		progress = 0;
		// progress += _progress;
		// angleOfMoveCircle += _progress;
		// System.out.println(progress);
		// if (progress > endAngle)
		// {
		// progress = 0;
		// angleOfMoveCircle = startAngle;
		// }
		invalidate();
	}

	private void calcSpeed()
	{

		if (progress < mnCurrent)
		{
			progress += 2;
			angleOfMoveCircle += progress;
			if (progress > mnCurrent)
			{
				return;
			}
			invalidate();
		}

	}

	/**
	 * 設置弧形背景的畫筆寬度。
	 */
	public void setBgStrokeWidth(int bgStrokeWidth)
	{
		this.bgStrokeWidth = bgStrokeWidth;
	}

	/**
	 * 設置弧形ProgressBar的畫筆寬度。
	 */
	public void setBarStrokeWidth(int barStrokeWidth)
	{
		this.barStrokeWidth = barStrokeWidth;
	}

	/**
	 * 設置弧形背景的顏色。
	 */
	public void setBgColor(int bgColor)
	{
		this.bgColor = bgColor;
	}

	/**
	 * 設置弧形ProgressBar的顏色。
	 */
	public void setBarColor(int barColor)
	{
		this.barColor = barColor;
	}

	/**
	 * 設置弧形小背景的顏色。
	 */
	public void setSmallBgColor(int smallBgColor)
	{
		this.smallBgColor = smallBgColor;
	}

	/**
	 * 設置弧形的直徑。
	 */
	public void setDiameter(int diameter)
	{
		this.diameter = diameter;
	}

	/**
	 * 是否顯示小背景。
	 */
	public void setShowSmallBg(boolean showSmallBg)
	{
		this.showSmallBg = showSmallBg;
	}

	/**
	 * 是否顯示移動的小圓。
	 */
	public void setShowMoveCircle(boolean showMoveCircle)
	{
		this.showMoveCircle = showMoveCircle;
	}

}

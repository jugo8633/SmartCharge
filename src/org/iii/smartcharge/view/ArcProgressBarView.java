package org.iii.smartcharge.view;

import org.iii.smartcharge.common.Device;

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

	private int		bgStrokeWidth		= 90;
	private int		barStrokeWidth		= 70;
	private int		bgColor				= Color.DKGRAY;
	private int		barColor			= Color.GREEN;
	private int		smallBgColor		= Color.WHITE;
	private int		progress			= 0;
	private int		angleOfMoveCircle	= 140;			// ���ʤp�骺�_�l���סC
	private int		startAngle			= 140;
	private int		endAngle			= 260;
	private Paint	mPaintBar			= null;
	private Paint	mPaintSmallBg		= null;
	private Paint	mPaintBg			= null;
	private Paint	mPaintCircle		= null;
	private RectF	rectBg				= null;
	private int		diameter			= 350;			// ���|
	private boolean	showSmallBg			= true;			// �O�_��ܤp�I���C
	private boolean	showMoveCircle		= false;		// �O�_��ܲ��ʪ��p��C
	private int		cx1					= 0;
	private int		cy1					= 0;
	private int		arcRadius			= 0;
	private int		mnCurrent			= 0;
	private float	mnScale				= 1;

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
		mnScale = Device.getScaleSize(context);

		diameter = (int) (300 * mnScale) - bgStrokeWidth;
		rectBg = new RectF(bgStrokeWidth, bgStrokeWidth, diameter, diameter);
		cx1 = (diameter + bgStrokeWidth) / 2;
		cy1 = (diameter + bgStrokeWidth) / 2;
		arcRadius = (diameter - bgStrokeWidth) / 2;

	}

	private void DrawBar(Canvas canvas)
	{
		// ProgressBar�����M�}�l�e2�Ӷ�A��{ProgressBar���ꨤ�C
		mPaintCircle = new Paint();
		mPaintCircle.setAntiAlias(true);
		mPaintCircle.setColor(bgColor);

		canvas.drawCircle((float) (cx1 + arcRadius * Math.cos(startAngle * 3.14 / 180)),
				(float) (cy1 + arcRadius * Math.sin(startAngle * 3.14 / 180)), bgStrokeWidth / 2, mPaintCircle);// �p��

		canvas.drawCircle((float) (cx1 + arcRadius * Math.cos((180 - startAngle) * 3.14 / 180)),
				(float) (cy1 + arcRadius * Math.sin((180 - startAngle) * 3.14 / 180)), bgStrokeWidth / 2, mPaintCircle);// �p��

		// ���έI���C
		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStyle(Style.STROKE);
		mPaintBg.setStrokeWidth(bgStrokeWidth);
		mPaintBg.setColor(bgColor);
		canvas.drawArc(rectBg, startAngle, endAngle, false, mPaintBg);

		// ���Τp�I���C
		if (showSmallBg)
		{
			mPaintSmallBg = new Paint();
			mPaintSmallBg.setAntiAlias(true);
			mPaintSmallBg.setStyle(Style.STROKE);
			mPaintSmallBg.setStrokeWidth(barStrokeWidth);
			mPaintSmallBg.setColor(smallBgColor);
			canvas.drawArc(rectBg, startAngle, endAngle, false, mPaintSmallBg);
		}

		// ����ProgressBar�C
		mPaintBar = new Paint();
		mPaintBar.setAntiAlias(true);
		mPaintBar.setStyle(Style.STROKE);
		mPaintBar.setStrokeWidth(barStrokeWidth);
		mPaintBar.setColor(barColor);

		BlurMaskFilter mBGBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.INNER);
		mPaintBar.setMaskFilter(mBGBlur);

		canvas.drawArc(rectBg, startAngle, progress, false, mPaintBar);

		// �HProgressBar���ʪ���C
		if (showMoveCircle)
		{
			mPaintCircle.setColor(barColor);
			canvas.drawCircle((float) (cx1 + arcRadius * Math.cos(angleOfMoveCircle * 3.14 / 180)),
					(float) (cy1 + arcRadius * Math.sin(angleOfMoveCircle * 3.14 / 180)), bgStrokeWidth / 2,
					mPaintCircle);// �p��
		}
	}

	/**
	 * 
	 * @param progress
	 */
	public void setAmount(int nAmount)
	{
		mnCurrent = nAmount;
		progress = 0;
		angleOfMoveCircle = 140;
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
	 * �]�m���έI�����e���e�סC
	 */
	public void setBgStrokeWidth(int bgStrokeWidth)
	{
		this.bgStrokeWidth = bgStrokeWidth;
	}

	/**
	 * �]�m����ProgressBar���e���e�סC
	 */
	public void setBarStrokeWidth(int barStrokeWidth)
	{
		this.barStrokeWidth = barStrokeWidth;
	}

	/**
	 * �]�m���έI�����C��C
	 */
	public void setBgColor(int bgColor)
	{
		this.bgColor = bgColor;
	}

	/**
	 * �]�m����ProgressBar���C��C
	 */
	public void setBarColor(int barColor)
	{
		this.barColor = barColor;
	}

	/**
	 * �]�m���Τp�I�����C��C
	 */
	public void setSmallBgColor(int smallBgColor)
	{
		this.smallBgColor = smallBgColor;
	}

	/**
	 * �]�m���Ϊ����|�C
	 */
	public void setDiameter(int diameter)
	{
		this.diameter = diameter;
	}

	/**
	 * �O�_��ܤp�I���C
	 */
	public void setShowSmallBg(boolean showSmallBg)
	{
		this.showSmallBg = showSmallBg;
	}

	/**
	 * �O�_��ܲ��ʪ��p��C
	 */
	public void setShowMoveCircle(boolean showMoveCircle)
	{
		this.showMoveCircle = showMoveCircle;
	}

}

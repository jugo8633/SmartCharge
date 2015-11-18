package org.iii.smartcharge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.module.CameraHandler;
import org.iii.smartcharge.module.PlanarYUVLuminanceSource;

import com.zbar.lib.ZbarManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class QrScanerActivity extends Activity
{
	public static final int ACTIVITY_REQUEST_CODE = 0x0003;

	private CameraHandler	cameraHandler	= null;
	private SurfaceView		surfaceView		= null;
	private SurfaceHolder	surfaceHolder	= null;
	private int				scanX			= 0;
	private int				scanY			= 0;
	private int				scanWidth		= 0;
	private int				scanHeight		= 0;
	private RelativeLayout	contain			= null;
	private boolean			mbIsCapture		= false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_scan);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceViewScanner);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback);
		cameraHandler = CameraHandler.getInstance(this);
		cameraHandler.setOnPreviewListener(previewListener);
		contain = (RelativeLayout) this.findViewById(R.id.capture_containter);

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		cameraHandler.stopPreview();
		cameraHandler.release();
	}

	private void initScanArea()
	{
		Point point = cameraHandler.getCameraResolution();

		int width = point.y;
		int height = point.x;

		RelativeLayout rlArea = (RelativeLayout) this.findViewById(R.id.capture_crop_layout);

		// int nScal = (int) Device.getScaleSize(this);

		scanX = rlArea.getLeft() * width / contain.getWidth();
		scanX -= 10;
		scanY = rlArea.getTop() * height / contain.getHeight();
		scanY -= 10;

		scanWidth = rlArea.getWidth() * width / contain.getWidth();
		scanWidth += 10;
		scanHeight = rlArea.getHeight() * height / contain.getHeight();
		scanHeight += 10;
	}

	private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback()
	{
		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			Logs.showTrace("surfaceCreated");

			try
			{
				cameraHandler.open(surfaceHolder);
				initScanArea();
				cameraHandler.setAutoFocus(true);
				cameraHandler.startPreview();
			}
			catch (IOException e)
			{
				Logs.showError("Exception: " + e.getMessage());
			}

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			Logs.showTrace("surfaceChanged");

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			Logs.showTrace("surfaceDestroyed");

		}
	};

	private CameraHandler.OnPreviewListener previewListener = new CameraHandler.OnPreviewListener()
	{
		@Override
		public void onPreview(byte[] data)
		{
			Logs.showTrace("Get Preview Data:" + data.toString());

			String strQrData = decode(data, cameraHandler.getCameraResolution().x,
					cameraHandler.getCameraResolution().y);
			Logs.showTrace("QR Data: " + strQrData);
		}
	};

	private String decode(byte[] data, int width, int height)
	{
		ZbarManager zbar = new ZbarManager();

		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;// Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;

		String result = zbar.decode(rotatedData, width, height, true, scanX, scanY, scanWidth, scanHeight);

		if (mbIsCapture)
		{
			capture(rotatedData, width, height, scanX, scanY, scanWidth, scanHeight);
		}

		if (null != result)
		{
			Logs.showTrace("QR Code Decode Success");
			returnCode(result);
		}
		else
		{
			Logs.showTrace("QR Code Decode Fail");
		}

		zbar = null;
		return result;
	}

	private void returnCode(final String strCode)
	{
		Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("barcode", strCode);
		resultIntent.putExtras(bundle);
		this.setResult(RESULT_OK, resultIntent);
		finish();
	}

	private void capture(byte[] rotatedData, int width, int height, int x, int y, int cwidth, int cheight)
	{
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(rotatedData, width, height, x, y, cwidth,
				cheight, false);
		int[] pixels = source.renderThumbnail();
		int w = source.getThumbnailWidth();
		int h = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.ARGB_8888);
		try
		{
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Qrcode/";
			File root = new File(rootPath);
			if (!root.exists())
			{
				root.mkdirs();
			}
			File f = new File(rootPath + "Qrcode.jpg");
			if (f.exists())
			{
				f.delete();
			}
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

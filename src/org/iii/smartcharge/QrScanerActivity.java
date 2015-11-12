package org.iii.smartcharge;


import java.io.IOException;
import org.iii.smartcharge.common.Logs;
import org.iii.smartcharge.module.CameraHandler;
import com.zbar.lib.ZbarManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class QrScanerActivity extends Activity
{
	public static final int	ACTIVITY_REQUEST_CODE	= 0x0003;
	private final int		SCAN_LEFT				= 0;
	private final int		SCAN_RIGHT				= 1;

	private CameraHandler	cameraHandler	= null;
	private SurfaceView		surfaceView		= null;
	private SurfaceHolder	surfaceHolder	= null;
	private int				scanX			= 0;
	private int				scanY			= 0;
	private int				scanWidth		= 0;
	private int				scanHeight		= 0;
	private RelativeLayout	contain			= null;
	private int				SCAN			= SCAN_LEFT;
	private ImageView		ivScanLeft		= null;
	private ImageView		ivScanRight		= null;
	private String			strCodeLeft		= null;
	private String			strCodeRight	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_scan);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceViewScanner);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback);
		cameraHandler = CameraHandler.getInstance(this);
		cameraHandler.setOnPreviewListener(previewListener);
		contain = (RelativeLayout) this.findViewById(R.id.relativeLayoutScanMain);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		SCAN = SCAN_LEFT;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		cameraHandler.stopPreview();
		cameraHandler.release();
	}

	private void initScanArea(boolean bLeft)
	{
		Point point = cameraHandler.getCameraResolution();
		int width = point.y;
		int height = point.x;

		ImageView ivArea = null;
		if (bLeft)
		{
			ivScanLeft = ivArea = (ImageView) this.findViewById(R.id.imageViewScanLeft);
		}
		else
		{
			ivScanRight = ivArea = (ImageView) this.findViewById(R.id.imageViewScanRight);
		}
		scanX = ivArea.getLeft() * width / contain.getWidth();
		scanY = ivArea.getTop() * height / contain.getHeight();

		scanWidth = ivArea.getWidth() * width / contain.getWidth();
		scanHeight = ivArea.getHeight() * height / contain.getHeight();
	}

	private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback()
	{
		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			Logs.showTrace("surfaceCreated");

			try
			{
				strCodeLeft = null;
				strCodeRight = null;
				SCAN = SCAN_LEFT;
				cameraHandler.open(surfaceHolder);
				initScanArea(true);
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

		if (null != result)
		{
			if (SCAN == SCAN_LEFT)
			{
				strCodeLeft = result;
				ivScanLeft.setImageResource(R.drawable.a_sight_ok);
				SCAN = SCAN_RIGHT;
				initScanArea(false);
				Logs.showTrace("Scan Left Code Finish");
			}
			else if (SCAN == SCAN_RIGHT)
			{
				strCodeRight = result;
				ivScanRight.setImageResource(R.drawable.a_sight_ok);
				cameraHandler.setAutoFocus(false);
				Logs.showTrace("Scan Right Code Finish");
				returnCode(strCodeLeft, strCodeRight);
			}

			Logs.showError("QR Code Decode Success");
		}
		else
		{
			Logs.showError("QR Code Decode Fail");
		}

		zbar = null;
		return result;
	}

	private void returnCode(final String codeLeft, final String codeRight)
	{
		Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("barcode", codeLeft);
		bundle.putString("barcode2", codeRight);
		resultIntent.putExtras(bundle);
		this.setResult(RESULT_OK, resultIntent);
		finish();
	}

}

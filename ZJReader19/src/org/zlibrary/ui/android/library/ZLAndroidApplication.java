package org.zlibrary.ui.android.library;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import org.zlibrary.core.options.ZLBooleanOption;
import org.zlibrary.core.options.ZLIntegerRangeOption;
import org.zlibrary.core.sqliteconfig.ZLSQLiteConfig;
import org.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import org.zlibrary.ui.android.image.ZLAndroidImageManager;

public class ZLAndroidApplication extends Application
{
	private static ZLAndroidApplication ourApplication;

	public final ZLBooleanOption AutoOrientationOption = new ZLBooleanOption("LookNFeel", "AutoOrientation", false);
	public final ZLBooleanOption ShowStatusBarOption = new ZLBooleanOption("LookNFeel", "ShowStatusBar", hasNoHardwareMenuButton());
	public final ZLBooleanOption ShowStatusBarWhenMenuIsActiveOption = new ZLBooleanOption("LookNFeel", "ShowStatusBarWithMenu", true);
	public final ZLIntegerRangeOption BatteryLevelToTurnScreenOffOption = new ZLIntegerRangeOption("LookNFeel", "BatteryLevelToTurnScreenOff", 0, 100, 50);
	public final ZLBooleanOption DontTurnScreenOffDuringChargingOption = new ZLBooleanOption("LookNFeel", "DontTurnScreenOffDuringCharging", true);
	public final ZLIntegerRangeOption ScreenBrightnessLevelOption = new ZLIntegerRangeOption("LookNFeel", "ScreenBrightnessLevel", 0, 100, 0);
	public final ZLBooleanOption DisableButtonLightsOption = new ZLBooleanOption("LookNFeel", "DisableButtonLights", true);

	public static ZLAndroidApplication Instance()
	{
		return ourApplication;
	}

	public ZLAndroidApplication()
	{
		ourApplication = this;
	}

	private boolean hasNoHardwareMenuButton()
	{
		return
		// Eken M001
		(Build.DISPLAY != null && Build.DISPLAY.contains("simenxie")) ||
		// PanDigital
				"PD_Novel".equals(Build.MODEL);
	}

	public ZLAndroidApplicationWindow myMainWindow;

	@Override
	public void onCreate()
	{
		super.onCreate();
		new ZLSQLiteConfig(this);
		new ZLAndroidImageManager();
		new ZLAndroidLibrary(this);
	}

	private Bitmap mBitmap = null;

	public Bitmap getBitmap()
	{
		return mBitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		Log.i("xml", "setBitmap!!!");
		Log.i("xml", "app:bitmap:" + bitmap);
		this.mBitmap = bitmap;
	}

	public void setScale(float s)
	{
		Matrix m = new Matrix();
		m.setScale(s, s);
		int h = mBitmap.getHeight();
		int w = mBitmap.getWidth();
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, w, h, m, true);

	}

	public void setRotate(float s)
	{
		Matrix m = new Matrix();
		m.setRotate(s);
		int h = mBitmap.getHeight();
		int w = mBitmap.getWidth();
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, w, h, m, true);
	}
}

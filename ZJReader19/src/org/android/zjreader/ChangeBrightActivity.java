package org.android.zjreader;

import com.rdweiba.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChangeBrightActivity extends Activity
{
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.reader_bright);
		// 亮度调节
		SeekBar seekBar = (SeekBar) findViewById(R.id.MySeekBar);
		seekBar.setProgress((int) (android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255)));
		seekBar.setOnSeekBarChangeListener(seekListener);
	}

	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener()
	{

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (fromUser)
			{
				Integer tmpInt = seekBar.getProgress();

				android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, tmpInt); // 0-255
				tmpInt = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				if (50 <= tmpInt && tmpInt <= 255)
				{
					lp.screenBrightness = tmpInt / 255f;
					System.out.println(tmpInt);
				}
				getWindow().setAttributes(lp);
				lp = getWindow().getAttributes();
				System.out.println(lp.screenBrightness);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			// TODO Auto-generated method stub
			// put awesomeness here
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			// TODO Auto-generated method stub
			// System.out.println((float) seekBar.getProgress() / 10);
			// setScreenBrightness((float) seekBar.getProgress() / 10);
		}
	};

	// private void setScreenBrightness(float b)
	// {
	// // 取得window属性保存在layoutParams中
	// WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
	// layoutParams.screenBrightness = b;// b已经除以10
	// getWindow().setAttributes(layoutParams);
	// }
}

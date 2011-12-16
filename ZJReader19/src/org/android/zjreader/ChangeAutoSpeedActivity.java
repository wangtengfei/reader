package org.android.zjreader;

import org.zjreader.zjreader.ScrollingPreferences;

import com.rdweiba.main.Const;
import com.rdweiba.main.IbookActivity;
import com.rdweiba.main.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChangeAutoSpeedActivity extends Activity
{
	Integer tmpInt;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.reader_autospeed);
		// 亮度调节
		SeekBar seekBar = (SeekBar) findViewById(R.id.MySeekBar);
		seekBar.setProgress(Const.speed);
		seekBar.setOnSeekBarChangeListener(seekListener);
	}

	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener()
	{

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (fromUser)
			{
				tmpInt = seekBar.getProgress();
				Const.speed = tmpInt;
				System.out.println(tmpInt + "tmpInt");
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
		}
	};
}

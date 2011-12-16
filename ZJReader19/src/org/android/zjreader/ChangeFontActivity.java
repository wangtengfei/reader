package org.android.zjreader;

import org.zjreader.zjreader.ChangeFontSizeAction;
import org.zjreader.zjreader.ZJReaderApp;

import com.rdweiba.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChangeFontActivity extends Activity implements OnClickListener
{
	private ImageView font_plus;
	private ImageView font_minus;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.reader_changefont);

		font_plus = (ImageView) findViewById(R.id.font_plus);
		font_minus = (ImageView) findViewById(R.id.font_minus);
		font_plus.setOnClickListener(this);
		font_minus.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.font_plus:
			new ChangeFontSizeAction(getReader(), +2).run();
			break;
		case R.id.font_minus:
			new ChangeFontSizeAction(getReader(), -2).run();
			break;
		default:
			break;
		}
	}

	protected final ZJReaderApp getReader()
	{
		return (ZJReaderApp) ZJReaderApp.Instance();
	}
}

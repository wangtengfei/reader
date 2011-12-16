package org.android.zjreader;

import org.zjreader.bookmodel.BookModel;
import org.zjreader.library.Book;
import org.zjreader.zjreader.ChangeFontSizeAction;
import org.zjreader.zjreader.ColorProfile;
import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.options.ZLColorOption;
import org.zlibrary.core.util.ZLColor;
import org.zlibrary.text.hyphenation.ZLTextHyphenator;

import com.rdweiba.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChangeBgActivity extends Activity implements OnClickListener
{

	private ImageButton gray;
	private ImageButton blue;
	private ImageButton green;
	private ImageButton yellow;
	private ImageButton pink;
	private ImageButton mode;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.reader_changebg);
		gray = (ImageButton) findViewById(R.id.gray_bg_btn);
		green = (ImageButton) findViewById(R.id.green_bg_btn);
		yellow = (ImageButton) findViewById(R.id.yellow_bg_btn);
		blue = (ImageButton) findViewById(R.id.blue_bg_btn);
		pink = (ImageButton) findViewById(R.id.pink_bg_btn);
		mode = (ImageButton) findViewById(R.id.mode_bg_btn);
		gray.setOnClickListener(this);
		green.setOnClickListener(this);
		blue.setOnClickListener(this);
		yellow.setOnClickListener(this);
		pink.setOnClickListener(this);
		mode.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.gray_bg_btn:
			changeBgColor(200, 200, 200);
			break;
		case R.id.yellow_bg_btn:
			changeBgColor(239, 235, 210);
			break;
		case R.id.blue_bg_btn:
			changeBgColor(130, 200, 250);
			break;
		case R.id.green_bg_btn:
			changeBgColor(182, 205, 101);
			break;
		case R.id.pink_bg_btn:
			changeBgColor(254, 181, 181);
			break;
		case R.id.mode_bg_btn:
			changeMode();
		default:
			break;
		}
	}

	private void changeMode()
	{
		// TODO Auto-generated method stub
		if (getReader().getColorProfileName().equals(ColorProfile.DAY))
		{
			// changeBgColor(0, 0, 0);
			getReader().setColorProfileName(ColorProfile.NIGHT);
			mode.setBackgroundResource(R.drawable.white_bg_area);
		} else
		{
			// changeBgColor(239, 235, 210);
			getReader().setColorProfileName(ColorProfile.DAY);
			mode.setBackgroundResource(R.drawable.night_mode_area);
		}
		repaint();
	}

	private void changeBgColor(int r, int g, int b)
	{
		// getReader().setColorProfileName(ColorProfile.DAY);
		if (getReader().getColorProfileName().equals(ColorProfile.DAY))
		{
			getReader().setBackgroundColor(r, g, b);
			repaint();
		}
	}

	private void repaint()
	{
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		final BookModel model = fbreader.Model;
		if (model != null)
		{
			final Book book = model.Book;
			if (book != null)
			{
				book.reloadInfoFromDatabase();
				ZLTextHyphenator.Instance().load(book.getLanguage());
			}
		}
		fbreader.clearTextCaches();
		fbreader.getViewWidget().repaint();
	}

	protected final ZJReaderApp getReader()
	{
		return (ZJReaderApp) ZJReaderApp.Instance();
	}
}

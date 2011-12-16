package org.android.zjreader;

import java.text.DecimalFormat;

import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.text.view.ZLTextView;
import org.zlibrary.text.view.ZLTextWordCursor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rdweiba.main.R;

public class NavigateActivity extends Activity
{
	private volatile boolean myIsInProgress = false;
	public ZLTextWordCursor StartPosition;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.navigate);
		initPosition();
		setupNavigation();
		SeekBar slider = (SeekBar) findViewById(R.id.book_position_slider);
		final TextView text = (TextView) findViewById(R.id.book_position_text);

		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			private void gotoPage(int page)
			{
				final ZLTextView view = getReader().getTextView();
				if (page == 1)
				{
					view.gotoHome();
				} else
				{
					view.gotoPage(page);
				}
				getReader().getViewWidget().reset();
				getReader().getViewWidget().repaint();
			}

			public void onStopTrackingTouch(SeekBar seekBar)
			{
				myIsInProgress = false;
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
				myIsInProgress = true;
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (fromUser)
				{
					final int page = progress + 1;
					final int pagesNumber = seekBar.getMax() + 1;
					text.setText(makeProgressText(page, pagesNumber));
					gotoPage(page);
				}
			}
		});
	}

	protected final ZJReaderApp getReader()
	{
		return (ZJReaderApp) ZJReaderApp.Instance();
	}

	public final void initPosition()
	{
		if (StartPosition == null)
		{
			StartPosition = new ZLTextWordCursor(getReader().getTextView().getStartCursor());
		}
	}

	private void setupNavigation()
	{
		final SeekBar slider = (SeekBar) findViewById(R.id.book_position_slider);
		final TextView text = (TextView) findViewById(R.id.book_position_text);

		final ZLTextView textView = getReader().getTextView();
		final int page = textView.computeCurrentPage();
		final int pagesNumber = textView.computePageNumber();

		if (slider.getMax() != pagesNumber - 1 || slider.getProgress() != page - 1)
		{
			slider.setMax(pagesNumber - 1);
			slider.setProgress(page - 1);
			text.setText(makeProgressText(page, pagesNumber));
		}
	}

	private static String makeProgressText(int page, int pagesNumber)
	{
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		String progress = df.format(page * 100.00 / pagesNumber) + "%";
		return "位于全书的" + progress + "处";
	}

}

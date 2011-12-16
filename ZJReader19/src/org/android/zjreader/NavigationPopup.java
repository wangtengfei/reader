/*
 * Copyright (C) 2009-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.android.zjreader;

import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.resources.ZLResource;
import org.zlibrary.text.view.ZLTextView;
import org.zlibrary.text.view.ZLTextWordCursor;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rdweiba.main.R;

final class NavigationPopup extends PopupPanel {
	final static String ID = "NavigationPopup";

	private volatile boolean myIsInProgress;

	NavigationPopup(ZJReaderApp fbReader) {
		super(fbReader);
	}

	public void runNavigation() {
		if (myWindow.getVisibility() == View.GONE) {
			myIsInProgress = false;
			initPosition();
			Application.showPopup(ID);
		}
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void show_() {
		super.show_();
		if (myWindow != null) {
			setupNavigation(myWindow);
		}
	}

	@Override
	protected void update() {
		if (!myIsInProgress && myWindow != null) {
			setupNavigation(myWindow);
		}
	}

	@Override
	public void createControlPanel(ZJReader activity, RelativeLayout root, PopupWindow.Location location) {
		if (myWindow != null) {
			return;
		}

		myWindow = new PopupWindow(activity, root, location, true);

		final View layout = activity.getLayoutInflater().inflate(R.layout.navigate, myWindow, false);

		final SeekBar slider = (SeekBar)layout.findViewById(R.id.book_position_slider);
		final TextView text = (TextView)layout.findViewById(R.id.book_position_text);

		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			private void gotoPage(int page) {
				final ZLTextView view = getReader().getTextView();
				if (page == 1) {
					view.gotoHome();
				} else {
					view.gotoPage(page);
				}
				getReader().getViewWidget().reset();
				getReader().getViewWidget().repaint();
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				myIsInProgress = false;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				myIsInProgress = true;
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					final int page = progress + 1;
					final int pagesNumber = seekBar.getMax() + 1;
					text.setText(makeProgressText(page, pagesNumber));
					gotoPage(page);
				}
			}
		});
		myWindow.addView(layout);
	}

	private void setupNavigation(PopupWindow panel) {
		final SeekBar slider = (SeekBar)panel.findViewById(R.id.book_position_slider);
		final TextView text = (TextView)panel.findViewById(R.id.book_position_text);

		final ZLTextView textView = getReader().getTextView();
		final int page = textView.computeCurrentPage();
		final int pagesNumber = textView.computePageNumber();

		if (slider.getMax() != pagesNumber - 1 || slider.getProgress() != page - 1) {
			slider.setMax(pagesNumber - 1);
			slider.setProgress(page - 1);
			text.setText(makeProgressText(page, pagesNumber));
		}
	}

	private static String makeProgressText(int page, int pagesNumber) {
		return page + " / " + pagesNumber;
	}
}

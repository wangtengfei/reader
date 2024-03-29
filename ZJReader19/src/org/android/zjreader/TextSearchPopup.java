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

import org.zjreader.zjreader.ActionCode;
import org.zjreader.zjreader.ZJReaderApp;

import android.widget.RelativeLayout;

import com.rdweiba.main.R;

final class TextSearchPopup extends ButtonsPopupPanel {
	final static String ID = "TextSearchPopup";

	TextSearchPopup(ZJReaderApp fbReader) {
		super(fbReader);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void hide_() {
		getReader().getTextView().clearFindResults();
		super.hide_();
	}

	@Override
	public void createControlPanel(ZJReader activity, RelativeLayout root, PopupWindow.Location location) {
		if (myWindow != null) {
			return;
		}

		myWindow = new PopupWindow(activity, root, location, false);

		addButton(ActionCode.FIND_PREVIOUS, false, R.drawable.text_search_previous);
		addButton(ActionCode.CLEAR_FIND_RESULTS, true, R.drawable.text_search_close);
		addButton(ActionCode.FIND_NEXT, false, R.drawable.text_search_next);
	}
}

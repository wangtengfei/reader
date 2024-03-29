/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
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

import android.app.Application;
import android.text.ClipboardManager;

import org.android.util.UIUtil;


import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.resources.ZLResource;
import org.zlibrary.ui.android.library.ZLAndroidApplication;


public class SelectionCopyAction extends FBAndroidAction {
	SelectionCopyAction(ZJReader baseActivity, ZJReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	public void run() {
		final String text = Reader.getTextView().getSelectedText();
		Reader.getTextView().clearSelection();

		final ClipboardManager clipboard =
			(ClipboardManager)ZLAndroidApplication.Instance().getSystemService(Application.CLIPBOARD_SERVICE);
		clipboard.setText(text);
		UIUtil.showMessageText(
			BaseActivity,
			ZLResource.resource("selection").getResource("textInBuffer").getValue().replace("%s", clipboard.getText())
		);
	}
}

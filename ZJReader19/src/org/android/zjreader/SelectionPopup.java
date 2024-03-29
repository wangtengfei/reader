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

import org.zjreader.zjreader.ActionCode;
import org.zjreader.zjreader.ZJReaderApp;

import android.view.View;
import android.widget.RelativeLayout;

import com.rdweiba.main.R;



class SelectionPopup extends ButtonsPopupPanel {
	final static String ID = "SelectionPopup";

	SelectionPopup(ZJReaderApp fbReader) {
		super(fbReader);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void createControlPanel(ZJReader activity, RelativeLayout root, PopupWindow.Location location) {
		if (myWindow != null) {
			return;
		}

		myWindow = new PopupWindow(activity, root, location, false);

//        addButton(ActionCode.SELECTION_COPY_TO_CLIPBOARD, true, R.drawable.selection_copy);
//        addButton(ActionCode.SELECTION_SHARE, true, R.drawable.selection_share);
//       // addButton(ActionCode.SELECTION_TRANSLATE, true, R.drawable.selection_translate);
//        addButton(ActionCode.SELECTION_BOOKMARK, true, R.drawable.selection_bookmark);
//        addButton(ActionCode.SELECTION_CLEAR, true, R.drawable.selection_close);
    }
    
    public void move(int selectionStartY, int selectionEndY) {
		if (myWindow == null) {
			return;
		}

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
		);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition; 
        final int screenHeight = ((View)myWindow.getParent()).getHeight();
		final int diffTop = screenHeight - selectionEndY;
		final int diffBottom = selectionStartY;
		if (diffTop > diffBottom) {
			verticalPosition = diffTop > myWindow.getHeight() + 20
				? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
		} else {
			verticalPosition = diffBottom > myWindow.getHeight() + 20
				? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
		}

        layoutParams.addRule(verticalPosition);
        myWindow.setLayoutParams(layoutParams);
    }
}

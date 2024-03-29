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

package org.zlibrary.ui.android.view;

import com.rdweiba.main.Const;

import android.graphics.*;

class SlideAnimationProvider extends SimpleAnimationProvider {
	private final Paint myPaint = new Paint();

	SlideAnimationProvider(BitmapManager bitmapManager) {
		super(bitmapManager);
	}

	@Override
	protected void drawInternal(Canvas canvas) {
		canvas.drawBitmap(getBitmapTo(), 0, Const.TITLE_HEIGHT, myPaint);
		myPaint.setColor(Color.rgb(127, 127, 127));
		if (myDirection.IsHorizontal) {
			final int dX = myEndX - myStartX;
			canvas.drawBitmap(getBitmapFrom(), dX, Const.TITLE_HEIGHT, myPaint);
			if (dX > 0 && dX < myWidth) {
				canvas.drawLine(dX, 0, dX, myHeight + 1, myPaint);
			} else if (dX < 0 && dX > -myWidth) {
				canvas.drawLine(dX + myWidth, 0, dX + myWidth, myHeight + 1, myPaint);
			}
		} else {
			final int dY = myEndY - myStartY;
			canvas.drawBitmap(getBitmapFrom(), 0, dY, myPaint);
			if (dY > 0 && dY < myHeight) {
				canvas.drawLine(0, dY, myWidth + 1, dY, myPaint);
			} else if (dY < 0 && dY > -myHeight) {
				canvas.drawLine(0, dY + myHeight, myWidth + 1, dY + myHeight, myPaint);
			}
		}
	}
}

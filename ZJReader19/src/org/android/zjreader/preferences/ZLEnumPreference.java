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

package org.android.zjreader.preferences;

import android.content.Context;

import org.zlibrary.core.options.ZLEnumOption;
import org.zlibrary.core.resources.ZLResource;

class ZLEnumPreference<T extends Enum<T>> extends ZLStringListPreference {
	private final ZLEnumOption<T> myOption;

	ZLEnumPreference(Context context, ZLEnumOption<T> option, ZLResource resource, String resourceKey) {
		super(context, resource, resourceKey);
		myOption = option;

		final T initialValue = option.getValue();
		final T[] allValues = initialValue.getDeclaringClass().getEnumConstants();
		final String[] stringValues = new String[allValues.length];
		for (int i = 0; i < stringValues.length; ++i) {
			stringValues[i] = allValues[i].toString();
		}
		setList(stringValues);
		setInitialValue(initialValue.toString());
	}

	public void onAccept() {
		myOption.setValue(Enum.valueOf(myOption.getValue().getDeclaringClass(), getValue()));
	}
}

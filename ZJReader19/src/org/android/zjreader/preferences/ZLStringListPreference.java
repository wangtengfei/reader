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
import android.preference.ListPreference;

import org.zlibrary.core.resources.ZLResource;

abstract class ZLStringListPreference extends ListPreference implements ZLPreference {
	private final ZLResource myResource;

	ZLStringListPreference(Context context, ZLResource rootResource, String resourceKey) {
		super(context);

		myResource = rootResource.getResource(resourceKey);
		setTitle(myResource.getValue());
	}

	protected final void setList(String[] values) {
		String[] texts = new String[values.length];
		for (int i = 0; i < values.length; ++i) {
			final ZLResource resource = myResource.getResource(values[i]);
			texts[i] = resource.hasValue() ? resource.getValue() : values[i];
		}
		setLists(values, texts);
	}

	protected final void setLists(String[] values, String[] texts) {
		assert(values.length == texts.length);
		setEntries(texts);
		setEntryValues(values);
	}

	protected final boolean setInitialValue(String value) {
		if (value == null) {
			return false;
		}
		// throws NPE in some cases (?)
		//final int index = findIndexOfValue(value);
		int index = -1;
		final CharSequence[] entryValues = getEntryValues();
		for (int i = 0; i < entryValues.length; ++i) {
			if (value.equals(entryValues[i])) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			setValueIndex(index);
			setSummary(getEntry());
			return true;
		}
		return false;
	}

	@Override
	protected void onDialogClosed(boolean result) {
		super.onDialogClosed(result);
		if (result) {
			setSummary(getEntry());
		}
	}
}

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

import java.util.List;

import android.content.Context;

import org.android.zjreader.DictionaryUtil;
import org.android.zjreader.PackageInfo;
import org.zlibrary.core.options.ZLStringOption;
import org.zlibrary.core.resources.ZLResource;


class DictionaryPreference extends ZLStringListPreference {
	private final ZLStringOption myOption;

	DictionaryPreference(Context context, ZLResource resource, String resourceKey, ZLStringOption dictionaryOption) {
		super(context, resource, resourceKey);

		myOption = dictionaryOption;
		final List<PackageInfo> infos = DictionaryUtil.dictionaryInfos(context);
		
		final String[] values = new String[infos.size()];
		final String[] texts = new String[infos.size()];
		int index = 0;
		for (PackageInfo i : infos) {
			values[index] = i.Id;
			texts[index] = i.Title;
			++index;
		}
		setLists(values, texts);

		setInitialValue(myOption.getValue());
	}

	public void onAccept() {
		myOption.setValue(getValue());
	}
}

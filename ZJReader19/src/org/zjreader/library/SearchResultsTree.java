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

package org.zjreader.library;

class SearchResultsTree extends FirstLevelTree {
	private final String myPattern;

	SearchResultsTree(RootTree root, String id, String pattern) {
		super(root, 0, id);
		myPattern = pattern != null ? pattern : "";
	}

	final String getPattern() {
		return myPattern;
	}

	@Override
	protected String getSummary() {
		return super.getSummary().replace("%s", myPattern);
	}
}

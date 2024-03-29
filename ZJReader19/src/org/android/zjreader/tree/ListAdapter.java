/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
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

package org.android.zjreader.tree;

import java.util.*;

import android.widget.BaseAdapter;

import org.zjreader.tree.FBTree;

public abstract class ListAdapter extends BaseAdapter {
	private final BaseActivity myActivity;
	private final List<FBTree> myItems;

	protected ListAdapter(BaseActivity activity) {
		myActivity = activity;
		myItems = Collections.synchronizedList(new ArrayList<FBTree>());
		activity.setListAdapter(this);
	}

	protected BaseActivity getActivity() {
		return myActivity;
	}

	public void remove(final FBTree item) {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				myItems.remove(item);
				notifyDataSetChanged();
			}
		});
	}

	public void add(final FBTree item) {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				myItems.add(item);
				notifyDataSetChanged();
			}
		});
	}

	public void add(final int index, final FBTree item) {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				myItems.add(index, item);
				notifyDataSetChanged();
			}
		});
	}

	public void replaceAll(final Collection<FBTree> items) {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (!myItems.equals(items)) {
					synchronized (myItems) {
						myItems.clear();
						myItems.addAll(items);
					}
					notifyDataSetChanged();
				}
			}
		});
	}

	public int getCount() {
		return myItems.size();
	}

	public FBTree getItem(int position) {
		return myItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getIndex(FBTree item) {
		return myItems.indexOf(item);
	}

	public FBTree getFirstSelectedItem() {
		synchronized (myItems) {
			for (FBTree t : myItems) {
				if (myActivity.isTreeSelected(t)) {
					return t;
				}
			}
		}
		return null;
	}
}

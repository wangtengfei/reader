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

import java.util.*;

import org.zlibrary.core.filesystem.*;
import org.zlibrary.core.util.ZLMiscUtil;

public final class FileInfoSet {
	private static final class Pair {
		private final String myName;
		private final FileInfo myParent;

		Pair(String name, FileInfo parent) {
			myName = name;
			myParent = parent;
		}

		@Override
		public int hashCode() {
			return (myParent == null) ? myName.hashCode() : myParent.hashCode() + myName.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Pair)) {
				return false;
			}
			Pair p = (Pair)o;
			return (myName.equals(p.myName)) && ZLMiscUtil.equals(myParent, p.myParent);
		}
	}

	private final HashMap<ZLFile,FileInfo> myInfosByFile = new HashMap<ZLFile,FileInfo>();
	private final HashMap<FileInfo,ZLFile> myFilesByInfo = new HashMap<FileInfo,ZLFile>();
	private final HashMap<Pair,FileInfo> myInfosByPair = new HashMap<Pair,FileInfo>();
	private final HashMap<Long,FileInfo> myInfosById = new HashMap<Long,FileInfo>();

	private final LinkedHashSet<FileInfo> myInfosToSave = new LinkedHashSet<FileInfo>();
	private final LinkedHashSet<FileInfo> myInfosToRemove = new LinkedHashSet<FileInfo>();

	public FileInfoSet() {
		load(BooksDatabase.Instance().loadFileInfos());
	}

	public FileInfoSet(ZLFile file) {
		load(BooksDatabase.Instance().loadFileInfos(file));
	}

	FileInfoSet(long fileId) {
		load(BooksDatabase.Instance().loadFileInfos(fileId));
	}

	private void load(Collection<FileInfo> infos) {
		for (FileInfo info : infos) {
			myInfosByPair.put(new Pair(info.Name, info.Parent), info);
			myInfosById.put(info.Id, info);
		}
	}

	public void save() {
		final BooksDatabase database = BooksDatabase.Instance();
		database.executeAsATransaction(new Runnable() {
			public void run() {
				for (FileInfo info : myInfosToRemove) {
					database.removeFileInfo(info.Id);
					myInfosByPair.remove(new Pair(info.Name, info.Parent));
				}
				myInfosToRemove.clear();
				for (FileInfo info : myInfosToSave) {
					database.saveFileInfo(info);
				}
				myInfosToSave.clear();
			}
		});
	}

	public boolean check(ZLPhysicalFile file, boolean processChildren) {
		if (file == null) {
			return true;
		}
		final long fileSize = file.size();
		FileInfo info = get(file);
		if (info.FileSize == fileSize) {
			return true;
		} else {
			info.FileSize = fileSize;
			if (processChildren && !"epub".equals(file.getExtension())) {
				removeChildren(info);
				myInfosToSave.add(info);
				addChildren(file);
			} else {
				myInfosToSave.add(info);
			}
			return false;
		}
	}

	public List<ZLFile> archiveEntries(ZLFile file) {
		final FileInfo info = get(file);
		if (!info.hasChildren()) {
			return Collections.emptyList();
		}
		final LinkedList<ZLFile> entries = new LinkedList<ZLFile>();
		for (FileInfo child : info.subTrees()) {
			if (!myInfosToRemove.contains(child)) {
				entries.add(ZLArchiveEntryFile.createArchiveEntryFile(file, child.Name));
			}
		}
		return entries;
	}

	private FileInfo get(String name, FileInfo parent) {
		final Pair pair = new Pair(name, parent);
		FileInfo info = myInfosByPair.get(pair);
		if (info == null) {
			info = new FileInfo(name, parent);
			myInfosByPair.put(pair, info);
			myInfosToSave.add(info);
		}
		return info;
	}


	private FileInfo get(ZLFile file) {
		if (file == null) {
			return null;
		}
		FileInfo info = myInfosByFile.get(file);
		if (info == null) {
			info = get(file.getLongName(), get(file.getParent()));
			myInfosByFile.put(file, info);
		}
		return info;
	}

	public long getId(ZLFile file) {
		final FileInfo info = get(file);
		if (info == null) {
			return -1;
		}
		if (info.Id == -1) {
			save();
		}
		return info.Id;
	}	

	private ZLFile getFile(FileInfo info) {
		if (info == null) {
			return null;
		}
		ZLFile file = myFilesByInfo.get(info);
		if (file == null) {
			file = ZLFile.createFile(getFile(info.Parent), info.Name);
			myFilesByInfo.put(info, file);
		}
		return file;
	}

	public ZLFile getFile(long id) {
		return getFile(myInfosById.get(id));
	}

	private void removeChildren(FileInfo info) {
		for (FileInfo child : info.subTrees()) {
			if (myInfosToSave.contains(child)) {
				myInfosToSave.remove(child);
			} else {
				myInfosToRemove.add(child);
			}
			removeChildren(child);
		}
	}

	private void addChildren(ZLFile file) {
		for (ZLFile child : file.children()) {
			final FileInfo info = get(child);
			if (myInfosToRemove.contains(info)) {
				myInfosToRemove.remove(info);
			} else {
				myInfosToSave.add(info);
			}
			addChildren(child);
		}
	}
}

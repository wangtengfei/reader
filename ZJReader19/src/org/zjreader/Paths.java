

package org.zjreader;

import org.zlibrary.core.options.ZLStringOption;

import android.os.Environment;

public abstract class Paths {
	public static String cardDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static ZLStringOption BooksDirectoryOption() {
		return new ZLStringOption("Files", "BooksDirectory", cardDirectory() + "/Books");
	}

	public static ZLStringOption FontsDirectoryOption() {
		return new ZLStringOption("Files", "FontsDirectory", cardDirectory() + "/Fonts");
	}

	public static ZLStringOption WallpapersDirectoryOption() {
		return new ZLStringOption("Files", "WallpapersDirectory", cardDirectory() + "/Wallpapers");
	}

	public static String cacheDirectory() {
		return BooksDirectoryOption().getValue() + "/.FBReader";
	}

	public static String networkCacheDirectory() {
		return cacheDirectory() + "/cache";
	}
}

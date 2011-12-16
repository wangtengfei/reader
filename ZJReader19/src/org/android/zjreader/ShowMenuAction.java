

package org.android.zjreader;

import org.zjreader.zjreader.ZJReaderApp;

class ShowMenuAction extends FBAndroidAction {
	ShowMenuAction(ZJReader baseActivity, ZJReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	public void run() {
		BaseActivity.openOptionsMenu();
	}
}

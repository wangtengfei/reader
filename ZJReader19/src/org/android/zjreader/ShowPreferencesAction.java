

package org.android.zjreader;

import android.content.Intent;

import org.android.zjreader.preferences.PreferenceActivity;
import org.zjreader.zjreader.ZJReaderApp;


class ShowPreferencesAction extends FBAndroidAction {
	ShowPreferencesAction(ZJReader baseActivity, ZJReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	public void run() {
		BaseActivity.startActivityForResult(
			new Intent(BaseActivity.getApplicationContext(), PreferenceActivity.class),
			ZJReader.REPAINT_CODE
		);
	}
}

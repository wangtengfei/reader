

package org.android.zjreader;

import android.content.Intent;

import org.zjreader.zjreader.ZJReaderApp;

abstract class RunActivityAction extends FBAndroidAction {
	private final Class<?> myActivityClass;

	RunActivityAction(ZJReader baseActivity, ZJReaderApp fbreader, Class<?> activityClass) {
		super(baseActivity, fbreader);
		myActivityClass = activityClass;
	}

	public void run() {
		BaseActivity.startActivity(new Intent(BaseActivity.getApplicationContext(), myActivityClass));
	}
}

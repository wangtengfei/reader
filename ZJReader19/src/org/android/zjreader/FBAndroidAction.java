

package org.android.zjreader;

import org.zjreader.zjreader.FBAction;
import org.zjreader.zjreader.ZJReaderApp;

abstract class FBAndroidAction extends FBAction {
	protected final ZJReader BaseActivity;

	FBAndroidAction(ZJReader baseActivity, ZJReaderApp fbreader) {
		super(fbreader);
		BaseActivity = baseActivity;
	}
}

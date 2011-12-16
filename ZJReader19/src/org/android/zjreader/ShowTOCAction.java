

package org.android.zjreader;

import org.zjreader.zjreader.ZJReaderApp;

class ShowTOCAction extends RunActivityAction {
	ShowTOCAction(ZJReader baseActivity, ZJReaderApp fbreader) {
		super(baseActivity, fbreader, TOCActivity.class);
	}

	public boolean isVisible() {
		return (Reader.Model != null) && Reader.Model.TOCTree.hasChildren();
	}
}

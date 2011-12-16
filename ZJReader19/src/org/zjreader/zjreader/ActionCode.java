

package org.zjreader.zjreader;

public interface ActionCode {
	String SHOW_LIBRARY = "library";
	String SHOW_PREFERENCES = "preferences";
	String SHOW_BOOK_INFO = "bookInfo";
	String SHOW_TOC = "toc";
	String SHOW_BOOKMARKS = "bookmarks";
	String SHOW_NETWORK_LIBRARY = "networkLibrary";

	String SWITCH_TO_NIGHT_PROFILE = "night";
	String SWITCH_TO_DAY_PROFILE = "day";

	String SEARCH = "search";
	String FIND_PREVIOUS = "findPrevious";
	String FIND_NEXT = "findNext";
	String CLEAR_FIND_RESULTS = "clearFindResults";

	String SET_TEXT_VIEW_MODE_VISIT_HYPERLINKS = "hyperlinksOnlyMode";
	String SET_TEXT_VIEW_MODE_VISIT_ALL_WORDS = "dictionaryMode";

	String TURN_PAGE_BACK = "previousPage";
	String TURN_PAGE_FORWARD = "nextPage";

	String VOLUME_KEY_SCROLL_FORWARD = "volumeKeyScrollForward";
	String VOLUME_KEY_SCROLL_BACK = "volumeKeyScrollBackward";
	String SHOW_MENU = "menu";
	String SHOW_NAVIGATION = "navigate";

	String GO_BACK = "goBack";
	String EXIT = "exit";
	String SHOW_CANCEL_MENU = "cancelMenu";

	String ROTATE = "rotate";
	String INCREASE_FONT = "increaseFont";
	String DECREASE_FONT = "decreaseFont";

	String PROCESS_HYPERLINK = "processHyperlink";

	String SELECTION_SHOW_PANEL = "selectionShowPanel";
	String SELECTION_HIDE_PANEL = "selectionHidePanel";
	String SELECTION_CLEAR = "selectionClear";
	String SELECTION_COPY_TO_CLIPBOARD = "selectionCopyToClipboard";
	String SELECTION_SHARE = "selectionShare";
	String SELECTION_TRANSLATE = "selectionTranslate";
	String SELECTION_BOOKMARK = "selectionBookmark";
}

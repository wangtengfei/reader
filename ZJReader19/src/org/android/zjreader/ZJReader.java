package org.android.zjreader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.android.util.UIUtil;
import org.android.zjreader.api.PluginApi;
import org.android.zjreader.library.KillerCallback;
import org.android.zjreader.library.SQLiteBooksDatabase;
import org.android.zjreader.preferences.PreferenceActivity;
import org.zjreader.bookmodel.BookModel;
import org.zjreader.library.Book;
import org.zjreader.zjreader.ActionCode;
import org.zjreader.zjreader.ScrollingPreferences;
import org.zjreader.zjreader.TurnPageAction;
import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.filesystem.ZLFile;
import org.zlibrary.core.view.ZLView;
import org.zlibrary.text.hyphenation.ZLTextHyphenator;
import org.zlibrary.text.view.ZLTextView;
import org.zlibrary.ui.android.library.ZLAndroidActivity;
import org.zlibrary.ui.android.library.ZLAndroidApplication;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.rdweiba.main.Const;
import com.rdweiba.main.IbookActivity;
import com.rdweiba.main.R;

public final class ZJReader extends ZLAndroidActivity implements OnClickListener
{

	private ImageButton book_cate;
	private ImageButton back;
	private ImageButton gotopage;
	private TableLayout menu_layout;
	private RelativeLayout title_bar;
	private LinearLayout menu_action_bookmark;
	private LinearLayout menu_action_changefont;
	private LinearLayout menu_action_autoread;
	private LinearLayout menu_action_search;
	private LinearLayout menu_action_changebright;
	private LinearLayout menu_action_changebg;
	private LinearLayout menu_action_rotate;
	private LinearLayout menu_action_setting;
	private TextView auto_read_text;
	private LinearLayout click_area;
	private Boolean isAutoRead = false;
	Timer autorread;

	public static final String BOOK_PATH_KEY = "BookPath";
	final static int REPAINT_CODE = 1;
	final static int CANCEL_CODE = 2;
	final static int AUTOREAD_CODE = 3;
	private int myFullScreenFlag;
	private static final String PLUGIN_ACTION_PREFIX = "___";
	private final List<PluginApi.ActionInfo> myPluginActions = new LinkedList<PluginApi.ActionInfo>();

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// 如果是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent aintent = new Intent(ZJReader.this, IbookActivity.class);
			this.setResult(ZJReader.RESULT_OK, aintent);
			this.finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			toggleMenu();
			return true;
		} else
		{
			return false;
		}
	}

	private void toggleMenu()
	{
		if (menu_layout.getVisibility() == View.GONE)
		{
			menu_layout.setVisibility(View.VISIBLE);
			title_bar.setVisibility(View.VISIBLE);
		} else
		{
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
		}
	}

	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(true).<PluginApi.ActionInfo> getParcelableArrayList(PluginApi.PluginInfo.KEY);
			if (actions != null)
			{
				synchronized (myPluginActions)
				{
					final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
					int index = 0;
					while (index < myPluginActions.size())
					{
						fbReader.removeAction(PLUGIN_ACTION_PREFIX + index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions)
					{
						fbReader.addAction(PLUGIN_ACTION_PREFIX + index++, new RunPluginAction(ZJReader.this, fbReader, info.getId()));
					}
				}
			}
		}
	};

	@Override
	protected ZLFile fileFromIntent(Intent intent)
	{
		String filePath = intent.getStringExtra(BOOK_PATH_KEY);
		if (filePath == null)
		{
			final Uri data = intent.getData();
			if (data != null)
			{
				filePath = data.getPath();
			}
		}
		return filePath != null ? ZLFile.createFileByPath(filePath) : null;
	}

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		myFullScreenFlag = application.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, myFullScreenFlag);

		final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
		if (fbReader.getPopupById(TextSearchPopup.ID) == null)
		{
			new TextSearchPopup(fbReader);
		}
		if (fbReader.getPopupById(NavigationPopup.ID) == null)
		{
			new NavigationPopup(fbReader);
		}
		if (fbReader.getPopupById(SelectionPopup.ID) == null)
		{
			new SelectionPopup(fbReader);
		}
		fbReader.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, fbReader));
		fbReader.addAction(ActionCode.SEARCH, new SearchAction(this, fbReader));
		fbReader.addAction(ActionCode.PROCESS_HYPERLINK, new ProcessHyperlinkAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, fbReader));
		book_cate = (ImageButton) findViewById(R.id.book_cate);
		back = (ImageButton) findViewById(R.id.back);
		gotopage = (ImageButton) findViewById(R.id.gotopage);
		book_cate.setOnClickListener(this);
		back.setOnClickListener(this);
		gotopage.setOnClickListener(this);
		menu_layout = (TableLayout) findViewById(R.id.menu_layout);
		title_bar = (RelativeLayout) findViewById(R.id.title_bar);
		auto_read_text = (TextView) findViewById(R.id.auto_read_text);
		menu_action_bookmark = (LinearLayout) findViewById(R.id.menu_action_bookmark);
		menu_action_changefont = (LinearLayout) findViewById(R.id.menu_action_changefont);
		menu_action_autoread = (LinearLayout) findViewById(R.id.menu_action_autoread);
		menu_action_search = (LinearLayout) findViewById(R.id.menu_action_search);
		menu_action_changebright = (LinearLayout) findViewById(R.id.menu_action_changebright);
		menu_action_changebg = (LinearLayout) findViewById(R.id.menu_action_changebg);
		menu_action_rotate = (LinearLayout) findViewById(R.id.menu_action_rotate);
		menu_action_setting = (LinearLayout) findViewById(R.id.menu_action_setting);
		click_area = (LinearLayout) findViewById(R.id.click_area);
		menu_action_bookmark.setOnClickListener(this);
		menu_action_changefont.setOnClickListener(this);
		menu_action_autoread.setOnClickListener(this);
		menu_action_search.setOnClickListener(this);
		menu_action_changebright.setOnClickListener(this);
		menu_action_changebg.setOnClickListener(this);
		menu_action_rotate.setOnClickListener(this);
		menu_action_setting.setOnClickListener(this);
		click_area.setOnClickListener(this);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		if (!application.ShowStatusBarOption.getValue() && application.ShowStatusBarWhenMenuIsActiveOption.getValue())
		{
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		super.onOptionsMenuClosed(menu);
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		if (!application.ShowStatusBarOption.getValue() && application.ShowStatusBarWhenMenuIsActiveOption.getValue())
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0)
		{
			super.onNewIntent(intent);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable()
			{
				public void run()
				{
					final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
					final TextSearchPopup popup = (TextSearchPopup) fbReader.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					fbReader.TextSearchPatternOption.setValue(pattern);
					if (fbReader.getTextView().search(pattern, true, false, false, false) != 0)
					{
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								fbReader.showPopup(popup.getId());
							}
						});
					} else
					{
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								UIUtil.showErrorMessage(ZJReader.this, "textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else
		{
			super.onNewIntent(intent);
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();

		final int fullScreenFlag = application.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (fullScreenFlag != myFullScreenFlag)
		{
			finish();
			startActivity(new Intent(this, getClass()));
		}

		final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
		final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_view);
		((PopupPanel) fbReader.getPopupById(TextSearchPopup.ID)).createControlPanel(this, root, PopupWindow.Location.Bottom);
		((PopupPanel) fbReader.getPopupById(NavigationPopup.ID)).createControlPanel(this, root, PopupWindow.Location.Bottom);
		((PopupPanel) fbReader.getPopupById(SelectionPopup.ID)).createControlPanel(this, root, PopupWindow.Location.Floating);

		synchronized (myPluginActions)
		{
			myPluginActions.clear();
		}

		sendOrderedBroadcast(new Intent(PluginApi.ACTION_REGISTER), null, myPluginInfoReceiver, null, RESULT_OK, null, null);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		try
		{
			sendBroadcast(new Intent(getApplicationContext(), KillerCallback.class));
		} catch (Throwable t)
		{
		}
		PopupPanel.restoreVisibilities(ZJReaderApp.Instance());
	}

	@Override
	public void onStop()
	{
		PopupPanel.removeAllWindows(ZJReaderApp.Instance());
		super.onStop();
	}

	@Override
	protected ZJReaderApp createApplication(ZLFile file)
	{
		if (SQLiteBooksDatabase.Instance() == null)
		{
			new SQLiteBooksDatabase(this, "READER");
		}
		return new ZJReaderApp(file != null ? file.getPath() : null);
	}

	@Override
	public boolean onSearchRequested()
	{
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		final ZJReaderApp.PopupPanel popup = fbreader.getActivePopup();
		fbreader.hideActivePopup();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener()
		{
			public void onCancel()
			{
				if (popup != null)
				{
					fbreader.showPopup(popup.getId());
				}
				manager.setOnCancelListener(null);
			}
		});
		startSearch(fbreader.TextSearchPatternOption.getValue(), true, null, false);
		return true;
	}

	@Override
	protected void onDestroy()
	{
		setAutoRead(true);
		super.onDestroy();
	}

	public void showSelectionPanel()
	{
		final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
		final ZLTextView view = fbReader.getTextView();
		((SelectionPopup) fbReader.getPopupById(SelectionPopup.ID)).move(view.getSelectionStartY(), view.getSelectionEndY());
		fbReader.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel()
	{
		final ZJReaderApp fbReader = (ZJReaderApp) ZJReaderApp.Instance();
		final ZJReaderApp.PopupPanel popup = fbReader.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID)
		{
			ZJReaderApp.Instance().hideActivePopup();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		switch (requestCode)
		{
		case REPAINT_CODE:
		{
			final BookModel model = fbreader.Model;
			if (model != null)
			{
				final Book book = model.Book;
				if (book != null)
				{
					book.reloadInfoFromDatabase();
					ZLTextHyphenator.Instance().load(book.getLanguage());
				}
			}
			fbreader.clearTextCaches();
			fbreader.getViewWidget().repaint();
			break;
		}
		case AUTOREAD_CODE:
			isAutoRead = false;
			setAutoRead(false);
		case CANCEL_CODE:
			fbreader.runCancelAction(resultCode - 1);
			break;
		}
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		// menu_layout.setVisibility(View.GONE);
		switch (arg0.getId())
		{
		case R.id.book_cate:
			startActivity(new Intent(this, TOCActivity.class));
			break;
		case R.id.back:
			finish();
			break;
		case R.id.menu_action_bookmark:
			startActivity(new Intent(this, BookmarksActivity.class));
			break;
		case R.id.menu_action_autoread:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			if (isAutoRead)
				setAutoRead(false);
			else
			{
				auto_read_text.setText(R.string.stop);
				startActivityForResult(new Intent(this, ChangeAutoSpeedActivity.class), AUTOREAD_CODE);
			}
			break;
		case R.id.menu_action_changebg:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			startActivity(new Intent(this, ChangeBgActivity.class));
			break;
		case R.id.menu_action_changebright:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			startActivity(new Intent(this, ChangeBrightActivity.class));
			break;
		case R.id.menu_action_changefont:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			startActivity(new Intent(this, ChangeFontActivity.class));
			break;
		case R.id.menu_action_rotate:
			rotate();
			break;
		case R.id.menu_action_search:
			onSearchRequested();
			break;
		case R.id.menu_action_setting:
			startActivityForResult(new Intent(this, PreferenceActivity.class), REPAINT_CODE);
			break;
		case R.id.gotopage:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			startActivity(new Intent(this, NavigateActivity.class));
		case R.id.click_area:
			toggleMenu();
			break;
		default:
			break;
		}
	}

	private void setAutoRead(boolean isBack)
	{
		// TODO Auto-generated method stub
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		ScrollingPreferences sp = ScrollingPreferences.Instance();
		if (isAutoRead || isBack)
		{
			isAutoRead = false;
			auto_read_text.setText(R.string.menu_autoread);
			if (autorread != null)
				autorread.cancel();
			sp.AnimationOption.setValue(ZLView.Animation.curl);
			sp.HorizontalOption.setValue(true);
			fbreader.clearTextCaches();
			fbreader.getViewWidget().repaint();
		} else
		{
			isAutoRead = true;
			auto_read_text.setText(R.string.stop);
			sp.AnimationOption.setValue(ZLView.Animation.shift);
			sp.HorizontalOption.setValue(false);
			autorread = new Timer();
			autorread.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					new TurnPageAction(fbreader, true).run();
				}
			}, 1, 100);
		}
	}
}

package com.rdweiba.main.pdfview;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdweiba.main.Const;
import com.rdweiba.main.R;
import com.rdweiba.main.pagesview.PagesView;
import com.rdweiba.main.pdfview.PagePickerDialog.OnPageSetListener;

import cx.hell.android.pdfview.PDF;

/**
 * Document display activity.
 */
public class OpenFileActivity extends Activity implements OnClickListener
{

	private final static String TAG = "cx.hell.android.pdfview";

	private final static int[] zoomAnimations =
	{ R.anim.zoom_disappear, R.anim.zoom_almost_disappear, R.anim.zoom };

	private final static int[] pageNumberAnimations =
	{ R.anim.page_disappear, R.anim.page_almost_disappear, R.anim.page, R.anim.page_show_always };

	private PDF pdf = null;
	private PagesView pagesView = null;
	private PDFPagesProvider pdfPagesProvider = null;
	private Actions actions = null;
	private int myOrientation = 1;
	private Handler zoomHandler = null;
	private Runnable zoomRunnable = null;
	private LinearLayout pageview_layout = null;
	private PagePickerDialog m_go_dialog;
	// currently opened file path
	private String filePath = "/";

	// zoom buttons, layout and fade animation
	private ImageView zoomDownButton;
	private ImageView zoomWidthButton;
	private ImageView zoomUpButton;
	private Animation zoomAnim;
	private TableLayout menu_layout;
	private LinearLayout zoom_area;
	private RelativeLayout title_bar;
	private LinearLayout menu_action_rotate;
	private LinearLayout menu_action_changesize;
	private LinearLayout menu_action_gopage;
	private LinearLayout menu_action_bookmark;
	private LinearLayout click_area;
	private ImageButton btn_back;
	private ImageButton btn_front;
	// page number display
	private TextView pageNumberTextView;
	private Animation pageNumberAnim;
	private int box = 2;
	public boolean showZoomOnScroll = false;
	private int fadeStartOffset = 7000;
	private int colorMode = Options.COLOR_MODE_NORMAL;
	public static int RESULT_OK = 100;
	private ProgressDialog pd = null;

	/**
	 * Called when the activity is first created. TODO: initialize dialog fast,
	 * then move file loading to other thread TODO: add progress bar for file
	 * load TODO: add progress icon for file rendering
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Options.setOrientation(this);
		SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.pdf_main);
		this.box = Integer.parseInt(options.getString(Options.PREF_BOX, "2"));

		// Get display metrics
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// use a relative layout to stack the views
		pageview_layout = (LinearLayout) findViewById(R.id.pageview_layout);
		pageNumberTextView = (TextView) findViewById(R.id.pageNumberTextView);
		zoomDownButton = (ImageView) findViewById(R.id.font_minus);
		zoomUpButton = (ImageView) findViewById(R.id.font_plus);
		menu_layout = (TableLayout) findViewById(R.id.menu_layout);
		zoom_area = (LinearLayout) findViewById(R.id.zoom_area);
		title_bar = (RelativeLayout) findViewById(R.id.title_bar);
		menu_action_rotate = (LinearLayout) findViewById(R.id.menu_action_rotate);
		menu_action_changesize = (LinearLayout) findViewById(R.id.menu_action_changesize);
		menu_action_gopage = (LinearLayout) findViewById(R.id.menu_action_gopage);
		btn_back = (ImageButton) findViewById(R.id.back);
		btn_front = (ImageButton) findViewById(R.id.frontpage);
		menu_action_bookmark = (LinearLayout) findViewById(R.id.menu_action_bookmark);
		click_area = (LinearLayout) findViewById(R.id.click_area);
		menu_action_rotate.setOnClickListener(this);
		menu_action_changesize.setOnClickListener(this);
		menu_action_gopage.setOnClickListener(this);
		menu_action_bookmark.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_front.setOnClickListener(this);
		click_area.setOnClickListener(this);

		// the PDF view
		this.pagesView = new PagesView(this, handler);
		pagesView.setFocusable(true);
		pagesView.setFocusableInTouchMode(true);
		pageview_layout.addView(pagesView);
		filePath = getIntent().getStringExtra("file");
		pd = ProgressDialog.show(this, "加载中", "文件加载中，请稍后...", true);
		startPDF(options, filePath);
		this.zoomHandler = new Handler();
		setZoomButtonHandlers();
		initGoDialog();
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 0:
				if (pd != null)
				{
					pd.dismiss();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void initGoDialog()
	{
		m_go_dialog = new PagePickerDialog(this);
		m_go_dialog.setMax(pdfPagesProvider.getPageCount());
		m_go_dialog.setOnPageSetListener(new OnPageSetListener()
		{
			public void onPageSet(DialogInterface picker, int page)
			{
				gotoPage(page - 1);
			}
		});
	}

	/**
	 * Save the current page before exiting
	 */
	@Override
	protected void onPause()
	{
		saveLastPage();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Options.setOrientation(this);
		SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(this);
		boolean eink = options.getBoolean(Options.PREF_EINK, false);
		this.pagesView.setEink(eink);
		if (eink)
			this.setTheme(android.R.style.Theme_Light);
		this.pagesView.setNook2(options.getBoolean(Options.PREF_NOOK2, false));
		if (options.getBoolean(Options.PREF_KEEP_ON, false))
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		else
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		actions = new Actions(options);
		this.pagesView.setActions(actions);
		this.showZoomOnScroll = options.getBoolean(Options.PREF_SHOW_ZOOM_ON_SCROLL, false);
		this.pagesView.setDoubleTap(Integer.parseInt(options.getString(Options.PREF_DOUBLE_TAP, "" + Options.DOUBLE_TAP_ZOOM_IN_OUT)));
		int newBox = Integer.parseInt(options.getString(Options.PREF_BOX, "2"));
		if (this.box != newBox)
		{
			saveLastPage();
			this.box = newBox;
			startPDF(options, filePath);
			this.pagesView.goToBookmark();
		}
		this.colorMode = Options.getColorMode(options);
		this.pdfPagesProvider.setGray(Options.isGray(this.colorMode));
		this.pdfPagesProvider.setExtraCache(1024 * 1024 * Options.getIntFromString(options, Options.PREF_EXTRA_CACHE, 0));
		this.pdfPagesProvider.setOmitImages(options.getBoolean(Options.PREF_OMIT_IMAGES, false));
		this.pagesView.setColorMode(this.colorMode);
		this.pdfPagesProvider.setRenderAhead(options.getBoolean(Options.PREF_RENDER_AHEAD, true));
		this.pagesView.setVerticalScrollLock(false);
		this.pagesView.invalidate();
		int zoomAnimNumber = Integer.parseInt(options.getString(Options.PREF_ZOOM_ANIMATION, "2"));
		if (zoomAnimNumber == Options.ZOOM_BUTTONS_DISABLED)
			zoomAnim = null;
		else
			zoomAnim = AnimationUtils.loadAnimation(this, zoomAnimations[zoomAnimNumber]);
		int pageNumberAnimNumber = Integer.parseInt(options.getString(Options.PREF_PAGE_ANIMATION, "3"));
		if (pageNumberAnimNumber == Options.PAGE_NUMBER_DISABLED)
			pageNumberAnim = null;
		else
			pageNumberAnim = AnimationUtils.loadAnimation(this, pageNumberAnimations[pageNumberAnimNumber]);
		fadeStartOffset = 1000 * Integer.parseInt(options.getString(Options.PREF_FADE_SPEED, "7"));
		this.pageNumberTextView.setVisibility(pageNumberAnim == null ? View.GONE : View.VISIBLE);
		showAnimated(true);
	}

	/**
	 * Set handlers on zoom level buttons
	 */
	private void setZoomButtonHandlers()
	{
		this.zoomDownButton.setOnLongClickListener(new View.OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				pagesView.doAction(actions.getAction(Actions.LONG_ZOOM_IN));
				return true;
			}
		});
		this.zoomDownButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				pagesView.doAction(actions.getAction(Actions.ZOOM_IN));
			}
		});
		// this.zoomWidthButton.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// pagesView.zoomWidth();
		// }
		// });
		// this.zoomWidthButton.setOnLongClickListener(new
		// View.OnLongClickListener()
		// {
		// public boolean onLongClick(View v)
		// {
		// pagesView.zoomFit();
		// return true;
		// }
		// });
		this.zoomUpButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				pagesView.doAction(actions.getAction(Actions.ZOOM_OUT));
			}
		});
		this.zoomUpButton.setOnLongClickListener(new View.OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				pagesView.doAction(actions.getAction(Actions.LONG_ZOOM_OUT));
				return true;
			}
		});
	}

	private void startPDF(SharedPreferences options, String filePath)
	{
		this.pdf = this.getPDF(filePath);
		this.colorMode = Options.getColorMode(options);
		this.pdfPagesProvider = new PDFPagesProvider(this, pdf, Options.isGray(this.colorMode), options.getBoolean(Options.PREF_OMIT_IMAGES, false), options.getBoolean(
				Options.PREF_RENDER_AHEAD, true), handler);
		pagesView.setPagesProvider(pdfPagesProvider);
		Bookmark b = new Bookmark(this.getApplicationContext()).open();
		pagesView.setStartBookmark(b, filePath);
		b.close();
	}

	/**
	 * Return PDF instance wrapping file referenced by Intent. Currently reads
	 * all bytes to memory, in future local files should be passed to native
	 * code and remote ones should be downloaded to local tmp dir.
	 * 
	 * @param filePath
	 * 
	 * @return PDF instance
	 */
	private PDF getPDF(String filePath)
	{
		return new PDF(new File(filePath), this.box);
	}

	/**
	 * Intercept touch events to handle the zoom buttons animation
	 */
	public void showZoom()
	{
		zoomHandler.removeCallbacks(zoomRunnable);
		zoomHandler.postDelayed(zoomRunnable, fadeStartOffset);
	}

	public void showPageNumber(boolean force)
	{
		if (pageNumberAnim == null)
		{
			pageNumberTextView.setVisibility(View.GONE);
			return;
		}
		pageNumberTextView.setVisibility(View.VISIBLE);
		String newText = "" + (this.pagesView.getCurrentPage() + 1) + "/" + this.pdfPagesProvider.getPageCount();
		if (!force && newText.equals(pageNumberTextView.getText()))
			return;
		pageNumberTextView.setText(newText);
		pageNumberTextView.clearAnimation();
	}

	/**
	 * Show zoom buttons and page number
	 */
	public void showAnimated(boolean alsoZoom)
	{
		if (alsoZoom)
			showZoom();
		showPageNumber(true);
	}

	/**
	 * Show error message to user.
	 * 
	 * @param message
	 *            message to show
	 */
	private void errorMessage(String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog = builder.setMessage(message).setTitle("Error").create();
		dialog.show();
	}

	private void gotoPage(int page)
	{
		Log.i(TAG, "rewind to page " + page);
		if (this.pagesView != null)
		{
			pd = ProgressDialog.show(this, "加载中", "文件加载中，请稍后...", true);
			this.pagesView.scrollToPage(page);
			showAnimated(true);
		}
	}

	/**
	 * Save the last page in the bookmarks
	 */
	private void saveLastPage()
	{
		BookmarkEntry entry = this.pagesView.toBookmarkEntry();
		Bookmark b = new Bookmark(this.getApplicationContext()).open();
		b.setLast(filePath, entry);
		b.close();
		Log.i(TAG, "last page saved for " + filePath);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		Log.i(TAG, "onConfigurationChanged(" + newConfig + ")");
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.back:
			finish();
			break;
		case R.id.menu_action_changesize:
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
			zoom_area.setVisibility(View.VISIBLE);
			break;
		case R.id.menu_action_bookmark:
			Intent intent = new Intent(this, BookmarksActivity.class);
			intent.putExtra("page", pagesView.getCurrentPage());
			intent.putExtra("path", filePath);
			startActivityForResult(intent, RESULT_OK);
			break;
		case R.id.menu_action_gopage:
			m_go_dialog.setCurrent(this.pagesView.getCurrentPage() + 1);
			m_go_dialog.show();
			break;
		case R.id.frontpage:
			gotoPage(0);
			break;
		case R.id.menu_action_rotate:
			switch (myOrientation)
			{
			case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
				myOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
				myOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			default:
				break;
			}
			setRequestedOrientation(myOrientation);
		case R.id.click_area:
			toggleMenu();
			break;
		default:
			break;
		}
	}

	private void toggleMenu()
	{
		if (menu_layout.getVisibility() == View.GONE)
		{
			menu_layout.setVisibility(View.VISIBLE);
			title_bar.setVisibility(View.VISIBLE);
			zoom_area.setVisibility(View.GONE);
		} else
		{
			menu_layout.setVisibility(View.GONE);
			title_bar.setVisibility(View.GONE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			toggleMenu();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (zoom_area.getVisibility() == View.VISIBLE)
			{
				zoom_area.setVisibility(View.GONE);
				title_bar.setVisibility(View.GONE);
			} else
				this.finish();
			return true;
		} else
			return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		System.out.println(requestCode + "requestCode");
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK)
		{
			System.out.println("RESULT_OK");
			if (data != null && data.getStringExtra("page") != null)
			{
				int page = Integer.parseInt(data.getStringExtra("page"));
				gotoPage(page - 1);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy()
	{
		if (Const.isCode)
		{
			File file = new File(filePath);
			file.delete();
		}
		super.onDestroy();
	}
}

package org.android.zjreader;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.android.util.UIUtil;
import org.zjreader.library.Book;
import org.zjreader.library.Bookmark;
import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.options.ZLStringOption;
import org.zlibrary.core.resources.ZLResource;
import org.zlibrary.core.util.ZLMiscUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rdweiba.main.R;

public class BookmarksActivity extends Activity implements OnClickListener
{
	private static final int OPEN_ITEM_ID = 0;
	private static final int EDIT_ITEM_ID = 1;
	private static final int DELETE_ITEM_ID = 2;

	private ImageButton addbookmark_btn;
	private ImageButton back_btn;
	List<Bookmark> AllBooksBookmarks;
	private final List<Bookmark> myThisBookBookmarks = new LinkedList<Bookmark>();
	private final List<Bookmark> mySearchResults = new LinkedList<Bookmark>();

	private ListView myThisBookView;
	private final ZLStringOption myBookmarkSearchPatternOption = new ZLStringOption("BookmarkSearch", "Pattern", "");

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		Thread.setDefaultUncaughtExceptionHandler(new org.zlibrary.ui.android.library.UncaughtExceptionHandler(this));

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(null);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bookmarks);

		AllBooksBookmarks = Bookmark.bookmarks();
		Collections.sort(AllBooksBookmarks, new Bookmark.ByTimeComparator());
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();

		if (fbreader.Model != null)
		{
			final long bookId = fbreader.Model.Book.getId();
			for (Bookmark bookmark : AllBooksBookmarks)
			{
				if (bookmark.getBookId() == bookId)
				{
					myThisBookBookmarks.add(bookmark);
				}
			}

			// myThisBookView = createTab("thisBook", R.id.this_book);
			myThisBookView = (ListView) findViewById(R.id.this_book);
			myThisBookView.setDivider(null);
			new BookmarksAdapter(myThisBookView, myThisBookBookmarks, true);
		} else
		{
			findViewById(R.id.this_book).setVisibility(View.GONE);
		}
		addbookmark_btn = (ImageButton) findViewById(R.id.add_bookmark);
		back_btn = (ImageButton) findViewById(R.id.back);
		addbookmark_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);

	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		if (!Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			return;
		}
		String pattern = intent.getStringExtra(SearchManager.QUERY);
		myBookmarkSearchPatternOption.setValue(pattern);

		final LinkedList<Bookmark> bookmarks = new LinkedList<Bookmark>();
		pattern = pattern.toLowerCase();
		for (Bookmark b : AllBooksBookmarks)
		{
			if (ZLMiscUtil.matchesIgnoreCase(b.getText(), pattern))
			{
				bookmarks.add(b);
			}
		}
		if (!bookmarks.isEmpty())
		{
		} else
		{
			UIUtil.showErrorMessage(this, "bookmarkNotFound");
		}
	}

	@Override
	public void onPause()
	{
		for (Bookmark bookmark : AllBooksBookmarks)
		{
			bookmark.save();
		}
		super.onPause();
	}

	private void invalidateAllViews()
	{
		myThisBookView.invalidateViews();
		myThisBookView.requestLayout();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		final ListView view = (ListView) findViewById(R.id.this_book);
		final Bookmark bookmark = ((BookmarksAdapter) view.getAdapter()).getItem(position);
		switch (item.getItemId())
		{
		case OPEN_ITEM_ID:
			gotoBookmark(bookmark);
			return true;
		case EDIT_ITEM_ID:
			final Intent intent = new Intent(this, BookmarkEditActivity.class);
			startActivityForResult(intent, 1);
			// TODO: implement
			return true;
		case DELETE_ITEM_ID:
			bookmark.delete();
			myThisBookBookmarks.remove(bookmark);
			AllBooksBookmarks.remove(bookmark);
			mySearchResults.remove(bookmark);
			invalidateAllViews();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void addBookmark()
	{
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		final Bookmark bookmark = fbreader.addBookmark(20, true);
		if (bookmark != null)
		{
			myThisBookBookmarks.add(0, bookmark);
			AllBooksBookmarks.add(0, bookmark);
			invalidateAllViews();
		}
	}

	private void gotoBookmark(Bookmark bookmark)
	{
		bookmark.onOpen();
		final ZJReaderApp fbreader = (ZJReaderApp) ZJReaderApp.Instance();
		final long bookId = bookmark.getBookId();
		if ((fbreader.Model == null) || (fbreader.Model.Book.getId() != bookId))
		{
			final Book book = Book.getById(bookId);
			if (book != null)
			{
				finish();
				fbreader.openBook(book, bookmark);
			} else
			{
				UIUtil.showErrorMessage(this, "cannotOpenBook");
			}
		} else
		{
			finish();
			fbreader.gotoBookmark(bookmark);
		}
	}

	private final class BookmarksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener
	{
		private final List<Bookmark> myBookmarks;
		private final boolean myCurrentBook;

		BookmarksAdapter(ListView listView, List<Bookmark> bookmarks, boolean currentBook)
		{
			myBookmarks = bookmarks;
			myCurrentBook = currentBook;
			listView.setAdapter(this);
			listView.setOnItemClickListener(this);
			listView.setOnCreateContextMenuListener(this);
		}

		public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
		{
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			if (getItem(position) != null)
			{
				menu.setHeaderTitle(getItem(position).getText());
				final ZLResource resource = ZLResource.resource("bookmarksView");
				menu.add(0, OPEN_ITEM_ID, 0, resource.getResource("open").getValue());
				menu.add(0, DELETE_ITEM_ID, 0, resource.getResource("delete").getValue());
			}
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
			final ImageView imageView = (ImageView) view.findViewById(R.id.bookmark_item_icon);
			final TextView textView = (TextView) view.findViewById(R.id.bookmark_item_text);
			final TextView bookTitleView = (TextView) view.findViewById(R.id.bookmark_item_booktitle);
			final TextView bookDateView = (TextView) view.findViewById(R.id.bookmark_item_time);

			final Bookmark bookmark = getItem(position);
			if (bookmark == null)
			{
				imageView.setVisibility(View.VISIBLE);
				textView.setText(ZLResource.resource("bookmarksView").getResource("new").getValue());
				bookTitleView.setVisibility(View.GONE);
			} else
			{
				imageView.setVisibility(View.GONE);
				textView.setText(bookmark.getText());
				bookTitleView.setVisibility(View.GONE);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String dateString = formatter.format(bookmark.getTime(Bookmark.CREATION));
				bookDateView.setText(dateString);
			}
			return view;
		}

		public final boolean areAllItemsEnabled()
		{
			return true;
		}

		public final boolean isEnabled(int position)
		{
			return true;
		}

		public final long getItemId(int position)
		{
			return position;
		}

		public final Bookmark getItem(int position)
		{
			return myBookmarks.get(position);
		}

		public final int getCount()
		{
			return myBookmarks.size();
		}

		public final void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			final Bookmark bookmark = getItem(position);
			if (bookmark != null)
			{
				gotoBookmark(bookmark);
			} else
			{
				addBookmark();
			}
		}
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.add_bookmark:
			addBookmark();
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
}

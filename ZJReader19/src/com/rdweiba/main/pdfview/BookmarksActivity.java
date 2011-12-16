package com.rdweiba.main.pdfview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.zlibrary.core.resources.ZLResource;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rdweiba.main.R;

public class BookmarksActivity extends Activity implements OnClickListener
{
	private ImageButton addbookmark_btn;
	private ImageButton back_btn;
	private DBHelper db;
	private ArrayList<MyBookmark> bookmarkList;
	private int page = 0;
	private String filepath = "";
	private ListView myThisBookView;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bookmarks);
		addbookmark_btn = (ImageButton) findViewById(R.id.add_bookmark);
		back_btn = (ImageButton) findViewById(R.id.back);
		myThisBookView = (ListView) findViewById(R.id.this_book);
		myThisBookView.setDivider(null);
		addbookmark_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		page = getIntent().getIntExtra("page", 0);
		filepath = getIntent().getStringExtra("path");
		getBookmark();
		new BookmarksAdapter(myThisBookView);
	}

	private void getBookmark()
	{
		// TODO Auto-generated method stub
		bookmarkList = new ArrayList<MyBookmark>();
		db = new DBHelper(this);
		Cursor cur = db.findAll(filepath);
		cur.moveToFirst();
		while (!cur.isAfterLast())
		{
			try
			{
				MyBookmark bookmark = new MyBookmark();
				bookmark.setId(cur.getInt(0));
				bookmark.setTitle(cur.getString(1));
				bookmark.setDate(cur.getString(2));
				bookmarkList.add(bookmark);
			} catch (Exception e)
			{
				cur.moveToNext();
				continue;
			}
			cur.moveToNext();
		}
		cur.close();
	}

	private void addBookmark()
	{
		String date = getNowTime();
		String title = "" + (page + 1);
		db.save(title, date, filepath);
		MyBookmark bookmark = new MyBookmark();
		bookmark.setDate(date);
		bookmark.setTitle(title);
		bookmarkList.add(bookmark);
		myThisBookView.invalidateViews();
	}

	private String getNowTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	private void gotoBookmark(MyBookmark bookmark)
	{
		Intent intent = new Intent();
		intent.putExtra("page", bookmark.getTitle());
		setResult(OpenFileActivity.RESULT_OK, intent);
		finish();
	}

	public class BookmarksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener
	{

		public BookmarksAdapter(ListView listView)
		{
			listView.setAdapter(this);
			listView.setOnItemClickListener(this);
			listView.setOnCreateContextMenuListener(this);
		}

		public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
		{
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			if (getItem(position) != null)
			{
				menu.setHeaderTitle(getItem(position).getTitle());
				final ZLResource resource = ZLResource.resource("bookmarksView");
				menu.add(0, 0, 0, resource.getResource("open").getValue());
				menu.add(0, 1, 0, resource.getResource("delete").getValue());
			}
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
			final ImageView imageView = (ImageView) view.findViewById(R.id.bookmark_item_icon);
			final TextView textView = (TextView) view.findViewById(R.id.bookmark_item_text);
			final TextView bookTitleView = (TextView) view.findViewById(R.id.bookmark_item_booktitle);
			final TextView bookDateView = (TextView) view.findViewById(R.id.bookmark_item_time);

			final MyBookmark bookmark = getItem(position);
			if (bookmark == null)
			{
				imageView.setVisibility(View.VISIBLE);
				textView.setText(ZLResource.resource("bookmarksView").getResource("new").getValue());
				bookTitleView.setVisibility(View.GONE);
			} else
			{
				imageView.setVisibility(View.GONE);
				textView.setText(" È«©“≥" + bookmark.getTitle());
				bookTitleView.setVisibility(View.GONE);
				bookDateView.setText(bookmark.getDate());
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

		public final MyBookmark getItem(int position)
		{
			return bookmarkList.get(position);
		}

		public final int getCount()
		{
			return bookmarkList.size();
		}

		public final void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			final MyBookmark bookmark = getItem(position);
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
	public boolean onContextItemSelected(MenuItem item)
	{
		final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		final ListView view = (ListView) findViewById(R.id.this_book);
		final MyBookmark bookmark = ((BookmarksAdapter) view.getAdapter()).getItem(position);
		switch (item.getItemId())
		{
		case 0:
			gotoBookmark(bookmark);
			return true;
		case 1:
			db.delete(bookmark.getDate(), filepath);
			bookmarkList.remove(bookmark);
			view.invalidateViews();
			return true;
		}
		return super.onContextItemSelected(item);
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

	@Override
	protected void onDestroy()
	{
		if (db != null)
		{
			db.close();
		}
		super.onDestroy();
	}
}

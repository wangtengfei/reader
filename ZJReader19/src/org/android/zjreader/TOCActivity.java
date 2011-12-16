package org.android.zjreader;

import org.zjreader.bookmodel.TOCTree;
import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.application.ZLApplication;
import org.zlibrary.core.resources.ZLResource;
import org.zlibrary.core.tree.ZLTree;
import org.zlibrary.text.view.ZLTextWordCursor;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rdweiba.main.R;

public class TOCActivity extends Activity implements OnClickListener
{
	private TOCAdapter myAdapter;
	private ZLTree<?> mySelectedItem;
	private ListView tocListView;
	private ImageButton back_btn;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		Thread.setDefaultUncaughtExceptionHandler(new org.zlibrary.ui.android.library.UncaughtExceptionHandler(this));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.toc_tree);
		tocListView = (ListView) findViewById(R.id.book_toc_listview);
		back_btn = (ImageButton) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
		System.out.println("tocListView" + tocListView);

		final ZJReaderApp fbreader = (ZJReaderApp) ZLApplication.Instance();
		final TOCTree root = fbreader.Model.TOCTree;
		myAdapter = new TOCAdapter(root, tocListView);
		final ZLTextWordCursor cursor = fbreader.BookTextView.getStartCursor();
		int index = cursor.getParagraphIndex();
		if (cursor.isEndOfParagraph())
		{
			++index;
		}
		TOCTree treeToSelect = null;
		for (TOCTree tree : root)
		{
			final TOCTree.Reference reference = tree.getReference();
			if (reference == null)
			{
				continue;
			}
			if (reference.ParagraphIndex > index)
			{
				break;
			}
			treeToSelect = tree;
		}
		myAdapter.selectItem(treeToSelect);
		mySelectedItem = treeToSelect;
	}

	private static final int PROCESS_TREE_ITEM_ID = 0;
	private static final int READ_BOOK_ITEM_ID = 1;

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		final TOCTree tree = (TOCTree) myAdapter.getItem(position);
		switch (item.getItemId())
		{
		case PROCESS_TREE_ITEM_ID:
			myAdapter.runTreeItem(tree);
			return true;
		case READ_BOOK_ITEM_ID:
			myAdapter.openBookText(tree);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private final class TOCAdapter extends ZLTreeAdapter
	{
		TOCAdapter(TOCTree root, ListView tocList)
		{
			super(tocList, root);
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
		{
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			final TOCTree tree = (TOCTree) getItem(position);
			if (tree.hasChildren())
			{
				menu.setHeaderTitle(tree.getText());
				final ZLResource resource = ZLResource.resource("tocView");
				menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
				menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
			final TOCTree tree = (TOCTree) getItem(position);
			// view.setBackgroundColor((tree == mySelectedItem) ? 0xff808080 :
			// 0);
			setIcon((ImageView) view.findViewById(R.id.toc_tree_item_icon), tree);
			((TextView) view.findViewById(R.id.toc_tree_item_text)).setText(tree.getText());
			return view;
		}

		void openBookText(TOCTree tree)
		{
			final TOCTree.Reference reference = tree.getReference();
			if (reference != null)
			{
				finish();
				final ZJReaderApp fbreader = (ZJReaderApp) ZLApplication.Instance();
				fbreader.addInvisibleBookmark();
				fbreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
				fbreader.showBookTextView();
			}
		}

		@Override
		protected boolean runTreeItem(ZLTree<?> tree)
		{
			if (super.runTreeItem(tree))
			{
				return true;
			}
			openBookText((TOCTree) tree);
			return true;
		}
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.back_btn)
		{
			finish();
		}
	}
}

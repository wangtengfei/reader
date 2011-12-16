package com.rdweiba.main.com.multimedia.main.player;

import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdweiba.main.R;

public class SongsListActivity extends ListActivity {

	private ListView listView;
	private ImageView imageView;
	private AudioPlayerActivity audioPlayerActivity;
	private SongsSdListActivity songsSdListActivity;
	private TextView textView;
	private ImageButton shunxu = null;
	private ImageButton wuxu = null;
	private ImageButton changpian = null;
	private ImageButton geci = null;

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.songslist);

		shunxu = (ImageButton) findViewById(R.id.xunhuanbutton);
		wuxu = (ImageButton) findViewById(R.id.wuxubutton);

		listView = (ListView) findViewById(android.R.id.list);
		
		Toast.makeText(SongsListActivity.this,
				"长按实现移除", Toast.LENGTH_SHORT).show();
		Toast.makeText(SongsListActivity.this,
				"后台播放已开启，用户可从顶部通知栏放回播放/暂停控制", Toast.LENGTH_SHORT).show();

		if (audioPlayerActivity.playlist != null) {
			AppAdapter appAdapter = new AppAdapter(this,
					audioPlayerActivity.playlist);
			listView.setAdapter(appAdapter);
			registerForContextMenu(listView);
		}

		textView = (TextView) findViewById(R.id.textView1);
		if (audioPlayerActivity.playlist != null) {
			textView.setText("播放列表-" + audioPlayerActivity.playlist.size()
					+ "首歌曲");
		} else {
			textView.setText("播放列表-" + 0 + "首歌曲");
		}

		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SongsListActivity.this,
						SongsSdListActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		shunxu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				audioPlayerActivity.playState = 0;
				shunxu.setImageResource(R.drawable.shunxu2);
				wuxu.setImageResource(R.drawable.wuxu);
				Toast.makeText(SongsListActivity.this, "顺序播放",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		wuxu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				audioPlayerActivity.playState = 1;
				shunxu.setImageResource(R.drawable.shunxu);
				wuxu.setImageResource(R.drawable.wuxu2);
				Toast.makeText(SongsListActivity.this, "随机播放",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		changpian = (ImageButton) findViewById(R.id.changpianbutton);
		changpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SongsListActivity.this, AudioPlayerActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		geci = (ImageButton) findViewById(R.id.gecibutton);
		geci.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(SongsListActivity.this,
						"请返回播放器再使用本功能！！！", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	public void onCreateContextMenu(ContextMenu menu, View source,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "移除");
		menu.add(0, 2, 0, "移除全部");
		menu.setGroupCheckable(0, true, true);
		menu.setHeaderTitle("选项");
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = null;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		AppAdapter appAdapter = new AppAdapter(this,
				audioPlayerActivity.playlist);
		switch (item.getItemId()) {
		case 1:
			HashMap<String, String> hashmap = audioPlayerActivity.playlist
					.get(menuInfo.position);
			String name = hashmap.get("Name");

			for (int i = 0; i < songsSdListActivity.sdlist.size(); i++) {
				HashMap<String, String> hashmap1 = songsSdListActivity.sdlist
						.get(i);
				if (name.equals(hashmap1.get("Name"))) {
					songsSdListActivity.checkedMap.put(i, false);
				}
			}

			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
			
			audioPlayerActivity.playlist.remove(menuInfo.position);
			listView.setAdapter(appAdapter);
			registerForContextMenu(listView);
			break;
		case 2:
			audioPlayerActivity.playlist.clear();
			listView.setAdapter(appAdapter);
			for (int i = 0; i < songsSdListActivity.checkedMap.size(); i++) {
				songsSdListActivity.checkedMap.put(i, false);
			}
			break;
		}
		return false;
	}
	
	private Handler handler = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int wt = msg.what;
			switch (wt) {
			case 0:
				textView.setText("播放列表-" + audioPlayerActivity.playlist.size()
						+ "首歌曲");
				break;
			}
		}
	};

	class AppAdapter extends BaseAdapter {

		private List<HashMap<String, String>> list;
		private Context tex;

		public AppAdapter(Context tex, List<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
			this.tex = tex;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout linearLayout = (LinearLayout) View.inflate(tex,
					R.layout.songsinforowt, null);
			@SuppressWarnings("unchecked")
			HashMap<String, String> hm = (HashMap<String, String>) getItem(position);

			TextView name = (TextView) linearLayout.findViewById(R.id.songname);
			TextView author = (TextView) linearLayout
					.findViewById(R.id.songauthor);

			String songName = ((hm.get("Name")).equals("<unknown>") ? "未知歌名"
					: (hm.get("Name")));

			String songAuthor = ((hm.get("Album")).equals("<unknown>") ? "未知专辑"
					: (hm.get("Album")));

			String songArtist = ((hm.get("Singer")).equals("<unknown>") ? "未知艺术家"
					: (hm.get("Singer")));

			name.setText(songName);
			author.setText(songArtist + songAuthor);

			return linearLayout;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(SongsListActivity.this,AudioPlayerActivity.class);
		intent.putExtra("beforeAct", 1);
		intent.putExtra("beforeActList", 1);
		intent.putExtra("position", position);
		startActivity(intent);
		finish();
	}
}

package com.rdweiba.main.com.multimedia.main.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rdweiba.main.Const;
import com.rdweiba.main.R;

public class SongsSdListActivity extends Activity {

	private AppAdapter appAdapter;
	private ListView listView;
	public static List<HashMap<String, String>> sdlist = new ArrayList<HashMap<String, String>>();
	private ImageView imageView;
	private TextView textView;
	private Button b1, b2;
	public static List<Integer> checkstatelist = new ArrayList<Integer>();;
	public AudioPlayerActivity audioPlayerActivity;
	public static Map<Integer, Boolean> checkedMap = new HashMap<Integer, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.songssdlist);
		listView = (ListView) findViewById(android.R.id.list);
		
		sdlist = Const.playlist;

		int size = checkedMap.size();
		if (size < sdlist.size()) {
			for (int i = 0; i < sdlist.size() - size; i++) {
				checkedMap.put(checkedMap.size(), false);
			}
		}

		textView = (TextView) findViewById(R.id.textView1);
		textView.setText("播放列表-" + sdlist.size() + "首歌曲");

		appAdapter = new AppAdapter(this, sdlist);
		listView.setAdapter(appAdapter);

		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				audioPlayerActivity.playlist.clear();
				for (int i = 0; i < listView.getCount(); i++) {
					if (checkedMap.get(i))
						audioPlayerActivity.playlist.add(sdlist.get(i));
				}

				Intent intent = new Intent(SongsSdListActivity.this,
						SongsListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		b1 = (Button) findViewById(R.id.button2);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int j = 0; j < sdlist.size(); j++) {
					checkedMap.put(j, true);
				}

				AppAdapter appAdapter = new AppAdapter(
						SongsSdListActivity.this, sdlist);
				listView.setAdapter(appAdapter);
			}
		});

		b2 = (Button) findViewById(R.id.button3);
		b2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int j = 0; j < sdlist.size(); j++) {
					checkedMap.put(j, false);
				}

				AppAdapter appAdapter = new AppAdapter(
						SongsSdListActivity.this, sdlist);
				listView.setAdapter(appAdapter);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				CheckBox checkBox = (CheckBox) view
						.findViewById(R.id.checkBox1);

				if (checkBox.isChecked()) {
					checkBox.setChecked(false);
					checkedMap.put(position, false);
				} else {
					checkBox.setChecked(true);
					checkedMap.put(position, true);
				}
			}
		});
	}

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
					R.layout.songsinforow, null);

			@SuppressWarnings("unchecked")
			HashMap<String, String> hm = (HashMap<String, String>) getItem(position);

			TextView name = (TextView) linearLayout.findViewById(R.id.songname);
			TextView author = (TextView) linearLayout
					.findViewById(R.id.songauthor);
			CheckBox checkBox = (CheckBox) linearLayout
					.findViewById(R.id.checkBox1);

			String songName = ((hm.get("Name")).equals("<unknown>") ? "未知歌名"
					: (hm.get("Name")));

			String songAuthor = ((hm.get("Album")).equals("<unknown>") ? "未知专辑"
					: (hm.get("Album")));

			String songArtist = ((hm.get("Singer")).equals("<unknown>") ? "未知艺术家"
					: (hm.get("Singer")));

			name.setText(songName);
			author.setText(songArtist + songAuthor);
			checkBox.setChecked(checkedMap.get(position));

			return linearLayout;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		audioPlayerActivity.playlist.clear();
		for (int i = 0; i < listView.getCount(); i++) {
			if (checkedMap.get(i))
				audioPlayerActivity.playlist.add(sdlist.get(i));
		}
	}
}

package com.rdweiba.main.com.multimedia.main.player;

import java.io.File;
import java.io.IOException;

import com.rdweiba.main.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MServier extends Service {
	private final static int SCREEN_DEFAULT = 1;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private boolean isStart = true;
	private boolean isPause = true;
	private boolean start = false;
	private boolean mIsPrepared = true;
	private int mSeekWhenPrepared;
	public static final String BIAOZHI = "LOGIN";
	private AudioPlayerActivity audioPlayerActivity;
	
	private int h = 0;
	private int i = 0;
	
	private int songid = 0;
	private String singer;
	private String name;
	private String album;
	private String imagesource;
	private String filepath;
	private int songtime = 0;
	private int startTFM = 0;
	
	private int playContent = 0;
	private boolean isPlay = true;
	private int playState = 0;
	private boolean playForMe = false;
	private String roUrl = null;
	
	
	
	// 媒体播放器
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		ZhuCe();
		try {
			
			int msg = intent.getIntExtra("MSG", 0);
			int progress = intent.getIntExtra("progress", 0);
			int proint = progress * 1000;
			
			startTFM = intent.getIntExtra("start-thread-for-me!", 0);
			playContent = intent.getIntExtra("playcontent", 0);
			isPlay = intent.getBooleanExtra("isplay", true);
			playForMe = intent.getBooleanExtra("playforme", false);
			playState = intent.getIntExtra("playstate", 0);
			
			roUrl = intent.getStringExtra("roUrl");
			i = intent.getIntExtra("i", 0);
			songid = intent.getIntExtra("songid", 0);
			filepath = intent.getStringExtra("filepath");
			singer = intent.getStringExtra("singer");
			name = intent.getStringExtra("name");
			album = intent.getStringExtra("album");
			imagesource = intent.getStringExtra("ImageSource");
			
			if (filepath != null) {
				mMediaPlayer.setDataSource(filepath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();				
				
				if (mMediaPlayer.isPlaying()) {
					songtime = mMediaPlayer.getDuration();
					songtime /= 1000;
					Log.d("songtime------>", songtime + "");
					Intent intent1 = new Intent("FirstQiDong");
					intent1.putExtra("roUrl", roUrl);
					intent1.putExtra("songtime", songtime);
					intent1.putExtra("name", name);
					intent1.putExtra("singer", singer);
					intent1.putExtra("album", album);
					intent1.putExtra("ImageSource", imagesource);
					sendBroadcast(intent1);
				}
			}

			if (proint != 0) {
				seekTo(proint);
			}
			
			if (msg != 0) {
				switch (msg) {
				case 1:
					pause();
					break;
				case 2:
					start();
					break;
				}
			}
			if (i != 0 || playForMe) {
				h = i;
				if(isPlay){
					MThread thread = new MThread();
					thread.start();
				}	
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void seekTo(int msec) {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.seekTo(msec);
			System.out.println("msec------>" + msec);
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public void onCreate(Bundle savedInstanceState)
			throws IllegalArgumentException, IllegalStateException, IOException {
		super.onCreate();
		new com.rdweiba.main.com.multimedia.main.player.VideoView.MySizeChangeLinstener() {
			public void doMyThings() {
			}
		};
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(this.receiver2);
		h = songtime + 5;
		super.onDestroy();
		mMediaPlayer.stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}

	public void start() {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.start();

			isStart = false;
		} else {
			isStart = true;
		}
	}

	public void pause() {
		if (mMediaPlayer != null && mIsPrepared) {
			System.out.println("pause/start....");

			mMediaPlayer.pause();
		}
		isPause = false;
	}

	@SuppressWarnings("static-access")
	class MThread extends Thread {
		public void run() {
			if(isPlay){
				while (h <= songtime) {
					h++;
					Log.d("h------>", h + "");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(playContent == 0 && h == songtime + 1){
				Intent intent = new Intent(MServier.this,MServier.class);
				stopService(intent);
				if(roUrl != null)
					deleteFilepath();
				Log.d("service close", "kill myself!");
			}else if(playContent == 1 && h == songtime + 1 && audioPlayerActivity.playlist.size() != 0){
				if(roUrl != null)
					deleteFilepath();
				Intent intent = new Intent("kill and survive myself");
				intent.putExtra("songid", songid);
				sendBroadcast(intent);
			}
		}
	}

	private BroadcastReceiver receiver2 = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("backinfo")) {
				Intent intent2 = new Intent("back-info");
				intent2.putExtra("i", h);
				intent2.putExtra("roUrl", roUrl);
				intent2.putExtra("songtime", songtime);
				intent2.putExtra("name", name);
				intent2.putExtra("singer", singer);
				intent2.putExtra("album", album);
				intent2.putExtra("ImageSource", imagesource);
				intent2.putExtra("isplay", isPlay);
				sendBroadcast(intent2);
				h = songtime + 5;
			}
			if (intent.getAction().equals("stop-thread")) {
				h = songtime + 5;
				Log.d("thread is stop!!", "Yeah!!!");
			}
		}
	};
	
	void deleteFilepath(){
		try {
			File file = new File(filepath);
			file.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 注册广播
	public void ZhuCe() {
		IntentFilter filter = new IntentFilter("back-info");
		registerReceiver(this.receiver2, filter);
		IntentFilter filter1 = new IntentFilter("FirstQiDong");
		registerReceiver(this.receiver2, filter1);
		IntentFilter filter3 = new IntentFilter("backinfo");
		registerReceiver(this.receiver2, filter3);
		IntentFilter filter4 = new IntentFilter("stop-thread");
		registerReceiver(this.receiver2, filter4);
		IntentFilter filter5 = new IntentFilter("kill and survive myself");
		registerReceiver(this.receiver2, filter5);
	}
}

package com.rdweiba.main.com.multimedia.main.player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.zjreader.formats.dcf.DRMAgentUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rdweiba.main.Const;
import com.rdweiba.main.R;
import com.rdweiba.main.com.multimedia.main.player.VideoView.MySizeChangeLinstener;

public class AudioPlayerActivity extends Activity {

	private final static String TAG = "VideoPlayerActivity";
	private boolean isOnline = false;
	private TextView audio_author;
	private TextView audio_name;
	private TextView audio_album;

	private VideoView vv = null;
	private ImageButton shunxu = null;
	private ImageButton wuxu = null;
	private SeekBar seekBar = null;
	private SeekBar soundbar = null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private AudioManager mAudioManager = null;
	private ImageView imageView = null;

	private int maxVolume = 0;
	private int currentVolume = 0;
	private ImageButton forwardbutton = null;
	private ImageButton backforwardbutton = null;
	private ImageButton bn3 = null;
	private ImageButton close = null;

	private View controlView = null;
	private PopupWindow controler = null;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int controlHeight = 0;

	private ImageButton listbutton = null;
	private ImageButton gecibutton = null;
	private ImageButton xunhuanbutton = null;
	private ImageButton wuxubutton = null;
	private ImageButton HouTai = null;

	public static int songid = 0;
	public int songtime = 0;
	public int i = 0;
	public static List<HashMap<String, String>> playlist = new ArrayList<HashMap<String, String>>();
	private String filepath;
	
	private String coUrl;
	private String roUrl;
	private String type;
	private String deviceCert;
	private String deviceKey;
	
	private String singer;
	private String name;
	private String album;
	private String imagesource;
	private ProgressDialog progressDialog;

	public int beforeAct = 0;
	public int beforeActList = 0;
	public static int playState = 0;
	public boolean isPlay = true;
	public static int playContent = 0;
	public int albumImageDisplayed = 0;
	private Bitmap bm = null;
	
	public int songtime1 = 0;
	public int minute = 0;
	public int hour = 0;
	public int second = 0;
	
	public ActivityManager mActivityManager = null; 
	public List<ActivityManager.RunningServiceInfo> mServiceList = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.audio_player);

		Looper.myQueue().addIdleHandler(new IdleHandler() {
			public boolean queueIdle() {
				if (controler != null) {
					controler.showAtLocation(vv, Gravity.TOP, 0, 0);
					controler.update(0, 0, screenWidth, controlHeight);
				}
				return false;
			}
		});

		imageView = (ImageView) findViewById(R.id.imageView1);
		
		controlView = getLayoutInflater().inflate(R.layout.audio_controler,
				null);
		controler = new PopupWindow(controlView);
		shunxu = (ImageButton) controlView.findViewById(R.id.xunhuanbutton);
		wuxu = (ImageButton) controlView.findViewById(R.id.wuxubutton);
		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		durationTextView = (TextView) controlView.findViewById(R.id.duration);
		playedTextView = (TextView) controlView.findViewById(R.id.has_played);
		audio_author = (TextView) controlView.findViewById(R.id.audio_author);
		audio_album = (TextView) controlView.findViewById(R.id.audio_album);
		audio_name = (TextView) controlView.findViewById(R.id.audio_name);

		backforwardbutton = (ImageButton) controlView
				.findViewById(R.id.backforwardbutton);
		forwardbutton = (ImageButton) controlView
				.findViewById(R.id.forwardbutton);
		bn3 = (ImageButton) controlView.findViewById(R.id.playbutton);
		close = (ImageButton) controlView.findViewById(R.id.back);

		listbutton = (ImageButton) controlView.findViewById(R.id.listbutton);
		gecibutton = (ImageButton) controlView.findViewById(R.id.gecibutton);
		xunhuanbutton = (ImageButton) controlView
				.findViewById(R.id.xunhuanbutton);
		wuxubutton = (ImageButton) controlView.findViewById(R.id.wuxubutton);
		HouTai = (ImageButton) controlView.findViewById(R.id.houtai);
		Intent nService = new Intent(AudioPlayerActivity.this, NService.class);
		startService(nService);
		
		Intent intent = getIntent();
		beforeAct = intent.getIntExtra("beforeAct", 0);
		beforeActList = intent.getIntExtra("beforeActList", 0);

		singer = intent.getStringExtra("Singer");
		name = intent.getStringExtra("Name");
		album = intent.getStringExtra("Album");
		imagesource = intent.getStringExtra("ImageSource");
		coUrl = intent.getStringExtra("CoUrl");
		roUrl = intent.getStringExtra("RoUrl");
		deviceCert = intent.getStringExtra("DeviceCert");
		deviceKey = intent.getStringExtra("DeviceKey");
		type = intent.getStringExtra("Type");

		mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE); 
		mServiceList = mActivityManager.getRunningServices(30);
		
		if (name != null) {

			if(isServiceRunning(mServiceList)){
				
				Log.d("Service is Running in Act = 1", "!!!!!!!!!!!!!!!!!");
				stopThread();
			}
			
			filepath = jiemi(coUrl, roUrl, deviceCert, deviceKey, type);
			
		} else if (beforeAct == 1) {

			if (beforeActList == 1) {

				if (playlist.size() != 0) {

					if(isServiceRunning(mServiceList)){
						
						Log.d("Service is Running in Act = 1", "!!!!!!!!!!!!!!!!!");
						stopThread();
					}
					
					playContent = 1;

					songid = intent.getIntExtra("position", 0);

					Log.d("position", songid + "-1");
					HashMap<String, String> hashMap = playlist.get(songid);
					singer = hashMap.get("Singer");
					name = hashMap.get("Name");
					album = hashMap.get("Album");
					imagesource = hashMap.get("ImageSource");
					coUrl = hashMap.get("CoUrl");
					roUrl = hashMap.get("RoUrl");
					deviceCert = hashMap.get("DeviceCert");
					deviceKey = hashMap.get("DeviceKey");
					type = hashMap.get("Type");
					
					filepath = jiemi(coUrl, roUrl, deviceCert, deviceKey, type);
					
				} else {
					bn3.setImageResource(R.drawable.play);
				}
			}
		} else {
			AReceiver();
			bn3.setImageResource(R.drawable.pause);
		}

		// ---------------------------------------------------------------------------------------------------------
		vv = (VideoView) findViewById(R.id.vv);
		vv.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				vv.stopPlayback();
				isOnline = false;
				new AlertDialog.Builder(AudioPlayerActivity.this)
						.setTitle(R.string.warn)
						.setMessage(R.string.player_type_error)
						.setPositiveButton(R.string.sure,
								new AlertDialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										vv.stopPlayback();

									}

								}).setCancelable(false).show();

				return false;
			}

		});

		vv.setMySizeChangeLinstener(new MySizeChangeLinstener() {

			public void doMyThings() {
				// TODO Auto-generated method stub
				setVideoScale(SCREEN_DEFAULT);
			}

		});

		getScreenSize();
		// ---------------------------------------------------------------------------------------------------------
		bn3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isPlay) {
					Intent intent1 = new Intent(AudioPlayerActivity.this,
							MServier.class);
					intent1.putExtra("MSG", 1);
					startService(intent1);
					bn3.setImageResource(R.drawable.play);
				} else {
					Intent intent1 = new Intent(AudioPlayerActivity.this,
							MServier.class);
					intent1.putExtra("MSG", 2);
					startService(intent1);
					bn3.setImageResource(R.drawable.pause);
				}
				isPlay = !isPlay;
			}
		});

		backforwardbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (playContent == 0) {
					Toast.makeText(AudioPlayerActivity.this,
							"播放列表为空！！！", Toast.LENGTH_SHORT).show();
				} else {
					if (playlist.size() != 0) {
						playback();
					} else {
						Toast.makeText(AudioPlayerActivity.this,
								"播放列表为空！！！", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		forwardbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (playContent == 0) {
					Toast.makeText(AudioPlayerActivity.this,
							"播放列表为空！！！", Toast.LENGTH_SHORT).show();
				} else {
					if (playlist.size() != 0) {
						playnext();
					} else {
						Toast.makeText(AudioPlayerActivity.this,
								"播放列表为空！！！", Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		});

		close.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AudioPlayerActivity.this,
						MServier.class);
				stopService(intent);
				killMark();
				i = songtime + 5;
				playContent = 0;
				finish();
			}
		});

		listbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				starthoutai();
				showNotification();
				i = songtime + 5;
				Intent intent = new Intent(AudioPlayerActivity.this,
						SongsListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		gecibutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(albumImageDisplayed == 0){
					imageView.setImageBitmap(bm);
					albumImageDisplayed = 1;
					gecibutton.setImageResource(R.drawable.geci2);
				}else {
					imageView.setImageBitmap(null);
					albumImageDisplayed = 0;
					gecibutton.setImageResource(R.drawable.geci);
				}
			}
		});

		xunhuanbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playState = 0;
				Message msg = new Message();
				msg.what = 5;
				handler.sendMessage(msg);
				Toast.makeText(AudioPlayerActivity.this, "顺序播放",
						Toast.LENGTH_SHORT).show();
			}
		});

		wuxubutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playState = 1;
				Message msg = new Message();
				msg.what = 6;
				handler.sendMessage(msg);
				Toast.makeText(AudioPlayerActivity.this, "随机播放",
						Toast.LENGTH_SHORT).show();
			}
		});

		HouTai.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				starthoutai();
				finish();
				showNotification();
				Toast.makeText(AudioPlayerActivity.this,
						"后台播放已开启，用户可从顶部通知栏放回播放/暂停控制", Toast.LENGTH_SHORT).show();
			}
		});
		// ------------------------------------------------------------------------
		soundbar = (SeekBar) controlView.findViewById(R.id.soundbar);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		setVolum();
		soundbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							progress, 0);
				}
			}

			public void onStartTrackingTouch(SeekBar arg0) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		// ---------------------------------------------------------------------------

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					i = progress;
					Intent intent = new Intent(AudioPlayerActivity.this,
							MServier.class);
					intent.putExtra("progress", progress);
					startService(intent);
				}
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
	}

	// ---------------------------------------------------------------------
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		controlHeight = screenHeight;
	}

	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;

	private void setVideoScale(int flag) {
		@SuppressWarnings("unused")
		LayoutParams lp = vv.getLayoutParams();

		switch (flag) {
		case SCREEN_FULL:
			Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: "
					+ screenHeight);
			vv.setVideoScale(screenWidth, screenHeight);
			break;
		case SCREEN_DEFAULT:
			int videoWidth = vv.getVideoWidth();
			int videoHeight = vv.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - 25;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					mWidth = mHeight * videoWidth / videoHeight;
				} else {
				}
			}
			vv.setVideoScale(mWidth, mHeight);
			break;
		}
	}

	private void setVolum() {
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundbar.setMax(maxVolume);
		currentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundbar.setProgress(currentVolume);
	}

	// -------------------------------------------------------------------------------------

	public void playnext() {
		
		if(playlist.size() != 0){
			isPlay = true;
			
			Message msg = new Message();
			msg.what = 4;
			handler.sendMessage(msg);
			
			i = songtime + 5;
			
			if (playState == 0) {
				if (songid >= playlist.size() - 1) {
					songid = 0;
				} else {
					songid = songid + 1;
				}
				
			} else {
				if(playlist.size() == 1){
					songid = 0;
				}else{
					int size = playlist.size();
					Random random = new Random();
					int n = random.nextInt(size - 1);
					songid = n;
				}
			}
			setinfo();
		}else {
			Toast.makeText(AudioPlayerActivity.this,
					"播放列表为空!!!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void playback() {
		
		if(playlist.size() != 0){
			isPlay = true;
			
			Message msg = new Message();
			msg.what = 4;
			handler.sendMessage(msg);
			
			i = songtime + 5;
			
			if (playState == 0) {
				if (songid == 0) {
					songid = playlist.size() - 1;
				} else {
					songid = songid - 1;
				}

			} else {
				if(playlist.size() == 1){
					songid = 0;
				}else{
					int size = playlist.size();
					Random random = new Random();
					int n = random.nextInt(size - 1);
					songid = n;
				}
			}
			setinfo();
		}else {
			Toast.makeText(AudioPlayerActivity.this,
					"播放列表为空！！！", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void playsong(String filepath) {

		Intent Service = new Intent();
		Service.setClass(AudioPlayerActivity.this, MServier.class);
		stopService(Service);
		Service.putExtra("filepath", filepath);
		Service.putExtra("name", name);
		Service.putExtra("roUrl", roUrl);
		Service.putExtra("singer", singer);
		Service.putExtra("album", album);
		Service.putExtra("ImageSource", imagesource);
		Log.d("serviceQidong", "------------");
		startService(Service);
	}

	class FileThread extends Thread {
		@Override
		public void run() {

			Log.d("i", "before thread:" + i);

			while (i <= songtime) {
				Message msg = new Message();
				msg.what = 1;
				if (isPlay) {
					msg.arg1 = i;
					i++;
					songtime1 = songtime - i + 1;
					handler.sendMessage(msg);
					Log.d("i-------", i + "");
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (playContent == 0 && i == songtime + 1) {
				Log.d("one song------>", "over!");
				if(roUrl != null)
					deleteFilepath();
				finish();
			}else if(playContent == 1 && i == songtime + 1 && playlist.size() != 0){
				if(roUrl != null)
					deleteFilepath();
				playnext();
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int wt = msg.what;
			switch (wt) {
			case 1:
				int q = msg.arg1;
				seekBar.setProgress(q);
				playedTextView.setText(String.format("%02d:%02d:%02d", q/60/60,q/60, q%60));
				minute = songtime1 / 60;
				hour = minute / 60;
				second = songtime1 % 60;
				minute %= 60;
				durationTextView.setText(String.format("%02d:%02d:%02d", hour,minute, second));
				break;
			case 2:
				progressDialog = ProgressDialog.show(AudioPlayerActivity.this,"读取文件", "正在加载文件中，请稍候·····", true);
				break;
			case 3:
				progressDialog.dismiss();
				break;
			case 4:
				bn3.setImageResource(R.drawable.play);
				break;
			case 5:
				shunxu.setImageResource(R.drawable.shunxu2);
				wuxu.setImageResource(R.drawable.wuxu);
				break;
			case 6:
				shunxu.setImageResource(R.drawable.shunxu);
				wuxu.setImageResource(R.drawable.wuxu2);
				break;
			}
		}
	};

	public boolean isServiceRunning(
		List<ActivityManager.RunningServiceInfo> mServiceList) {
		
			for (int i = 0; i < mServiceList.size(); i++) {
				if ("com.rdweiba.main.com.multimedia.main.player.MServier"
						.equals(mServiceList.get(i).service.getClassName())) {
					return true;
				}
			}
		return false;
	}

	public BroadcastReceiver receiver1 = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("FirstQiDong")) {
				songtime = intent.getIntExtra("songtime", 0);
				Log.d("songTime.....------->", "" + songtime);
				
				album = intent.getStringExtra("album");
				audio_album.setText(album);
				name = intent.getStringExtra("name");
				audio_name.setText(name);
				singer = intent.getStringExtra("singer");
				audio_author.setText(singer);
				imagesource = intent.getStringExtra("ImageSource");
				bm = BitmapFactory.decodeFile(imagesource);
				roUrl = intent.getStringExtra("roUrl");
				bn3.setImageResource(R.drawable.pause);
				seekBar.setMax(songtime);
				FileThread fileThread = new FileThread();
				fileThread.start();
			}
			if (intent.getAction().equals("back-info")) {
				System.out.println("back i---->" + intent.getAction());
				songtime = intent.getIntExtra("songtime", 0);
				seekBar.setMax(intent.getIntExtra("songtime", 0));
				album = intent.getStringExtra("album");
				audio_album.setText(album);
				name = intent.getStringExtra("name");
				audio_name.setText(name);
				singer = intent.getStringExtra("singer");
				audio_author.setText(singer);
				imagesource = intent.getStringExtra("ImageSource");
				roUrl = intent.getStringExtra("roUrl");
				bm = BitmapFactory.decodeFile(imagesource);
				i = intent.getIntExtra("i", 0);
				isPlay = intent.getBooleanExtra("isplay", true);
				if(isPlay){
					bn3.setImageResource(R.drawable.pause);
				}else {
					bn3.setImageResource(R.drawable.play);
				}
				System.out.println("playerTime--->" + songtime);
				System.out.println("back i--->" + i);
				FileThread fileThread = new FileThread();
				fileThread.start();
			}
		}
	};

	protected void onStart() {

		IntentFilter filter1 = new IntentFilter("FirstQiDong");
		registerReceiver(this.receiver1, filter1);

		IntentFilter filter = new IntentFilter("backinfo");
		registerReceiver(this.receiver1, filter);

		IntentFilter filter3 = new IntentFilter("back-info");
		registerReceiver(this.receiver1, filter3);
		
		IntentFilter filter4 = new IntentFilter("stop-thread");
		registerReceiver(this.receiver1, filter4);

		super.onStart();
	}

	public void AReceiver() {
		Intent intent = new Intent("backinfo");
		sendBroadcast(intent);
		Log.d("sendBroadcast", "Give me i!!!");
	}
	
	public void stopThread() {
		Intent intent = new Intent("stop-thread");
		sendBroadcast(intent);
		Log.d("sendBroadcast", "stop-thread!!!");
	}

	private void showNotification() {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.icon,
				"Ibook播放器", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;
		CharSequence contentTitle = "Ibook正在播放……"; // 通知栏标题
		CharSequence contentText = name + "正在播放……"; // 通知栏内容
		Intent notificationIntent = new Intent(AudioPlayerActivity.this,AudioPlayerActivity.class); // 点击该通知后要跳转的Activity
		PendingIntent contentItent = PendingIntent.getActivity(
				AudioPlayerActivity.this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(AudioPlayerActivity.this, contentTitle,
				contentText, contentItent);

		notificationManager.notify(0, notification);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == 4) {
			Intent intent = new Intent(AudioPlayerActivity.this, MServier.class);
			stopService(intent);
			killMark();
			playContent = 0;
			i = songtime + 5;
			
		}
		return super.onKeyDown(keyCode, event);
	}

	public String jiemi(final String coUrl, final String roUrl, final String deviceCert,
			final String deviceKey, final String type) {
		
		 Message msg = new Message();
		 msg.what = 2;
		 handler.sendMessage(msg);
		 new Thread(){
			 
			 public void run(){
				 
				 try{
					 
					 Const.isCode = true;
					 filepath = DRMAgentUtil.getInstance().decryptFile(coUrl, roUrl, deviceCert, deviceKey, type);
					 
				 } catch (Exception e)
				 {
						 e.printStackTrace();
				 } finally
				 {
					 Message msg = new Message();
					 msg.what = 3;
					 handler.sendMessage(msg);
				 	 i = 0;
				 	 playsong(filepath);
				 }
			 }
		 }.start();

		return filepath;
	}

	public void setinfo() {

		HashMap<String, String> hashMap = playlist.get(songid);

		singer = hashMap.get("Singer");
		name = hashMap.get("Name");
		album = hashMap.get("Album");
		imagesource = hashMap.get("ImageSource");
		coUrl = hashMap.get("CoUrl");
		roUrl = hashMap.get("RoUrl");
		deviceCert = hashMap.get("DeviceCert");
		deviceKey = hashMap.get("DeviceKey");
		type = hashMap.get("Type");

		filepath = jiemi(coUrl, roUrl, deviceCert, deviceKey, type);
	}

	public void starthoutai() {
		Intent intent = new Intent(AudioPlayerActivity.this, MServier.class);
		intent.putExtra("i", i);
		intent.putExtra("playcontent", playContent);
		intent.putExtra("isplay", isPlay);
		intent.putExtra("name", name);
		intent.putExtra("singer", singer);
		intent.putExtra("album", album);
		intent.putExtra("roUrl", roUrl);
		intent.putExtra("ImageSource", imagesource);
		intent.putExtra("playstate", playState);
		intent.putExtra("songid", songid);
		startService(intent);
		i = songtime + 5;
	}

	protected void onDestroy() {
		unregisterReceiver(this.receiver1);
		super.onDestroy();
	}
	
	public void killMark(){
		NotificationManager notificationManager = (NotificationManager) this 
                .getSystemService(NOTIFICATION_SERVICE); 
        notificationManager.cancel(0);
	}
	
	void deleteFilepath(){
		try {
			File file = new File(filepath);
			file.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
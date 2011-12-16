package com.rdweiba.main.com.multimedia.main.player;

import java.util.HashMap;

import org.zjreader.formats.dcf.DRMAgentUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;

import com.rdweiba.main.Const;
import com.rdweiba.main.R;

public class NService extends Service {

	private AudioPlayerActivity audioPlayerActivity;
	
	private String name;
	private String singer;
	private String album;
	private String filepath;
	private String imagesource;
	
	private String coUrl;
	private String roUrl;
	private String type;
	private String deviceCert;
	private String deviceKey;

	private int songid = 0;
	
	private boolean playForMe = true;
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ZhuCe();
	}

	private BroadcastReceiver receiver3 = new BroadcastReceiver() {
		@SuppressWarnings("static-access")
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("kill and survive myself")) {
				
				songid = intent.getIntExtra("songid", 0);
				
				if (songid >= audioPlayerActivity.playlist.size() - 1) {
					songid = 0;
				} else {
					songid = songid + 1;
				}
				
				setInfo(songid);
				
				jiemi(coUrl, roUrl, deviceCert, deviceKey, type);
				
				Intent mService = new Intent(NService.this, MServier.class);
				stopService(mService);
				mService.putExtra("songid", songid);
				mService.putExtra("album", album);
				mService.putExtra("singer", singer);
				mService.putExtra("name", name);
				mService.putExtra("filepath", filepath);
				mService.putExtra("playforme", playForMe);
				mService.putExtra("playcontent", 1);
				mService.putExtra("ImageSource", imagesource);
				mService.putExtra("roUrl", roUrl);
				startService(mService);
				showNotification();
			}
		}
	};
	
	public void setInfo(int id){
		
		@SuppressWarnings("static-access")
		HashMap<String, String> hashMap = audioPlayerActivity.playlist.get(id);
		
		singer = hashMap.get("Singer");
		name = hashMap.get("Name");
		album = hashMap.get("Album");
		imagesource = hashMap.get("ImageSource");
		coUrl = hashMap.get("CoUrl");
		roUrl = hashMap.get("RoUrl");
		deviceCert = hashMap.get("DeviceCert");
		deviceKey = hashMap.get("DeviceKey");
		type = hashMap.get("Type");
	}
	
	public String jiemi(final String coUrl, final String roUrl, final String deviceCert,
			final String deviceKey, final String type) {
		
		 new Thread(){
			 
			 public void run(){
				 
				 try{
					 
					 Const.isCode = true;
					 filepath = DRMAgentUtil.getInstance().decryptFile(coUrl, roUrl, deviceCert, deviceKey, type);
					 
				 } catch (Exception e)
				 {
						 e.printStackTrace();
				 }
			 }
		 }.start();

		return filepath;
	}
	
	public void ZhuCe() {
		IntentFilter filter = new IntentFilter("kill and survive myself");
		registerReceiver(this.receiver3, filter);
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
		Intent notificationIntent = new Intent(NService.this,AudioPlayerActivity.class); // 点击该通知后要跳转的Activity
		PendingIntent contentItent = PendingIntent.getActivity(
				NService.this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(NService.this, contentTitle,
				contentText, contentItent);

		notificationManager.notify(0, notification);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.rdweiba.main;

import com.rdweiba.main.com.multimedia.main.player.AudioPlayerActivity;
import com.rdweiba.main.com.multimedia.main.player.MServier;
import com.rdweiba.main.com.multimedia.main.player.NService;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity implements OnClickListener
{
	/** Called when the activity is first created. */

	private Button epub_btn;
	private Button pdf_btn;
	private Button mp4_btn;
	private Button epub1_btn;
	private Button pdf1_btn;
	private Button mp41_btn;
	private Button mp3_btn;
	private Button txt_btn;
	private Button html_btn;
	String ProductGuid = "1";
	String RoUrl;
	String CoUrl;
	String Type;
	String DeviceCert;
	String DeviceKey;
	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	private AudioPlayerActivity audioPlayerActivity;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main22);
		epub_btn = (Button) findViewById(R.id.epub);
		pdf_btn = (Button) findViewById(R.id.pdf);
		mp4_btn = (Button) findViewById(R.id.mp4);
		epub1_btn = (Button) findViewById(R.id.epub1);
		pdf1_btn = (Button) findViewById(R.id.pdf1);
		mp41_btn = (Button) findViewById(R.id.mp41);
		mp3_btn = (Button) findViewById(R.id.mp3);

		epub_btn.setOnClickListener(this);
		pdf_btn.setOnClickListener(this);
		mp4_btn.setOnClickListener(this);
		epub1_btn.setOnClickListener(this);
		pdf1_btn.setOnClickListener(this);
		mp41_btn.setOnClickListener(this);
		mp3_btn.setOnClickListener(this);
		// txt_btn.setOnClickListener(this);
		// html_btn.setOnClickListener(this);
	}

	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.epub:
			ProductGuid = "1";
			RoUrl = path + "/reader/epub/1.oro";
			CoUrl = path + "/reader/epub/1.dcf";
			Type = ".epub";
			DeviceCert = path + "/reader/epub/DeviceCert";
			DeviceKey = path + "/reader/epub/DeviceKey";
			break;
		case R.id.mp4:
			ProductGuid = "2";
			RoUrl = path + "/reader/mp4/1.oro";
			CoUrl = path + "/reader/mp4/1.dcf";
			Type = ".mp4";
			DeviceCert = path + "/reader/mp4/DeviceCert";
			DeviceKey = path + "/reader/mp4/DeviceKey";
			break;
		case R.id.pdf:
			ProductGuid = "3";
			CoUrl = path + "/reader/pdf/1.dcf";
			RoUrl = path + "/reader/pdf/1.oro";
			Type = ".pdf";
			DeviceCert = path + "/reader/pdf/DeviceCert";
			DeviceKey = path + "/reader/pdf/DeviceKey";
			break;
		case R.id.epub1:
			ProductGuid = "1";
			CoUrl = path + "/reader/epub1/1.epub";
			RoUrl = "";
			Type = ".epub";
			DeviceCert = "";
			DeviceKey = "";
			break;
		case R.id.mp41:
			ProductGuid = "2";
			CoUrl = path + "/reader/mp41/1.mp4";
			RoUrl = "";
			Type = ".mp4";
			DeviceCert = "";
			DeviceKey = "";
			break;
		case R.id.pdf1:
			ProductGuid = "3";
			CoUrl = path + "/reader/pdf1/1.pdf";
			RoUrl = "";
			Type = ".pdf";
			DeviceCert = "";
			DeviceKey = "";
			break;
		case R.id.mp3:
			ProductGuid = "3";
			CoUrl = path + "/reader/mp3/1.mp3";
			RoUrl = "";
			Type = ".mp3";
			DeviceCert = "";
			DeviceKey = "";
			break;
		default:
			break;
		}
		
		if (arg0.getId() == R.id.mp3) {
			Intent intent = new Intent();
			intent.setClass(TestActivity.this, AudioPlayerActivity.class);
			intent.putExtra("Name", "1");
			intent.putExtra("Singer", "1");
			intent.putExtra("Album", "1");
			intent.putExtra("ImageSource", path + "/reader/mp3/1.png");
			startActivity(intent);
		}else {
			Intent intent = new Intent();
			intent.putExtra("ProductGuid", ProductGuid);
			intent.putExtra("CoUrl", CoUrl);
			intent.putExtra("RoUrl", RoUrl);
			intent.putExtra("DeviceCert", DeviceCert);
			intent.putExtra("DeviceKey", DeviceKey);
			intent.putExtra("Type", Type);
			intent.setClass(TestActivity.this, IbookActivity.class);
			startActivity(intent);
		}
	}
	
	@SuppressWarnings("static-access")
	protected void onDestroy() {

		Intent intent = new Intent(TestActivity.this, MServier.class);
		stopService(intent);
		
		audioPlayerActivity.playContent = 0;
		
		Intent intent1 = new Intent(TestActivity.this, NService.class);
		stopService(intent1);
		
		NotificationManager notificationManager = (NotificationManager) this 
                .getSystemService(NOTIFICATION_SERVICE); 
        notificationManager.cancel(0);
		
		super.onDestroy();
	}


}

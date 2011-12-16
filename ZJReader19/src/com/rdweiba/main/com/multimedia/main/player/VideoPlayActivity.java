package com.rdweiba.main.com.multimedia.main.player;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.rdweiba.main.R;

public class VideoPlayActivity extends Activity implements 
	MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener
{
	public static final String TAG = "mohoplayer";
	
	private VideoView mVideoView;
	private Uri mUri;
	private int mPositionWhenPaused = -1;
	private MediaController mMediaController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if (findViewById(R.id.vv) == null){
			Log.v("test", "null");
		}
		mVideoView = (VideoView)findViewById(R.id.vv);
		
		Bundle bundle = this.getIntent().getExtras();
		String filepath = bundle.getString("file");
		mUri = Uri.parse(filepath);
		
		mMediaController = new MediaController(this);
		mVideoView.setMediaController(mMediaController);
		
		
	}


	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}


	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	@Override
	protected void onResume() {
		if (mPositionWhenPaused > 0){
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}
		
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		mVideoView.setVideoURI(mUri);
		mVideoView.start();
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mPositionWhenPaused = mVideoView.getCurrentPosition();
		mVideoView.stopPlayback();
		super.onPause();
	}
	
	
}

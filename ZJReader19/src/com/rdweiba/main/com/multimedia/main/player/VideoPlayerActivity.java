package com.rdweiba.main.com.multimedia.main.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.rdweiba.main.Const;
import com.rdweiba.main.R;
import com.rdweiba.main.TestActivity;
import com.rdweiba.main.com.multimedia.main.player.VideoView.MySizeChangeLinstener;

public class VideoPlayerActivity extends Activity
{

	private final static String TAG = "VideoPlayerActivity";
	private boolean isOnline = false;
	private boolean isChangedVideo = false;
	private String filePath;
	
	private AudioPlayerActivity audioPlayerActivity;

	public static LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();

	public class MovieInfo
	{
		String displayName;
		String path;
	}

	private static int position;
	private int playedTime;
	private VideoView vv = null;
	private SeekBar seekBar = null;
	private SeekBar soundbar = null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private GestureDetector mGestureDetector = null;
	private AudioManager mAudioManager = null;

	private int maxVolume = 0;
	private int currentVolume = 0;
	private ImageButton bn2 = null;
	private ImageButton bn3 = null;
	private ImageButton close = null;
	private ImageButton fullscreen = null;

	private View controlView = null;
	private PopupWindow controler = null;
	private PopupWindow mSoundWindow = null;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int controlHeight = 0;
	private final static int TIME = 6868;
	private boolean isControllerShow = true;
	private boolean isPaused = false;
	private boolean isFullScreen = false;

	/** Called when the activity is first created. */
	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.player);
		Log.d("OnCreate", getIntent().toString());
		Looper.myQueue().addIdleHandler(new IdleHandler()
		{
			public boolean queueIdle()
			{
				if (controler != null && vv.isShown())
				{
					controler.showAtLocation(vv, Gravity.TOP, 0, 0);
					controler.update(0, 0, screenWidth, controlHeight);
				}
				return false;
			}
		});

		Intent intent9 = new Intent(VideoPlayerActivity.this, MServier.class);
		stopService(intent9);
		
		audioPlayerActivity.playContent = 0;
		
		NotificationManager notificationManager = (NotificationManager) this 
                .getSystemService(NOTIFICATION_SERVICE); 
        notificationManager.cancel(0);
		
		controlView = getLayoutInflater().inflate(R.layout.controler, null);
		controler = new PopupWindow(controlView);
		durationTextView = (TextView) controlView.findViewById(R.id.duration);
		playedTextView = (TextView) controlView.findViewById(R.id.has_played);
		position = -1;
		bn2 = (ImageButton) controlView.findViewById(R.id.backforwardbutton);
		bn3 = (ImageButton) controlView.findViewById(R.id.playbutton);
		close = (ImageButton) controlView.findViewById(R.id.back);
		fullscreen = (ImageButton) controlView.findViewById(R.id.fullscreen);

		vv = (VideoView) findViewById(R.id.vv);

		vv.setOnErrorListener(new OnErrorListener()
		{

			public boolean onError(MediaPlayer mp, int what, int extra)
			{

				vv.stopPlayback();
				isOnline = false;

				new AlertDialog.Builder(VideoPlayerActivity.this).setTitle(R.string.warn).setMessage(R.string.player_type_error)
						.setPositiveButton(R.string.sure, new AlertDialog.OnClickListener()
						{

							public void onClick(DialogInterface dialog, int which)
							{

								vv.stopPlayback();

							}

						}).setCancelable(false).show();

				return false;
			}

		});

		// <<更新部分
		Intent intent = this.getIntent();
		filePath = intent.getStringExtra("file");

		FileInputStream inputStream = null;

		try
		{
			inputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e1)
		{
			Log.e("@ZZZ", "filepath not FOUND!!!", e1);
		}
		if (inputStream != null)
		{

			vv.stopPlayback();
			vv.setVideoInputStream(inputStream);
			isOnline = true;
			bn3.setImageResource(R.drawable.pause);
		} else
		{
			bn3.setImageResource(R.drawable.play);
		}

		vv.setMySizeChangeLinstener(new MySizeChangeLinstener()
		{

			public void doMyThings()
			{
				// TODO Auto-generated method stub
				setVideoScale(SCREEN_DEFAULT);
			}
		});

		bn2.setAlpha(0xBB);
		bn3.setAlpha(0xBB);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		bn3.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				cancelDelayHide();
				if (isPaused)
				{
					vv.start();
					bn3.setImageResource(R.drawable.pause);
					hideControllerDelay();
				} else
				{
					vv.pause();
					bn3.setImageResource(R.drawable.play);
				}
				isPaused = !isPaused;
			}
		});

		bn2.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				isOnline = false;
				if (--position >= 0)
				{
					vv.setVideoPath(playList.get(position).path);
					cancelDelayHide();
					hideControllerDelay();
				} else
				{
					VideoPlayerActivity.this.finish();
				}
			}

		});

		fullscreen.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (isFullScreen)
				{
					setVideoScale(SCREEN_DEFAULT);
				} else
				{
					setVideoScale(SCREEN_FULL);
				}
				isFullScreen = !isFullScreen;
			}
		});

		close.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (Const.isCode == true)
				{
					File f = new File(filePath);
					f.delete();
				}
				finish();
			}
		});

		soundbar = (SeekBar) controlView.findViewById(R.id.soundbar);
		setVolum();
		soundbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser)
			{
				// TODO Auto-generated method stub
				if (fromUser)
				{
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				}
			}

			public void onStartTrackingTouch(SeekBar arg0)
			{
				myHandler.removeMessages(HIDE_CONTROLER);
			}

			public void onStopTrackingTouch(SeekBar seekBar)
			{

				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});

		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser)
			{
				// TODO Auto-generated method stub
				if (fromUser)
				{
					vv.seekTo(progress);
				}
			}

			public void onStartTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				myHandler.removeMessages(HIDE_CONTROLER);
			}

			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});

		getScreenSize();

		mGestureDetector = new GestureDetector(new SimpleOnGestureListener()
		{

			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				// TODO Auto-generated method stub
				if (isFullScreen)
				{
					setVideoScale(SCREEN_DEFAULT);
				} else
				{
					setVideoScale(SCREEN_FULL);
				}
				isFullScreen = !isFullScreen;
				Log.d(TAG, "onDoubleTap");

				if (isControllerShow)
				{
					showController();
				}
				// return super.onDoubleTap(e);
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				// TODO Auto-generated method stub
				if (!isControllerShow)
				{
					showController();
					hideControllerDelay();
				} else
				{
					cancelDelayHide();
					hideController();
				}
				// return super.onSingleTapConfirmed(e);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e)
			{
				// TODO Auto-generated method stub
				if (isPaused)
				{
					vv.start();
					bn3.setImageResource(R.drawable.pause);
					cancelDelayHide();
					hideControllerDelay();
				} else
				{
					vv.pause();
					bn3.setImageResource(R.drawable.play);
					cancelDelayHide();
					showController();
				}
				isPaused = !isPaused;
				// super.onLongPress(e);
			}
		});

		vv.setOnPreparedListener(new OnPreparedListener()
		{

			public void onPrepared(MediaPlayer arg0)
			{
				// TODO Auto-generated method stub

				setVideoScale(SCREEN_DEFAULT);
				isFullScreen = false;
				if (isControllerShow)
				{
					showController();
				}

				int i = vv.getDuration();
				Log.d("onCompletion", "" + i);
				seekBar.setMax(i);
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
				vv.start();
				bn3.setImageResource(R.drawable.pause);
				hideControllerDelay();
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		});

		vv.setOnCompletionListener(new OnCompletionListener()
		{

			public void onCompletion(MediaPlayer arg0)
			{
				// TODO Auto-generated method stub
				int n = playList.size();
				isOnline = false;
				if (++position < n)
				{
					vv.setVideoPath(playList.get(position).path);
				} else
				{
					vv.stopPlayback();
					VideoPlayerActivity.this.finish();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == Activity.RESULT_OK)
		{

			vv.stopPlayback();

			int result = data.getIntExtra("CHOOSE", -1);
			Log.d("RESULT", "" + result);
			if (result != -1)
			{
				isOnline = false;
				isChangedVideo = true;
				vv.setVideoPath(playList.get(result).path);
				position = result;
			} else
			{
				String url = data.getStringExtra("CHOOSE_URL");
				if (url != null)
				{
					vv.setVideoPath(url);
					isOnline = true;
					isChangedVideo = true;
				}
			}

			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;

	Handler myHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub

			switch (msg.what)
			{

			case PROGRESS_CHANGED:

				int i = vv.getCurrentPosition();
				seekBar.setProgress(i);

				if (isOnline)
				{
					int j = vv.getBufferPercentage();
					seekBar.setSecondaryProgress(j * seekBar.getMax() / 100);
				} else
				{
					seekBar.setSecondaryProgress(0);
				}

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				playedTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
				break;

			case HIDE_CONTROLER:
				hideController();
				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub

		boolean result = mGestureDetector.onTouchEvent(event);

		if (!result)
		{
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
			}
			result = super.onTouchEvent(event);
		}

		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub

		getScreenSize();
		if (isControllerShow)
		{
			cancelDelayHide();
			hideController();
			showController();
			hideControllerDelay();
		}

		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		playedTime = vv.getCurrentPosition();
		vv.pause();
		bn3.setImageResource(R.drawable.play);
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		if (!isChangedVideo)
		{
			vv.seekTo(playedTime);
			vv.start();
		} else
		{
			isChangedVideo = false;
		}
		if (vv.isPlaying())
		{
			bn3.setImageResource(R.drawable.pause);
			hideControllerDelay();
		}
		Log.d("REQUEST", "NEW AD !");
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub

		if (controler.isShowing())
		{
			controler.dismiss();
		}
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		if (vv.isPlaying())
		{
			vv.stopPlayback();
		}
		playList.clear();
		super.onDestroy();
	}

	private void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		controlHeight = screenHeight;
	}

	private void hideController()
	{
		if (controler.isShowing())
		{
			controler.update(0, 0, 0, 0);
			isControllerShow = false;
		}
	}

	private void hideControllerDelay()
	{
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	private void showController()
	{
		controler.update(0, 0, screenWidth, controlHeight);
		isControllerShow = true;
	}

	private void cancelDelayHide()
	{
		myHandler.removeMessages(HIDE_CONTROLER);
	}

	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;

	private void setVideoScale(int flag)
	{

		LayoutParams lp = vv.getLayoutParams();

		switch (flag)
		{
		case SCREEN_FULL:
			Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: " + screenHeight);
			vv.setVideoScale(screenWidth, screenHeight);
			break;
		case SCREEN_DEFAULT:
			int videoWidth = vv.getVideoWidth();
			int videoHeight = vv.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - 25;

			if (videoWidth > 0 && videoHeight > 0)
			{
				if (videoWidth * mHeight > mWidth * videoHeight)
				{
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight)
				{
					mWidth = mHeight * videoWidth / videoHeight;
				} else
				{
				}
			}
			vv.setVideoScale(mWidth, mHeight);
			break;
		}
	}

	private void setVolum()
	{

		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundbar.setMax(maxVolume);
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundbar.setProgress(currentVolume);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// TODO Auto-generated method stub
			if (Const.isCode == true)
			{
				File f = new File(filePath);
				f.delete();
			}
			this.finish();
		}
		return true;
	}
}
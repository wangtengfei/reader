package com.rdweiba.main;

import java.io.File;

import org.android.zjreader.ZJReader;
import org.zjreader.formats.dcf.DRMAgentUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

import com.rdweiba.main.com.multimedia.main.player.AudioPlayerActivity;
import com.rdweiba.main.com.multimedia.main.player.VideoPlayerActivity;
import com.rdweiba.main.pdfview.OpenFileActivity;

public class IbookActivity extends Activity
{
	private String productGuid;
	private String filePath;
	private String coUrl;
	private String roUrl;
	private String type;
	private String deviceCert;
	private String deviceKey;
	private ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.main1);
		getSdCardPath();
		Intent intent = getIntent();
		productGuid = intent.getStringExtra("ProductGuid");
		coUrl = intent.getStringExtra("CoUrl");
		roUrl = intent.getStringExtra("RoUrl");
		type = intent.getStringExtra("Type");

		if (coUrl.equals("") || coUrl == null)
		{
			AlertDialog.Builder build = new AlertDialog.Builder(this).setTitle(R.string.warn).setMessage(R.string.coisnull).setIcon(R.drawable.icon);
			build.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					IbookActivity.this.finish();
				}
			});
			build.show();
		} else if ((!type.equals(".epub")) && (!type.equals(".pdf")) && (!type.equals(".3gp")) && (!type.equals(".mp3")) && (!type.equals(".mp4")))
		{
			AlertDialog.Builder build = new AlertDialog.Builder(this).setTitle(R.string.unsupportformat).setMessage(R.string.filenotfound).setIcon(R.drawable.icon);
			build.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					IbookActivity.this.finish();
				}
			});
			build.show();
		} else
		{
			if (coUrl.endsWith(".dcf") && roUrl.endsWith(".oro"))
			{
				File file = new File(roUrl);
				if (!file.exists())
				{
					AlertDialog.Builder build = new AlertDialog.Builder(this).setTitle(R.string.warn).setMessage(R.string.roisnull).setIcon(R.drawable.icon);
					build.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int whichButton)
						{
							IbookActivity.this.finish();
						}
					});
					build.show();
				} else
				{
					deviceCert = intent.getStringExtra("DeviceCert");
					deviceKey = intent.getStringExtra("DeviceKey");
					try
					{
						Log.i("@DRM", "productGuid : " + productGuid);
						Log.i("@DRM", "coUrl : " + coUrl);
						Log.i("@DRM", "roUrl : " + roUrl);
						Log.i("@DRM", "deviceCert : " + deviceCert);
						Log.i("@DRM", "deviceKey : " + deviceKey);
						Log.i("@DRM", "type : " + type);
						progressDialog = ProgressDialog.show(IbookActivity.this, "读取文件", "正在加载文件中，请稍候·····", true);
						new Thread()
						{
							public void run()
							{
								try
								{
									Const.isCode = true;
									filePath = DRMAgentUtil.getInstance().decryptFile(coUrl, roUrl, deviceCert, deviceKey, type);
									Log.i("@DRM", "filePath : " + filePath);
									if (type.equals(".mp4") || type.equals(".3gp"))
									{
										Intent inten = new Intent();
										inten.putExtra("file", filePath);
										inten.setClass(IbookActivity.this, VideoPlayerActivity.class);
										startActivityForResult(inten, 2);
										finish();
									} else if (type.equals(".mp3"))
									{
										Intent inten = new Intent();
										inten.putExtra("file", filePath);
										inten.setClass(IbookActivity.this, AudioPlayerActivity.class);
										startActivityForResult(inten, 2);
										finish();
									} else if (type.equals(".pdf"))
									{
										Intent inten = new Intent();
										inten.putExtra("file", filePath);
										inten.setClass(IbookActivity.this, OpenFileActivity.class);
										startActivityForResult(inten, 1);
										finish();
									} else if (type.equals(".epub"))
									{
										Intent inten = new Intent();
										inten.putExtra("fileDir", filePath);
										inten.setClass(IbookActivity.this, ZJReader.class);
										startActivityForResult(inten, 5);
										finish();

									}

								} catch (Exception e)
								{
									e.printStackTrace();
								} finally
								{
									progressDialog.dismiss();
								}
							}
						}.start();

					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			} else
			{
				filePath = coUrl;
				Const.isCode = false;
				if (type.equals(".mp4") || type.equals(".3gp"))
				{
					Intent inten = new Intent();
					inten.putExtra("file", filePath);
					inten.setClass(IbookActivity.this, VideoPlayerActivity.class);
					startActivityForResult(inten, 2);
					finish();
				} else if (type.equals(".mp3"))
				{
					Intent inten = new Intent();
					inten.putExtra("file", filePath);
					inten.setClass(IbookActivity.this, AudioPlayerActivity.class);
					startActivityForResult(inten, 2);
					finish();
				} else if (type.equals(".pdf"))
				{
					Intent inten = new Intent();
					inten.putExtra("file", filePath);
					inten.setClass(IbookActivity.this, OpenFileActivity.class);
					startActivityForResult(inten, 1);
					finish();
				} else if (type.equals(".epub"))
				{
					Intent inten = new Intent();
					inten.putExtra("fileDir", filePath);
					inten.setClass(IbookActivity.this, ZJReader.class);
					startActivityForResult(inten, 5);
					finish();
				}
			}
		}

	}

	// 杩斿洖瀹㈡埛绔殑鍙傛暟
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case 5:
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				bundle.putString("BookMark", "");
				bundle.putString("ProductGuid", productGuid);
				data.setClass(IbookActivity.this, TestActivity.class);
				startActivity(data);
				this.finish();
			}
			break;
		}

		case 1:
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				bundle.putString("BookMark", "");
				bundle.putString("ProductGuid", productGuid);
				data.setClass(IbookActivity.this, TestActivity.class);
				startActivity(data);
				this.finish();
			}
			break;
		}

		case 2:
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				bundle.putString("BookMark", "");
				bundle.putString("ProductGuid", productGuid);
				data.setClass(IbookActivity.this, TestActivity.class);
				startActivity(data);
				this.finish();
			}
			break;
		}
		case 3:
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				bundle.putString("BookMark", "");
				bundle.putString("ProductGuid", productGuid);
				data.setClass(IbookActivity.this, TestActivity.class);
				startActivity(data);
				this.finish();
			}
			break;
		}
		case 4:
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				bundle.putString("BookMark", "");
				bundle.putString("ProductGuid", productGuid);
				data.setClass(IbookActivity.this, TestActivity.class);
				startActivity(data);
				this.finish();
			}
			break;
		}

		}
	}

	public void getSdCardPath()
	{
		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getParent() + java.io.File.separator + sdcardDir.getName();
		Const.filePath = path + File.separator + "gmrb" + File.separator + "gmym";
		System.out.println("__________" + Const.filePath);
		createFile();
	}

	public void createFile()
	{
		try
		{
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			{
				System.out.println("_______Directory________");
				File path = new File(Const.filePath);
				if (!path.exists())
				{
					path.mkdirs();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

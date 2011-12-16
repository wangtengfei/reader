package com.rdweiba.main.com.multimedia.main.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rdweiba.main.R;

public class Util
{
	public static ArrayList<String> combineArrayList(ArrayList<String> a1, ArrayList<String> a2)
	{
		for (String obj : a2)
		{
			a1.add(obj);
		}
		return a1;
	}

	public static Object convertfilesize(long fileSize)
	{
		String newSize = "";
		if (fileSize < 1024)
		{
			newSize = fileSize + "B";
		} else if (fileSize >= 1024 && fileSize < 1024 * 1024)
		{
			newSize = String.valueOf(fileSize / 1024) + "K";
		} else
		{
			DecimalFormat format = new DecimalFormat("0.00");
			String result = format.format((double) fileSize / (1024 * 1024));
			newSize = result + "M";
		}

		return newSize;
	}

	public static Bitmap getFileIcon(Context context, File file)
	{
		if (file.isDirectory())
		{
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
		}
		String fileName = file.getName();
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
		String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
		// if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
		// end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.music_style1);
		// }else if(end.equals("apk")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.apk);
		// }else if(end.equals("text")||end.equals("txt")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.text);
		// }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
		// end.equals("jpeg")||end.equals("bmp")||end.equals("ico")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.image);
		// }else if(end.equals("rar")||end.equals("zip")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.rar);
		// }else if(end.equals("jar")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.jar);
		// }else if(end.equals("rmvb")||end.equals("avi")||end.equals("mp4")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.media);
		// }else if(end.equals("doc")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.word);
		// }else if(end.equals("xls")){
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.excel);
		// }
		//
		// else{
		// bm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.unknow);
		// }

		return bm;
	}

}

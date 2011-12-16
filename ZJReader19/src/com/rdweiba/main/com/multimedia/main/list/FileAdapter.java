package com.rdweiba.main.com.multimedia.main.list;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdweiba.main.R;
import com.rdweiba.main.com.multimedia.main.util.Util;



public class FileAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> items;
	private ArrayList<String> paths;
	
	public FileAdapter(Context context,
			ArrayList<String> items, ArrayList<String> paths) {
		this.context = context;
		this.items   = items;
		this.paths   = paths;
	}

	
	public int getCount() {
		return items.size();
	}

	
	public Object getItem(int position) {
		return items.get(position);
	}

	
	public long getItemId(int position) {
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.simple_file, null);
			holder = new ViewHolder();
			holder.fileiv = (ImageView)convertView.findViewById(R.id.file_icon);
			holder.nametv = (TextView) convertView.findViewById(R.id.file_name);
			holder.sizetv = (TextView) convertView.findViewById(R.id.file_size);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		File f = new File(paths.get(position).toString());
		holder.nametv.setText(items.get(position).toString());
		holder.sizetv.setText(Util.convertfilesize(f.length()).toString());
		holder.fileiv.setImageBitmap(Util.getFileIcon(context,f));
		return convertView;
	}

	private class ViewHolder{
		ImageView fileiv;
		TextView nametv;
		TextView sizetv;	
	}
}

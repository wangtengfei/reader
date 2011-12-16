package com.rdweiba.main.com.multimedia.main.palylist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

public class BasePlaylist implements Playlist {
	protected Vector<PlaylistItem> v_playlist = null;
	protected int i_cursorPos = -1;
	protected boolean isModified = false;
	
	public BasePlaylist(){
		v_playlist = new Vector<PlaylistItem>();
	}
	
	
	public boolean isModified() {
		return isModified;
	}


	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}


	
	public void addItemAt(PlaylistItem pli, int pos) {
		v_playlist.insertElementAt(pli, pos);
		setModified(true);
	}

	public void appendItem(PlaylistItem pli) {
		v_playlist.add(pli);
		setModified(true);
	}

	
	public Collection<PlaylistItem> getAllItems() {
		// TODO Auto-generated method stub
		return null;
	}


	public PlaylistItem getItemAt(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getPlaylistsize() {
		// TODO Auto-generated method stub
		return v_playlist.size();
	}

	
	public boolean load(String filename) {
		setModified(true);
		BufferedReader br = null;
		boolean loaded = false;
		if ((filename != null) && (filename.toLowerCase().endsWith(".m3u")))
		{
			try {
				br = new BufferedReader(new FileReader(filename));
				String line = null;
				String songname = null;
				String songpath = null;
				while ((line = br.readLine())!= null){
					if (line.trim().length() == 0) continue;
					if (line.startsWith("#")){
						if (line.toUpperCase().startsWith("#EXTINF")){
							int indA = line.indexOf(",", 0);
							if (indA != -1 ){
								songname = line.substring(indA+1, line.length());
							}
						}
					}else{
						songpath = line;
						PlaylistItem pli = new PlaylistItem(songname, songpath);
						this.appendItem(pli);
					}
				}
				loaded = true;
				
			}catch(Exception e)
			{
				Log.v("playlist", e.getMessage());
			}finally{
				try {
					if (br != null)
						br.close();
				}catch (IOException e){
					Log.v("playlist", "can not load playlist!");
				}
			}
			
		}
		return loaded;
	}

	
	public void removeAllItems() {
		v_playlist.removeAllElements();
		i_cursorPos = -1;
		setModified(true);
	}

	
	public void removeItem(PlaylistItem pli) {
		v_playlist.remove(pli);
		setModified(true);
	}

	
	public void removeItemAt(int pos) {
		v_playlist.removeElementAt(pos);
		setModified(true);
	}

	
	public boolean save(String filename) {
		if (v_playlist != null){
			BufferedWriter bw = null;
			try{
				bw = new BufferedWriter(new FileWriter(filename));
				bw.write("#EXTM3U");
				bw.newLine();
				Iterator<PlaylistItem> it = v_playlist.iterator();
				while(it.hasNext()){
					PlaylistItem pli = (PlaylistItem)it.next();
					bw.write("#EXTINF:,"+pli.s_name);
					bw.newLine();
					bw.write(pli.s_location);
					bw.newLine();
				}
				return true;
			}catch(Exception e){
				Log.v("playlist", e.toString());
			}finally {
				try {
					if (bw!=null){
						bw.close();
					}
				}catch(Exception e){
					Log.v("playlist", "can not write m3u");
				}
			}
		}
		return false;
	}

	
	public void sortItems(int sortmode) {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<String> getItemsName(){
		if (v_playlist.size()==0){
			return null;
		}
		ArrayList<String> mArrayList = new ArrayList<String>();
		for (int i =0 ; i < v_playlist.size();i ++){
			Log.v("moholist", v_playlist.get(i).s_name);
			mArrayList.add(v_playlist.get(i).s_name);
		}
 
		return  mArrayList;
	}

}

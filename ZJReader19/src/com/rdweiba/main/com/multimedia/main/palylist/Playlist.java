package com.rdweiba.main.com.multimedia.main.palylist;

import java.util.Collection;

public interface Playlist 
{
	public boolean load(String filename);
	
	public boolean save(String filename);
	
	public void addItemAt(PlaylistItem pli,int pos);
	
	public void removeItem(PlaylistItem pli);
	
	public void removeItemAt(int pos);
	
	public void removeAllItems();
	
	public void appendItem(PlaylistItem pli);
	
	public void sortItems(int sortmode);
	
	public PlaylistItem getItemAt(int pos);
	
	public Collection<PlaylistItem> getAllItems();
	
	public int getPlaylistsize();

}

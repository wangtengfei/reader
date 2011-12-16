package com.rdweiba.main.com.multimedia.main.palylist;

public class PlaylistItem {
	protected String s_name = null;
	protected String s_location = null;
	public PlaylistItem(String sName, String sLocation) {
		super();
		s_name = sName;
		s_location = sLocation;
	}
	public String getS_name() {
		return s_name;
	}
	public void setS_name(String sName) {
		s_name = sName;
	}
	public String getS_location() {
		return s_location;
	}
	public void setS_location(String sLocation) {
		s_location = sLocation;
	}
	
	
}

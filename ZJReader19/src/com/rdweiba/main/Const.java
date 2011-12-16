package com.rdweiba.main;

import java.util.HashMap;
import java.util.List;

public class Const
{
	public static boolean isCode = true;
	public static final int TITLE_HEIGHT = 24;
	public static String progress;
	public static Boolean isPotrait = true;
	public static int speed = 10;
	public static String filePath = "";
	public static List<HashMap<String, String>> playlist;

	public static int setPlayList(List<HashMap<String, String>> arraylist)
	{
		playlist=arraylist;
		return 0;
	}

//	public static String[] getPlayList()
//	{
//		return null;
//	}
}

package com.rdweiba.main.pdfview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper
{
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "pdf.db";
	SQLiteDatabase db;
	Context context;

	public DBHelper(Context _context)
	{
		context = _context;
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
		String findtable = "select sql from sqlite_master where type='table' and tbl_name='pdfbookmark'";
		Cursor cs = db.rawQuery(findtable, null);
		if (cs.getCount() == 0)
		{
			CreateTable();
		}
		cs.close();
		Log.v(TAG, "db path=" + db.getPath());
	}

	public void CreateTable()
	{
		try
		{
			db.execSQL("CREATE TABLE pdfbookmark (" + "id INTEGER PRIMARY KEY autoincrement," + "title TEXT, date TEXT,path TEXT);");
			Log.v(TAG, "Create Table pdfbookmark ok");
		} catch (Exception e)
		{
			Log.v(TAG, "Create Table pdfbookmark,table exists.");
		}
	}

	public boolean save(String string, String date, String path)
	{
		String sql = "";
		try
		{
			sql = "insert into pdfbookmark(title,date,path) values('" + string + "','" + date + "','" + path + "')";
			db.execSQL(sql);
			Log.v(TAG, "insert Table pdfbookmark ok ,sql: " + sql);
			return true;
		} catch (Exception e)
		{
			Log.v(TAG, "insert Table pdfbookmark err ,sql: " + sql);
			return false;
		}
	}

	public boolean delete(String date, String path)
	{
		String sql = "";
		try
		{
			sql = "delete from pdfbookmark where path='" + path + "' and date='" + date + "'";
			db.execSQL(sql);
			Log.v(TAG, "delete ok ,sql: " + sql);
			return true;
		} catch (Exception e)
		{
			Log.v(TAG, "delete err ,sql: " + sql);
			return false;
		}
	}

	public Cursor findAll(String filepath)
	{
		Cursor cur = db.rawQuery("SELECT * FROM pdfbookmark where path=?", new String[]
		{ filepath });
		if (cur.moveToNext())
		{
		}
		return cur;
	}

	public void close()
	{

		db.close();
	}

}

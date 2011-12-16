


package org.android.zjreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.android.util.UIUtil;
import org.zjreader.library.Bookmark;
import org.zjreader.zjreader.FBView;
import org.zjreader.zjreader.ZJReaderApp;
import org.zlibrary.core.resources.ZLResource;

import android.content.Intent;

public class SelectionBookmarkAction extends FBAndroidAction {
	SelectionBookmarkAction(ZJReader baseApplication, ZJReaderApp fbreader) {
		super(baseApplication, fbreader);
	}

	public void run() {
		final FBView fbview = Reader.getTextView();
		final String text = fbview.getSelectedText();

		// new Bookmark(Reader.Model.Book, fbview.getModel().getId(),
		// fbview.getSelectionStartPosition(), text, true).save();
		fbview.clearSelection();
		Intent intent = new Intent(BaseActivity, AnnotationActivity.class);
		intent.putExtra("fileStr", text);
		intent.putExtra("title", Reader.Model.Book.getTitle());
		intent.putExtra("id", Reader.Model.Book.getId());
		BaseActivity.startActivity(intent);
		// UIUtil.showMessageText(BaseActivity, ZLResource.resource("selection")
		// .getResource("annotationCreated").getValue()
		// .replace("%s", text));
	}

	public static String readTxtFile(File filename) {
		String readStr = "";
		String read;
		FileReader fileread;
		BufferedReader bufread;
		try {
			fileread = new FileReader(filename);
			bufread = new BufferedReader(fileread);
			try {
				while ((read = bufread.readLine()) != null) {
					readStr = readStr + read + "\r\n";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("文件内容是:" + "\r\n" + readStr);
		return readStr;
	}

	public static void writeTxtFile(String newStr, File filename)
			throws IOException {
		// 先读取原有文件内容，然后进行写入操作
		String readStr = readTxtFile(filename);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				filename), "UTF-8");
		BufferedWriter writer = new BufferedWriter(osw);
		writer.write(readStr + newStr);
		writer.close();
		osw.close();
	}

	public static void creatTxtFile(File filename) throws IOException {
		if (!filename.exists()) {
			filename.createNewFile();
			System.err.println(filename + "已创建！");
		}
	}
}

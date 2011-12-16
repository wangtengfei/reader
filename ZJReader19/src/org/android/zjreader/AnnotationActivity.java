package org.android.zjreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.android.util.UIUtil;
import org.zlibrary.core.resources.ZLResource;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.rdweiba.main.R;


public class AnnotationActivity extends Activity implements OnClickListener {

	private String fileStr = "";
	private String title = "";
	private int id;
	private EditText et;
	private Button ok;
	private Button back;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.reader_annotation);
		fileStr = getIntent().getStringExtra("fileStr");
		title = getIntent().getStringExtra("title");
		id = getIntent().getIntExtra("id", 0);
		et = (EditText) findViewById(R.id.annotation_body);
		ok = (Button) findViewById(R.id.annotation_ok);
		back = (Button) findViewById(R.id.annotation_exit);
		et.setText("原文内容：\"" + fileStr + "\"\r\n我的批注：");
		ok.setOnClickListener(this);
		back.setOnClickListener(this);
	}
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.annotation_ok:
			String path = "/sdcard/reader_" + id + "_" + title + ".txt";
			String breakline = "\r\n---------------------------------------------------------------------------------------------------------\r\n";
			File filename = new File(path);
			try {
				creatTxtFile(filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.println(et.getText().toString() + breakline);
				writeTxtFile(et.getText().toString() + breakline, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UIUtil.showMessageText(this, ZLResource.resource("selection")
					.getResource("annotationCreated").getValue());
			finish();
			break;
		case R.id.annotation_exit:
			finish();
			break;

		default:
			break;
		}
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

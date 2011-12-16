package org.android.zjreader.crash;

import org.android.zjreader.ZJReader;
import org.zjreader.Paths;
import org.zlibrary.core.resources.ZLResource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rdweiba.main.R;






public class FixBooksDirectoryActivity extends Activity {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.books_directory_fix);

		final ZLResource resource = ZLResource.resource("crash").getResource("fixBooksDirectory");
		final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");

		setTitle(resource.getResource("title").getValue());

		final TextView textView = (TextView)findViewById(R.id.books_directory_fix_text);
		textView.setText(resource.getResource("text").getValue());

		final EditText directoryView = (EditText)findViewById(R.id.books_directory_fix_directory);
		directoryView.setText(Paths.BooksDirectoryOption().getValue());

		final Button okButton = (Button)findViewById(R.id.books_directory_fix_ok_button);
		okButton.setText(buttonResource.getResource("ok").getValue());
		okButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Paths.BooksDirectoryOption().setValue(directoryView.getText().toString());
				startActivity(new Intent(FixBooksDirectoryActivity.this, ZJReader.class));
				finish();
			}
		});

		final Button cancelButton = (Button)findViewById(R.id.books_directory_fix_cancel_button);
		cancelButton.setText(buttonResource.getResource("cancel").getValue());
		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}

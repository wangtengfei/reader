

package org.android.zjreader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rdweiba.main.R;



public class PopupWindow extends LinearLayout {
	public static enum Location {
		Bottom,
		Floating
	}

	private final Activity myActivity;

	public PopupWindow(Activity activity, RelativeLayout root, Location location, boolean fillWidth) {
		super(activity);
		myActivity = activity;

		setFocusable(false);
		
		final LayoutInflater inflater =
			(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(
			location == Location.Bottom
				? R.layout.control_panel_bottom : R.layout.control_panel_floating,
			this,
			true
		);

		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
			fillWidth ? ViewGroup.LayoutParams.FILL_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		p.addRule(RelativeLayout.CENTER_HORIZONTAL);
		root.addView(this, p);

		setVisibility(View.GONE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void show() {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				setVisibility(View.VISIBLE);
			}
		});
	}

	public void hide() {
		myActivity.runOnUiThread(new Runnable() {
			public void run() {
				setVisibility(View.GONE);
			}
		});
	}
	
	public void addView(View view) {
		((LinearLayout)findViewById(R.id.tools_plate)).addView(view);
	}
}

package org.sek.grizzly;

import java.util.Date;

import org.sek.grizzly.model.android.AndroidGrizzlyModel;
import org.sek.grizzly.model.GrizzlyModel.ModelReadyCallback;
import org.sek.grizzly.model.TimeLog;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	protected AndroidGrizzlyModel model;
	protected Long currentEntry;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress("Loading Database...");
        new Thread() {
        	public void run() {
        		model = new AndroidGrizzlyModel(MainActivity.this, new ModelReadyCallback() {
					@Override
					public void onReady() {
		        		runOnUiThread(new Runnable() {
							@Override
							public void run() {
				                showStartScreen();
							}
						});
					}
				});
        	}
        }.start();
    }
	
	private void showStartScreen() {
        setContentView(R.layout.activity_start);
        ((Button) findViewById(R.id.btn_start)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress("Logging entry...");
				new Thread() {
					public void run() {
						TimeLog t = model.startTimeLog(new Date());
						currentEntry = t.id;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showEndScreen();
							}
						});
					}
				}.start();
			}
		});
	}
	
	private void showEndScreen() {
		setContentView(R.layout.activity_end);
		((Button) findViewById(R.id.btn_end)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress("Logging entry...");
				new Thread() {
					public void run() {
						model.endTimeLog(currentEntry);
						currentEntry = null;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showStartScreen();
							}
						});
					}
				}.start();
			}
		});
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
    
    public void showProgress(String label) {
    	setContentView(R.layout.activity_progress);
        ((TextView) findViewById(R.id.txt_progress)).setText(label);
    }

}

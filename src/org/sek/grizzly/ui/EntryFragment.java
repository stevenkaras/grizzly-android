package org.sek.grizzly.ui;

import java.util.Date;

import org.sek.grizzly.GrizzlyApplication;
import org.sek.grizzly.R;
import org.sek.grizzly.model.TimeLog;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class EntryFragment extends StatefulFragment {
	
	private static final int SCREEN_START = 1;
	private static final int SCREEN_END = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = super.onCreateView(inflater, container, savedInstanceState);

		int screenToDisplay = SCREEN_START;
		//TODO: deflate the bundle and determine which state to start with
		final int screenToShow = screenToDisplay;

		showProgress("Loading Grizzly model...");
		new Thread() {
			public void run() {
				synchronized (GrizzlyApplication.application.modelLock) {
					while (!GrizzlyApplication.application.modelReady) {
						try {
							GrizzlyApplication.application.modelLock.wait();
						} catch (InterruptedException e) {
							// NOP
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showScreen(screenToShow);
						}
					});
				}
			}
		}.start();
		
		return result;
	}
	
	private void showScreen(int screen) {
		switch (screen) {
		case SCREEN_START:
			showStartScreen();
			break;
		case SCREEN_END:
			showEndScreen();
			break;
		}
	}
	
	private void showProgress(String statusText) {
    	setContentView(R.layout.fragment_progress);
        ((TextView) findViewById(R.id.txt_progress)).setText(statusText);
	}
	
	private void showEndScreen() {
		setContentView(R.layout.fragment_end);
		final Chronometer chrono = (Chronometer) findViewById(R.id.chrono_elapsed);
		chrono.setVisibility(View.INVISIBLE);
		new Thread() {
			public void run() {
				TimeLog log = GrizzlyApplication.application.model.getTimeLog(currentEntry);
				log.startEntry.getTime();
				long adjust = new Date().getTime() - SystemClock.elapsedRealtime();
				final long base = log.startEntry.getTime() - adjust;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						chrono.setBase(base);
						chrono.start();
						chrono.setVisibility(View.VISIBLE);
					}
				});
			}
		}.start();
		((Button) findViewById(R.id.btn_end)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress("Logging entry...");
				new Thread() {
					public void run() {
						GrizzlyApplication.application.model.endTimeLog(currentEntry, new Date());
						currentEntry = null;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								chrono.stop();
								showStartScreen();
							}
						});
					}
				}.start();
			}
		});
	}

	protected Long currentEntry;
	
	private void showStartScreen() {
		setContentView(R.layout.fragment_start);
		((Button) findViewById(R.id.btn_start)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress("Logging entry...");
				new Thread() {
					public void run() {
						TimeLog t = GrizzlyApplication.application.model.startTimeLog(new Date());
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
	
}

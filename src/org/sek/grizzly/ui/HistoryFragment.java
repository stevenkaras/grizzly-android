package org.sek.grizzly.ui;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.sek.grizzly.GrizzlyApplication;
import org.sek.grizzly.R;
import org.sek.grizzly.model.GrizzlyUtils;
import org.sek.grizzly.model.TimeLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HistoryFragment extends StatefulFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = super.onCreateView(inflater, container, savedInstanceState);

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
							showHistoryScreen();
						}
					});
				}
			}
		}.start();
		
		return result;
	}

	private void showProgress(String statusText) {
    	setContentView(R.layout.fragment_progress);
        ((TextView) findViewById(R.id.txt_progress)).setText(statusText);
	}

	private void showHistoryScreen() {
		setContentView(R.layout.fragment_history);
		((Button) findViewById(R.id.btn_history_refresh)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshHistory();
			}
		});
		refreshHistory();
	}
	
	private void refreshHistory() {
		final TextView v = (TextView) findViewById(R.id.text_history);
		new Thread() {
			public void run() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				List<TimeLog> logs = GrizzlyApplication.application.model.getAllLogs();
				final StringBuilder sb = new StringBuilder();
				for (TimeLog log : logs) {
					sb.append("Id: ");
					sb.append(log.id);
					sb.append("\n");
					sb.append("Start: ");
					sb.append(dateFormat.format(log.startEntry));
					sb.append("\n");
					sb.append(GrizzlyUtils.getHumanInterval(log.startEntry, log.endEntry));
					sb.append("\n");
					sb.append("\n");
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						v.setText(sb.toString());
					}
				});
			}
		}.start();
	}
	
}

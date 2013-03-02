package org.sek.grizzly.model.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.sek.grizzly.model.GrizzlyModel;
import org.sek.grizzly.model.TimeLog;

import android.content.ContentValues;
import android.content.Context;

public class AndroidGrizzlyModel implements GrizzlyModel {

	private GrizzlyDBHelper db;

	public AndroidGrizzlyModel(Context context, final ModelReadyCallback modelReadyCallback) {
		this.db = new GrizzlyDBHelper(context);
		db.getWritableDatabase();
		if (modelReadyCallback != null) {
			modelReadyCallback.onReady();
		}
	}
	
	private static final String TIME_LOG_TABLE = "time_logs";

	@Override
	public TimeLog startTimeLog(Date startTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US); 
		ContentValues initialValues = new ContentValues(); 
		initialValues.put("start_entry", dateFormat.format(startTime));
		long rowId = db.getWritableDatabase().insert(TIME_LOG_TABLE, null, initialValues);
		
		TimeLog result = new TimeLog();
		result.startEntry = startTime;
		result.id = rowId;
		return result;
	}

	@Override
	public TimeLog endTimeLog(Long currentEntry) {
		//NOP
		
		return null;
	}
	
}

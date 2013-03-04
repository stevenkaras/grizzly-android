package org.sek.grizzly.model.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.sek.grizzly.model.GrizzlyModel;
import org.sek.grizzly.model.TimeLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
		
		//TODO: throw an exception if rowId == -1
		
		TimeLog result = new TimeLog();
		result.startEntry = startTime;
		result.id = rowId;
		return result;
	}

	@Override
	public TimeLog endTimeLog(Long entry, Date endTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US); 
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("end_entry", dateFormat.format(endTime));
		db.getWritableDatabase().update(TIME_LOG_TABLE, updatedValues, "_id = ?", new String[] { String.valueOf(entry) });
		
		// TODO: throw an exception if rowsAffected != 1
		
		Cursor c = db.getReadableDatabase().query(TIME_LOG_TABLE, null, "_id = ?", new String[] { String.valueOf(entry) }, null, null, null);
		c.moveToFirst();
		return readTimeLog(c);
	}

	@Override
	public List<TimeLog> getAllLogs() {
		Cursor c = db.getReadableDatabase().query(TIME_LOG_TABLE, null, null, null, null, null, null);
		c.moveToFirst();
		List<TimeLog> result = new ArrayList<TimeLog>();
		while (!c.isAfterLast()) {
			result.add(readTimeLog(c));
			c.moveToNext();
		}
		return result;
	}

	@Override
	public TimeLog getTimeLog(Long id) {
		Cursor c = db.getReadableDatabase().query(TIME_LOG_TABLE, null, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);
		c.moveToFirst();
		return readTimeLog(c);
	}
	
	private TimeLog readTimeLog(Cursor c) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		TimeLog result = new TimeLog();
		result.id = c.getLong(c.getColumnIndexOrThrow("_id"));
		try {
			result.startEntry = dateFormat.parse(c.getString(c.getColumnIndexOrThrow("start_entry")));
		} catch (ParseException e) {
			// Rethrow as runtime, since the database should contain clean data
			throw new RuntimeException(e);
		}
		String endEntryValue = c.getString(c.getColumnIndexOrThrow("end_entry"));
		if (endEntryValue != null) {
			try {
				result.endEntry = dateFormat.parse(endEntryValue);
			} catch (ParseException e) {
				// Rethrow as runtime, since the database should contain clean data
				throw new RuntimeException(e);
			}
		} else {
			result.endEntry = null;
		}
		return result;
	}
	
}

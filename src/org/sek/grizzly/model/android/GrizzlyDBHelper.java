package org.sek.grizzly.model.android;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GrizzlyDBHelper extends SQLiteOpenHelper {
	
	public GrizzlyDBHelper(Context context) {
		super(context, "grizzly", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE time_logs ("+
				"  _id          INTEGER PRIMARY KEY AUTOINCREMENT,"+
				"  start_entry  DATE,"+
				"  end_entry    DATE"+
				");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}

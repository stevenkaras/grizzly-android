package org.sek.grizzly.model;

import java.util.Date;
import java.util.List;

public interface GrizzlyModel {
	public static interface ModelReadyCallback {
		/**
		 * Called when the model is ready to be used 
		 */
		public void onReady();
	}
	
	public TimeLog startTimeLog(Date startTime);

	public TimeLog endTimeLog(Long currentEntry, Date endTime);
	
	public TimeLog getTimeLog(Long id);
	
	public List<TimeLog> getAllLogs();

}

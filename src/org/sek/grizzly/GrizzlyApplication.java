package org.sek.grizzly;

import org.sek.grizzly.model.GrizzlyModel.ModelReadyCallback;
import org.sek.grizzly.model.android.AndroidGrizzlyModel;

import android.app.Application;

public class GrizzlyApplication extends Application {
	
	public AndroidGrizzlyModel model;
	public Object modelLock = new Object();
	public volatile boolean modelReady = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
        new Thread() {
        	public void run() {
        		model = new AndroidGrizzlyModel(GrizzlyApplication.this, new ModelReadyCallback() {
					@Override
					public void onReady() {
						synchronized (modelLock) {
							modelReady = true;
							modelLock.notifyAll();
						}
					}
				});
        	}
        }.start();
	}
	
	public static GrizzlyApplication application = null; 
	{
		application = this;
	}
	
}

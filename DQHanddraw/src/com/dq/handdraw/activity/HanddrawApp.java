package com.dq.handdraw.activity;

import android.app.Application;
import android.content.Context;

public class HanddrawApp extends Application{
	private static Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		context=this;
	}

	public static Context getContext(){
		return context;
	}
}

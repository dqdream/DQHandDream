package com.dq.handdraw.util;

import android.util.Log;

public class LogUtil {
	static boolean isOpen = true;
	private static final String TAG = "dqvv";

	public static void d(String tag, String msg) {
		if (isOpen) {
			Log.d(tag, msg);
		}
	}

	public static void d(String msg) {
		if (isOpen) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isOpen) {
			Log.e(tag, msg);
		}
	}

	public static void e(String msg) {
		if (isOpen) {
			Log.e(TAG, msg);
		}
	}
}

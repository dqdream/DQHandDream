package com.dq.handdraw.util;

import com.dq.handdraw.activity.HanddrawApp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	static boolean  isOpen=true;
	
	@SuppressLint("ShowToast")
	public static void ShowToast(Context context,String text){
		if (isOpen) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}
	
	
}

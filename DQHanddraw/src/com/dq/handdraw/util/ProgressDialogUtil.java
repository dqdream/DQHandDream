package com.dq.handdraw.util;

import com.dq.handdraw.view.ShapeLoadingDialog;

import android.content.Context;

public class ProgressDialogUtil {
	private static ShapeLoadingDialog dialog = null;
	public static void show(Context context,String text){
		if(dialog == null){
			dialog = new ShapeLoadingDialog(context);
		}
		dialog.setLoadingText(text);
		dialog.show();
	}
	public static void dismiss(){
		if(dialog != null){
			dialog.dismiss();
			dialog = null;
		}
	}
}

package com.dq.handdraw.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dq.handdraw.activity.HanddrawApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class FileUtil {

	public final static String imgDir = "handdraw/img/";
	public final static String gifDir = "handdraw/gif/";
	
	public static File getGifDir(){
		File dir = new File(Environment.getExternalStorageDirectory(), gifDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(dir, System.currentTimeMillis() + ".gif");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public static String saveBitmap(Context context,Bitmap bitmap) {
		if (bitmap==null) {
			return null;
		}
		File dir = new File(Environment.getExternalStorageDirectory(), imgDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(dir, System.currentTimeMillis() + ".jpg");
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		LogUtil.d("保存成功");
		try {
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
					Uri.fromFile(f)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}
	
	
}

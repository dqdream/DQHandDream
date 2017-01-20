package com.dq.handdraw.setting;

import com.dq.handdraw.util.LogUtil;

import android.graphics.Bitmap;
import android.util.Log;

public class SettingManager {

	public static SettingManager gInst;
	
	public static SettingManager getInstance(){
		synchronized (SettingManager.class) {
			if(gInst == null){
				gInst = new SettingManager();
			}
			return gInst;
		}
	}
	
	
	public Bitmap getSobel(Bitmap bitmap){
		Bitmap bp = null;
		switch (Setting.SETTING_COLOR) {
		case 0:
			bp=new Sobel().Sobel(bitmap, Setting.THRESHOLD_0_1, Setting.THRESHOLD_0_2, Setting.THRESHOLD_0_3);
			LogUtil.d("THRESHOLD_0_1 = "+Setting.THRESHOLD_0_1+",THRESHOLD_0_2 = "+Setting.THRESHOLD_0_2+",THRESHOLD_0_3 = "+Setting.THRESHOLD_0_3);
			break;
		case 1:
			bp=new SobelColorMore().Sobel(bitmap, Setting.THRESHOLD_1_1, Setting.THRESHOLD_1_2, Setting.THRESHOLD_1_3);
			LogUtil.d("THRESHOLD_1_1 = "+Setting.THRESHOLD_1_1+",THRESHOLD_1_2 = "+Setting.THRESHOLD_1_2+",THRESHOLD_1_3 = "+Setting.THRESHOLD_1_3);
			break;
		}
		return bp;
	}
	
}

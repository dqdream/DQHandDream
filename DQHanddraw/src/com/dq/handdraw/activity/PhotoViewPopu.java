package com.dq.handdraw.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dq.handdraw.R;
import com.dq.handdraw.photoview.PhotoView;
import com.dq.handdraw.photoview.PhotoViewAttacher.OnViewTapListener;
import com.dq.handdraw.setting.Setting;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PhotoViewPopu extends PopupWindow implements OnSeekBarChangeListener {

	private PhotoView photoview;
	private String imagePath;
	private Context context;
	private RadioGroup photo_color_gourp;
	private SeekBar seekBar1, seekBar2, seekBar3;

	public PhotoViewPopu(Context context) {
		super(context);
		this.context = context;
		View v = LayoutInflater.from(context).inflate(R.layout.photo_view, null);
		setContentView(v);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		photoview = (PhotoView) v.findViewById(R.id.photoview);
		seekBar1 = (SeekBar) v.findViewById(R.id.seekBar1);
		seekBar2 = (SeekBar) v.findViewById(R.id.seekBar2);
		seekBar3 = (SeekBar) v.findViewById(R.id.seekBar3);
		photo_color_gourp = (RadioGroup) v.findViewById(R.id.photo_color_gourp);
		setAnimationStyle(R.style.popuanim);
		seekBar1.setOnSeekBarChangeListener(this);
		seekBar2.setOnSeekBarChangeListener(this);
		seekBar3.setOnSeekBarChangeListener(this);
		photo_color_gourp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (photo_color_gourp.getCheckedRadioButtonId() == arg1) {
					switch (arg1) {
					case R.id.photo_rbtn1:
						Setting.SETTING_COLOR = 0;
						seekBar1.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_0_1 * 100)));
						seekBar2.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_0_2 * 100)));
						seekBar3.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_0_3 * 100)));
						break;
					case R.id.photo_rbtn2:
						Setting.SETTING_COLOR = 1;
						seekBar1.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_1_1 * 100)));
						seekBar2.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_1_2 * 100)));
						seekBar3.setProgress(getSeekbarProgress((int) (Setting.THRESHOLD_1_3 * 100)));
						break;
					}
				}
			}
		});
	}

	public void init() {
		switch (Setting.SETTING_COLOR) {
		case 0:
			photo_color_gourp.check(R.id.photo_rbtn1);
			break;
		case 1:
			photo_color_gourp.check(R.id.photo_rbtn2);
			break;
		}
	}
	
	private int getSeekbarProgress(int pro){
		return 50-pro;
	}
	
	public void showPhotoDetail(String imagepath, View parent) {
		this.imagePath = imagepath;
		if (!TextUtils.isEmpty(imagePath)) {
			Glide.with(context).load(imagePath).thumbnail(0.2f).crossFade(200).diskCacheStrategy(DiskCacheStrategy.NONE)
					.skipMemoryCache(false).into(photoview);
		}
		photoview.setOnViewTapListener(new OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				dismiss();
			}
		});
		super.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (!fromUser) {
			return;
		}
		double p=getSeekbarProgress(progress)*1.0/100;
		switch (seekBar.getId()) {
		case R.id.seekBar1:
			if (Setting.SETTING_COLOR == 0) {
				Setting.THRESHOLD_0_1=p;
			} else if (Setting.SETTING_COLOR == 1) {
				Setting.THRESHOLD_1_1=p;
			}
			break;
		case R.id.seekBar2:
			if (Setting.SETTING_COLOR == 0) {
				Setting.THRESHOLD_0_2=p;
			} else if (Setting.SETTING_COLOR == 1) {
				Setting.THRESHOLD_1_2=p;
			}
			break;
		case R.id.seekBar3:
			if (Setting.SETTING_COLOR == 0) {
				Setting.THRESHOLD_0_3=p;
			} else if (Setting.SETTING_COLOR == 1) {
				Setting.THRESHOLD_1_3=p;
			}
			break;
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}

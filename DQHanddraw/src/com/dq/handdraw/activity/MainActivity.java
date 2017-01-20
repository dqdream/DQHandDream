package com.dq.handdraw.activity;

import java.io.File;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dq.handdraw.R;
import com.dq.handdraw.setting.SettingManager;
import com.dq.handdraw.util.FileUtil;
import com.dq.handdraw.util.ImageZoomUtil;
import com.dq.handdraw.util.ProgressDialogUtil;
import com.dq.handdraw.util.ToastUtil;
import com.dq.handdraw.view.CustomDialog;
import com.dq.handdraw.view.HandDrawView;
import com.dq.handdraw.view.HandDrawView.HandDrawListener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 手绘
 * 
 * http://iconfont.cn/plus/collections/detail?cid=2559
 * 
 * @author dq
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	private HandDrawView drawOutlineView;
	private Bitmap sobelBm;
	private ImageView image_pickbtn, image_startbtn,image_save;
	private ImageView image_demo;
	private RelativeLayout rel_setting;
	/**
	 * 0 未绘制/绘制完成 1准备中 2绘制中
	 */
	int drawStatus = 0;
	private String imagePath;
	private RelativeLayout parent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		image_pickbtn = (ImageView) findViewById(R.id.image_pickbtn);
		image_demo = (ImageView) findViewById(R.id.image_demo);
		image_startbtn = (ImageView) findViewById(R.id.image_startbtn);
		image_save=(ImageView) findViewById(R.id.image_save);
		drawOutlineView = (HandDrawView) findViewById(R.id.outline);
		rel_setting = (RelativeLayout) findViewById(R.id.rel_setting);
		parent = (RelativeLayout) findViewById(R.id.parent);
		image_pickbtn.setOnClickListener(this);
		image_startbtn.setOnClickListener(this);
		image_demo.setOnClickListener(this);
		drawOutlineView.setOnClickListener(this);
		image_save.setOnClickListener(this);
		drawOutlineView.setHandDrawListener(new HandDrawListener() {

			@Override
			public void onStartDraw() {
				drawStatus = 2;
				image_save.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinishDraw() {
				drawStatus = 0;

			}
		});
	}

	private void initZoomBitmp() {
		new Thread(new Runnable() {
			public void run() {
				drawStatus = 1;
				final Bitmap bm = ImageZoomUtil.compress(imagePath, drawOutlineView.getWidth(),
						drawOutlineView.getHeight());
				sobelBm = SettingManager.getInstance().getSobel(bm);
				Bitmap paintBm = ImageZoomUtil.getResourceZoomBitmap(MainActivity.this, R.drawable.pencil, -1,
						100 * 100);
				drawOutlineView.setPaintBm(paintBm);
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (sobelBm != null) {
				ProgressDialogUtil.dismiss();
				drawOutlineView.startdraw(getArray1(sobelBm));
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_pickbtn:
			Intent intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1);
			break;
		case R.id.image_startbtn:
			if (!TextUtils.isEmpty(imagePath)) {
				showSetting(false);
				new CustomDialog.Builder(this)
				.setTitle(getString(R.string.title))
				.setMessage(getString(R.string.gif_record))
				.setNegativeButton("", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawOutlineView.setFinishDraw(true);
						drawOutlineView.setStartGif(true);
						ProgressDialogUtil.show(MainActivity.this, getString(R.string.watting));
						initZoomBitmp();
						dialog.dismiss();
					}
				}).setPositiveButton("", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawOutlineView.setFinishDraw(true);
						ProgressDialogUtil.show(MainActivity.this, getString(R.string.watting));
						initZoomBitmp();
						dialog.dismiss();
					}
				}).create().show();
			} else {
				ToastUtil.ShowToast(this, getString(R.string.select_pic));
			}
			break;
		case R.id.outline:
			if (rel_setting.isShown()) {
				showSetting(false);
			} else {
				showSetting(true);
			}
			break;
		case R.id.image_demo:
			showPhotoDetail();
			break;
		case R.id.image_save:
			String path=FileUtil.saveBitmap(this,drawOutlineView.getCacheBitmap());
			if (!TextUtils.isEmpty(path)) {
				ToastUtil.ShowToast(this, getString(R.string.pic_save_success)+path);
			}
			break;
		}

	}
	PhotoViewPopu popu;
	void showPhotoDetail() {
		popu = new PhotoViewPopu(this);
		popu.init();
		popu.showPhotoDetail(imagePath, parent);
	}
	
	@Override
	public void onBackPressed() {
		if (popu!=null&&popu.isShowing()) {
			popu.dismiss();
		}else{
			drawOutlineView.setFinishDraw(true);
			super.onBackPressed();
		}
	}

	public void showSetting(boolean isshow) {
		if (isshow) {
			rel_setting.setVisibility(View.VISIBLE);
		} else {
			rel_setting.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 获取图片路径
		if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumns = { MediaStore.Images.Media.DATA };
			Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePathColumns[0]);
			imagePath = c.getString(columnIndex);
			c.close();
			showPhotoDetail();
			Glide.with(this).load(imagePath).thumbnail(0.2f).crossFade(200).diskCacheStrategy(DiskCacheStrategy.NONE)
					.skipMemoryCache(false).into(image_demo);
		}
	}

	private int[][] getArray1(Bitmap bitmap) {
		int[][] b = new int[bitmap.getWidth()][bitmap.getHeight()];

		for (int i = 0; i < bitmap.getWidth(); i++) {
			for (int j = 0; j < bitmap.getHeight(); j++) {
				b[i][j] = bitmap.getPixel(i, j);
			}
		}
		return b;
	}

}

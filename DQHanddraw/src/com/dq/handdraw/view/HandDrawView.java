package com.dq.handdraw.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.dq.handdraw.activity.HanddrawApp;
import com.dq.handdraw.util.AnimatedGifMaker;
import com.dq.handdraw.util.FileUtil;
import com.dq.handdraw.util.ImageZoomUtil;
import com.dq.handdraw.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 手绘View
 */
public class HandDrawView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mSurfaceHolder;
	private Bitmap mTmpBm;
	private Canvas mTmpCanvas;
	private int mWidth;
	private int mHeight;
	private Paint mPaint;
	private int mSrcBmWidth;
	private int mSrcBmHeight;
	private int[][] mArray1;
	private int offsetX = 0;
	private int offsetY = 0;

	private Bitmap mPaintBm;
	private Point mLastPoint = new Point(0, 0);
	private int mLastColor;
	private HandDrawListener handDrawListener;
	private Bitmap mGifBmp;
	private Canvas mGifCanvas;
	public HandDrawListener getHandDrawListener() {
		return handDrawListener;
	}

	public void setHandDrawListener(HandDrawListener handDrawListener) {
		this.handDrawListener = handDrawListener;
	}

	public HandDrawView(Context context) {
		super(context);
		init();
	}

	public HandDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
	}

	// 设置画笔图片
	public void setPaintBm(Bitmap paintBm) {
		mPaintBm = paintBm;
	}

	// 获取离指定点最近的一个未绘制过的点
	private Point getNearestPoint(Point p) {
		if (p == null)
			return null;
		// 以点p为中心，向外扩大搜索范围，每次搜索的是与p点相距add的正方形
		for (int add = 1; add < mSrcBmWidth && add < mSrcBmHeight; add++) {
			//
			int beginX = (p.x - add) >= 0 ? (p.x - add) : 0;
			int endX = (p.x + add) < mSrcBmWidth ? (p.x + add) : mSrcBmWidth - 1;
			int beginY = (p.y - add) >= 0 ? (p.y - add) : 0;
			int endY = (p.y + add) < mSrcBmHeight ? (p.y + add) : mSrcBmHeight - 1;
			// 搜索正方形的上下边
			for (int x = beginX; x <= endX; x++) {
				if (mArray1[x][beginY] != -1) {
					mLastColor = mArray1[x][beginY];
					mArray1[x][beginY] = -1;
					return new Point(x, beginY);
				}
				if (mArray1[x][endY] != -1) {
					mLastColor = mArray1[x][endY];
					mArray1[x][endY] = -1;
					return new Point(x, endY);
				}
			}
			// 搜索正方形的左右边
			for (int y = beginY + 1; y <= endY - 1; y++) {
				if (mArray1[beginX][y] != -1) {
					mLastColor = mArray1[beginX][beginY];
					mArray1[beginX][beginY] = -1;
					return new Point(beginX, beginY);
				}
				if (mArray1[endX][y] != -1) {
					mLastColor = mArray1[endX][beginY];
					mArray1[endX][y] = -1;
					return new Point(endX, y);
				}
			}
		}

		return null;
	}

	// 获取下一个需要绘制的点
	private Point getNextPoint() {
		mLastPoint = getNearestPoint(mLastPoint);
		return mLastPoint;
	}

	/**
	 * //绘制 return :false 表示绘制完成，true表示还需要继续绘制
	 */
	private boolean draw() {

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.BLACK);
		// 获取count个点后，一次性绘制到bitmap在把bitmap绘制到SurfaceView
		int count = 100;
		Point p = null;
		while (count-- > 0) {
			p = getNextPoint();
			if (p == null) {// 如果p为空，说明所有的点已经绘制完成
				return false;
			}
			mPaint.setColor(mLastColor);
			mTmpCanvas.drawPoint(p.x + offsetX, p.y + offsetY, mPaint);
		}
		// 将bitmap绘制到SurfaceView中
		Canvas canvas = mSurfaceHolder.lockCanvas();
		if (canvas != null) {
			canvas.drawBitmap(mTmpBm, 0, 0, mPaint);
			mGifCanvas.drawBitmap(mTmpBm, 0, 0, mPaint);
			if (p != null){
				canvas.drawBitmap(mPaintBm, p.x + offsetX, p.y - mPaintBm.getHeight() + offsetY, mPaint);
				mGifCanvas.drawBitmap(mPaintBm, p.x + offsetX, p.y - mPaintBm.getHeight() + offsetY, mPaint);
			}
			mSurfaceHolder.unlockCanvasAndPost(canvas);
		}

		return true;
	}

	public void startdraw(int[][] array) {
		if (isDrawing)
			return;
		mTmpBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mTmpCanvas = new Canvas(mTmpBm);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL);
		mTmpCanvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		mLastPoint = new Point(0, 0);
		

		mGifBmp= Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mGifCanvas = new Canvas(mGifBmp);
		mGifCanvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		beginDraw(array);
	}
	
	public void beginDraw(int[][] array) {
		if (isDrawing)
			return;
		this.mArray1 = array;
		mSrcBmWidth = array.length;
		mSrcBmHeight = array[0].length;
		isDrawing = true;
		isStopDraw=false;
		if (handDrawListener!=null) {
			handDrawListener.onStartDraw();
		}
		new Thread() {
			@Override
			public void run() {
				while (true) {
					if (isStopDraw) {
						break;
					}
					isDrawing = true;
					boolean rs = draw();
					if (!rs)
						break;
//					try {
//						sleep(10);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				}
				isDrawing = false;
				if (handDrawListener!=null) {
					handDrawListener.onFinishDraw();
				}
			}
		}.start();
		if (isStartGif) {
			startRecordGif();
		}
	}

	public void startRecordGif(){
		new Thread( new Runnable() {
			public void run() {
				FileOutputStream out = null;
				File file=FileUtil.getGifDir();
				try {
					out = new FileOutputStream(file);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
		        AnimatedGifMaker gifs = new AnimatedGifMaker();
		        gifs.start(out);
		        gifs.setFrameRate(15);
		        gifs.setRepeat(0);
		        gifs.setTransparent(new Color());
		        while (isDrawing) {
		        	if (isStopDraw) {
						break;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Bitmap bitmap=Bitmap.createBitmap(mGifBmp);
					gifs.addFrame(ImageZoomUtil.compressTo2(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2));
					LogUtil.d("size"+file.length());
				}
		        gifs.finish();
		        try {
					getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
							Uri.fromFile(file)));
				} catch (Exception e) {
					e.printStackTrace();
				}
		        LogUtil.d("gif finish");
			}
		}).start();
	}
	private boolean isDrawing = false;
	private boolean isStopDraw=false;//结束录制
	private boolean isStartGif=false;
	public boolean isStartGif() {
		return isStartGif;
	}

	public void setStartGif(boolean isStartGif) {
		this.isStartGif = isStartGif;
	}

	public void setFinishDraw(boolean isFinishDraw) {
		this.isStopDraw = isFinishDraw;
	}

	public boolean isDrawing() {
		return isDrawing;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
		initBackground();
	}
	
	public Bitmap getCacheBitmap(){
		return  mTmpBm;
	}
	
	public void initBackground() {
		if (mTmpBm == null) {
			mTmpBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
			mTmpCanvas = new Canvas(mTmpBm);
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.FILL);
			mTmpCanvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		}
		Canvas canvas = mSurfaceHolder.lockCanvas();
		canvas.drawBitmap(mTmpBm, 0, 0, mPaint);
		mSurfaceHolder.unlockCanvasAndPost(canvas);
		mPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public interface HandDrawListener {
		public void onStartDraw();

		public void onFinishDraw();
	}
}

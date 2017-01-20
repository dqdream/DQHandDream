package com.dq.handdraw.setting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class Sobel implements ISobel {

	/**
	 * Sobel算法
	 *
	 * @param bitmap
	 * @return
	 */
	@Override
	public Bitmap Sobel(Bitmap bitmap, double a, double b, double c) {
		Bitmap temp = toGrayscale(bitmap);
		if (a == 0 && b == 0 && c == 0) {
			return temp;
		}
		int w = temp.getWidth();
		int h = temp.getHeight();

		int[] mmap = new int[w * h];
		double[] tmap = new double[w * h];
		int[] cmap = new int[w * h];

		temp.getPixels(mmap, 0, temp.getWidth(), 0, 0, temp.getWidth(), temp.getHeight());

		double max = Double.MIN_VALUE;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				double gx = GX(i, j, temp);
				double gy = GY(i, j, temp);
				tmap[j * w + i] = Math.sqrt(gx * gx + gy * gy);
				if (max < tmap[j * w + i]) {
					max = tmap[j * w + i];
				}
			}
		}

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (tmap[j * w + i] > max * a) {
					// 如果大于阙值max*a，则保存灰度图该点的像素
					cmap[j * w + i] = mmap[j * w + i];
				} else if (tmap[j * w + i] > max * b) {
					// 否则如果大于阙值max*b，则保存灰度图该点的像素+50,(变淡)
					cmap[j * w + i] = getColor(mmap[j * w + i], 50);
				} else if (tmap[j * w + i] > max * c) {
					// 否则如果大于阙值max*c，则保存灰度图该点的像素+80,(变得更淡)
					cmap[j * w + i] = getColor(mmap[j * w + i], 80);
				} else {
					// 否则该点为白色
					cmap[j * w + i] = Color.WHITE;
				}
			}
		}
		if (!temp.isRecycled()) {
			temp.recycle();
		}
		return Bitmap.createBitmap(cmap, temp.getWidth(), temp.getHeight(), Config.ARGB_8888);
	}

	/**
	 * 转化成灰度图
	 *
	 * @param bmpOriginal
	 * @return
	 */
	protected Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		if (!bmpOriginal.isRecycled()) {
			bmpOriginal.recycle();
		}
		return bmpGrayscale;
	}

	/**
	 * 处理颜色
	 * 
	 * @param color
	 * @param value
	 *            value为负数时颜色加深，为正数时颜色变淡
	 * @return
	 */
	protected int getColor(int color, int value) {

		int cr, cg, cb;

		cr = (color & 0x00ff0000) >> 16;
		cg = (color & 0x0000ff00) >> 8;
		cb = color & 0x000000ff;

		cr += value;
		cg += value;
		cb += value;

		if (cr > 255) {
			cr = 255;
		}
		if (cg > 255) {
			cg = 255;
		}
		if (cb > 255) {
			cb = 255;
		}

		if (cr < 0) {
			cr = 0;
		}
		if (cg < 0) {
			cg = 0;
		}
		if (cb < 0) {
			cb = 0;
		}

		return Color.argb(255, cr, cg, cb);
	}

	/**
	 * 获取横向的
	 *
	 * @param x
	 *            第x行
	 * @param y
	 *            第y列
	 * @param bitmap
	 * @return
	 */
	protected double GX(int x, int y, Bitmap bitmap) {
		double res = (-1) * getPixel(x - 1, y - 1, bitmap) + 1 * getPixel(x + 1, y - 1, bitmap)
				+ (-Math.sqrt(2)) * getPixel(x - 1, y, bitmap) + Math.sqrt(2) * getPixel(x + 1, y, bitmap)
				+ (-1) * getPixel(x - 1, y + 1, bitmap) + 1 * getPixel(x + 1, y + 1, bitmap);
		return res;
	}

	/**
	 * 获取纵向的
	 *
	 * @param x
	 *            第x行
	 * @param y
	 *            第y列
	 * @param bitmap
	 * @return
	 */
	protected double GY(int x, int y, Bitmap bitmap) {
		double res = 1 * getPixel(x - 1, y - 1, bitmap) + Math.sqrt(2) * getPixel(x, y - 1, bitmap)
				+ 1 * getPixel(x + 1, y - 1, bitmap) + (-1) * getPixel(x - 1, y + 1, bitmap)
				+ (-Math.sqrt(2)) * getPixel(x, y + 1, bitmap) + (-1) * getPixel(x + 1, y + 1, bitmap);
		return res;
	}

	/**
	 * 获取第x行第y列的色度
	 *
	 * @param x
	 *            第x行
	 * @param y
	 *            第y列
	 * @param bitmap
	 * @return
	 */
	protected double getPixel(int x, int y, Bitmap bitmap) {
		if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight())
			return 0;
		return bitmap.getPixel(x, y);
	}
}

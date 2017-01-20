package com.dq.handdraw.setting;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class SobelColorMore extends Sobel {
	
	public Bitmap Sobel(Bitmap bitmap, double a, double b, double c) {
		Bitmap temp = (bitmap);
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

		return Bitmap.createBitmap(cmap, temp.getWidth(), temp.getHeight(), Config.ARGB_8888);
	}

}

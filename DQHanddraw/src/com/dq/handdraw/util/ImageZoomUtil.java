package com.dq.handdraw.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * 图片缩放 2016.11.25
 * @author DQ
 */
public class ImageZoomUtil {
	
    public static Bitmap getResourceZoomBitmap(Context context, int imgId, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imgId, newOpts);
        newOpts.inSampleSize = computeSampleSize(newOpts, minSideLength,
        		maxNumOfPixels);
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap=  BitmapFactory.decodeResource(context.getResources(), imgId, newOpts);
        return bitmap;

    }
    /**
     * 获取文件缩放图片
     * 动�?缩放，避免OOM
     * @param context
     * @param imgId
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static Bitmap getFileZoomBitmap(String path, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, newOpts);
        newOpts.inSampleSize = 1;
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap=  BitmapFactory.decodeFile(path, newOpts);
        return bitmap;

    }
    /**
     * 动�?计算采样�?缩放�?     * @param options
     * @param minSideLength �?��宽或者高的大小，-1为不设置
     * @param maxNumOfPixels �?��占用内存 格式 w*h
     * @return
     */
    private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * Bitmap精确压缩
     * @param bm
     * @param reqWidth 缩放宽度
     * @param reqHeight 缩放高度
     * @return
     */
    public static Bitmap compress(String path, int reqWidth, int reqHeight) {
    	BitmapFactory.Options newOpts = new BitmapFactory.Options();
    	Bitmap bm=  BitmapFactory.decodeFile(path, newOpts);
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (height > reqHeight || width > reqWidth) {
            float scaleWidth = (float) reqWidth / width;
            float scaleHeight = (float) reqHeight / height;
            float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            bm.recycle();
            return result;
        }
        return bm;
    }
    
    /**
     * Bitmap精确压缩
     * @param bm
     * @param reqWidth 缩放宽度
     * @param reqHeight 缩放高度
     * @return
     */
    public static Bitmap compress(final Bitmap bm, int reqWidth, int reqHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (height > reqHeight || width > reqWidth) {
            float scaleWidth = (float) reqWidth / width;
            float scaleHeight = (float) reqHeight / height;
            float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            bm.recycle();
            return result;
        }
        return bm;
    }
    
    public static Bitmap compressTo2(final Bitmap bm, int reqWidth, int reqHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (height > reqHeight || width > reqWidth) {
            float scaleWidth = (float) reqWidth / width;
            float scaleHeight = (float) reqHeight / height;
            float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();  
            result.compress(Bitmap.CompressFormat.JPEG, 80, os);
            try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            bm.recycle();
            return result;
        }
        return bm;
    }
}

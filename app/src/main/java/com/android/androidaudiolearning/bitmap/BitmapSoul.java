package com.android.androidaudiolearning.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.android.androidaudiolearning.R;

import java.io.ByteArrayOutputStream;

import static com.android.androidaudiolearning.R.drawable.ic_launcher_background;

public class BitmapSoul {
    /**
     * 质量压缩
     * @param bitmap
     * @param quality 改变值0-100
     * @return
     */
    private Bitmap qualityZip(Bitmap bitmap, int quality){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 采样率压缩
     * @return
     */
    private Bitmap optionsZip(String path,Bitmap bitmap){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//宽高减少至原始值一半
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算SampleSize
     * @param op
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options op, int reqWidth,
                                     int reqHeight) {
        int originalWidth = op.outWidth;
        int originalHeight = op.outHeight;
        int inSampleSize = 1;
        if (originalWidth > reqWidth || originalHeight > reqHeight) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;
            while ((halfWidth / inSampleSize > reqWidth)
                    &&(halfHeight / inSampleSize > reqHeight)) {
                inSampleSize *= 2;

            }
        }
        return inSampleSize;
    }

    /**
     * 从资源图片中加载大图
     * @param context
     * @return
     */
    private Bitmap optionRawBigResources(Context context){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只读图片，不加载到内存中
        BitmapFactory.decodeResource(context.getResources(), ic_launcher_background,options);
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inSampleSize = calculateInSampleSize(options, 200, 200);//计算合适的sampleSize
        options.inJustDecodeBounds = false;//加载到内存中
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background,options);
    }

    /**
     * 缩放法进行压缩
     * @param bitmap
     * @return
     */
    private Bitmap scaleZip(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f,0.5f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}

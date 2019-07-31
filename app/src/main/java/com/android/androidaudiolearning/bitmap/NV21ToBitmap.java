package com.android.androidaudiolearning.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * nv21数据高效转bitmap
 */
public class NV21ToBitmap {
    private RenderScript mRenderScript;
    private ScriptIntrinsicYuvToRGB mScriptIntrinsicYuvToRGB;
    private Type.Builder yuvType;
    private Type.Builder rgbType;
    public Allocation mInAllocation;
    private Allocation mOutAllocation;
    public NV21ToBitmap(Context context){
        mRenderScript = RenderScript.create(context);
        mScriptIntrinsicYuvToRGB = ScriptIntrinsicYuvToRGB.create(mRenderScript, Element.U8_4(mRenderScript));
    }
    /**
     * 耗时几毫秒
     * @param nv21
     * @param width
     * @param height
     */
    public Bitmap nv21ToBitmapFast(byte[] nv21,int width,int height){
        if (yuvType == null){
            yuvType = new Type.Builder(mRenderScript,Element.U8(mRenderScript)).setX(nv21.length);
            mInAllocation = Allocation.createTyped(mRenderScript,yuvType.create(),Allocation.USAGE_SCRIPT);
        }
        if (rgbType == null){
            rgbType = new Type.Builder(mRenderScript,Element.RGBA_8888(mRenderScript)).setX(width).setY(height);
            mOutAllocation = Allocation.createTyped(mRenderScript,rgbType.create(),Allocation.USAGE_SCRIPT);
        }
        mInAllocation.copyFrom(nv21);
        mScriptIntrinsicYuvToRGB.setInput(mInAllocation);
        mScriptIntrinsicYuvToRGB.forEach(mOutAllocation);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mOutAllocation.copyTo(bitmap);
        return bitmap;
    }

    /**
     * 耗时几十毫秒
     * @param nv21
     * @param width
     * @param height
     * @return
     */
    public Bitmap nv21ToBitmap(byte[] nv21,int width,int height){
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0,0,0,0),100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, byteArrayOutputStream.size());
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

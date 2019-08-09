package com.android.androidaudiolearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import com.android.androidaudiolearning.android_record.record.SoundPoolPlay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.android.androidaudiolearning.R.drawable.ic_launcher_background;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}

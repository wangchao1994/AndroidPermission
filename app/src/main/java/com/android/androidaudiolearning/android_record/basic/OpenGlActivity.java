package com.android.androidaudiolearning.android_record.basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.android_record.basic.audio_record.AudioRecordActivity;

public class OpenGlActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_layout);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_audio_get:
                startActivity(new Intent(OpenGlActivity.this, AudioRecordActivity.class));
                break;
        }
    }
}

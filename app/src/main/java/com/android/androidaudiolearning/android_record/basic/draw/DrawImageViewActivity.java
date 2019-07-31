package com.android.androidaudiolearning.android_record.basic.draw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.persmisson.GlobalPermission;
import com.android.androidaudiolearning.persmisson.OnPermissionListener;
import com.android.androidaudiolearning.persmisson.Permission;

import java.util.List;

/**
 * 在 Android 平台绘制一张图片，使用至少 3 种不同的 API
 * ImageView
 * SurfaceView(推荐)
 * 自定义 View
 */
public class DrawImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image_view);
        initPermission();
        initView();
    }

    private void initPermission() {
        GlobalPermission.with(this)
                .permission(Permission.Group.STORAGE,Permission.Group.RECORD_AUDIO)
                .request(new OnPermissionListener() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
    }

    private void initView() {
        //loadSurfaceView();
        //loadCustomImageView();
        loadBallView();
    }

    private void loadSurfaceView() {
        CustomSurfaceView customImageView = findViewById(R.id.sf_custom);
        customImageView.setVisibility(View.VISIBLE);
    }

    private void loadCustomImageView() {
        CustomImageView customImageView = findViewById(R.id.img_custom);
        customImageView.setBitmap(Util.getImageFromAssetsFile(this, "prettygirl.png"));
       // customImageView.setVisibility(View.VISIBLE);
    }

    private void loadBallView() {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        BallView ballView = new BallView(this, screenWidth, screenHeight);
        RelativeLayout root = findViewById(R.id.rl_root);
        root.addView(ballView);
    }
}

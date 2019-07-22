package com.android.androidaudiolearning.android_record;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.persmisson.GlobalPermission;
import com.android.androidaudiolearning.persmisson.OnPermissionListener;
import com.android.androidaudiolearning.persmisson.Permission;

import java.util.List;

public class CameraRecordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initPermissionAndIntent(savedInstanceState);
    }

    private void initPermissionAndIntent(Bundle savedInstanceState) {
        initPermission();
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container,new CameraRecordFragment(),"CameraRecordFragment").commitAllowingStateLoss();
        }
    }

    private void initPermission() {
        GlobalPermission.with(this).permission(Permission.Group.STORAGE,Permission.Group.RECORD_AUDIO)
                .request(new OnPermissionListener() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(CameraRecordActivity.this,"获取权限成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CameraRecordActivity.this,"获取权限成功，部分权限未正常授予",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if(quick) {
                            Toast.makeText(CameraRecordActivity.this,"被永久拒绝授权，请手动授予权限",Toast.LENGTH_SHORT).show();
                            GlobalPermission.goPermissionSettingsPage(CameraRecordActivity.this);
                        }else {
                            Toast.makeText(CameraRecordActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
         });
    }
}

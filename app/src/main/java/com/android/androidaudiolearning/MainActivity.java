package com.android.androidaudiolearning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.androidaudiolearning.persmisson.GlobalPermission;
import com.android.androidaudiolearning.persmisson.OnPermissionListener;
import com.android.androidaudiolearning.persmisson.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
    }
    private void initPermission() {
        GlobalPermission.with(this).permission(Permission.Group.STORAGE, Permission.Group.CALENDAR)
                .request(new OnPermissionListener() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(MainActivity.this,"获取权限成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"获取权限成功，部分权限未正常授予",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if(quick) {
                            Toast.makeText(MainActivity.this,"被永久拒绝授权，请手动授予权限",Toast.LENGTH_SHORT).show();
                            GlobalPermission.goPermissionSettingsPage(MainActivity.this);
                        }else {
                            Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.android.androidaudiolearning.persmisson;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PermissionFragment extends Fragment implements Runnable {
    private static final String PERMISSION_GROUP = "permission_group"; // 请求的权限
    private static final String REQUEST_CODE = "request_code"; // 请求码（自动生成）
    private static final String REQUEST_CONSTANT = "request_constant"; // 是否不断请求
    private final static SparseArray<OnPermissionListener> mSparseArray = new SparseArray<>();
    private boolean isBackCall; // 是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调
    public static PermissionFragment newInstance(ArrayList<String> permissions,boolean mConstant) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // requestCode = new Random().nextInt(65535); // Studio编译的APK请求码必须小于65536
            requestCode = new Random().nextInt(255); // Eclipse编译的APK请求码必须小于256
        } while (mSparseArray.get(requestCode) != null);
        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, permissions);
        bundle.putBoolean(REQUEST_CONSTANT, mConstant);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 准备请求连接
     * @param activity
     * @param permissionListener
     */
    public void prepareRequest(AppCompatActivity activity, OnPermissionListener permissionListener){
        //将当前的请求码和对象添加到集合中
        if (getArguments() == null)return;
        mSparseArray.put(getArguments().getInt(REQUEST_CODE),permissionListener);
        //activity.getFragmentManager().beginTransaction().add(this,activity.getClass().getName()).commitAllowingStateLoss();
        activity.getSupportFragmentManager().beginTransaction().add(this,activity.getClass().getName()).commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)return;
        ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
        if (permissions != null && getActivity() != null) {
            if ((permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isHasInstallPermission(getActivity()))
                    || (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isHasOverlaysPermission(getActivity()))) {
                if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.isHasInstallPermission(getActivity())) {
                    // 跳转到允许安装未知来源设置页面
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, getArguments().getInt(REQUEST_CODE));
                }
                if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.isHasOverlaysPermission(getActivity())) {
                    // 跳转到悬浮窗设置页面
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, getArguments().getInt(REQUEST_CODE));
                }
            } else {
                requestPermission();
            }
        }
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        if (PermissionUtils.isOverMarshmallow() && getArguments() != null) {
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
            if (permissions != null && permissions.size() > 0){
                requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        OnPermissionListener onPermissionListener = mSparseArray.get(requestCode);
        // 根据请求码取出的对象为空，就直接返回不处理
        if (onPermissionListener == null) return;
        for (int i = 0; i < permissions.length; i++) {
            // 重新检查安装权限
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permissions[i])) {
                if (PermissionUtils.isHasInstallPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
            }
            // 重新检查悬浮窗权限
            if (Permission.SYSTEM_ALERT_WINDOW.equals(permissions[i])) {
                if (PermissionUtils.isHasOverlaysPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
            }

            // 重新检查8.0的两个新权限
            if (permissions[i].equals(Permission.ANSWER_PHONE_CALLS) || permissions[i].equals(Permission.READ_PHONE_NUMBERS)) {
                // 检查当前的安卓版本是否符合要求
                if (!PermissionUtils.isOverOreo()) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
            }
        }

        // 获取授予权限
        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            // 代表申请的所有的权限都授予了
            onPermissionListener.hasPermission(succeedPermissions, true);
        } else {

            // 获取拒绝权限
            List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);
            // 检查是否开启了继续申请模式，如果是则检查没有授予的权限是否还能继续申请
            if (getArguments() == null)return;
            if (getArguments().getBoolean(REQUEST_CONSTANT)
                    && PermissionUtils.isRequestDeniedPermission(getActivity(), failPermissions)) {
                // 如果有的话就继续申请权限，直到用户授权或者永久拒绝
                requestPermission();
                return;
            }
            // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回true给开发人员，让开发者引导用户去设置界面开启权限
            onPermissionListener.noPermission(failPermissions, PermissionUtils.checkMorePermissionPermanentDenied(getActivity(), failPermissions));
            // 证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                onPermissionListener.hasPermission(succeedPermissions, false);
            }
        }
        // 权限回调结束后要删除集合中的对象，避免重复请求
        mSparseArray.remove(requestCode);
        if (getFragmentManager() != null){
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (getArguments() == null)return;
        if (!isBackCall && requestCode == getArguments().getInt(REQUEST_CODE) ) {
            isBackCall = true;
            // 需要延迟执行，不然有些机型授权了但是获取不到权限
            new Handler(Looper.getMainLooper()).postDelayed(this, 500);
        }
    }
    @Override
    public void run() {
        requestPermission();
    }
}

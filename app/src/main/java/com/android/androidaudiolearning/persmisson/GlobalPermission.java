package com.android.androidaudiolearning.persmisson;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局权限处理
 */
public final class GlobalPermission {
    private Activity mActivity;
    private List<String> mPermissions;
    private boolean mConstant;
    /**
     * 私有化构造函数
     * @param activity
     */
    private GlobalPermission(Activity activity) {
        this.mActivity = activity;
    }
    /**
     * 设置请求的对象
     * @param activity
     * @return
     */
    public static GlobalPermission with(Activity activity) {
        return new GlobalPermission(activity);
    }

    /**
     * 设置权限组
     *
     * @param permissions
     * @return
     */
    public GlobalPermission permission(String... permissions) {
        if (mPermissions == null) {
            mPermissions = new ArrayList<>(permissions.length);
        }
        mPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * 设置权限组
     * @param permissions
     * @return
     */
    public GlobalPermission permission(String[]... permissions) {
        if (mPermissions == null){
            int length = 0;
            for (String[] permission : permissions) {
                length += permission.length;
            }
            mPermissions = new ArrayList<>(length);
            for (String[] group : permissions) {
                mPermissions.addAll(Arrays.asList(group));
            }
        }
        return this;
    }

    /**
     * 设置权限组
     * @param permissions
     * @return
     */
    public GlobalPermission permission(List<String> permissions){
        if (mPermissions == null){
            mPermissions = permissions;
        }else{
            mPermissions.addAll(permissions);
        }
        return this;
    }
    /**
     * 权限拒绝后继续申请，直到授权或是永久拒绝
     * @return
     */
    public GlobalPermission constantRequest(){
        mConstant = true;
        return this;
    }

    /**
     * 请求权限
     * @param permissionListener
     */
    public void request(OnPermissionListener permissionListener){
        if (mActivity == null)throw new IllegalArgumentException("The Context cannot be empty");
        if (mPermissions == null || mPermissions.isEmpty()){
            mPermissions = PermissionUtils.getManifestPermissions(mActivity);
            if (mPermissions == null || mPermissions.isEmpty()){
                throw new IllegalArgumentException("The requested permission cannot be empty");
            }
        }
        if (permissionListener == null){
            throw new IllegalArgumentException("The permission request callback interface must be implemented");
        }
        PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions);
        //获取申请失败后的权限
        ArrayList<String> mFailPermissions = PermissionUtils.getFailPermissions(mActivity, mPermissions);
        if (mFailPermissions == null || mFailPermissions.isEmpty()){
            permissionListener.hasPermission(mPermissions,true);
        }else{
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissions(mActivity,mPermissions);
            //重新申请权限 //AppCompatActivity
            PermissionFragment.newInstance(new ArrayList<>(mPermissions),mConstant).prepareRequest((AppCompatActivity) mActivity,permissionListener);
            //PermissionFragment.newInstance(new ArrayList<>(mPermissions),mConstant).prepareRequest(mActivity,permissionListener);
        }
    }

    /**
     * 检查某些权限是否全部授权
     * @param permissions
     * @return
     */
    public static boolean isHasPermission(Context context,String... permissions){
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, Arrays.asList(permissions));
        return failPermissions == null || failPermissions.isEmpty();
    }
    /**
     * 检查某些权限是否全部授权
     * @param permissions
     * @return
     */
    public static boolean isHasPermission(Context context,String[]... permissions){
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(Arrays.asList(group));
        }
        ArrayList<String> failPermissions = PermissionUtils.getFailPermissions(context, permissionList);
        return failPermissions == null || failPermissions.isEmpty();
    }

    /**
     * 跳转至应用设置界面
     */
    public static void goPermissionSettingsPage(Context context){
        PermissionSettingPage.start(context,false);
    }

    /**
     * 跳转至应用设置界面
     * 是否使用新的任务栈启动
     * @param context
     * @param isNewTask
     */
    public static void goPermissionSettingsPage(Context context,boolean isNewTask){
        PermissionSettingPage.start(context,isNewTask);
    }
}


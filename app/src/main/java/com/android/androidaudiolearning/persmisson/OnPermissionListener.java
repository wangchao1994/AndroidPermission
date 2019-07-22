package com.android.androidaudiolearning.persmisson;

import java.util.List;

/**
 * 权限回调
 */
public interface OnPermissionListener {
    /**
     *权限同意
     * @param granted
     * @param isAll
     */
    void hasPermission(List<String> granted,boolean isAll);

    /**
     * 权限拒绝
     * @param denied
     * @param quick
     */
    void noPermission(List<String> denied,boolean quick);
}

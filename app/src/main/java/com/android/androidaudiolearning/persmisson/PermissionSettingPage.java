package com.android.androidaudiolearning.persmisson;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

final class PermissionSettingPage {
    /**
     * 跳转到应用设置界面
     *
     * @param context
     * @param newTask
     */
    static void start(Context context, boolean newTask) {
        Intent intent = null;
        if (intent == null || !hasIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        if (newTask){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private static boolean hasIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }
}

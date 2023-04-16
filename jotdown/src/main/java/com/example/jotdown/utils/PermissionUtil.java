package com.example.jotdown.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    private final static String TAG = "PermissionUtil";

    // 检查某个权限。返回true表示已启用该权限，返回false表示未启用该权限
    public static boolean checkSelfPermission(Activity act, String permission, int requestCode) {
        Log.d(TAG, "checkPermission: "+permission);
        boolean result = true;
        // 只对Android6.0及以上系统进行校验
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查当前App是否开启了名称为permission的权限
            int check = ContextCompat.checkSelfPermission(act, permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                // 未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, new String[]{permission}, requestCode);
                result = false;
            }
        }
        return result;
    }

    // 检查多个权限。返回true表示已完全启用权限，返回false表示未完全启用权限
    public static boolean checkMultiPermission(Activity act, String[] permissions, int requestCode) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = PackageManager.PERMISSION_GRANTED;
            // 通过权限数组检查是否都开启了这些权限
            for (String permission : permissions) {
                check = ContextCompat.checkSelfPermission(act, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            if (check != PackageManager.PERMISSION_GRANTED) {
                // 未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, permissions, requestCode);
                result = false;
            }
        }
        return result;
    }
}

package id.nyaa.tesvideoplayerii.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    static Activity context;

    public PermissionUtils(Activity context) {
        this.context=context;
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE=200;
    public static void needPermission(int requestCode)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        requestAllPermissions(requestCode);
    }
    private static void requestAllPermissions( int requestCode)
    {

        ActivityCompat.requestPermissions(context,
                new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_CALL_PHONE);

    }
}


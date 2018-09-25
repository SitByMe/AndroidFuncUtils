package ptv.example.zoulinheng.androidutils;

import android.Manifest;
import android.os.Environment;

/**
 * Created by lhZou on 2018/8/28.
 * desc:
 */
public class Constants {

    public static final String[] permissions = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    public static final String[] storagePermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };//红米note5，需要同时开启CAMERA权限，才能真正获取SD卡读取权限
    public static final String USER_ROOT = "user_root";
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String BASE_DOWNLOAD_PATH = BASE_PATH + "/permission_app";//下载资源存放路径
}

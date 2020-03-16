package ptv.example.zoulinheng.androidutils.utils.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    /**
     * 获取请求权限中需要授权的权限（禁止 和 询问）
     *
     * @param context     上下文
     * @param permissions 查询目标集合
     * @return 需要授权的权限集合
     */
    public static String[] getDeniedPermissions(final Context context, final String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 是否包含彻底拒绝了的权限
     */
    public static boolean hasAlwaysDeniedPermission(final Context context, final String... deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!isHasPermission(context, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该权限是否已经被授予
     *
     * @param permission 待判断的权限
     * @return true 已经授予该权限 ，false未授予该权限
     */
    private static boolean isHasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取权限描述数组
     *
     * @param permissions 权限数组
     * @return
     */
    public static String[] getPermissionNames(String... permissions) {
        List<String> perNameList = new ArrayList<>();
        for (String permission : permissions) {
            String permissionName = getPermissionName(permission);
            if (!TextUtils.isEmpty(permissionName)) {
                perNameList.add(permissionName);
            }
        }
        return perNameList.toArray(new String[perNameList.size()]);
    }

    /**
     * 获取权限描述
     *
     * @param permission 权限
     * @return 描述
     */
    private static String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.CALL_PHONE:
                return "电话权限";
            case Manifest.permission.CAMERA:
                return "相机权限";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "位置权限";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "存储权限";
            case Manifest.permission.READ_PHONE_STATE:
                return "手机状态";
            default:
                return "";
        }
    }

    /**
     * 获取已禁止再次询问的权限
     *
     * @param context     上下文
     * @param permissions 权限集合源
     * @return
     */
    public static String[] getAllAlwaysDeniedPermission(Context context, String... permissions) {
        //需要提示的权限（询问）
        String[] shouldShowPermissions = getShouldShowRequestPermissionArray(context, permissions);
        //需要授权的权限（禁止和询问）
        String[] deniedPermissions = getDeniedPermissions(context, permissions);

        List<String> pers = new ArrayList<>();

        for (String s : deniedPermissions) {
            if (!isExist(shouldShowPermissions, s)) {
                pers.add(s);
            }
        }
        return pers.toArray(new String[pers.size()]);
    }

    /**
     * 获取需要权限需要说明提示的权限集合
     *
     * @param context           上下文
     * @param deniedPermissions 查询目标集合
     * @return
     */
    public static String[] getShouldShowRequestPermissionArray(Context context, String... deniedPermissions) {
        List<String> pers = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return pers.toArray(new String[pers.size()]);
        String[] deniedPers = getDeniedPermissions(context, deniedPermissions);
        for (String permission : deniedPers) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                pers.add(permission);
            }
        }
        return pers.toArray(new String[pers.size()]);
    }

    private static boolean isExist(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
}

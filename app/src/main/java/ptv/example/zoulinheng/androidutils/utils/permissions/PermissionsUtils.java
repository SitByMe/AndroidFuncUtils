package ptv.example.zoulinheng.androidutils.utils.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by lhZou on 2018/8/30.
 * desc:权限管理工具类
 */
public class PermissionsUtils {

    private RxPermissions rxPermissions;
    private OnPermissionsRequestResultListener resultListener;

    private PermissionsUtils(RxPermissions rxPermissions, OnPermissionsRequestResultListener resultListener) {
        this.rxPermissions = rxPermissions;
        this.resultListener = resultListener;
    }

    public static PermissionsUtils setOnPermissionsRequestResultListener(RxPermissions rxPermissions, OnPermissionsRequestResultListener onPermissionsRequestResultListener) {
        return new PermissionsUtils(rxPermissions, onPermissionsRequestResultListener);
    }

    /**
     * 申请权限
     *
     * @param permissions 需要申请的权限集合
     */
    @SuppressLint("CheckResult")
    public void requestEachCombined(String... permissions) {
        rxPermissions.requestEachCombined(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            // `permission.name` is granted !
                            if (resultListener != null) resultListener.allDenied();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            if (resultListener != null) resultListener.hasUnDenied();
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            if (resultListener != null) resultListener.hasNeverAgain();
                        }

                        clear();
                    }
                });
    }

    /**
     * 获取请求权限中未获取的权限集合（禁止 和 询问）
     *
     * @param context     上下文
     * @param permissions 查询目标集合
     * @return 需要授权的权限集合
     */
    private static String[] getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 获取需要询问的权限集合（询问）
     *
     * @param context     上下文
     * @param permissions 查询目标集合
     * @return 需要询问的权限集合
     */
    public static String[] getNeedToAskPermissionArray(Context context, String... permissions) {
        List<String> pers = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return pers.toArray(new String[pers.size()]);
        String[] deniedPers = PermissionsUtils.getDeniedPermissions(context, permissions);
        for (String permission : deniedPers) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                pers.add(permission);
            }
        }
        return pers.toArray(new String[pers.size()]);
    }

    /**
     * 获取被禁止的权限集合（询问）
     *
     * @param context     上下文
     * @param permissions 查询目标集合
     * @return 被禁止的权限集合
     */
    public static String[] getNoLongerAskPermissionArray(Context context, String... permissions) {
        //未获取的权限集合（禁止和询问）
        String[] unDeniedPermissions = getDeniedPermissions(context, permissions);
        //需要询问的权限集合（询问）
        String[] shouldShowPermissions = getNeedToAskPermissionArray(context, permissions);

        List<String> pers = new ArrayList<>();
        for (String s : unDeniedPermissions) {
            if (!isExist(shouldShowPermissions, s)) {
                pers.add(s);
            }
        }

        return pers.toArray(new String[pers.size()]);
    }

    /**
     * 是否含有未获取的权限
     *
     * @param context           上下文
     * @param deniedPermissions 查询目标集合
     * @return
     */
    public static boolean hasUnDeniedPermission(Context context, String... deniedPermissions) {
        return getDeniedPermissions(context, deniedPermissions).length != 0;
    }

    /**
     * 是否含有需要询问的权限
     *
     * @param context           上下文
     * @param deniedPermissions 查询目标集合
     * @return
     */
    public static boolean hasNeedToAskPermission(Context context, String... deniedPermissions) {
        return getNeedToAskPermissionArray(context, deniedPermissions).length != 0;
    }

    /**
     * 是否含有被禁止的权限
     *
     * @param context     上下文
     * @param permissions 查询的权限集合
     * @return
     */
    public static boolean hasNoLongerAskPermission(Context context, String... permissions) {
        return getNoLongerAskPermissionArray(context, permissions).length != 0;
    }

    /**
     * 判断该权限是否已经被授予
     *
     * @param permission 待判断的权限
     * @return true 已经授予该权限 ，false未授予该权限
     */
    public static boolean hasPermission(Context context, String permission) {
        return !hasUnDeniedPermission(context, permission);
    }

    /**
     * 跳转到系统的权限设置页面
     *
     * @param act activity
     */
    public static void toSettingAct(Activity act) {
        act.startActivity(PermissionsPageManager.getIntent(act));
    }

    private static boolean isExist(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }

    /**
     * 释放内存
     */
    private void clear() {
        rxPermissions = null;
        resultListener = null;
    }

    public interface OnPermissionsRequestResultListener {
        void allDenied();

        void hasUnDenied();

        void hasNeverAgain();
    }
}

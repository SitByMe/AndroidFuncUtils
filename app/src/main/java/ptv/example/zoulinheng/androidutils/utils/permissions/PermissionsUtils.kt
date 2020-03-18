package ptv.example.zoulinheng.androidutils.utils.permissions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tbruyelle.rxpermissions2.RxPermissions
import ptv.example.zoulinheng.androidutils.constants.TagConstants
import ptv.example.zoulinheng.androidutils.utils.Utils
import ptv.example.zoulinheng.androidutils.utils.baseutils.LogUtils
import java.util.*

/**
 * Created by zoulinheng on 2018/8/30.
 * desc:权限管理工具类
 */
class PermissionsUtils private constructor(rxPermissions: RxPermissions, resultListener: OnPermissionsRequestResultListener) {
    private var rxPermissions: RxPermissions?
    private var resultListener: OnPermissionsRequestResultListener?
    /**
     * 申请权限
     *
     * @param permissions 需要申请的权限集合
     */
    @SuppressLint("CheckResult")
    fun requestEachCombined(vararg permissions: String?) {
        rxPermissions!!.requestEachCombined(*permissions)
                .subscribe { permission ->
                    if (permission.granted) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "granted:" + permission.name)
                        if (resultListener != null) resultListener!!.allDenied()
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "shouldShowRequestPermissionRationale:" + permission.name)
                        // Denied permission without ask never again
                        if (resultListener != null) resultListener!!.hasUnDenied()
                    } else {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "else:" + permission.name)
                        // Denied permission with ask never again
                        // Need to go to the settings
                        if (resultListener != null) resultListener!!.hasNeverAgain()
                    }
                    clear()
                }
    }

    /**
     * 释放内存
     */
    private fun clear() {
        rxPermissions = null
        resultListener = null
    }

    interface OnPermissionsRequestResultListener {
        fun allDenied()
        fun hasUnDenied()
        fun hasNeverAgain()
    }

    companion object {
        @JvmStatic
        fun setOnPermissionsRequestResultListener(rxPermissions: RxPermissions, onPermissionsRequestResultListener: OnPermissionsRequestResultListener): PermissionsUtils {
            return PermissionsUtils(rxPermissions, onPermissionsRequestResultListener)
        }

        /**
         * 获取请求权限中未获取的权限集合（禁止 和 询问）
         *
         * @param permissions 查询目标集合
         * @return 需要授权的权限集合
         */
        private fun getDeniedPermissions(vararg permissions: String): Array<String> {
            val deniedPermissions: MutableList<String> = ArrayList()
            for (permission in permissions) {
                if (isGranted(permission)) {
                    deniedPermissions.add(permission)
                }
            }
            return deniedPermissions.toTypedArray()
        }

        /**
         * 获取需要询问的权限集合（询问）
         *
         * @param context     上下文
         * @param permissions 查询目标集合
         * @return 需要询问的权限集合
         */
        fun getNeedToAskPermissionArray(activity: Activity, vararg permissions: String): Array<String> {
            val pers: MutableList<String> = ArrayList()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return pers.toTypedArray()
            val deniedPers = getDeniedPermissions(*permissions)
            for (permission in deniedPers) {
                if (shouldShowRequestPermissionRationale(activity, permission)) {
                    pers.add(permission)
                }
            }
            return pers.toTypedArray()
        }

        /**
         * 获取被禁止的权限集合（询问）
         *
         * @param activity     上下文
         * @param permissions 查询目标集合
         * @return 被禁止的权限集合
         */
        fun getNoLongerAskPermissionArray(activity: Activity, vararg permissions: String): Array<String> { //未获取的权限集合（禁止和询问）
            val unDeniedPermissions = getDeniedPermissions(*permissions)
            //需要询问的权限集合（询问）
            val shouldShowPermissions = getNeedToAskPermissionArray(activity, *permissions)
            val pers: MutableList<String> = ArrayList()
            for (s in unDeniedPermissions) {
                if (!isExist(shouldShowPermissions, s)) {
                    pers.add(s)
                }
            }
            return pers.toTypedArray()
        }

        /**
         * 是否含有未获取的权限
         *
         * @param deniedPermissions 查询目标集合
         * @return
         */
        @JvmStatic
        fun hasUnDeniedPermission(vararg deniedPermissions: String): Boolean {
            return getDeniedPermissions(*deniedPermissions).isNotEmpty()
        }

        /**
         * 是否含有需要询问的权限
         *
         * @param activity           上下文
         * @param deniedPermissions 查询目标集合
         * @return
         */
        fun hasNeedToAskPermission(activity: Activity, vararg deniedPermissions: String): Boolean {
            return getNeedToAskPermissionArray(activity, *deniedPermissions).isNotEmpty()
        }

        /**
         * 是否含有被禁止的权限
         *
         * @param activity     上下文
         * @param permissions 查询的权限集合
         * @return
         */
        fun hasNoLongerAskPermission(activity: Activity, vararg permissions: String): Boolean {
            return getNoLongerAskPermissionArray(activity, *permissions).isNotEmpty()
        }

        /**
         * 判断该权限是否已经被授予
         *
         * @param permission 待判断的权限
         * @return true 已经授予该权限 ，false未授予该权限
         */
        fun hasPermission(permission: String): Boolean {
            return !hasUnDeniedPermission(permission)
        }

        /**
         * 跳转到系统的权限设置页面
         *
         * @param act activity
         */
        @JvmStatic
        fun toSettingAct(act: Activity) {
            act.startActivity(PermissionsPageManager.getIntent(act))
        }

        private fun isExist(arr: Array<String>, targetValue: String): Boolean {
            for (s in arr) {
                if (s == targetValue) return true
            }
            return false
        }

        private fun isGranted(permission: String): Boolean {
            return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(Utils.getApp(), permission))
        }

        private fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    init {
        this.rxPermissions = rxPermissions
        this.resultListener = resultListener
    }
}
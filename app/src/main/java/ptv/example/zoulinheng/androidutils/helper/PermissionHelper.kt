package ptv.example.zoulinheng.androidutils.helper

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import ptv.example.zoulinheng.androidutils.utils.Utils
import java.util.*

object PermissionHelper {

    fun request(activity: Activity, onRequestCallBack: OnRequestCallBack?,
                @PermissionConstants.Permission vararg permissions: String) {
        PermissionUtils.permission(*permissions).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                onRequestCallBack?.allGranted()
            }

            override fun onDenied() {
                val needAsk: Array<String> = getNeedToAskPermissionArray(activity, *permissions)
                val unAskAgain = getUnAskAgainPermissionArray(activity, *permissions)
                if (needAsk.isNotEmpty()) {
                    onRequestCallBack?.hasNeedAsk(getNeedToAskPermissionArray(activity, *permissions))
                } else if (unAskAgain.isNotEmpty()) {
                    onRequestCallBack?.allUnAskAgain(getUnAskAgainPermissionArray(activity, *permissions))
                }
            }
        }).request()
    }

    /**
     * 获取[permissions]中未授予的权限集合（禁止 和 询问）
     */
    private fun getDeniedPermissions(vararg permissions: String): Array<String> {
        val deniedPermissions: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return deniedPermissions.toTypedArray()
        for (permission in permissions) {
            if (!isGranted(permission)) {
                deniedPermissions.add(permission)
            }
        }
        return deniedPermissions.toTypedArray()
    }

    /**
     * 获取[permissions]中需要询问的权限集合（询问）
     */
    @PermissionConstants.Permission
    private fun getNeedToAskPermissionArray(activity: Activity, @PermissionConstants.Permission vararg permissions: String): Array<String> {
        val needAskPermissions: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return needAskPermissions.toTypedArray()
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                needAskPermissions.add(permission)
            }
        }
        return needAskPermissions.toTypedArray()
    }

    /**
     * 获取[permissions]中被禁止且不再询问的权限集合（不再询问）
     */
    @PermissionConstants.Permission
    fun getUnAskAgainPermissionArray(activity: Activity, @PermissionConstants.Permission vararg permissions: String): Array<String> { //未获取的权限集合（禁止和询问）
        val unAskAgainPermissions: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (!isGranted(permission) and !shouldShowRequestPermissionRationale(activity, permission)) {
                unAskAgainPermissions.add(permission)
            }
        }
        return unAskAgainPermissions.toTypedArray()
    }

    private fun isGranted(permission: String): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(Utils.getApp(), permission))
    }

    private fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    @JvmStatic
    @PermissionConstants.Permission
    fun getPermissionNames(@PermissionConstants.Permission permissions: Array<String>): Array<String> {
        return permissions
    }

    interface OnRequestCallBack {
        fun allGranted()
        fun hasNeedAsk(@PermissionConstants.Permission needAsks: Array<String>)
        fun allUnAskAgain(@PermissionConstants.Permission unAskAgains: Array<String>)
    }
}
package ptv.example.zoulinheng.androidutils.utils.permissions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import ptv.example.zoulinheng.androidutils.utils.Utils
import java.util.ArrayList

/**
 * Created by zoulinheng on 2020/2/24.
 * desc: 权限操作类
 */
class PermissionHelper {
    companion object {

        /**
         * 申请权限
         * @param activity FragmentActivity
         * @param callBack 回调
         * @param permissions 需要申请的权限
         */
        @SuppressLint("CheckResult")
        fun permissions(activity: FragmentActivity, callBack: CallBack, vararg permissions: String) {
            val rxPermissions = RxPermissions(activity)
            rxPermissions.requestEachCombined(*permissions)
                    ?.subscribe { permission ->
                        when {
                            permission.granted -> {//全部权限均已获取
                                val grantedList = getDeniedPermissions(*permission.name.split(",").toTypedArray())
                                callBack.onGranted(grantedList)
                            }
                            permission.shouldShowRequestPermissionRationale -> {//有需要询问的权限
                                val shouldShowList = getNeedToAskPermissionArray(activity, *permission.name.split(",").toTypedArray())
                                callBack.shouldShowRequest(shouldShowList)
                            }
                            else -> {//此处对已不再询问的权限做操作
                                val noAgainAskList = getNoAskAgainPermissionArray(activity, *permission.name.split(",").toTypedArray())
                                callBack.noAskAgain(noAgainAskList)
//                                toSettingAct(activity)
                            }
                        }
                    }
        }

        private fun toSettingAct(activity: Activity) {
            activity.startActivity(PermissionsPageManager.getIntent(activity))
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
        fun getNeedToAskPermissionArray(context: Context, vararg permissions: String): Array<String> {
            val pers: MutableList<String> = ArrayList()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return pers.toTypedArray()
            val deniedPers = getDeniedPermissions(*permissions)
            for (permission in deniedPers) {
                if (shouldShowRequestPermissionRationale(context as Activity, permission)) {
                    pers.add(permission)
                }
            }
            return pers.toTypedArray()
        }

        /**
         * 获取被禁止的权限集合（询问）
         *
         * @param context     上下文
         * @param permissions 查询目标集合
         * @return 被禁止的权限集合
         */
        fun getNoAskAgainPermissionArray(context: Context, vararg permissions: String): Array<String> { //未获取的权限集合（禁止和询问）
            val unDeniedPermissions = getDeniedPermissions(*permissions)
            //需要询问的权限集合（询问）
            val shouldShowPermissions = getNeedToAskPermissionArray(context, *permissions)
            val pers: MutableList<String> = ArrayList()
            for (s in unDeniedPermissions) {
                if (!shouldShowPermissions.contains(s)) {
                    pers.add(s)
                }
            }
            return pers.toTypedArray()
        }

        private fun isGranted(permission: String): Boolean {
            return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(Utils.getApp(), permission))
        }

        private fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    interface CallBack {
        fun onGranted(permissions: Array<String>)
        fun shouldShowRequest(permissions: Array<String>)
        fun noAskAgain(permissions: Array<String>)
    }
}
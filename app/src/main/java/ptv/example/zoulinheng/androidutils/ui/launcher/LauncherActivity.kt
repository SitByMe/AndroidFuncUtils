package ptv.example.zoulinheng.androidutils.ui.launcher

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.LogUtils
import ptv.example.zoulinheng.androidutils.R
import ptv.example.zoulinheng.androidutils.constants.PerConstants
import ptv.example.zoulinheng.androidutils.constants.TagConstants
import ptv.example.zoulinheng.androidutils.databinding.ActivityLauncherBinding
import ptv.example.zoulinheng.androidutils.helper.PermissionHelper.OnRequestCallBack
import ptv.example.zoulinheng.androidutils.helper.PermissionHelper.request
import ptv.example.zoulinheng.androidutils.ui.BaseActivity
import ptv.example.zoulinheng.androidutils.ui.download.DownloadDemoActivity
import ptv.example.zoulinheng.androidutils.utils.apputils.StartActivityUtils
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsPageManager
import ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow
import ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow.PopupDismissListener

class LauncherActivity : BaseActivity() {
    private lateinit var binding: ActivityLauncherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.bind(LayoutInflater.from(this).inflate(R.layout.activity_launcher, null))
        setContentView(binding.root)
    }

    override fun onStop() {
        super.onStop()
        if (popupWindow != null && popupWindow!!.isShowing) popupWindow!!.dismiss()
    }

    fun requestPermissionUnForce(view: View) {
        requestPermission(false)
    }

    fun requestPermissionForce(view: View) {
        requestPermission(true)
    }

    private fun requestPermission(force: Boolean) {
        request(this, object : OnRequestCallBack {
            override fun allGranted() {
                LogUtils.e(TagConstants.TAG_PERMISSION, "allGranted")
            }

            override fun hasNeedAsk(needAsks: Array<String>) {
                LogUtils.e(TagConstants.TAG_PERMISSION, "hasNeedAsk - " + needAsks.contentToString())
                if (force) {
                    requestPermission(true)
                }
            }

            override fun allUnAskAgain(unAskAgains: Array<String>) {
                LogUtils.e(TagConstants.TAG_PERMISSION, "allUnAskAgain - " + unAskAgains.contentToString())
                openPermissionPopupWindow(binding.clMain, unAskAgains, force)
            }
        }, *PerConstants.permissions)
    }

    private fun toSettingAct(act: Activity) {
        act.startActivity(PermissionsPageManager.getIntent(act))
    }

    private var popupWindow: PermissionPopupWindow? = null
    /**
     * 打开授权弹窗
     *
     * @param force 是否强制跳转到权限设置页面
     */
    private fun openPermissionPopupWindow(parent: View, @PermissionConstants.Permission permissions: Array<String>, force: Boolean) {
        val ppw = PermissionPopupWindow(this@LauncherActivity, permissions, force,
                PopupDismissListener { mForce, _, closeOption ->
                    when (closeOption) {
                        PermissionPopupWindow.CLOSE_RESULT.CLOSE_OPTION_REQUEST_AGAIN -> requestPermission(mForce)
                        PermissionPopupWindow.CLOSE_RESULT.CLOSE_OPTION_TO_SETTING_ACT -> toSettingAct(this@LauncherActivity)
                        PermissionPopupWindow.CLOSE_RESULT.CLOSE_OPTION_NON, PermissionPopupWindow.CLOSE_RESULT.CLOSE_OPTION_EXIT -> {
                        }
                    }
                })
        popupWindow = ppw
        ppw.showPopupWindow(parent)
    }

    fun downloadDemo(view: View) {
        StartActivityUtils.startActivity(this, DownloadDemoActivity::class.java)
    }

    fun enterApp(view: View) {}
}
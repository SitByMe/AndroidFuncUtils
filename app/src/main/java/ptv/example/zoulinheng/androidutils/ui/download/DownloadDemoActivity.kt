package ptv.example.zoulinheng.androidutils.ui.download

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import ptv.example.zoulinheng.androidutils.R
import ptv.example.zoulinheng.androidutils.constants.PerConstants
import ptv.example.zoulinheng.androidutils.databinding.ActivityDownloadDemoBinding
import ptv.example.zoulinheng.androidutils.databinding.DialogLayoutDownloadDemoBinding
import ptv.example.zoulinheng.androidutils.download.DownLoadManager
import ptv.example.zoulinheng.androidutils.download.DownLoadService
import ptv.example.zoulinheng.androidutils.download.TaskInfo
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsUtils.Companion.hasDeniedPermission
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsUtils.Companion.toSettingAct

class DownloadDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadDemoBinding
    private lateinit var adapter: DownloadListAdapter
    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private lateinit var manager: DownLoadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadDemoBinding.bind(LayoutInflater.from(this).inflate(R.layout.activity_download_demo, null))
        this.setContentView(binding.root)
        val s = ActivityUtils.getLauncherActivity()
        //下载管理器需要启动一个Service,在刚启动应用的时候需要等Service启动起来后才能获取下载管理器，所以稍微延时获取下载管理器
        handler.sendEmptyMessageDelayed(1, 50)
    }

    private var handler: Handler = object : Handler(Looper.myLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            manager = DownLoadService.getDownLoadManager()    /*获取下载管理器*/
            manager.changeUser(PerConstants.USER_ROOT)        /*设置用户ID，客户端切换用户时可以显示相应用户的下载任务*/
            /*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*/manager.setSupportBreakpoint(true)
            adapter = DownloadListAdapter(this@DownloadDemoActivity, manager)
            binding.lvData.adapter = adapter
            binding.btnChangeUser.text = "用户 : ${manager.userID}"
        }
    }

    fun addDownloadTask(view: View) {
        if (hasDeniedPermission(*PerConstants.storagePermissions)) {
            toSettingAct(this)
            return
        }
        val dialogBinding = DialogLayoutDownloadDemoBinding.bind(LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout_download_demo, null))
        AlertDialog.Builder(this@DownloadDemoActivity)
                .setView(dialogBinding.root)
                .setPositiveButton("确定") { _, _ ->
                    if (dialogBinding.etFileName.text.isEmpty() || dialogBinding.etFileUrl.text.isEmpty()) {
                        ToastUtils.showShort("请输入文件名和下载路径")
                    } else {
                        val info = TaskInfo()
                        info.fileName = dialogBinding.etFileName.text.toString()
                        /*服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况*/
                        info.taskID = dialogBinding.etFileName.text.toString()
                        info.isOnDownloading = true
                        /*将任务添加到下载队列，下载器会自动开始下载*/
                        manager.addTask(dialogBinding.etFileName.text.toString()
                                , dialogBinding.etFileUrl.text.toString()
                                , dialogBinding.etFileName.text.toString())
                        adapter.addItem(info)
                    }
                }.setNegativeButton("取消", null)
                .show()
    }

    fun changeUser(view: View) {
        AlertDialog.Builder(this@DownloadDemoActivity).setTitle("切换用户")
                .setPositiveButton("zohar") { _, _ ->
                    manager.changeUser("zohar")
                    binding.btnChangeUser.text = "用户: " + "zohar"
                    adapter.setListData(manager.getAllTask(true))
                }.setNegativeButton(PerConstants.USER_ROOT) { _, _ ->
                    manager.changeUser(PerConstants.USER_ROOT)
                    binding.btnChangeUser.text = "用户 : ${PerConstants.USER_ROOT}"
                    adapter.setListData(manager.getAllTask(true))
                }.show()
    }
}
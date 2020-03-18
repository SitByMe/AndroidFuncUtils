package ptv.example.zoulinheng.androidutils.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ptv.example.zoulinheng.androidutils.Constants;
import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.download.DownLoadManager;
import ptv.example.zoulinheng.androidutils.download.DownLoadService;
import ptv.example.zoulinheng.androidutils.download.TaskInfo;
import ptv.example.zoulinheng.androidutils.adapters.DownloadListAdapter;
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsUtils;

public class DownloadDemoActivity extends AppCompatActivity {
    private Button btnUser;
    private ListView listView;
    private EditText nameText;
    private EditText urlText;
    private DownloadListAdapter adapter;

    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private DownLoadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_download_demo);

        //下载管理器需要启动一个Service,在刚启动应用的时候需要等Service启动起来后才能获取下载管理器，所以稍微延时获取下载管理器
        handler.sendEmptyMessageDelayed(1, 50);

        btnUser = findViewById(R.id.btn_change_user);
        listView = findViewById(R.id.listView);
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*获取下载管理器*/
            manager = DownLoadService.getDownLoadManager();
            /*设置用户ID，客户端切换用户时可以显示相应用户的下载任务*/
            manager.changeUser(Constants.USER_ROOT);
            /*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*/
            manager.setSupportBreakpoint(true);
            adapter = new DownloadListAdapter(DownloadDemoActivity.this, manager);
            listView.setAdapter(adapter);
            btnUser.setText("用户 : ".concat(manager.getUserID()));
        }
    };

    public void addDownloadTask(View view) {
        if (PermissionsUtils.hasUnDeniedPermission(Constants.storagePermissions)) {
            PermissionsUtils.toSettingAct(this);
            return;
        }

        View showView = LayoutInflater.from(DownloadDemoActivity.this).inflate(R.layout.dialog_layout_download_demo, null);
        nameText = showView.findViewById(R.id.file_name);
        urlText = showView.findViewById(R.id.file_url);
        new AlertDialog.Builder(DownloadDemoActivity.this).setView(showView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("".equals(nameText.getText().toString()) || "".equals(urlText.getText().toString())) {
                    Toast.makeText(DownloadDemoActivity.this, "请输入文件名和下载路径", Toast.LENGTH_SHORT).show();
                } else {
                    TaskInfo info = new TaskInfo();
                    info.setFileName(nameText.getText().toString());
                    /*服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况*/
                    info.setTaskID(nameText.getText().toString());
                    info.setOnDownloading(true);
                    /*将任务添加到下载队列，下载器会自动开始下载*/
                    manager.addTask(nameText.getText().toString(), urlText.getText().toString(), nameText.getText().toString());
                    adapter.addItem(info);
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    public void changeUser(View view) {
        new AlertDialog.Builder(DownloadDemoActivity.this).setTitle("切换用户")
                .setPositiveButton("zohar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.changeUser("zohar");
                        btnUser.setText("用户: ".concat("zohar"));
                        adapter.setListData(manager.getAllTask(true));

                    }
                }).setNegativeButton(Constants.USER_ROOT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.changeUser(Constants.USER_ROOT);
                btnUser.setText("用户 : ".concat(Constants.USER_ROOT));
                adapter.setListData(manager.getAllTask(true));
            }
        }).show();
    }
}

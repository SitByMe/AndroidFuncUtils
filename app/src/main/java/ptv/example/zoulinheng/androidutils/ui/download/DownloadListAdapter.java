package ptv.example.zoulinheng.androidutils.ui.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;

import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.download.DownLoadListener;
import ptv.example.zoulinheng.androidutils.download.DownLoadManager;
import ptv.example.zoulinheng.androidutils.download.TaskInfo;
import ptv.example.zoulinheng.androidutils.download.dbcontrol.FileHelper;
import ptv.example.zoulinheng.androidutils.download.dbcontrol.bean.SQLDownLoadInfo;
import ptv.example.zoulinheng.androidutils.utils.viewutils.ToastUtils;
import ptv.example.zoulinheng.androidutils.utils.zip.ZipUtils;

public class DownloadListAdapter extends BaseAdapter {
    private boolean containsDownloaded;
    private ArrayList<TaskInfo> listData;
    private Context myContext;
    private DownLoadManager downLoadManager;

    public DownloadListAdapter(Context context, DownLoadManager downLoadManager) {
        containsDownloaded = true;
        this.myContext = context;
        this.downLoadManager = downLoadManager;
        listData = downLoadManager.getAllTask(containsDownloaded);
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(myContext).inflate(R.layout.list_item_layout_download, null);
            holder.fileName = convertView.findViewById(R.id.file_name);
            holder.textProgress = convertView.findViewById(R.id.file_size);
            holder.fileProgress = convertView.findViewById(R.id.progressbar);
            holder.downloadIcon = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.fileName.setText(listData.get(position).getFileName());
        holder.downloadIcon.setOnCheckedChangeListener(new CheckedChangeListener(position));
        if (listData.get(position).downloadCompleted()) {
            holder.fileProgress.setVisibility(View.INVISIBLE);
            holder.fileProgress.setProgress(100);
            holder.textProgress.setText("下载完成");
            holder.downloadIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.fileProgress.setVisibility(View.VISIBLE);
            holder.fileProgress.setProgress(listData.get(position).getProgress());
            holder.textProgress.setText(String.valueOf(listData.get(position).getProgress()).concat("%"));
            holder.downloadIcon.setVisibility(View.VISIBLE);
            if (listData.get(position).isOnDownloading()) {
                holder.downloadIcon.setChecked(true);
            } else {
                holder.downloadIcon.setChecked(false);
            }
        }
        return convertView;
    }

    static class Holder {
        TextView fileName = null;
        TextView textProgress = null;
        ProgressBar fileProgress = null;
        CheckBox downloadIcon = null;
    }

    public void addItem(TaskInfo taskinfo) {
        this.listData.add(taskinfo);
        this.notifyDataSetInvalidated();
    }

    public void setListData(ArrayList<TaskInfo> listData) {
        this.listData = listData;
        this.notifyDataSetInvalidated();
    }

    class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int position;

        public CheckedChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // 继续下载
                listData.get(position).setOnDownloading(true);
                downLoadManager.startTask(listData.get(position).getTaskID());
            } else {
                //停止下载
                listData.get(position).setOnDownloading(false);
                downLoadManager.stopTask(listData.get(position).getTaskID());
            }
            DownloadListAdapter.this.notifyDataSetChanged();
        }
    }

    private class DownloadManagerListener implements DownLoadListener {

        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {

        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            //根据监听到的信息查找列表相对应的任务，更新相应任务的进度
            for (TaskInfo taskInfo : listData) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    DownloadListAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，删除对应的任务
            for (TaskInfo taskInfo : listData) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    if (!containsDownloaded) {
                        listData.remove(taskInfo);
                    }
                    DownloadListAdapter.this.notifyDataSetChanged();
                    if (ZipUtils.isZip(taskInfo.getFileName())) {
                        unZip(taskInfo);
                    }
                    ToastUtils.showShort(taskInfo.getFileName().concat("-下载完成"));
                    LogUtils.i(taskInfo.getFileName().concat("-下载完成"));
                    break;
                }
            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，停止该任务
            for (TaskInfo taskInfo : listData) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setOnDownloading(false);
                    DownloadListAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private void unZip(final TaskInfo taskInfo) {
        ZipUtils.UnZipFile(FileHelper.getDownloadFileSavePath(taskInfo.getTaskID(), taskInfo.getFileName()), FileHelper.getFileUnZipPath(taskInfo.getTaskID()),
                new ZipUtils.ZipListener() {
                    public void zipSuccess() {
                        System.out.println("success!");
                        ToastUtils.showShort(taskInfo.getTaskID().concat("_").concat(taskInfo.getFileName()).concat("_").concat("解压完成"));
                        LogUtils.i(taskInfo.getTaskID().concat("_").concat(taskInfo.getFileName()).concat("_").concat("解压完成"));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ZipUtils.zip(FileHelper.getFileUnZipPath(taskInfo.getTaskID()), FileHelper.getFileZipFilePath(taskInfo.getFileName()));//你要压缩的文件夹 和 压缩后的文件
                                LogUtils.i(taskInfo.getFileName().concat("压缩成功！"));
                            }
                        }).start();
                    }

                    public void zipStart() {
                        System.out.println("start!");
                    }

                    public void zipProgress(int progress) {
                       /* Message message = new Message();
                        message.what = 1;
                        message.obj = String.valueOf(progress).concat("%");
                        handler.sendMessage(message);*/
                    }

                    public void zipFail() {
                        System.out.println("failed!");
                    }
                });
    }
}

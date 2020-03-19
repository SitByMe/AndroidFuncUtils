
package ptv.example.zoulinheng.androidutils.download;

import android.content.Context;
import android.content.SharedPreferences;

import ptv.example.zoulinheng.androidutils.constants.PerConstants;
import ptv.example.zoulinheng.androidutils.download.DownLoader.*;
import ptv.example.zoulinheng.androidutils.download.dbcontrol.DataKeeper;
import ptv.example.zoulinheng.androidutils.download.dbcontrol.FileHelper;
import ptv.example.zoulinheng.androidutils.download.dbcontrol.bean.SQLDownLoadInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lhZou on 2018/8/28.
 * desc:下载管理类
 */
public class DownLoadManager {
    private Context myContext;

    private ArrayList<DownLoader> taskList = new ArrayList<>();

    private final int MAX_DOWNLOADING_TASK = 5; // 最大同时下载数

    private DownLoader.DownLoadSuccess downloadSuccessListener = null;

    // 服务器是否支持断点续传
    private boolean isSupportBreakpoint = false;

    // 线程池
    private ThreadPoolExecutor pool;

    // 用户ID,默认值man
    private String userID = PerConstants.USER_ROOT;

    private SharedPreferences sharedPreferences;

    private DownLoadListener allTaskListener;

    public DownLoadManager(Context context) {
        myContext = context;
        init(context);
    }

    private void init(Context context) {
        pool = new ThreadPoolExecutor(
                MAX_DOWNLOADING_TASK, MAX_DOWNLOADING_TASK, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000));

        downloadSuccessListener = new DownLoadSuccess() {
            @Override
            public void onTaskSuccess(String TaskID) {
                int taskSize = taskList.size();
                for (int i = 0; i < taskSize; i++) {
                    DownLoader deleteDownloader = taskList.get(i);
                    if (deleteDownloader.getTaskID().equals(TaskID)) {
                        taskList.remove(deleteDownloader);
                        return;
                    }
                }
            }
        };
        sharedPreferences = myContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("UserID", PerConstants.USER_ROOT);
        recoverData(myContext, userID);
    }

    /**
     * 从数据库恢复下载任务信息
     *
     * @param context 上下文
     * @param userID  用户ID
     */
    private void recoverData(Context context, String userID) {
        stopAllTask();
        taskList = new ArrayList<>();
        DataKeeper datakeeper = new DataKeeper(context);
        ArrayList<SQLDownLoadInfo> sqlDownloadInfoList;
        if (userID == null) {
            sqlDownloadInfoList = datakeeper.getAllDownLoadInfo();
        } else {
            sqlDownloadInfoList = datakeeper.getUserDownLoadInfo(userID);
        }
        if (sqlDownloadInfoList.size() > 0) {
            int listSize = sqlDownloadInfoList.size();
            for (int i = 0; i < listSize; i++) {
                SQLDownLoadInfo sqlDownLoadInfo = sqlDownloadInfoList.get(i);
                DownLoader sqlDownLoader = new DownLoader(context, sqlDownLoadInfo, pool, userID, isSupportBreakpoint, false);
                sqlDownLoader.setDownLodSuccessListener(downloadSuccessListener);
                sqlDownLoader.setDownLoadListener("public", allTaskListener);
                taskList.add(sqlDownLoader);
            }
        }
    }

    /**
     * 设置下载管理是否支持断点续传
     *
     * @param isSupportBreakpoint
     */
    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
        if ((!this.isSupportBreakpoint) && isSupportBreakpoint) {
            int taskSize = taskList.size();
            for (int i = 0; i < taskSize; i++) {
                DownLoader downloader = taskList.get(i);
                downloader.setSupportBreakpoint(true);
            }
        }
        this.isSupportBreakpoint = isSupportBreakpoint;
    }

    /**
     * 切换用户
     *
     * @param userID 用户ID
     */
    public void changeUser(String userID) {
        this.userID = userID;
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString("UserID", userID);
        editor.apply();// 提交修改
        FileHelper.setUserID(userID);
        recoverData(myContext, userID);
    }

    public String getUserID() {
        return userID;
    }

    /**
     * 增加一个任务，默认开始执行下载任务
     *
     * @param TaskID   任务号
     * @param url      请求下载的路径
     * @param fileName 文件名
     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
     */
    public int addTask(String TaskID, String url, String fileName) {
        return addTask(TaskID, url, fileName, null);
    }

    /**
     * 增加一个任务，默认开始执行下载任务
     *
     * @param TaskID   任务号
     * @param url      请求下载的路径
     * @param fileName 文件名
     * @param filepath 下载到本地的路径
     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
     */
    public int addTask(String TaskID, String url, String fileName, String filepath) {
        if (TaskID == null) {
            TaskID = fileName;
        }
        int state = getAttachmentState(TaskID, fileName, filepath);
        if (state != 1) {
            return state;
        }

        SQLDownLoadInfo downloadInfo = new SQLDownLoadInfo();
        downloadInfo.setUserID(userID);
        downloadInfo.setDownloadSize(0);
        downloadInfo.setFileSize(0);
        downloadInfo.setTaskID(TaskID);
        downloadInfo.setFileName(fileName);
        downloadInfo.setUrl(url);
        if (filepath == null) {
            downloadInfo.setFilePath(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
        } else {
            downloadInfo.setFilePath(filepath);
        }
        DownLoader taskDownLoader = new DownLoader(myContext, downloadInfo, pool, userID, isSupportBreakpoint, true);
        taskDownLoader.setDownLodSuccessListener(downloadSuccessListener);
        if (isSupportBreakpoint) {
            taskDownLoader.setSupportBreakpoint(true);
        } else {
            taskDownLoader.setSupportBreakpoint(false);
        }
        taskDownLoader.start();
        taskDownLoader.setDownLoadListener("public", allTaskListener);
        taskList.add(taskDownLoader);
        return 1;
    }

    /**
     * 获取附件状态
     *
     * @param TaskID   任务号
     * @param fileName 文件名
     * @param filepath 下载到本地的路径
     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
     */
    private int getAttachmentState(String TaskID, String fileName, String filepath) {

        int taskSize = taskList.size();
        for (int i = 0; i < taskSize; i++) {
            DownLoader downloader = taskList.get(i);
            if (downloader.getTaskID().equals(TaskID)) {
                return 0;
            }
        }
        File file = null;
        if (filepath == null) {
            file = new File(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
            if (file.exists()) {
                return -1;
            }
        } else {
            file = new File(filepath);
            if (file.exists()) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * 删除一个任务，包括已下载的本地文件
     *
     * @param taskID
     */
    public void deleteTask(String taskID) {
        int taskSize = taskList.size();
        for (int i = 0; i < taskSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            if (deleteDownloader.getTaskID().equals(taskID)) {
                deleteDownloader.destroy();
                taskList.remove(deleteDownloader);
                break;
            }
        }
    }

    /**
     * 获取当前任务列表的所有任务ID
     *
     * @return
     */
    public ArrayList<String> getAllTaskID() {
        ArrayList<String> taskIDList = new ArrayList<>();
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            taskIDList.add(deleteDownloader.getTaskID());
        }
        return taskIDList;
    }

    /**
     * 获取当前任务列表的所有任务，以TaskInfo列表的方式返回
     *
     * @param containsDownloaded 是否包含已下载的内容
     * @return
     */
    public ArrayList<TaskInfo> getAllTask(boolean containsDownloaded) {
        ArrayList<TaskInfo> taskInfoList = new ArrayList<>();
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            SQLDownLoadInfo sqldownloadinfo = deleteDownloader.getSQLDownLoadInfo();
            TaskInfo taskinfo = new TaskInfo();
            taskinfo.setFileName(sqldownloadinfo.getFileName());
            taskinfo.setOnDownloading(deleteDownloader.isDownLoading());
            taskinfo.setTaskID(sqldownloadinfo.getTaskID());
            taskinfo.setFileSize(sqldownloadinfo.getFileSize());
            taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
            // 这段代码是为了修正数据
            if (taskinfo.downloadCompleted() && !FileHelper.fileIsExisted(sqldownloadinfo.getTaskID(), sqldownloadinfo.getFileName())) {
                deleteTask(sqldownloadinfo.getTaskID());
                FileHelper.deleteBook(this, sqldownloadinfo.getTaskID(), sqldownloadinfo.getFileName());
                continue;
            }
            taskInfoList.add(taskinfo);
        }
        if (containsDownloaded) {
            List<TaskInfo> localTaskList = new ArrayList<>();
            local:
            for (TaskInfo localTaskInfo : FileHelper.detectLocalRes()) {
                for (TaskInfo taskInfo : taskInfoList) {
                    if (taskInfo.getTaskID().equals(localTaskInfo.getTaskID())) {
                        continue local;
                    }
                }
                localTaskList.add(localTaskInfo);
            }
            taskInfoList.addAll(localTaskList);
        }
        return taskInfoList;
    }

    public ArrayList<TaskInfo> getAllTask() {
        return getAllTask(false);
    }

    /**
     * 根据任务ID开始执行下载任务
     *
     * @param taskID
     */
    public void startTask(String taskID) {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            if (deleteDownloader.getTaskID().equals(taskID)) {
                deleteDownloader.start();
                break;
            }
        }
    }

    /**
     * 根据任务ID停止相应的下载任务
     *
     * @param taskID
     */
    public void stopTask(String taskID) {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            if (deleteDownloader.getTaskID().equals(taskID)) {
                deleteDownloader.stop();
                break;
            }
        }
    }

    /**
     * 开始当前任务列表里的所有任务
     */
    public void startAllTask() {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            deleteDownloader.start();
        }
    }

    /**
     * 停止当前任务列表里的所有任务
     */
    public void stopAllTask() {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            deleteDownloader.stop();
        }
    }

    /**
     * 根据任务ID将监听器设置到相对应的下载任务
     *
     * @param taskID
     * @param listener
     */
    public void setSingleTaskListener(String taskID, DownLoadListener listener) {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            if (deleteDownloader.getTaskID().equals(taskID)) {
                deleteDownloader.setDownLoadListener("private", listener);
                break;
            }
        }
    }

    /**
     * 将监听器设置到当前任务列表所有任务
     *
     * @param listener
     */
    public void setAllTaskListener(DownLoadListener listener) {
        allTaskListener = listener;
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            deleteDownloader.setDownLoadListener("public", listener);
        }
    }

    /**
     * 根据任务ID移除相对应的下载任务的监听器
     *
     * @param taskID
     */
    public void removeDownLoadListener(String taskID) {
        DownLoader downLoader = getDownloader(taskID);
        if (downLoader != null) {
            downLoader.removeDownLoadListener("private");
        }
    }

    /**
     * 删除监听所有任务的监听器
     */
    public void removeAllDownLoadListener() {
        int listSize = taskList.size();
        for (int i = 0; i < listSize; i++) {
            DownLoader deleteDownloader = taskList.get(i);
            deleteDownloader.removeDownLoadListener("public");
        }
    }

    /**
     * 根据任务号获取当前任务是否正在下载
     *
     * @param taskID
     * @return
     */
    public boolean isTaskdownloading(String taskID) {
        DownLoader downLoader = getDownloader(taskID);
        if (downLoader != null) {
            return downLoader.isDownLoading();
        }
        return false;
    }

    /**
     * 根据附件id获取下载器
     */
    private DownLoader getDownloader(String taskID) {
        for (int i = 0; i < taskList.size(); i++) {
            DownLoader downloader = taskList.get(i);
            if (taskID != null && taskID.equals(downloader.getTaskID())) {
                return downloader;
            }
        }
        return null;
    }

    /**
     * 根据id获取下载任务列表中某个任务
     */
    public TaskInfo getTaskInfo(String taskID) {
        DownLoader downloader = getDownloader(taskID);
        if (downloader == null) {
            return null;
        }
        SQLDownLoadInfo sqldownloadinfo = downloader.getSQLDownLoadInfo();
        if (sqldownloadinfo == null) {
            return null;
        }
        TaskInfo taskinfo = new TaskInfo();
        taskinfo.setFileName(sqldownloadinfo.getFileName());
        taskinfo.setOnDownloading(downloader.isDownLoading());
        taskinfo.setTaskID(sqldownloadinfo.getTaskID());
        taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
        taskinfo.setFileSize(sqldownloadinfo.getFileSize());
        return taskinfo;
    }
}

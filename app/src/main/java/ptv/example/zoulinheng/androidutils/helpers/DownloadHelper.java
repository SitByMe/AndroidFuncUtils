package ptv.example.zoulinheng.androidutils.helpers;

import ptv.example.zoulinheng.androidutils.download.DownLoadManager;

public class DownloadHelper {
    public void beginDownload(DownLoadManager manager, String TaskID, String url, String fileName) {
        manager.addTask(TaskID, url, fileName);
    }
}

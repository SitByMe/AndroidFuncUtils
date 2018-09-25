package ptv.example.zoulinheng.androidutils.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by lhZou on 2018/8/28.
 * desc:下载器后台服务
 */
public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
    }
}

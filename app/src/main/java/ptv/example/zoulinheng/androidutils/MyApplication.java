package ptv.example.zoulinheng.androidutils;

import android.app.Application;
import android.content.Intent;

import ptv.example.zoulinheng.androidutils.download.DownLoadService;

public class MyApplication extends Application {
    private static MyApplication myApplication;

    public static MyApplication getInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        this.startService(new Intent(this, DownLoadService.class));
    }
}

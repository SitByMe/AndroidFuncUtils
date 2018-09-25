package ptv.example.zoulinheng.androidutils;

import android.app.Application;
import android.content.Intent;

import ptv.example.zoulinheng.androidutils.download.DownLoadService;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownLoadService.class));
    }
}

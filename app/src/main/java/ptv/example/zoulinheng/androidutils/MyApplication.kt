package ptv.example.zoulinheng.androidutils

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import ptv.example.zoulinheng.androidutils.download.DownLoadService

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, DownLoadService::class.java))
    }

    override fun attachBaseContext(base: Context) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }
}
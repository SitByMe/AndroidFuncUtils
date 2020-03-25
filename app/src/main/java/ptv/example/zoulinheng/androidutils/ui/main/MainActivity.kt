package ptv.example.zoulinheng.androidutils.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import ptv.example.zoulinheng.androidutils.R
import ptv.example.zoulinheng.androidutils.databinding.ActivityMainBinding
import ptv.example.zoulinheng.androidutils.ui.download.DownloadDemoActivity
import ptv.example.zoulinheng.androidutils.utils.apputils.StartActivityUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.bind(LayoutInflater.from(this).inflate(R.layout.activity_main, null)).root)
    }

    fun downloadDemo(view: View) {
        StartActivityUtils.startActivity(this, DownloadDemoActivity::class.java)
    }
}

package ptv.example.zoulinheng.androidutils.utils.permissions;

import android.content.Intent;

/**
 * Created by lhZou on 2018/8/30.
 * desc:
 */
public interface PermissionsPage {
    String PACK_TAG = "package";

    // normally, ActivityNotFoundException
    Intent settingIntent() throws Exception;
}

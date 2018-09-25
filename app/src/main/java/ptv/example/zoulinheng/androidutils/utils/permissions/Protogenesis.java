package ptv.example.zoulinheng.androidutils.utils.permissions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by lhZou on 2018/8/30.
 * desc:
 */
public class Protogenesis implements PermissionsPage {
    private final Activity activity;

    public Protogenesis(Activity activity) {
        this.activity = activity;
    }

    // system details setting page
    @Override
    public Intent settingIntent() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);

        return intent;
    }
}

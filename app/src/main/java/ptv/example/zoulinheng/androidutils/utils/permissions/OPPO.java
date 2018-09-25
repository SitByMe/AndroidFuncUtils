package ptv.example.zoulinheng.androidutils.utils.permissions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

/**
 * Created by lhZou on 2018/8/30.
 * desc:
 */
public class OPPO implements PermissionsPage {
    private final Activity context;
    private final String PKG = "com.coloros.safecenter";
    private final String MANAGER_OUT_CLS = "com.coloros.safecenter.permission.singlepage" +
            ".PermissionSinglePageActivity";

    public OPPO(Activity context) {
        this.context = context;
    }

    @Override
    public Intent settingIntent() throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp;
        comp = new ComponentName(PKG, MANAGER_OUT_CLS);
        // do not work!!
//        comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission" + ".PermissionAppAllPermissionActivity");
        intent.setComponent(comp);

        return intent;
    }
}

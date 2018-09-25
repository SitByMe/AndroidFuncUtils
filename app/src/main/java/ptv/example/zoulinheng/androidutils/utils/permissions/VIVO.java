package ptv.example.zoulinheng.androidutils.utils.permissions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

/**
 * Created by lhZou on 2018/8/30.
 * desc:
 */
public class VIVO implements PermissionsPage {
    private final String MAIN_CLS = "com.iqoo.secure.MainActivity";
    private final String PKG = "com.iqoo.secure";
    private final Activity context;

    public VIVO(Activity context) {
        this.context = context;
    }

    @Override
    public Intent settingIntent() throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, MAIN_CLS);

        // starting Intent { flg=0x10000000 cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has
        // extras) } from ProcessRecord
//        ComponentName comp = new ComponentName(PKG, "com.iqoo.secure.safeguard.PurviewTabActivity");

        // can enter, but blank
//        try {
//            PackageInfo pi = context.getPackageManager().getPackageInfo(PKG,
//                    PackageManager.GET_ACTIVITIES);
//            for (ActivityInfo activityInfo : pi.activities) {
//                Log.e("TAG", "settingIntent:  " + activityInfo.name);
//                if (activityInfo.name.contains(IN_CLS)) {
//                    comp = new ComponentName(PKG, "com.iqoo.secure.safeguard
// .SoftPermissionDetailActivity");
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        intent.setComponent(comp);

        return intent;
    }
}

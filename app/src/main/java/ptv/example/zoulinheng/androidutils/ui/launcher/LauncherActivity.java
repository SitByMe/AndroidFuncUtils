package ptv.example.zoulinheng.androidutils.ui.launcher;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.List;

import ptv.example.zoulinheng.androidutils.Constants;
import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.activities.DownloadDemoActivity;
import ptv.example.zoulinheng.androidutils.constants.TagConstants;
import ptv.example.zoulinheng.androidutils.databinding.ActivityLauncherBinding;
import ptv.example.zoulinheng.androidutils.ui.BaseActivity;
import ptv.example.zoulinheng.androidutils.utils.apputils.StartActivityUtils;
import ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow;

public class LauncherActivity extends BaseActivity {
    private ActivityLauncherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.bind(LayoutInflater.from(this).inflate(R.layout.activity_launcher, null));
        setContentView(binding.getRoot());

        dismissListener = new PermissionPopupWindow.PopupDismissListener() {
            @Override
            public void onDismiss(int mode, int operation, int resultCode) {
                if (operation == PermissionPopupWindow.OPERATION.OPERATION_OK && mode == PermissionPopupWindow.MODE.MODE_NON) {
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }

    public void requestPermission(View view) {
        PermissionUtils.permission(Constants.permissions)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "onGranted - " + permissionsGranted.toString());
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "onDenied - " + permissionsDenied.toString());
                    }
                }).request();
    }

    private PermissionPopupWindow popupWindow;
    private PermissionPopupWindow.PopupDismissListener dismissListener;

    /**
     * 打开授权弹窗
     *
     * @param parent          parentView
     * @param permissions     权限集合
     * @param mode            1:直接提示  其他:跳转到权限设置页面
     * @param dismissListener dismiss监听
     */
    private void openPermissionPopupWindow(Activity activity, View parent, @NonNull final String[] permissions, @PermissionPopupWindow.MODE int mode, PermissionPopupWindow.PopupDismissListener dismissListener) {
        PermissionPopupWindow ppw = new PermissionPopupWindow(activity, permissions, mode, dismissListener);
        popupWindow = ppw;
        ppw.showPopupWindow(parent);
    }

    public void downloadDemo(View view) {
        StartActivityUtils.startActivity(this, DownloadDemoActivity.class);
    }

    public void enterApp(View view) {
    }
}

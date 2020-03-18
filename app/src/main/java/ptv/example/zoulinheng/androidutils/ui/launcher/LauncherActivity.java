package ptv.example.zoulinheng.androidutils.ui.launcher;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import ptv.example.zoulinheng.androidutils.Constants;
import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.activities.DownloadDemoActivity;
import ptv.example.zoulinheng.androidutils.constants.TagConstants;
import ptv.example.zoulinheng.androidutils.databinding.ActivityLauncherBinding;
import ptv.example.zoulinheng.androidutils.ui.BaseActivity;
import ptv.example.zoulinheng.androidutils.utils.apputils.StartActivityUtils;
import ptv.example.zoulinheng.androidutils.utils.baseutils.LogUtils;
import ptv.example.zoulinheng.androidutils.utils.helpers.PermissionHelper;
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsUtils;
import ptv.example.zoulinheng.androidutils.utils.viewutils.ToastUtils;
import ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow;

public class LauncherActivity extends BaseActivity {
    private ActivityLauncherBinding binding;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.bind(LayoutInflater.from(this).inflate(R.layout.activity_launcher, null));
        setContentView(binding.getRoot());
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);

        dismissListener = new PermissionPopupWindow.PopupDismissListener() {
            @Override
            public void onDismiss(int mode, int operation, int resultCode) {
                if (operation == PermissionPopupWindow.OPERATION.OPERATION_OK && mode == PermissionPopupWindow.MODE.MODE_NON) {
                    requestPermissions(rxPermissions);
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
        requestPermissions(rxPermissions);
    }

    private void requestPermissions(final RxPermissions rxPermissions) {
        PermissionsUtils.setOnPermissionsRequestResultListener(rxPermissions, new PermissionsUtils.OnPermissionsRequestResultListener() {
            @Override
            public void allDenied() {
                ToastUtils.showLong("所有权限已被允许");
            }

            @Override
            public void hasUnDenied() {
                ToastUtils.showLong("有需要询问的权限");
                requestPermissions(rxPermissions);
            }

            @Override
            public void hasNeverAgain() {
                ToastUtils.showLong("有被禁止的权限");
                String[] alwaysDeniedPermissions = PermissionHelper.getAllAlwaysDeniedPermission(LauncherActivity.this, Constants.permissions);
                openPermissionPopupWindow(LauncherActivity.this, binding.clMain, alwaysDeniedPermissions, PermissionPopupWindow.MODE.MODE_TO_SETTING, dismissListener);
            }
        }).requestEachCombined(Constants.permissions);
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

    public void requestPermission2(View view) {
        ptv.example.zoulinheng.androidutils.utils.permissions.PermissionHelper.Companion.permissions(this,
                new ptv.example.zoulinheng.androidutils.utils.permissions.PermissionHelper.CallBack() {

                    @Override
                    public void onGranted(@NotNull String[] permissions) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "onGranted - " + Arrays.toString(permissions));
                    }

                    @Override
                    public void noAskAgain(@NotNull String[] permissions) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "noAskAgain - " + Arrays.toString(permissions));
                    }

                    @Override
                    public void shouldShowRequest(@NotNull String[] permissions) {
                        LogUtils.e(TagConstants.TAG_PERMISSION, "shouldShow - " + Arrays.toString(permissions));
                    }

                }, Constants.permissions);
    }
}

package ptv.example.zoulinheng.androidutils.activities;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;

import ptv.example.zoulinheng.androidutils.Constants;
import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.utils.apputils.StartActivityUtils;
import ptv.example.zoulinheng.androidutils.utils.helpers.PermissionHelper;
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsUtils;
import ptv.example.zoulinheng.androidutils.utils.viewutils.ToastUtils;
import ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow;

public class MainActivity extends BaseActivity {
    private ConstraintLayout clMain;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);

        clMain = findViewById(R.id.cl_main);

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
                String[] alwaysDeniedPermissions = PermissionHelper.getAllAlwaysDeniedPermission(MainActivity.this, Constants.permissions);
                openPermissionPopupWindow(MainActivity.this, clMain, alwaysDeniedPermissions, PermissionPopupWindow.MODE.MODE_TO_SETTING, dismissListener);
            }
        }).requestEachCombined(Constants.permissions);
    }

    private PermissionPopupWindow popupWindow;
    private PermissionPopupWindow.PopupDismissListener dismissListener;

    /**
     * 打开授权弹窗
     *
     * @param context         上下文
     * @param parent          parentView
     * @param permissions     权限集合
     * @param mode            1:直接提示  其他:跳转到权限设置页面
     * @param dismissListener dismiss监听
     */
    private void openPermissionPopupWindow(Activity context, View parent, @NonNull final String[] permissions, @PermissionPopupWindow.MODE int mode, PermissionPopupWindow.PopupDismissListener dismissListener) {
        PermissionPopupWindow ppw = new PermissionPopupWindow(context, permissions, mode, dismissListener);
        popupWindow = ppw;
        ppw.showPopupWindow(parent);
    }

    public void downloadDemo(View view) {
        StartActivityUtils.startActivity(this, DownloadDemoActivity.class);
    }
}

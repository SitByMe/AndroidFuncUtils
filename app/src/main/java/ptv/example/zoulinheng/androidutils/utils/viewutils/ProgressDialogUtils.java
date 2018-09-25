package ptv.example.zoulinheng.androidutils.utils.viewutils;

import android.app.Activity;
import android.app.ProgressDialog;

import java.lang.ref.WeakReference;

/**
 * Created by lhZou on 2018/8/17.
 * desc:数据访问等待框操作类
 */
public class ProgressDialogUtils {
    /**
     * 数据访问等待框
     */
    private static ProgressDialog loadingDialog;
    private static WeakReference<Activity> reference;

    private static void init(Activity act, CharSequence title, CharSequence message) {
        if (loadingDialog == null || reference == null || reference.get() == null || reference.get().isFinishing()) {
            reference = new WeakReference<>(act);

            loadingDialog = new ProgressDialog(reference.get());
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
    }

    public static void setCancelable(boolean b) {
        if (loadingDialog == null) return;
        loadingDialog.setCancelable(b);
    }

    /**
     * 显示等待框
     */
    public static void show(Activity act, CharSequence title, CharSequence message) {
        init(act, title, message);
        loadingDialog.show();
    }

    /**
     * 隐藏等待框
     */
    public static void dismiss() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    /**
     * 注销加载框，避免发生内存泄露
     */
    public static void unInit() {
        dismiss();
        loadingDialog = null;
        reference = null;
    }
}

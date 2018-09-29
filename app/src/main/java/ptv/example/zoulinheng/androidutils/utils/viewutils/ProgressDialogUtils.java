package ptv.example.zoulinheng.androidutils.utils.viewutils;

import android.app.Activity;
import android.app.ProgressDialog;

import java.lang.ref.WeakReference;

/**
 * Created by lhZou on 2018/8/17.
 * desc:
 */
public class ProgressDialogUtils {
    /**
     * 数据访问等待框
     */
    private static ProgressDialog loadingDialog;
    private static WeakReference<Activity> reference;

    private static int count = 0;

    private static ProgressDialogUtils mInstance;

    public static ProgressDialogUtils getInstance(Activity act) {
        if (mInstance == null || loadingDialogIsNull()) {
            synchronized (ProgressDialogUtils.class) {
                if (mInstance == null || loadingDialogIsNull()) {
                    mInstance = new ProgressDialogUtils(act);
                }
            }
        }
        return mInstance;
    }

    private ProgressDialogUtils(Activity act) {
        init(act);
    }

    private void init(Activity act) {
        if (loadingDialogIsNull()) {
            reference = new WeakReference<>(act);

            loadingDialog = new ProgressDialog(reference.get());
            loadingDialog.setCancelable(false);
        }
    }

    private static boolean loadingDialogIsNull() {
        return loadingDialog == null || reference == null || reference.get() == null || reference.get().isFinishing();
    }

    public void setCancelable(boolean b) {
        if (loadingDialog == null) return;
        loadingDialog.setCancelable(b);
    }

    /**
     * 显示等待框
     */
    public void show(CharSequence title, CharSequence message) {
        count++;
        System.out.println("progressView   show   count = " + count);
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏等待框
     */
    public void dismiss() {
        count--;
        System.out.println("progressView   dismiss   count = " + count);
        if (count <= 0) {
            count = 0;
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    /**
     * 注销加载框，避免发生内存泄露
     */
    public void unInit() {
        dismiss();
        loadingDialog = null;
        reference.clear();
        reference = null;
    }
}

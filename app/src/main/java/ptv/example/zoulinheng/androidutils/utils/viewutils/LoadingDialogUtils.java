package ptv.example.zoulinheng.androidutils.utils.viewutils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import java.lang.ref.WeakReference;

import ptv.example.zoulinheng.androidutils.R;

/**
 * Created by lhZou on 2018/8/17.
 * desc:
 */
public class LoadingDialogUtils {
    /**
     * 数据访问等待框
     */
    private static Dialog loadingDialog;
    private static WeakReference<Activity> reference;

    private static int count = 0;

    private static LoadingDialogUtils mInstance;

    public static LoadingDialogUtils getInstance(Activity act) {
        if (mInstance == null || loadingDialogIsNull()) {
            synchronized (LoadingDialogUtils.class) {
                if (mInstance == null || loadingDialogIsNull()) {
                    mInstance = new LoadingDialogUtils(act);
                }
            }
        }
        return mInstance;
    }

    private LoadingDialogUtils(Activity act) {
        init(act);
    }

    private void init(Activity act) {
        if (loadingDialogIsNull()) {
            reference = new WeakReference<>(act);

            View vi = act.getLayoutInflater().inflate(R.layout.loading_dialog, null);
            loadingDialog = new Dialog(reference.get(), R.style.loading_dialog);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setContentView(vi);
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
    public void show() {
        count++;
        System.out.println("progressView   show   count = " + count);
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

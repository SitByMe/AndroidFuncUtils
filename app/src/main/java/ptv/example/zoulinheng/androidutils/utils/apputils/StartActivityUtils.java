package ptv.example.zoulinheng.androidutils.utils.apputils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by lhZou on 2018/4/26.
 * desc:页面跳转工具类
 */
public class StartActivityUtils {

    /**
     * 跳转源生界面
     *
     * @param context 上下文
     * @param cls     泛型
     */
    public static void startActivity(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
    }

    /**
     * 跳转源生界面
     *
     * @param context 上下文
     * @param cls     泛型
     * @param bundle  传递参数
     */
    public static void startActivity(Context context, Class<?> cls, Bundle bundle) {
        context.startActivity(new Intent(context, cls).putExtras(bundle));
    }

    /**
     * 跳转源生界面
     *
     * @param context   上下文
     * @param cls       泛型
     * @param bundleMap 传递参数
     */
    public static void startActivity(Context context, Class<?> cls, Map<String, Bundle> bundleMap) {
        Intent intent = new Intent(context, cls);
        for (String bundleKey : bundleMap.keySet()) {
            intent.putExtra(bundleKey, bundleMap.get(bundleKey));
        }
        context.startActivity(intent);
    }

    /**
     * 跳转源生界面
     *
     * @param context     上下文
     * @param cls         泛型
     * @param requestCode code
     */
    public static void startActivityForResult(Context context, Class<?> cls, int requestCode) {
        ((Activity) context).startActivityForResult(new Intent(context, cls), requestCode);
    }

    /**
     * 跳转源生界面
     *
     * @param context     上下文
     * @param cls         泛型
     * @param bundle      传递参数
     * @param requestCode code
     */
    public static void startActivityForResult(Context context, Class<?> cls, Bundle bundle, int requestCode) {
        ((Activity) context).startActivityForResult(new Intent(context, cls).putExtras(bundle), requestCode);
    }

    /**
     * 跳转源生界面
     *
     * @param context     上下文
     * @param cls         泛型
     * @param bundleMap   传递参数
     * @param requestCode code
     */
    public static void startActivityForResult(Context context, Class<?> cls, Map<String, Bundle> bundleMap, int requestCode) {
        Intent intent = new Intent(context, cls);
        for (String bundleKey : bundleMap.keySet()) {
            intent.putExtra(bundleKey, bundleMap.get(bundleKey));
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}

package ptv.example.zoulinheng.androidutils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;

import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.utils.glide.GlideCircleTransform;
import ptv.example.zoulinheng.androidutils.utils.glide.GlideRoundTransform;

/**
 * Created by Zohar on 2017/9/7.
 * desc:
 */
public class ShowImageUtils {
    private static int errorPicResId = R.mipmap.ic_launcher;
    private static int placeholderResId = R.mipmap.ic_launcher;

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       图片链接
     * @param imageView view
     */
    public static void loadImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, placeholderResId, imageView);
    }

    /**
     * 加载图片
     *
     * @param context        上下文
     * @param url            图片链接
     * @param placeholderRes 占位图
     * @param imageView      view
     */
    public static void loadImage(Context context, String url, @DrawableRes int placeholderRes, ImageView imageView) {
        Glide.with(context).load(url)// 加载图片
                .error(errorPicResId)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(placeholderRes)// 设置占位图
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context        上下文
     * @param url            图片链接
     * @param errorRes       错误图片
     * @param placeholderRes 占位图
     * @param imageView      view
     */
    public static void loadImage(Context context, String url, @DrawableRes int errorRes, @DrawableRes int placeholderRes, ImageView imageView) {
        Glide.with(context).load(url)// 加载图片
                .error(errorRes)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(placeholderRes)// 设置占位图
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context   上下文
     * @param url       图片链接
     * @param imageView view
     */
    public static void loadCircleImage(Context context, String url, ImageView imageView) {
        loadCircleImage(context, url, placeholderResId, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context        上下文
     * @param url            图片链接
     * @param placeholderRes 占位图
     * @param imageView      view
     */
    public static void loadCircleImage(Context context, String url, @DrawableRes int placeholderRes, ImageView imageView) {
        loadCircleImage(context, url, errorPicResId, placeholderRes, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context        上下文
     * @param url            图片链接
     * @param errorRes       错误图片
     * @param placeholderRes 占位图
     * @param imageView      view
     */
    public static void loadCircleImage(Context context, String url, @DrawableRes int errorRes, @DrawableRes int placeholderRes, ImageView imageView) {
        Glide.with(context).load(url)// 加载图片
                .transform(new GlideCircleTransform(context))
                .error(errorRes)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(placeholderRes)// 设置占位图
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param context   上下文
     * @param url       图片链接
     * @param imageView view
     */
    public static void loadRoundImage(Context context, String url, ImageView imageView) {
        loadRoundImage(context, url, placeholderResId, imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param context   上下文
     * @param url       图片链接
     * @param imageView view
     */
    public static void loadRoundImage(Context context, String url, @DrawableRes int placeholderRes, ImageView imageView) {
        Glide.with(context).load(url)// 加载图片
                .error(errorPicResId)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(placeholderRes)// 设置占位图
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .transform(new GlideRoundTransform(context, 10))
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param bitmap    bitmap
     * @param imageView view
     */
    public static void loadImage(Context context, Bitmap bitmap, ImageView imageView) {
        loadImage(context, bitmap, placeholderResId, imageView);
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param bitmap    bitmap
     * @param imageView view
     */
    public static void loadImage(Context context, Bitmap bitmap, @DrawableRes int placeholderRes, ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        Glide.with(context)
                .load(bytes)
                .centerCrop()
//                    .thumbnail(0.1f)   //缩略图为原来的十分之一
//                .override(Utils.px2dip(context, 130), Utils.px2dip(mContext, 130)) //设置大小
                .override(200, 200) //设置大小
                .placeholder(placeholderRes)
                .error(errorPicResId)
                .into(imageView);
    }
}
package com.niugeyin.dcenter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ptv.example.zoulinheng.androidutils.MyApplication;

/**
 * Created by lhZou on 2018/8/8.
 * desc: SharedPreferences工具类
 * 支持对于 List集合 与 Map集合 的存取
 */
public class SharedPreferencesUtils {
    private static SharedPreferences sp;
    private static final String PREFERENCES_FILE_NAME = "sp_file_name";

    private static SharedPreferences getSp() {
        if (sp == null) {
            sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 移除指定key的数据
     *
     * @param key 键
     */
    public static void remove(String key) {
        SharedPreferences preferences = getSp();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除所有的数据
     */
    public static void clear() {
        SharedPreferences preferences = getSp();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 存入字符串
     *
     * @param key   键
     * @param value 值
     */
    public static void putString(String key, String value) {
        SharedPreferences preferences = getSp();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取字符串
     *
     * @param key 键
     * @return 得到的字符串
     */
    public static String getString(String key) {
        SharedPreferences preferences = getSp();
        return preferences.getString(key, "");
    }

    /**
     * 获取字符串
     *
     * @param key      键
     * @param defValue 默认值
     * @return 得到的字符串
     */
    public static String getString(String key, String defValue) {
        SharedPreferences preferences = getSp();
        return preferences.getString(key, defValue);
    }

    /**
     * 保存布尔值
     *
     * @param key   键
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取布尔值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 返回保存的值
     */
    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(key, defValue);
    }

    /**
     * 保存long值
     *
     * @param key   键
     * @param value 值
     */
    public static void putLong(String key, long value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 获取long值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public static long getLong(String key, long defValue) {
        SharedPreferences sp = getSp();
        return sp.getLong(key, defValue);
    }

    /**
     * 保存int值
     *
     * @param key   键
     * @param value 值
     */
    public static void putInt(String key, int value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 获取int值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public static int getInt(String key, int defValue) {
        SharedPreferences sp = getSp();
        return sp.getInt(key, defValue);
    }

    /**
     * 保存对象
     *
     * @param key 键
     * @param obj 要保存的对象（Serializable的子类）
     * @param <T> 泛型定义
     */
    public static <T extends Serializable> void putObject(String key, T obj) {
        try {
            put(key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     *
     * @param key 键
     * @param <T> 指定泛型
     * @return 泛型对象
     */
    public static <T extends Serializable> T getObject(String key) {
        try {
            return (T) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储List集合
     *
     * @param key  存储的键
     * @param list 存储的集合
     */
    public static void putList(String key, List<? extends Serializable> list) {
        try {
            put(key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取List集合
     *
     * @param key 键
     * @param <E> 指定泛型
     * @return List集合
     */
    public static <E extends Serializable> List<E> getList(String key) {
        try {
            return (List<E>) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储Map集合
     *
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定Map的键类型
     * @param <V> 指定Map的值类型
     */
    public static <K extends Serializable, V extends Serializable> void putMap(String key, Map<K, V> map) {
        try {
            put(key, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Map集合
     *
     * @param key 键
     * @param <K> Map的键类型
     * @param <V> Map的值类型
     * @return Map集合
     */
    public static <K extends Serializable, V extends Serializable> Map<K, V> getMap(String key) {
        try {
            return (Map<K, V>) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储对象
     *
     * @param key 键
     * @param obj 对象
     * @throws IOException
     */
    private static void put(String key, Object obj) throws IOException {
        if (obj == null) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        baos.close();
        oos.close();

        putString(key, objectStr);
    }

    /**
     * 获取对象
     *
     * @param key 键
     * @return 对象
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object get(String key) throws IOException, ClassNotFoundException {
        String wordBase64 = getString(key);
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException异常
            return null;
        }
        byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }
}

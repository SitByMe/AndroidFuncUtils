package ptv.example.zoulinheng.androidutils.utils.baseutils;

import java.text.SimpleDateFormat;
import java.util.Date;

import ptv.example.zoulinheng.androidutils.constants.enums.DateStyle;

/**
 * Created by lhZou on 2018/6/26.
 * desc:日期工具类
 */
public class DateUtils {
    /**
     * 获取SimpleDateFormat
     *
     * @param pattern 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException 异常：非法日期格式
     */
    private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date      日期
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String dateToString(Date date, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = dateToString(date, dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 将时间戳转化为日期字符串。失败返回null。
     *
     * @param time      时间戳
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String LongToTime(long time, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = dateToString(new Date(time), dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    private static String dateToString(Date date, String pattern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = getDateFormat(pattern).format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateString;
    }

    /**
     * 时间戳转换为yyyy.MM.dd格式时间
     *
     * @param t 时间戳
     * @return 时间
     */
    public static String LongToTime(long t) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date(t);
        time = sdf.format(date);
        return time;
    }

    /**
     * 时间戳转换为yyyy-MM-dd HH:ss:mm
     *
     * @param t 时间戳
     * @return 时间
     */
    public static String LongToStrTime(long t) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(t);
        time = sdf.format(date);
        return time;
    }

    /**
     * 时间戳转换为yyyy.MM.dd HH:ss:mm
     *
     * @param t 时间戳
     * @return 时间
     */
    public static String LongToFormTime(long t) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date(t);
        time = sdf.format(date);
        return time;
    }

    /**
     * 时间戳转换为yyyy年MM月dd日格式时间
     *
     * @param t 时间戳
     * @return 时间
     */
    public static String getMsgTime(long t) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(t);
        time = sdf.format(date);
        return time;
    }

    /**
     * 根据时间长度得到long
     *
     * @param minute 分
     * @param second 秒
     * @return 时长（单位：毫秒）
     */
    public static long timeLengthToLong(int minute, int second) {
        return minute * 60 * 1000 + second * 1000;
    }
}

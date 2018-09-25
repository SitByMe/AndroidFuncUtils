package ptv.example.zoulinheng.androidutils.utils.baseutils;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lhZou on 2017/9/26.
 * desc: 判空工具类
 */
public class NonUtils {

    /**
     * 判断字符串是否为空
     *
     * @param s string
     * @return true is means this string is empty.
     */
    public static boolean isEmpty(String s) {
        return null == s || TextUtils.isEmpty(s) || "null".equals(s) || "non".equals(s);
    }

    /**
     * 判断list是否为空
     *
     * @param t list
     * @return true is means this list is empty.
     */
    public static boolean isEmpty(List<?> t) {
        return null == t || t.size() == 0;
    }

    /**
     * 判断Map是否为空
     *
     * @param m map
     * @return true is means this map is empty.
     */
    public static boolean isEmpty(Map<?, ?> m) {
        return null == m || m.size() == 0;
    }

    /**
     * 判断Set是否为空
     *
     * @param s set
     * @return true is means this set is empty.
     */
    public static boolean isEmpty(Set<?> s) {
        return null == s || s.size() == 0;
    }
}

package ptv.example.zoulinheng.androidutils.utils.datautils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ptv.example.zoulinheng.androidutils.utils.baseutils.NonUtils;

/**
 * Created by lhZou on 2017/9/6.
 * desc:Gson工具类
 */
public class GsonUtils {
    /**
     * json 转 实体
     *
     * @param json     json
     * @param key      要解析的 value 对应的 key
     * @param classOfT 类型
     * @param <T>      泛型
     * @return 实体
     */
    public static <T> T jsonToClass(String json, String key, Class<T> classOfT) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            return jsonToClass(jsonObj.getString(key), classOfT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json 转 实体
     *
     * @param json     json
     * @param classOfT 类型
     * @param <T>      泛型
     * @return 实体
     */
    public static <T> T jsonToClass(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }

    /**
     * json 转 list
     *
     * @param json     json
     * @param classOfT list元素类型
     * @param <T>      泛型
     * @return list
     */
    public static <T> List<T> jsonToList(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        ArrayList<T> retList = new ArrayList<>();
        if (!NonUtils.isEmpty(json)) {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                retList.add(gson.fromJson(elem, classOfT));
            }
        }
        return retList;
    }

    /**
     * json 转 list
     *
     * @param json     json
     * @param key      要解析的 value 对应的 key
     * @param classOfT list元素类型
     * @param <T>      泛型
     * @return list
     */
    public static <T> List<T> jsonToList(String json, String key, Class<T> classOfT) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        return jsonToList(jsonObj.getString(key), classOfT);
    }

    /**
     * list 转 json
     *
     * @param list 列表
     * @param <T>  泛型
     * @return json
     */
    public static <T> String listToJson(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        System.out.println(json);
        return json;
    }

    /**
     * 实体 转 json
     *
     * @param t   实体
     * @param <T> 泛型
     * @return json
     */
    public static <T> String classToJson(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }
}

package com.hg.lib.tool;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gson转换工具类.
 */
public class GsonUtil {

    private Gson gson = new Gson();

    public static GsonUtil getInstance() {
        return GsonHolder.INSTANCE;
    }

    private static class GsonHolder {
        private static final GsonUtil INSTANCE = new GsonUtil();
    }

    /**
     * 对象->Json字符串
     *
     * @param obj
     */
    public String jsonStr(Object obj) {
        if (null != obj) {
            return gson.toJson(obj);
        }
        return null;
    }

    /**
     * Json->对象
     *
     * @param str
     * @param type
     * @param <T>
     * @return
     */
    public <T> T fromJson(String str, Class<T> type) {
        return gson.fromJson(str, type);
    }


    /**
     * list解析
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public <T> List<T> jsonToList(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, cls));
        }
        return list;
    }


    /**
     * Map->JSONObject
     *
     * @param data
     * @return
     */
    public JSONObject mapToJSONObject(Map<?, ?> data) {
        JSONObject object = new JSONObject();
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            String key = (String) entry.getKey();
            if (TextUtils.isEmpty(key)) {
                throw new NullPointerException("key == null");
            }
            try {
                object.put(key, wrap(entry.getValue()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    /**
     * 集合->JSONArray
     *
     * @param data
     * @return
     */
    public JSONArray collectionToJson(Collection<?> data) {
        JSONArray jsonArray = new JSONArray();
        if (null != data) {
            for (Object aData : data) {
                jsonArray.put(wrap(aData));
            }
        }
        return jsonArray;
    }

    /**
     * Object对象->JSONArray
     *
     * @param data
     * @return
     * @throws
     */
    public JSONArray objectToJSONArray(Object data) throws JSONException {
        if (!data.getClass().isArray()) {
            throw new JSONException("Not a primitive data: " + data.getClass());
        }
        int length = Array.getLength(data);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < length; ++i) {
            jsonArray.put(wrap(Array.get(data, i)));
        }
        return jsonArray;
    }

    private Object wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof JSONArray || obj instanceof JSONObject) {
            return obj;
        }
        try {
            if (obj instanceof Collection) {
                return collectionToJson((Collection<?>) obj);
            } else if (obj.getClass().isArray()) {
                return objectToJSONArray(obj);
            }
            if (obj instanceof Map) {
                return mapToJSONObject((Map<?, ?>) obj);
            }
            if (obj instanceof Boolean || obj instanceof Byte
                    || obj instanceof Character || obj instanceof Double
                    || obj instanceof Float || obj instanceof Integer || obj instanceof Long
                    || obj instanceof Short || obj instanceof String) {
                return obj;
            }
            if (obj.getClass().getPackage().getName().startsWith("java.")) {
                return obj.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串生成JSONObject对象
     *
     * @param json
     * @return
     */
    public JSONObject strToJSONObject(String json) {
        try {
            JSONTokener jsonParser = new JSONTokener(json);
            return (JSONObject) jsonParser.nextValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Json字符串生成JSONArray对象
     *
     * @param json
     * @return
     */
    public JSONArray strToJSONArray(String json) {
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象->Json
     *
     * @param obj
     * @return
     */
    public String objectToJson(Object obj) {
        StringBuilder json = new StringBuilder();
        if (null == obj) {
            json.append("\"\"");
        } else if (obj instanceof String || obj instanceof Integer
                || obj instanceof Float || obj instanceof Boolean
                || obj instanceof Short || obj instanceof Double
                || obj instanceof Long || obj instanceof BigDecimal
                || obj instanceof BigInteger || obj instanceof Byte) {
            json.append("\"").append(strToJson(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            json.append(arrayToJson((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(listToJson((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(mapToJson((Map<?, ?>) obj));
        } else if (obj instanceof Set) {
            json.append(setToJson((Set<?>) obj));
        }
        return json.toString();
    }

    /**
     * List集合->Json
     *
     * @param list
     * @return
     */
    public String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (null != list && list.size() > 0) {
            for (Object obj : list) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * 对象数组->Json
     *
     * @param array
     * @return
     */
    public String arrayToJson(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (null != array && array.length > 0) {
            for (Object obj : array) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * Map集合->Json
     *
     * @param map
     * @return
     */
    public String mapToJson(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (null != map && map.size() > 0) {
            for (Object key : map.keySet()) {
                json.append(objectToJson(key));
                json.append(":");
                json.append(objectToJson(map.get(key)));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    /**
     * Set集合->Json
     *
     * @param set
     * @return
     */
    public String setToJson(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (null != set && set.size() > 0) {
            for (Object obj : set) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * 字符串->Json
     *
     * @param str
     * @return
     */
    public String strToJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 字符数组->List集合
     *
     * @param array 数组
     * @return List
     */
    public List<String> arrayToList(String[] array) {
        return Arrays.asList(array);
    }

    /**
     * List集合->数组
     *
     * @param list 集合
     * @return String[]
     */
    public String[] listToArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取assets文件夹下的json数据
     *
     * @param ctx      上下文
     * @param fileName 文件, 如 assets 下的province.json
     * @return
     */
    public String getJson(Context ctx, String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            AssetManager assetManager = ctx.getAssets();
            InputStreamReader reader = new InputStreamReader(assetManager.open(fileName));
            BufferedReader bf = new BufferedReader(reader);
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

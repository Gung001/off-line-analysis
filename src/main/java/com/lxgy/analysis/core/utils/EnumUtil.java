package com.lxgy.analysis.core.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EnumUtil implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = -3978644370445914318L;

    private static Map<String, Object> cache = new HashMap<String, Object>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static Object getEnumByCode(Class<?> _class, String code) {
        Object o = EnumUtil.get(_class.getName() + code);
        if (null != o) {
            return o;
        }
        return null;
    }

}

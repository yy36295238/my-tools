package com.yyself.tool.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YangYu
 */
@Slf4j
public class ConvertMap<K, V> {

    private final Map<K, V> sourceMap;

    public ConvertMap() {
        sourceMap = new HashMap<>();
    }

    public ConvertMap(Map<K, V> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public ConvertMap(K key, V val) {
        sourceMap = new HashMap<>();
        sourceMap.put(key, val);
    }

    public ConvertMap<K, V> put(K key, V val) {
        sourceMap.put(key, val);
        return this;
    }

    /**
     * String
     */
    public String getString(K key) {
        return getStringOrDefault(key, null);
    }

    public String getStringOrDefault(K key, V defaultVal) {
        return (String) get(key, defaultVal, String.class);
    }

    /**
     * Integer
     */
    public Integer getInteger(K key) {
        return getIntegerOrDefault(key, null);
    }

    public Integer getIntegerOrDefault(K key, V defaultVal) {
        return (Integer) get(key, defaultVal, Integer.class);
    }

    /**
     * Long
     */
    public Long getLong(K key) {
        return getLongOrDefault(key, null);
    }

    public Long getLongOrDefault(K key, V defaultVal) {
        return (Long) get(key, defaultVal, Long.class);
    }

    /**
     * Double
     */
    public Double getDouble(K key) {
        return getDoubleOrDefault(key, null);
    }

    public Double getDoubleOrDefault(K key, V defaultVal) {
        return (Double) get(key, defaultVal, Double.class);
    }

    /**
     * Float
     */

    public Float getFloat(K key) {
        return getFloatOrDefault(key, null);
    }

    public Float getFloatOrDefault(K key, V defaultVal) {
        return (Float) get(key, defaultVal, Float.class);
    }

    /**
     * Short
     */
    public Short getShort(K key) {
        return getShortOrDefault(key, null);
    }

    public Short getShortOrDefault(K key, V defaultVal) {
        return (Short) get(key, defaultVal, Short.class);
    }

    /**
     * Boolean
     */
    public Boolean getBoolean(K key) {
        return getBooleanOrDefault(key, null);
    }

    public Boolean getBooleanOrDefault(K key, V defaultVal) {
        return (Boolean) get(key, defaultVal, Boolean.class);
    }


    private Object get(K key, V defaultVal, Class<?> type) {
        V v = sourceMap.get(key);
        if (v == null && defaultVal == null) {
            return null;
        }
        if (v == null) {
            return defaultVal;
        }
        if (String.class == type) {
            return v.toString();
        }
        if (Integer.class == type) {
            return Integer.parseInt(v.toString());
        }
        if (Long.class == type) {
            return Long.parseLong(v.toString());
        }
        if (Double.class == type) {
            return Double.parseDouble(v.toString());
        }
        if (Float.class == type) {
            return Float.parseFloat(v.toString());
        }
        if (Short.class == type) {
            return Short.parseShort(v.toString());
        }
        if (Boolean.class == type) {
            return Boolean.parseBoolean(v.toString());
        }
        return v;
    }

    public Map<K, V> toMap() {
        return sourceMap;
    }

    @Override
    public String toString() {
        return sourceMap.toString();
    }


    public static void main(String[] args) {

        ConvertMap<String, Object> convertMap = new ConvertMap<String, Object>("xxxx", "345")
                .put("string", "A")
                .put("long", 56L)
                .put("integer", 2)
                .put("double", 0.99D)
                .put("float", 0.88F)
                .put("short", 44)
                .put("boolean", true);

        System.out.println(convertMap.getString("string"));
        System.out.println(convertMap.getLong("long"));
        System.out.println(convertMap.getInteger("integer"));
        System.out.println(convertMap.getDouble("double"));
        System.out.println(convertMap.getFloat("float"));
        System.out.println(convertMap.getShort("short"));
        System.out.println(convertMap.getBoolean("boolean"));
        System.out.println(convertMap.getInteger("xxxx"));

        System.out.println(convertMap.toMap());


    }

}

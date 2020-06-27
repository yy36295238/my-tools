package com.yyself.tool.utils;

import com.yyself.tool.exception.KotException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author YangYu
 */

@Slf4j
public class CommonUtils {


    /**
     * 删除两个字符串中间的字符串
     */
    public static String removeStr(String origin, String startStr, String endStr) {
        int start = origin.indexOf(startStr);
        int end = origin.indexOf(endStr);
        if (start < 0 || end < 0) {
            log.warn("未匹配关键字,origin={},startStr={},endStr={}", origin, startStr, endStr);
            return origin;
        }
        return origin.substring(0, start + startStr.length()) + origin.substring(end, origin.length());
    }

    /**
     * 获取字符串最后一个字符
     */
    public static String lastStr(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.substring(str.length() - 1);
    }

    /**
     * 下划线转驼峰
     */
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 首字母大写
     */
    private static String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 单词首字母大写 UserName
     */
    public static String capitalName(String word) {
        return captureName(camelCaseName(word));
    }

    /**
     * 首字母小写
     */
    public static String lowerName(String name) {
        char[] cs = name.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }


    public static Class<?> changeType(String type) {
        type = type.toUpperCase();

        if ("DATE".equals(type) || "DATETIME".equals(type) || "TIMESTAMP".equals(type) || type.contains("TIMESTAMP")) {
            return Date.class;
        }
        if (type.contains("INT") && !type.equals("INT8")) {
            return Integer.class;
        }
        if (type.contains("BIGINT") || "INT8".equals(type)) {
            return Long.class;
        }
        if (type.contains("FLOAT") && !type.equals("FLOAT8")) {
            return Float.class;
        }
        if (type.contains("DOUBLE") || type.equals("FLOAT8") || type.equals("MONEY")) {
            return Double.class;
        }
        if ("DECIMAL".equals(type) || "NUMERIC".equals(type)) {
            return BigDecimal.class;
        }
        if ("BIT".equals(type) || "BOOLEAN".equals(type) || "BOOL".equals(type)) {
            return Boolean.class;
        }
        if (type.contains("CHAR") || type.contains("TEXT")) {
            return String.class;
        }
        throw new KotException("未知数据类型 = " + type);
    }
}

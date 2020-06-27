package com.yyself.tool.utils;

/**
 * @author yangyu
 * @date 2020/6/25 下午10:09
 */
public class ConvertUtils {
    public static String convertString(Object val) {
        return val == null ? "" : val.toString();
    }
}

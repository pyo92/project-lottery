package com.example.projectlottery.util;

public class StringUtils {

    /**
     *
     * @param str 문자열
     * @return null 또는 빈 문자열인 경우 "true"
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}

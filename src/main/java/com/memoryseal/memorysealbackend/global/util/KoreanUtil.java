package com.memoryseal.memorysealbackend.global.util;

public class KoreanUtil {

    private static final char[] CHOSUNG_LIST = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    private static final int HANGLE_FIRST = 0xAC00;
    private static final int HANGLE_END = 0xD7A3;
    private static final int CHOSUNG_UNIT = 21 * 28;

    // 초성 추출, 자음은 그냥 지나감
    public static String extractChosung(String str) {
        if(str == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for(char c : str.toCharArray()) {
            if(c >= HANGLE_FIRST && c <= HANGLE_END) {
                int unicode = c - HANGLE_FIRST;
                int choIndex = unicode / CHOSUNG_UNIT;
                result.append(CHOSUNG_LIST[choIndex]);
            }else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static boolean isChosungOnly(char c) {
        return c >= 0x3131 && c <= 0x314E;
    }

    // 특정 위치부터 한글자씩 비교
    public static boolean matchesFromIndex(String nickname, String query, int start) {
        for(int i = 0; i < query.length(); i++) {
            char queryChar = query.charAt(i);
            char nameChar = Character.toLowerCase(nickname.charAt(start + i));

            if(isChosungOnly(queryChar)) {
                char nameChosung = extractChosung(String.valueOf(nameChar)).charAt(0);
                if(nameChosung != queryChar) {
                    return false;
                }
            }else {
                if(Character.toLowerCase(queryChar) != nameChar) {
                    return false;
                }
            }
        }
        return true;
    }

    // 닉네임 안에서 패턴 찾기
    public static boolean matchesChosungPattern(String nickname, String query) {
        if(nickname.length() < query.length()) {
            return false;
        }

        for(int start = 0; start <= nickname.length() - query.length(); start++) {
            if(matchesFromIndex(nickname, query, start)) {
                return true;
            }
        }
        return false;
    }

}

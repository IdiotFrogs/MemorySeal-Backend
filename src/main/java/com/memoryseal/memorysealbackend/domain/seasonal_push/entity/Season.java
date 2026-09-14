package com.memoryseal.memorysealbackend.domain.seasonal_push.entity;

public enum Season {
    SPRING("지난 봄의 우리"),
    SUMMER("지난 여름의 우리"),
    FALL("지난 가을의 우리"),
    WINTER("지난 겨울의 우리");

    private final String content;

    Season(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static Season from(int month) {
        if(month >= 3 && month <= 5) {
            return SPRING;
        }
        if(month >= 6 && month <= 8) {
            return SUMMER;
        }
        if(month >= 9 && month <= 11) {
            return FALL;
        }
        return WINTER;
    }
}

package com.zzz.pro.enums;

public enum SexEnum implements CodeEnum{
    MALE("MALE","男"),

    FEMALE("FEMALE","女"),
    NONBINARY("NONBINARY","非二元性别")
    ;

    private String code;
    private String title;

    SexEnum(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}

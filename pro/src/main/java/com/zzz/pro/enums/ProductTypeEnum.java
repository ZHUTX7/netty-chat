package com.zzz.pro.enums;

/**
 * @Author zhutianxiang
 * @Description Product Type Enum
 * @Date 2023/11/6 17:09
 * @Version 1.0
 */
public enum ProductTypeEnum implements CodeEnum<Integer> {
    COUNT(1,"次数使用"),
    TIME (2,"生效时间"),
    VIP (3,"会员")
    ;

    private Integer code;

    private String title;

    ProductTypeEnum(Integer code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}

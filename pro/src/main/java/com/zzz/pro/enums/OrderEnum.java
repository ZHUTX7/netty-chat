package com.zzz.pro.enums;

/**
 * @Author zhutianxiang
 * @Description Order Enum
 * @Date 2023/11/6 17:14
 * @Version 1.0
 */
public enum OrderEnum implements CodeEnum<String> {
    PENDING("PENDING","未结算"),
    PAID("PAID","支付完成"),
    FAILED("FAILED","支付失败"),;
    private String code;

    private String title;

    OrderEnum(String code, String title) {
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

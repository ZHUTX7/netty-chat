package com.mindset.ameeno.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/11/6 17:05
 * @Version 1.0
 */
public enum CurrencyEnum implements CodeEnum<String> {
    CNY("CNY","人民币"),
    USD("USD","美元"),
    GBP("GBP","英镑"),;
    private String code;

    private String title;

    CurrencyEnum(String code, String title) {
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

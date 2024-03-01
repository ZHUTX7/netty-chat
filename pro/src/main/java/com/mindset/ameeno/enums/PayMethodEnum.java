package com.mindset.ameeno.enums;

/**
 * @Author zhutianxiang
 
 * @Date 2024/1/11 14:26
 * @Version 1.0
 */
public enum PayMethodEnum implements CodeEnum<String> {
    ALI_PAY("ALI_PAY","支付宝"),
    WX_PAY("WX_PAY","微信"),
    IOS_MANUAL_PAY("IOS_MANUAL_PAY","IOS手动付款"),
    IOS_AUTO_RENEW_PAY("IOS_AUTO_RENEW_PAY","IOS自动扣款"),
    CARD_PAY("CARD_PAY","银行卡、信用卡"),
    PAYPAL_PAY("PAYPAL_PAY","PayPal"),
    ;
    private String code;

    private String title;

    PayMethodEnum(String code, String title) {
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
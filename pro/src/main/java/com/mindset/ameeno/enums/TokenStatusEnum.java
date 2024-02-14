package com.mindset.ameeno.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/8/3 14:51
 * @Version 1.0
 */
public enum TokenStatusEnum implements CodeEnum<Integer>{
    ACCESS_TOKEN(1,"有效token" ),
    METHOD_WRONG(-1,"算法不一致" ),
    SIGN_WRONG(-2,"无效签名" ),
    TIME_DELAY(-3,"token已经过期" ),
    TOKEN_WRONG(-4,"token无效" )
            ;
    private Integer code;

    private String title;

    TokenStatusEnum(Integer code, String title) {
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
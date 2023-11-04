package com.zzz.pro.enums;


public enum ResultEnum implements CodeEnum<Integer>{
    SUCCESS(200, "成功"),
    FAILED(500, "失败"),

    

    NEED_LOGIN(1002, "用户未登陆"),

    FIELD_REQUIRED(1003, "必填字段"),

    CAPTCHA_ERROR(1004, "验证码不正确"),

    SERVER_ERROR(1005, "服务器错误"),

    SYSTEM_ERROR(1006, "系统错误"),

    CONFIG_ERROR(1008, "配置错误"),

    PASSPORT_ERROR(1009, "通行证不正确"),

    BANNED_USER(1010, "禁用用户"),

    DUPLICATE_DATA(1012, "数据重复"),

    NOT_EXISTS(1014, "不存在"),

    NO_PERMISSION(1015, "无权限"),

    IP_REPEAT_ERROR(1017, "ip重复"),

    NOVA_SUBMIT_ERROR(1026, "nova提交失败"),

    UPLOAD_ERROR(1018, "上传失败"),

    RPC_WARING(1019, "调用外部服务超时"),

    TOKEN_ERROR(4011, "token无效"),
    TOKEN_DELAY(4012, "token过期"),
    TOKEN_FLUSH(4012, "token刷新"),
    
    //客户端传值错误
    PARAM_ERROR(401, "客户端传值错误"),



    //---------支付业务报错 3开头
    ORDER_INIT_FAILED(3001,"订单初始化失败"),
    ORDER_UPDATE_FAILED(3002,"订单更新失败");
    ;

    private Integer code;

    private String title;

    ResultEnum(Integer code, String title) {
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

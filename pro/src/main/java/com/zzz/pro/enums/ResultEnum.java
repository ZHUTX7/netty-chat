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

    //用户业务报错 2开头
    PHONE_IS_EXIST(2001,"用户手机号已经被注册"),


    //---------支付业务报错 3开头

    ORDER_INIT_FAILED(3001,"订单初始化失败"),
    ORDER_UPDATE_FAILED(3002,"订单更新失败"),
    SKU_NOT_EXIST(3003,"商品不存在或已下架"),
    ORDER_NOT_EXIST(3004,"订单不存在") ,

    //----道具使用错误 5开头
    PROPS_NOT_ENOUGH(5001,"sku道具不足"),

    //约会错误 6开头
    DATING_POINT_NOT_EXIST(6001,"约会地点不存在"),
    DATING_POINT_NOT_OPEN(6002,"约会地点未在营业时间"),
    DATING_POINT_NOT_SUIT(6003,"没有合适的约会地点"),
    ;
    //商品不存在或已下架
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

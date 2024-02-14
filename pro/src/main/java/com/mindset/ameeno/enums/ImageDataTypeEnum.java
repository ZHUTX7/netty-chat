package com.mindset.ameeno.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/17 17:19
 * @Version 1.0
 */
public enum ImageDataTypeEnum implements CodeEnum {
    IMAGE_Base64(1,"base64"),
    IMAGE_URL(2,"url"),
    ;
    private Integer code;

    private String title;

    ImageDataTypeEnum(Integer code, String title) {
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

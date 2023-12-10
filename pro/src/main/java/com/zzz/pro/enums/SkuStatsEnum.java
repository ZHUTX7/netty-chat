package com.zzz.pro.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/11/6 15:21
 * @Version 1.0
 */
public enum SkuStatsEnum implements CodeEnum<String>{


    SALE("SALING","销售中"),
    OFF_SALE("OffShelf","下架"),
    ;
    private String code;

    private String title;

    SkuStatsEnum(String code, String title) {
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

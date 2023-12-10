package com.zzz.pro.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/8/21 23:56
 * @Version 1.0
 */
public enum RelEnum implements CodeEnum<Integer>{
    // 约会关系
    DATING_READY(0, "双方都未发起约会邀请"),
    DATING_WAIT_ACCEPT(1, "等待对方接受邀请"),
    DATING_WAIT_INVITE_CORRECT(2, "对方提出约会"),
    DATING_START(3,"双方都同意约会"),
    DATING_FINISHED(4,"约会完成"),
    DATING_WAIT_ARRIVE(5,"请等待对方到达"),
    DATING_I_INBOARD(6,"我还在路上"),
    DATING_BOTH_ARRIVE(7,"双方都已经到达"),
    DATING_I_CANCEL(8,"我鸽了对方"),
    DATING_U_CANCEL(9,"我被对方鸽了"),

    DATING_INCORRECT_DELAY(10,"约会确认时间过期"),

    //匹配关系
    MATCH_OK(20,"成功建立匹配"),
    MATCH_U_CANCEL(21,"对方解除匹配"),
    MATCH_I_CANCEL(23,"我解除了匹配"),



    ;

    private Integer code;
    private String title;

    RelEnum(Integer code, String title) {
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

    public static String getTitleByCode(Integer code) {
        if(code == null){
            return "";
        }
        for (RelEnum relEnum : RelEnum.values()) {
            if (relEnum.getCode().equals(code)) {
                return relEnum.getTitle();
            }
        }
        return "";
    }

}

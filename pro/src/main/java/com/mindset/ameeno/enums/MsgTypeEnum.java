package com.mindset.ameeno.enums;

public enum MsgTypeEnum implements CodeEnum<Integer>{

    MESSAGE_UNKNOW(0,"未知消息" ),
    MESSAGE_TEXT(1,"文本消息" ),
    MESSAGE_IMAGE(2,"图片消息" ),
    MESSAGE_VIDEO(3,"视频消息" ),
    MESSAGE_SOUND(4,"语音消息" ),
    MESSAGE_FILE(5,"文件消息" ),
    MESSAGE_LOCATION(6,"位置消息" ),
    MESSAGE_GIF_EMOJI(7,"动态表情消息" ),
    MESSAGE_RECALL(8,"撤回消息" ),
    MESSAGE_WRITING(9,"正在写入消息" ),
    MESSAGE_PHONECALL_REQUEST(10,"发起语音通话" ),
    MESSAGE_PHONECALL_ACCEPT(11,"接受语音通话" ),
    MESSAGE_PHONECALL_FINISHED(12,"结束语音通话" ),
    MESSAGE_SYSTEM(20,"系统消息" ),
    MESSAGE_ALERT(500,"系统提示消息" ),
    ;
    private Integer code;

    private String title;

    MsgTypeEnum(Integer code, String title) {
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
        for (MsgTypeEnum msgType : MsgTypeEnum.values()) {
            if (msgType.getCode().equals(code)) {
                return msgType.getTitle();
            }
        }
        return MESSAGE_UNKNOW.getTitle();
    }
}

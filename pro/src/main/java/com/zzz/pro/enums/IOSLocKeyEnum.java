package com.zzz.pro.enums;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/20 21:11
 * @Version 1.0
 */
public enum IOSLocKeyEnum implements CodeEnum<String>{
    MESSAGE_AUDIO("MESSAGE_AUDIO","语音信息"),
    MESSAGE_IMAGE("MESSAGE_IMAGE","图片信息"),
    NOTI_DATING_MATCHER_ARRIV("NOTI_DATING_MATCHER_ARRIV","对方已到达，请尽快到达约定地点。"),
    NOTI_DATING_BOTH_ARRIVE_KEY("NOTI_DATING_BOTH_ARRIVE_KEY","恭喜，你们均到达了约定地点，快上来与对方联系吧。"),
    MESSAGE_GIF("MESSAGE_GIF","动态表情"),
    MESSAGE_VIDEO("MESSAGE_VIDEO","视频消息"),
    MESSAGE_TYPE_UNSUPPORT("MESSAGE_TYPE_UNSUPPORT","不支持的消息类型"),
    NOTI_CHAT_ANONYMOUS_MSG_KEY("NOTI_CHAT_ANONYMOUS_MSG_KEY","一条新消息"),
    NOTI_MATCHING_MSG_KEY("NOTI_MATCHING_MSG_KEY","有一个新的用户匹配了你，快上来看看吧。"),
    NOTI_MATCHED_MSG_KEY("NOTI_MATCHED_MSG_KEY","恭喜你，成功建立匹配。别让Tā等待太久，快上来互相了解吧。"),
    NOTI_DATING_MSG_KEY("NOTI_DATING_MSG_KEY","太棒了！邀约成功！快来看看在哪里跟Tā见面。"),
    NOTI_MATCH_CANCELLED_MSG_KEY("NOTI_MATCH_CANCELLED_MSG_KEY","非常抱歉，对方已经解除了与您的匹配。现在重整旗鼓，再来寻找更加适合的人吧"),
    NOTI_MATCH_EXPIRED_MSG_KEY("NOTI_MATCH_EXPIRED_MSG_KEY","非常抱歉，您的匹配已过期。");;

    private String code;
    private String title;

    IOSLocKeyEnum(String code, String title) {
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

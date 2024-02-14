package com.mindset.ameeno.pojo.dto;

import com.mindset.ameeno.enums.MsgActionEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

//系统级消息
@Component
@Data
public class SysMessage {
    private Integer action = MsgActionEnum.SYSTEM.getType();
    private Object msg ;
    private String content;
    private String topic;
}

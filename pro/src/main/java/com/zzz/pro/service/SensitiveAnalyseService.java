package com.zzz.pro.service;

import com.zzz.pro.enums.ImageDataTypeEnum;
import com.zzz.pro.enums.MsgTypeEnum;
import com.zzz.pro.filter.SensitiveFilter;
import com.zzz.pro.netty.enity.ChatMsg;
import com.zzz.pro.service.api.ContentAnalyseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2023/10/17 23:02
 * @Version 1.0
 */
@Service
@Slf4j
public class SensitiveAnalyseService {
    @Resource
    SensitiveFilter sensitiveFilter;
    @Resource
    ContentAnalyseService contentAnalyseService;

    public Boolean isSensitive(ChatMsg chatMsg)  {
        System.out.printf("1111111111");
        //1.文本检测
        if(chatMsg.getMsgType().equals(MsgTypeEnum.MESSAGE_TEXT.getCode())){
            //TODO 添加百度文本检测
            return sensitiveFilter.isSensitiveText(chatMsg.getMsg());
        }
        //2.图片检测
        if (chatMsg.getMsgType().equals(MsgTypeEnum.MESSAGE_IMAGE.getCode())){
            String imgName = chatMsg.getMsg();
            String imgURL = "http://111.198.42.80:29000/chatimg/"+imgName;
            try {
                //百度图片检测
                log.info("图片检测中---------");
                return contentAnalyseService.ImgCensor(imgURL,
                        ImageDataTypeEnum.IMAGE_URL.getCode(),0);
            }catch (Exception e){
                log.error("图片检测失败，图片URL：{}",imgURL);
            }

        }
        return false;
    }

}

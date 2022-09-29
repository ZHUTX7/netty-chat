package com.proxy.server.service;

import com.proxy.server.task.WsLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author ztx
 * @date 2021-11-29 15:44
 * @description :
 */
public   class WsManager {
    private static Logger logger = LoggerFactory.getLogger(WsManager.class);
    private static WsLauncher ws ;

    public static void setWsLauncher(WsLauncher ws1) throws Exception {
        if(ws1 ==null){
            throw new Exception("ws不能为空");
        }else {
            ws = ws1;
            logger.info("ws成功添加到WsManager!");
        }

    }

//    public static WsLauncher getWsLauncher() throws Exception {
//        if(ws!=null)
//            return ws;
//        else{
//            logger.error("无法获取到ws连接，尝试重新建立中...");
//            try {
//                WsLauncher.start();
//            } catch (IOException e) {
//                logger.error("ws连接失败");
//                logger.error(e.toString());
//            } catch (InterruptedException e) {
//                logger.error("ws连接失败");
//                logger.error(e.toString());
//            }
//            return ws;
//        }
//    }

}

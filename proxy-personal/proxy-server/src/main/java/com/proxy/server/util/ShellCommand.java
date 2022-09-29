package com.proxy.server.util;

/**
 * @author ztx
 * @date 2022-03-08 10:55
 * @description :
 */
/**
 * @author ztx
 * @date 2021-11-22 16:56
 * @description : test
 */

import com.proxy.common.dto.ResultDTO;
import com.proxy.server.handler.TCPChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellCommand
{
    /**
     * 运行shell并获得结果，注意：如果sh中含有awk,一定要按new String[]{"/bin/sh","-c",shStr}写,才可以获得流
     *
     * @param shStr
     *            需要执行的shell
     * @return
     */

    private static Logger logger = LoggerFactory.getLogger(ShellCommand.class);

    public static ResultDTO execShell(String shStr) {
        ResultDTO resultDTO = new ResultDTO();
        try {
            //Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
            logger.debug("执行shell命令: "+shStr);
            Runtime.getRuntime().exec(shStr);
//            Process process = Runtime.getRuntime().exec(shStr);

//            InputStreamReader ir = new InputStreamReader(process.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//            String line;
//            process.waitFor();
//            while ((line = input.readLine()) != null){
//                strList.add(line);
//            }
//            resultDTO.setErrorCode(1);
            //map.put("execResult","success");
           // resultDTO.setPlaylod(map);
            resultDTO.setErrorCode(1);
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setErrorCode(-1);
        }
        return resultDTO;
    }

}


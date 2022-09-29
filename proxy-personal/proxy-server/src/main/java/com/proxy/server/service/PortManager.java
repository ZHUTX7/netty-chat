package com.proxy.server.service;

import com.proxy.common.util.PortTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author ztx
 * @date 2021-03-23 15:33
 * @description :  动态分配端口
 */
public class PortManager {
    private static final Logger logger = LoggerFactory.getLogger(PortManager.class);

    private  static Set<Integer> USED_PORT = new HashSet<Integer>() {
    };
    private  static Set<Integer> VPN_PORT = new HashSet<Integer>() {
    };
    public static synchronized Set<Integer> getUsedPortList(){
        return USED_PORT;
    }
    public static synchronized Set<Integer> getVpnPortList(){
        return VPN_PORT;
    }
    private  static synchronized int getMaxPort() {
        if (USED_PORT.size()>0) {
            return  Collections.max(USED_PORT);
        } else {
            return 11111;
        }
    }
    private  static synchronized int getVPNMaxPort() {
        if (VPN_PORT.size()>0) {
            return  Collections.max(VPN_PORT);
        } else {
            return 14111;
        }
    }
    public static synchronized int distributePort() {
        int port = getMaxPort();
        if(port >= 65500){
            port = 11111;
        }
        while(port < 65530){
            port++;
            logger.debug("尝试分配端口号...");
            if(ServerBeanManager.getProxyChannelService().getServerProxy(port)!=null){
                logger.debug("端口号{}已经被占用，继续分配...",port);
                continue;
            };
            if(!PortTestUtils.isLoclePortUsing(port)){
                logger.debug("端口号{}可用，尝试代理中...",port);
                break;
            }

        }
        return port;
    }

    public static synchronized int openDynamicTransefer(){
        int port = getVPNMaxPort();
        if(port >= 65500){
            port = 11111;
        }
        while(port < 65530){
            port++;
            logger.debug("尝试分配端口号...");
            if(ServerBeanManager.getProxyChannelService().getServerProxy(port)!=null ||
             getVpnPortList().contains(port)){
                logger.debug("端口号{}已经被占用，继续分配...",port);
                continue;
            };
            if(!PortTestUtils.isLoclePortUsing(port)){
                logger.debug("端口号{}可用，尝试代理中...",port);
                break;
            }

        }
        return port;
    }
}

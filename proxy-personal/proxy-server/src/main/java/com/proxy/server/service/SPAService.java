package com.proxy.server.service;

import java.io.IOException;

public interface SPAService {
    //1.添加保护端口
    boolean addProtectPort(String protocol,Integer[] ports) ;
    //2.删除保护端口
    boolean delProtectPort(String protocol,Integer[] ports);
    //3.添加放行IP-port
    boolean addAccessIP(String protocol,Integer[] ports,String ip);
    //4.删除放行IP-port
    boolean delAccessIP(String protocol,Integer[] ports,String ip);
}

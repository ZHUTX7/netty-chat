package com.proxy.server.service;

/**
 * @author ztx
 * @date 2021-07-07 17:48
 * @description : 调用shell命令
 */
public interface ShellService {
    boolean execute(String cmd);
}

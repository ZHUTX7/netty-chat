//package com.proxy.server.service;
//
//import com.proxy.server.handler.HttpRequestHandler;
//import com.proxy.server.util.ShellCommand;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.concurrent.TimeUnit;
//
///**
// * date: 2022-3-15
// * 简化版SPA
// */
//public class SPAServiceImpl implements SPAService{
//
//    private static Logger logger = LoggerFactory.getLogger(SPAServiceImpl.class);
//    private static final String netInterface = (String)ServerBeanManager.getConfigService().getConfigure("NetInterface");
//
//    @Override
//    public boolean addProtectPort(String protocol,Integer[] ports)  {
//        String command;
//        if (netInterface == null||"".equals(netInterface)) {
//            return false;
//        }
//        for (int e : ports) {
//            //添加保护端口
//
//            command = "iptables -t filter -A INPUT   -i " + netInterface + "  -p " + protocol + " --dport " + e + "  -j DROP";
//            try {
//                Runtime.getRuntime().exec(command);
//                logger.error("SPA - 执行shell命令成功： "+command);
//            } catch (IOException ioException) {
//                logger.error("SPA - 执行shell命令失败： "+command);
//            }
//        }
//
//        return true;
//    }
//    //删除放行端口
//    @Override
//    public boolean delProtectPort(String protocol,Integer[] ports) {
//        String command;
//        if (netInterface == null||"".equals(netInterface)) {
//            return false;
//        }
//        for (int e : ports) {
//            command = "iptables -t filter -D INPUT   -i " + netInterface + "  -p " + protocol + " --dport " + e + "  -j DROP";
//            try {
//                //由于iptables可以重复添加策略
//                //执行一次删除策略并不会删除所有相同的策略，因此A策略加了100次就得删100次
//                //1000次容错机会,防止死循环
//                int time = 1000;
//                while (true){
//                    try {
//
//                        Process pr =Runtime.getRuntime().exec(command);
//
//                        try (
//                                InputStream inputStream = pr.getInputStream();
//                                InputStreamReader isr = new InputStreamReader(inputStream);
//                                BufferedReader in = new BufferedReader(isr)) {
//                            StringBuilder content = new StringBuilder();
//
//                            String line;
//                            //当iptables没有可以删除的策略时会报错
//                            while ((line = in.readLine()) != null) {
//                                System.out.println(line);
//                                if(line != null) {
//                                    content.append(line);
//                                    return true;
//                                }
//                            }
//
//                        }
//
//                    } catch( Exception q) {
//                        q.printStackTrace();
//                    }
//                }
//
//            } catch (Exception s) {
//                logger.error("SPA - 执行shell命令失败： "+command);
//                return true;
//            }
//
//
//        }
//        return false;
//    }
//
//    @Override
//    public boolean addAccessIP(String protocol,Integer[] ports,String ip) {
//
//        String command ;
//        if (netInterface == null||"".equals(netInterface)) {
//            return false;
//        }
//        for (int e : ports) {
//            //添加放行IP
//            command = "iptables -t filter -I INPUT 1 -i "+netInterface+" -p "+protocol+" -s "+ip+" -d 0.0.0.0/0 -m multiport --dports "+e+" -j ACCEPT ";
//            try {
//                Runtime.getRuntime().exec(command);
//                logger.error("SPA - 执行shell命令成功： "+command);
//            } catch (IOException ioException) {
//                logger.error("SPA - 执行shell命令失败： "+command);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean delAccessIP(String protocol,Integer[] ports,String ip)  {
//        String command;
//        if (netInterface == null||"".equals(netInterface)) {
//            return false;
//        }
//        for (int e : ports) {
//            //删除放行IP
//            command = "iptables -t filter -D INPUT -i "+netInterface+" -p tcp -s "+ip+" -d 0.0.0.0/0 -m multiport --dports "+e+" -j ACCEPT";
//
//            //由于iptables可以重复添加策略
//            //执行一次删除策略并不会删除所有相同的策略，因此A策略加了100次就得删100次
//            //1000次容错机会,防止死循环
//            //int time = 1000;
//
//            try {
//                ProcessBuilder ps = new ProcessBuilder(command);
//                ps.redirectErrorStream(true);
//                Process pr = ps.start();
//                try (
//                        InputStream inputStream = pr.getInputStream();
//                        InputStreamReader isr = new InputStreamReader(inputStream);
//                        BufferedReader in = new BufferedReader(isr)) {
//                        StringBuilder content = new StringBuilder();
//
//                    String line;
//                    //当iptables没有可以删除的策略时会报错
//                    while ((line = in.readLine()) != null) {
//                        System.out.println(line);
//                        if(line != null) {
//                            content.append(line);
//                            return true;
//                        }
//                    }
//
//                }
//
//            } catch( Exception q) {
//                q.printStackTrace();
//            }
//
//
//        }
//        return true;
//    }
//}

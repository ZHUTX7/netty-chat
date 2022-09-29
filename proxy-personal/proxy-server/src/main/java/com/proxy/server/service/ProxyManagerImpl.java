package com.proxy.server.service;

import com.proxy.common.dto.ResultDTO;
import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.util.PortTestUtils;
import com.proxy.server.dao.GatewayRedisDAO;
import com.proxy.server.handler.HeartBeatRespHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ztx
 * @date 2021-09-13 15:33
 * @description :  proxyServer端口控制层
 */
public class ProxyManagerImpl implements ProxyManager {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRespHandler.class);


    @Override
    public ResultDTO openPort(String clientKey, int port, ProxyRealServer proxyRealServer) {
        ResultDTO resultDTO = new ResultDTO();
        //1、检测端口是否占用放在控制层，由于该端口会被频繁调用， 设计上将该service原子化
        if(PortTestUtils.isLoclePortUsing(port)){
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("开启失败，端口:"+port+"已经被占用");
            logger.error("开启失败，端口:"+port+"已经被占用");
            return resultDTO;
        }
        //2、获取clientNode
        ClientNode clientNode  = ServerBeanManager.getClientService().get(clientKey);
        if(clientNode == null){
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("开启失败，clientKey｛:"+clientKey+"｝不存在或不在线");
            logger.error("开启失败，clientKey｛:"+clientKey+"｝不存在或不在线");
            return resultDTO;
        }
        clientNode.addRealServer(proxyRealServer.getServerPort(), proxyRealServer);
        //记录代理端口号，动态分配端口时则直接跳过已经占用的端口号
        PortManager.getUsedPortList().add(proxyRealServer.getServerPort());
        try{
            switch( proxyRealServer.getProxyType()){
                //TCP转发
                case 1:ServerBeanManager.getClientService().TCPProxy(port,proxyRealServer);break;
                //HTTP转发
                case 2:ServerBeanManager.getClientService().HttpProxy(port,proxyRealServer);break;
                //UDP 转发
                case 3: ServerBeanManager.getClientService().UDPProxy(port,proxyRealServer);break;
                default:break;
            }
        }catch (Exception e){
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("开启失败，系统服务错误: "+e);
            logger.error("开启失败，系统服务错误: "+e);
        }
        resultDTO.setErrorCode(1);
        resultDTO.setMessage("开启端口"+port+" 成功 !");
        logger.error("开启端口"+port+" 成功 !");
        return resultDTO;

    }

    @Override
    public ResultDTO closeProxy(int port) {
        ResultDTO resultDTO = new ResultDTO();
        try{
            ServerBeanManager.getProxyChannelService().unBind(port);
        }catch(Exception e) {
            logger.debug("关闭端口发生异常 : "+e);
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("关闭端口发生异常 : "+e);
            return resultDTO;
        }
        resultDTO.setErrorCode(1);
        resultDTO.setMessage("关闭端口"+port+" 成功 !");
        logger.error("关闭端口"+port+" 成功 !");
        return resultDTO;
    }


    @Override
    public ResultDTO openDynamicProxy(ProxyRealServer proxyRealServer) {

        int port = 0;

        ResultDTO resultDTO = new ResultDTO();

        //1、分配随机端口
        port =  PortManager.distributePort();
        proxyRealServer.setServerPort(port);
        //2、获取clientNode
        Map<String ,ClientNode> nodeMap =  ServerBeanManager.getClientService().getAllNode();
        ClientNode clientNode = nodeMap.get(proxyRealServer.getClientKey());
        if(clientNode ==null ){
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("指定的代理访问服务器不在线。");
            logger.error("指定的代理访问服务器不在线。");
            return resultDTO;
        }
        clientNode.addRealServer(proxyRealServer.getServerPort(), proxyRealServer);
        PortManager.getUsedPortList().add(port);
        try{
            switch( proxyRealServer.getProxyType()){
                //TCP转发
                case 1:ServerBeanManager.getClientService().TCPProxy(port,proxyRealServer);
                       GatewayRedisDAO.addTcpRoute(port,proxyRealServer.getAddress(),clientNode.getClientKey());
                       break;
                    //HTTP转发
                case 2:ServerBeanManager.getClientService().HttpProxy(port,proxyRealServer);
                       break;
                    //UDP 转发
                case 3: ServerBeanManager.getClientService().UDPProxy(port,proxyRealServer);
                        GatewayRedisDAO.addUdpRoute(port,proxyRealServer.getAddress(),clientNode.getClientKey());
                        break;

                default:break;
            }

        }catch (Exception e){
            resultDTO.setErrorCode(-1);
            resultDTO.setMessage("开启失败，系统服务错误: "+e);
            logger.error("开启失败，系统服务错误: "+e);
            return resultDTO;
        }
        resultDTO.setErrorCode(1);
        resultDTO.setMessage("开启端口"+port+" 成功 !");
        Map<String,Object> map = new HashMap<>();
        map.put("port", port);
        map.put("clientKey", proxyRealServer.getClientKey());
        resultDTO.setPlaylod(map);
        logger.debug("开启端口"+port+" 成功 !");
        return resultDTO;
    }


}

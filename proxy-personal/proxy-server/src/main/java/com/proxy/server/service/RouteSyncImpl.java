//package com.proxy.server.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.proxy.common.dto.ResultDTO;
//import com.proxy.common.entity.server.ClientNode;
//import com.proxy.common.entity.server.ProxyRealServer;
//import com.proxy.common.protocol.CommonConstant;
//import com.proxy.common.protocol.RedisKeyNameConfig;
//import com.proxy.common.util.RpcUtils;
//import com.proxy.server.ProxyServer;
//import com.proxy.server.dto.GatewayRouteDTO;
//import com.proxy.server.handler.HttpRequestHandler;
//import com.proxy.server.util.RedisPoolDao;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Map;
//
///**
// * @author ztx
// * @date 2022-01-07 14:46
// * @description :
// */
//public class RouteSyncImpl implements RouteSync{
//
//    private static Logger logger = LoggerFactory.getLogger(RouteSyncImpl.class);
//
//    @Override
//    public ResultDTO clientRouteSync() {
//        ResultDTO result = new ResultDTO();
//
//        return null;
//    }
//
//    @Override
//    public ResultDTO clientRouteSync(String clientKey) {
//        ResultDTO result = new ResultDTO();
//        ClientNode targetPortNode = ServerBeanManager.getClientService().get(clientKey);
//        Map<Object, ProxyRealServer> bakMap = targetPortNode.getServerPort2RealServer();
//        try {
//            //获取客户端节点信息
//            targetPortNode = ServerBeanManager.getClientService().get(clientKey);
//
//            //更新节点 状态(离线)
//            ServerBeanManager.getClientService().setNodeStatus(clientKey, CommonConstant.ClientStatus.OFFLINE);
//
//            //获取最新路由信息
//            JSONObject j = JSON.parseObject("{\"clientKey\": \""+clientKey+"\" }");
//            String url =  (String)ServerBeanManager.getConfigService().getConfigure("controllerHttpURL");
//            JSONObject response = RpcUtils.callOtherInterface(j,url);
//            //TODO 变量对接问题，等待与控制器进行修改
//            response.put("gateway_id",10001);
//            response.put("proxy_client_key",clientKey);
//            response.put("gateway_ip",sa.getHostString());
//            response.put("route",response.get("routeList"));
//            response.remove("routeList");
//
//            int loginResult = Integer.parseInt(response.getString("errorCode"));
//            int routeNum = Integer.parseInt(response.getString("size"));
//            if(loginResult == 1){
//
//                    GatewayRouteDTO gatewayRouteDTO = response.toJavaObject(GatewayRouteDTO.class);
//                    //路由信息条数校验，防止发送过程中丢失
//                    if(gatewayRouteDTO.getRoute().size() != routeNum) {
//                        logger.warn("控制器传来的路由条数与实际数目不匹配 ！ ");
//                        result.setErrorCode(-1);
//                        result.setMessage("  ！，更新失败");
//                    }
//                    //更新路由
//
//                targetPortNode.setServerPort2RealServer(ProxyServer.addClient(gatewayRouteDTO).getServerPort2RealServer());
//
//                }
//
//
//        }catch(Exception e){
//
//        }
//
//        return null;
//    }
//}

//package com.proxy.server.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.proxy.common.dto.ResultDTO;
//import com.proxy.common.entity.server.ProxyRealServer;
//import com.proxy.server.service.*;
//import com.proxy.server.util.ShellCommand;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.*;
//import io.netty.util.CharsetUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author ztx
// * @date 2021-07-07 9:05
// * @description : 处理来自http Request ，根据URI分配+
// */
//public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//
//    private static Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
//    //流量加密程序位置
//    private static final String PRO_URL = (String)ServerBeanManager.getConfigService().getConfigure("transferURL");
//    //流量加密证书位置
//    private static final String CA_URL = (String)ServerBeanManager.getConfigService().getConfigure("caURL");
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
//        handlerControllerRequest(channelHandlerContext,fullHttpRequest);
//    }
//
//    public void handlerControllerRequest(ChannelHandlerContext ctx, FullHttpRequest request){
//        //TODO level-1 :API接口待添加保护操作
//        if(!"cahy1234".equals(request.headers().get("authToken-gateway"))){
//            logger.warn(" 收到未认证的管理平台调用请求");
//        }
//        String url  = request.uri();
//        logger.info("收到管理平台调用接口："+url);
//        ProxyManager proxyManager = new ProxyManagerImpl();
//        ResultDTO resultDTO = new ResultDTO();
//        Map<String,Object> playLoad = new HashMap<>();
//        SPAService s = new SPAServiceImpl();
//        Integer[] ports;
//        JSONObject jsonObject;
//        if(url.startsWith("/bdsec/controller")){
//            switch (url){
//                case "/bdsec/controller/openPort":
//                    //1.检查需要开启的端口是否被占用
//
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ProxyRealServer prs = new ProxyRealServer();
//                    prs.setServerPort(jsonObject.getIntValue("serverPort"));
//
//                    //2.解析剩余JSON
//                    prs.setClientKey(jsonObject.getString("clientKey"));
//                    prs.setDescription(jsonObject.getString("description"));
//                    prs.setName(jsonObject.getString("name"));
//                    prs.setDomain(jsonObject.getString("domain"));
//                    prs.setProxyType(jsonObject.getIntValue("proxyType"));
//                    prs.setRealHost(jsonObject.getString("realHost"));
//                    prs.setStatus(jsonObject.getIntValue("status"));
//                    prs.setRealHostPort(jsonObject.getIntValue("realHostPort"));
//                    //3.执行开启端口service
//                    resultDTO =  proxyManager.openPort(prs.getClientKey(),prs.getServerPort(),prs);
//                    //4.返回结果
//
//                    break;
//
//                case "/bdsec/controller/unBindPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    int port =jsonObject.getIntValue("port");
//                    resultDTO = proxyManager.closeProxy(port);
//                    break;
//                case "/bdsec/controller/distributePort":
//                    //1.检查需要开启的端口是否被占用
//
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ProxyRealServer prs2 = new ProxyRealServer();
//                    prs2.setServerPort(jsonObject.getIntValue("serverPort"));
//                    //2.解析剩余JSON
//                    prs2.setClientKey(jsonObject.getString("clientKey"));
//                    prs2.setDomain(jsonObject.getString("domain"));
//                    prs2.setProxyType(jsonObject.getIntValue("proxyType"));
//                    prs2.setRealHost(jsonObject.getString("realHost"));
//                    prs2.setStatus(jsonObject.getIntValue("status"));
//                    prs2.setRealHostPort(jsonObject.getIntValue("realHostPort"));
//                    resultDTO =  proxyManager.openDynamicProxy(prs2);
//                    break;
//
//                case "/bdsec/controller/openTransferPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    int tPort = PortManager.openDynamicTransefer();
//                    String usId =  jsonObject.getString("user_id");
//                    //2.解析剩余JSON
//                    resultDTO = ShellCommand.execShell("nohup "+PRO_URL +" start server -addr=:"+tPort+" "+CA_URL+ " &");
//                    PortManager.getVpnPortList().add(tPort);
//                    playLoad.put("user_id",usId);
//                    playLoad.put("port",tPort);
//                    resultDTO.setPlaylod(playLoad);
//                    break;
//                case "/bdsec/controller/closeTransferPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    int cPort = jsonObject.getIntValue("port");
//                    PortManager.getVpnPortList().remove(cPort);
//                    //2.解析剩余JSON
//                    resultDTO = ShellCommand.execShell("nohup "+ PRO_URL +" stop "+cPort+ " &");
//                    break;
//                case "/bdsec/controller/restartTransferPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    JSONArray jsonArray = jsonObject.getJSONArray("portList");
//                    Integer[] restartPorts = new Integer[jsonArray.size()];
//                    jsonArray.toArray(restartPorts);
//                    List<Integer> error = new ArrayList<>();
//                    for(int e :restartPorts){
//                        logger.info("重启VPN端口{}中",e);
//                        try{
//                            ShellCommand.execShell("nohup "+ PRO_URL +" stop "+e+ " &");
//                            ShellCommand.execShell("nohup "+PRO_URL +" start server -addr=:"+e+" "+CA_URL+ " &");
//                        }catch (Exception q){
//                            logger.error("重启VPN端口{}失败",e);
//                            error.add(e);
//                            q.printStackTrace();
//                        }
//
//                    }
//                    playLoad.put("error_restart_port",error);
//                    playLoad.put("error_sum",error.size());
//                    resultDTO.setPlaylod(playLoad);
//                    break;
//                    //----------------- TODO： SPA 待测试-----------------------
//                case "/bdsec/controller/add/protectPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ports = new Integer[jsonObject.getJSONArray("ports").size()];
//                    ports = jsonObject.getJSONArray("ports").toArray(ports);
//                     if(s.addProtectPort(jsonObject.getString("protocol"),
//                            ports)){
//                         resultDTO.setErrorCode(1);
//                         resultDTO.setMessage("添加保护端口成功");
//                     }else {
//                         resultDTO.setErrorCode(-1);
//                         resultDTO.setMessage("添加保护端口失败");
//                     }
//                    break;
//                case "/bdsec/controller/del/protectPort":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ports = new Integer[jsonObject.getJSONArray("ports").size()];
//                    ports = jsonObject.getJSONArray("ports").toArray(ports);
//                    if(s.delProtectPort(jsonObject.getString("protocol"),ports)){
//                        resultDTO.setErrorCode(1);
//                        resultDTO.setMessage("删除保护端口成功");
//                    }else {
//                        resultDTO.setErrorCode(-1);
//                        resultDTO.setMessage("删除保护端口失败");
//                    }
//                    break;
//                case "/bdsec/controller/add/accessIP":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ports = new Integer[jsonObject.getJSONArray("ports").size()];
//                    ports = jsonObject.getJSONArray("ports").toArray(ports);
//
//                    if(s.addAccessIP(jsonObject.getString("protocol"),ports,
//                            jsonObject.getString("source_ip"))){
//                        resultDTO.setErrorCode(1);
//                        resultDTO.setMessage("添加放行IP成功，IP："+jsonObject.getString("source_ip"));
//                    }else {
//                        resultDTO.setErrorCode(-1);
//                        resultDTO.setMessage("添加放行IP失败，IP："+jsonObject.getString("source_ip"));                    }
//                    break;
//                case "/bdsec/controller/del/accessIP":
//                    jsonObject = JSON.parseObject(request.content().toString(CharsetUtil.UTF_8));
//                    ports = new Integer[jsonObject.getJSONArray("ports").size()];
//                    ports = jsonObject.getJSONArray("ports").toArray(ports);
//                    if(s.delAccessIP(jsonObject.getString("protocol"),ports,
//                            jsonObject.getString("source_ip"))){
//                        resultDTO.setErrorCode(1);
//                        resultDTO.setMessage("删除放行IP成功，IP："+jsonObject.getString("source_ip"));
//                    }else {
//                        resultDTO.setErrorCode(-1);
//                        resultDTO.setMessage("删除放行IP失败，IP："+jsonObject.getString("source_ip"));
//                    }
//                    break;
//                default :
//                    resultDTO.setErrorCode(-1);
//                    resultDTO.setMessage("URL地址错误，请确认接口地址.");
//                    logger.error("URL地址错误，请确认接口地址.");
//                    break;
//            }
//        }else{
//            resultDTO.setErrorCode(-1);
//            resultDTO.setMessage("URL地址错误，请确认接口地址.");
//        }
//        jsonObject = null;
//        ports = null;
//        FullHttpResponse response = buildResponse(JSON.toJSONString(resultDTO));
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//    }
//
//    public static FullHttpResponse buildResponse(String json){
//        FullHttpResponse response = new DefaultFullHttpResponse(
//                HttpVersion.HTTP_1_1,
//                HttpResponseStatus.OK,
//                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
//        // 设置头信息
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
//        return response;
//    }
//
//
//}

package com.proxy.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.util.HostInfoUtil;
import com.proxy.server.dto.GatewayLogDTO;
import com.proxy.server.handler.WebSocketClientHandler;

import com.proxy.server.service.ServerBeanManager;
import com.proxy.server.service.WsManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ztx
 * @date 2021-11-29 14:35
 * @description :
 */
public class WsLauncher {
    private static  int time = 100;
    private static Logger logger = LoggerFactory.getLogger(WsLauncher.class);
    private final URI uri;
    private Channel ch;
    private static final EventLoopGroup group = new NioEventLoopGroup();
    private static boolean runningFlag = false;
    public WsLauncher(final String uri) {
        this.uri = URI.create(uri);
    }
    private static final int GATEWAY_ID = (Integer) ServerBeanManager.getConfigService().getConfigure("id");
    public boolean getRunningFlag() {return runningFlag;}
    public void setRunningFlag(boolean flag) {
        runningFlag = flag;
    }
    public void open() throws Exception {
        Bootstrap b = new Bootstrap();
        String protocol = uri.getScheme();
        if (!"ws".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        final WebSocketClientHandler handler =
                new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE, 1280000)
                );

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("http-codec", new HttpClientCodec());
                      //  pipeline.addLast("chunked-write", new ChunkedWriteHandler());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(1024*64));
                      //  pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        pipeline.addLast("ws-handler", handler);
                        //pipeline.addLast(new WebSocketServerProtocolHandler("/ws"))
                    }
                });

        ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
    }

    public void close() throws InterruptedException {
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
    }

    public void sendMessage(final String text) throws IOException {
        ch.writeAndFlush(new TextWebSocketFrame(text));
    }
    public void sendMessage2(final JSON json) throws IOException {
        ByteBuf b = Unpooled.buffer(1000);
        b.writeBytes(json.toJSONString().getBytes(StandardCharsets.UTF_8));
        ch.writeAndFlush(new BinaryWebSocketFrame(b));
    }
    public static void start () throws Exception {
        // 获取websocket URL
        String wsURL = (String) ServerBeanManager.getConfigService().getConfigure("wsURL") + "=" +
                ServerBeanManager.getConfigService().getConfigure("id");

        if (wsURL == null || "".equals(wsURL)) {
            logger.warn("---------- wsURL为空，请在proxy.yaml中配置   -------------");
            System.exit(-1);
        }

        WsLauncher ws = new WsLauncher(wsURL);
        ws.setRunningFlag(true);
        WsManager.setWsLauncher(ws);

        Map<String, Object> hostInfoMap = new HashMap<>();
        Map<String, Object> hostInfoMap2 = new HashMap<>();
        hostInfoMap.put("hostInfo", hostInfoMap2);
        Map<String, ClientNode> cMap = ServerBeanManager.getClientService().getAllNode();
        Map<Long, Channel> uMap = ServerBeanManager.getUserSessionService().getAll();
        //连接成功才开线程
        //开启一个线程发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if(ws.ch==null){
                            ws.open();
                            logger.info("与管理平台websocket连接成功！");
                        }
                        if(!ws.ch.isOpen()){
                            ws.open();
                            logger.info("与管理平台websocket连接成功！");
                        }

                    } catch (Exception e) {
                        logger.warn("与管理平台websocket连接失败，5s后重新尝试");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        continue;
                    }

                    int cpu = HostInfoUtil.getCpuLoad();
                    int memory = HostInfoUtil.getMemoryLoad();
                    hostInfoMap.put("messageType", 1); //1. 心跳信息
                    hostInfoMap2.put("cpu", cpu + "%");
                    hostInfoMap2.put("id", GATEWAY_ID);
                    hostInfoMap2.put("userConnSum", uMap.size());
                    hostInfoMap2.put("proxyClientSum", cMap.size());
                    hostInfoMap2.put("memory", memory + "%");
                    JSON hostInfo = new JSONObject(hostInfoMap);
                    try {
                        if (!ws.ch.isOpen()) {
                            throw new IOException("与管理平台websocket断开连接");
                        }
                        ws.sendMessage(hostInfo.toJSONString());
                        Thread.sleep(5000);
                        logger.info("发送性能信息到管理平台...");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("与管理平台websocket断开连接");
                        continue;
                    }

                }

            }
        }).start();


    }


    public void closeWebSocket(WsLauncher ws) throws InterruptedException {
        ws.setRunningFlag(false);
        ws.close();
        logger.info("websocket连接关闭");
    }

}

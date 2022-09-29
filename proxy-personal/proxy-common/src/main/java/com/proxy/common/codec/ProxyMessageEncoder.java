package com.proxy.common.codec;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.util.ProxyMessageUtil;
import com.proxy.common.util.SM4;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;


public class ProxyMessageEncoder extends MessageToByteEncoder<ProxyMessage> {
    private static Logger logger = LoggerFactory.getLogger(ProxyMessageEncoder.class);


    @Override
    protected void encode(ChannelHandlerContext ctx, ProxyMessage msg, ByteBuf in) throws Exception {
        //System.out.println(" 测试Handler----------------Encoder ------------------");

        if (msg == null) {
            throw new Exception("The encode message is null");
        }
        //2. 二层国密加密-------------  by ztx
        byte[] bytes = ProxyMessageUtil.encode(msg);
        //TODO  流量加密TEST
        //byte[] bytes = SM4.getSM4().encrypt(ProxyMessageUtil.encode(msg));
        //--------------------------
        in.writeInt(bytes.length);
        in.writeBytes(bytes); 
    }
    private static String intToHex(int n) {
        Integer q = n;
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(q != 0){
            s = s.append(b[q%16]);
            q = q/16;
        }
        a = s.reverse().toString();
        System.out.println(a);
        return a;
    }

}

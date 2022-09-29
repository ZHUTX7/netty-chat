package com.proxy.common.codec;


import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.util.ProxyMessageUtil;
import com.proxy.common.util.SM4;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static Logger logger = LoggerFactory.getLogger(ProxyMessageDecoder.class);

    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset,
                               int lengthFieldLength) {

        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        int length = frame.readInt();
        byte[] msg = new byte[length];
        frame.readBytes(msg);
        byte[] b = msg;

       // byte[] b = SM4.getSM4().decrypt(msg);
        ProxyMessage proxyMessage = ProxyMessageUtil.decode(b);
        frame.release();
        return proxyMessage;



    }
    private static String intToHex(int n) {
        int q = n;
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(q != 0){
            s = s.append(b[q%16]);
            q = q/16;
        }
        a = s.reverse().toString();
        return a;
    }
}

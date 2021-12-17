package com.zzz.pro.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

/**
 * @author ztx
 * @date 2021-12-17 19:08
 * @description :
 */
public class ResponseBuilder {
    public  static FullHttpResponse initialResponse(ByteBuf byteBuf){
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        return response;
    }
}

package com.proxy.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

import java.security.cert.Certificate;
import java.security.cert.X509Extension;


/*

 */
public class CertParseHandler extends ChannelInboundHandlerAdapter
{

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
	{
		if (evt instanceof SslHandshakeCompletionEvent)
		{
			SslHandler sslhandler = ctx.channel().pipeline().get(SslHandler.class);
			System.out.println("handshake done");
			Certificate cert = sslhandler.engine().getSession().getPeerCertificates()[0];
			// parse customer id here
			((X509Extension) cert).getExtensionValue("1.2.3.412");
		}
	}

}

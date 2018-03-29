package com.rgtech.websocket;

import com.rgtech.bean.PbTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoBufServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(WebsocketServer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PbTest.Person){
            log.info("receive msg:{}",msg.toString());
            ByteBuf result = Unpooled.buffer();
            result.writeBytes(((PbTest.Person) msg).toByteArray());
            ctx.writeAndFlush(result);
        }else{
            super.channelRead(ctx,msg);
            System.out.println("unkown msg post!!!!="+msg.toString());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
package com.rgtech.websocket;

import com.rgtech.bean.PbTest.Person;
import com.rgtech.bean.PbTest.Profile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoBufClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ProtoBufClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Person.Builder builder=Person.newBuilder();
        builder.setBool(true).setDouble(1.1).setFloat(1.1f).setInt32(1).setInt64(1l).setSint32(1).setString("1").setUint64(1l);
        ctx.writeAndFlush(builder.build());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Person){
            log.info("callback msg:{}",msg.toString());
        }else{
            super.channelRead(ctx,msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
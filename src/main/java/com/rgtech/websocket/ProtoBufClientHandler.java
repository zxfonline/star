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
        builder.setEmail("zxfonline@sina.com").setId(1).setName("zxf");
        builder.addProfile(Profile.newBuilder().setIcon("icon1").setNickName("nick1").build());
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
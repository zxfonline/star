package com.rgtech.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebSocketMessageHandler extends WebSocketFrameAggregator {
    private static final Logger log = LoggerFactory.getLogger(WebsocketServer.class);

    public WebSocketMessageHandler(int maxContentLength) {
        super(maxContentLength);
    }

    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        //二进制帧处理,将帧的内容往下传
        if (frame instanceof BinaryWebSocketFrame) {
            System.out.println("The WebSocketFrame is BinaryWebSocketFrame");
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            byte[] by = new byte[frame.content().readableBytes()];
            binaryWebSocketFrame.content().readBytes(by);
            ByteBuf bytebuf = Unpooled.buffer();
            bytebuf.writeBytes(by);
            out.add(bytebuf);
        }else {
            log.info("unknown 帧消息："+frame.toString());
        }
    }
}

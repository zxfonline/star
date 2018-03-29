package com.rgtech.websocket;

import java.util.Date;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rgtech.bean.PbTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.LoggerFactory;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WebsocketServer.class);

    private WebSocketServerHandshaker handshaker;
    private final String address;

    public WebSocketServerHandler(String address){
        this.address=address;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"掉线");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress() +"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress() +"离开");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"在线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	// (7)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
//        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
//        }else{
//            log.warn("{} received unkown msg:{}",ctx.channel(),msg);
//            ctx.fireChannelRead(msg);
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) {
//        log.info("{} received WebSocketFrame{}", ctx.channel(),frame);
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        }else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }else if (frame instanceof TextWebSocketFrame) {
            byte[] by = new byte[frame.content().readableBytes()];
            frame.content().readBytes(by);
            try {
                PbTest.Person person=PbTest.Person.parseFrom(by);
                ByteBuf result = Unpooled.buffer();
                result.writeBytes(person.toByteArray());
                ctx.writeAndFlush(new TextWebSocketFrame(result));
                log.info("\nparseMsg={}",person.toString());
            } catch (Throwable e) {
//                log.error("parse error：{}", StringUtil.toHexString(by));
                ctx.channel().writeAndFlush(new TextWebSocketFrame(Unpooled.buffer().writeBytes(by)));
            }
        }else if (frame instanceof BinaryWebSocketFrame){//二进制消息向下传递
            byte[] by = new byte[frame.content().readableBytes()];
            frame.content().readBytes(by);
            ctx.fireChannelRead(Unpooled.buffer().writeBytes(by));
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req) {
        if (!req.decoderResult().isSuccess()|| (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(address, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {
        return false;
    }
}
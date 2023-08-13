package org.example.imserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(StandardCharsets.UTF_8);
        System.out.println(new Date() + " 接收 -> " + message);

        byte[] bytes = ("服务端已经收到了:" + message).getBytes(StandardCharsets.UTF_8);
        ByteBuf bufOut = ctx.alloc().buffer();
        bufOut.writeBytes(bytes);
        ctx.channel().writeAndFlush(bufOut);
        System.out.println("服务端返回数据给客户端");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}

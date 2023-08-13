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
        System.out.println(new Date() + "服务端接收到客户端发来的数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
        System.out.println("服务端返回数据给客户端");
        byte[] bytes = "你发送给服务端的数据已经收到了".getBytes(StandardCharsets.UTF_8);
        ByteBuf bufOut = ctx.alloc().buffer();
        bufOut.writeBytes(bytes);
        ctx.channel().writeAndFlush(bufOut);

    }
}

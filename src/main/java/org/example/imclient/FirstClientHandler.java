package org.example.imclient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端向服务器发送数据");
        //分配内存缓冲区
        ByteBuf byteBuf = ctx.alloc().buffer();
        //定义发送给服务器的数据
        byte[] bytes = "你好啊, 服务器".getBytes(StandardCharsets.UTF_8);
        //将数据写入到缓冲区
        byteBuf.writeBytes(bytes);
        //通过上下文将缓冲区数据写入到channel
        ctx.channel().writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + "客户端接收到了服务端发来的数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
    }
}

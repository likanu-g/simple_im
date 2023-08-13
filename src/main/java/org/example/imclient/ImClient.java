package org.example.imclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ImClient {

    private final static short MAX_RETRY = 5;

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new FirstClientHandler());
                    }
                });
        connect(bootstrap, "localhost", 8000, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port)
                .addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("连接服务器成功!");
                    } else if (retry == 0) {
                        System.out.println("重试次数已经用完，放弃重试连接!");
                    } else {
                        // 第几次重连
                        int order = (MAX_RETRY - retry) + 1;
                        // 本次重连的间隔
                        int delay = 1 << order;
                        System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                        bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                                .SECONDS);
                    }
                });
    }
}

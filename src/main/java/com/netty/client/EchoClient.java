package com.netty.client;

import com.netty.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jingzhi.wu on 2018/3/22.
 */
public class EchoClient {
    private final String host;
    private final int port;
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); //1
            b.group(group) //2
                    .channel(NioSocketChannel.class) //3
                    .remoteAddress(new InetSocketAddress(host, port)) //4
                    .handler(new ChannelInitializer<SocketChannel>() { //5
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync(); //6
            f.channel().closeFuture().sync(); //7
        } finally {
            group.shutdownGracefully().sync(); //8
        }
    }
    public static void main(String[] args) throws Exception {

        final String host = "127.0.0.1";
        final int port = 8080;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 16; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        new EchoClient(host, port).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        executorService.shutdown();
    }
}

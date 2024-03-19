package fun.keepon;

import fun.keepon.channel.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
@Slf4j
public class NettyBootStrapInitializer {
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
    }

    private NettyBootStrapInitializer() {
    }

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}

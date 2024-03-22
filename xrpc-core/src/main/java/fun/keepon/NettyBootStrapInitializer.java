package fun.keepon;

import fun.keepon.channel.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LittleY
 * @description NettyBootStrap初始化器
 * @date 2024/2/4
 */
@Slf4j
public class NettyBootStrapInitializer {
    @Getter
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
    }

    private NettyBootStrapInitializer() {
    }
}

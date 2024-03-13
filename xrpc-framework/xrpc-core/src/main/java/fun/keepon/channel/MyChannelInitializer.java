package fun.keepon.channel;

import fun.keepon.channel.handler.MyChannelInboundHandler;
import fun.keepon.channel.handler.XRpcRequestEncoderHandler;
import fun.keepon.channel.handler.XRpcResponseDecoderHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author LittleY
 * @description 消费者端Channel初始化器
 * @date 2024/2/4
 */
@ChannelHandler.Sharable
public class MyChannelInitializer extends ChannelInitializer {
    private final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    @Override
    protected void initChannel(Channel ch){
        ch.pipeline().addLast(LOGGING_HANDLER);

        //入站处理器
        ch.pipeline().addLast(new XRpcResponseDecoderHandler());
        ch.pipeline().addLast(new MyChannelInboundHandler());

        //出站处理器
        ch.pipeline().addLast(new XRpcRequestEncoderHandler());
    }
}

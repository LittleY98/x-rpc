package fun.keepon.channel;

import fun.keepon.XRpcBootStrap;
import fun.keepon.channel.handler.MyChannelInboundHandler;
import fun.keepon.channel.handler.XRpcEncoderHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
public class MyChannelInitializer extends ChannelInitializer {
    private LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private MyChannelInboundHandler MY_CHANNEL_HANDLER = new MyChannelInboundHandler();
    private StringDecoder STRING_DECODER = new StringDecoder();
    @Override
    protected void initChannel(Channel ch){
        ch.pipeline().addLast(LOGGING_HANDLER);
        ch.pipeline().addLast(MY_CHANNEL_HANDLER);
        ch.pipeline().addLast(new XRpcEncoderHandler());
    }
}

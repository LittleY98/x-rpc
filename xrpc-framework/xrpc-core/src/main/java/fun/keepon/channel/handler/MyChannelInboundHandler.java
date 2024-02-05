package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
public class MyChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = ((ByteBuf) msg).toString(StandardCharsets.UTF_8);
        XRpcBootStrap.PENDING_REQUEST.get(1L).complete(str);
        super.channelRead(ctx, msg);
    }
}

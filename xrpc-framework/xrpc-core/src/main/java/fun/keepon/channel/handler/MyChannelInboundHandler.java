package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
public class MyChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String) msg;
        XRpcBootStrap.PENDING_REQUEST.get(1l).complete(str);
        super.channelRead(ctx, msg);
    }
}

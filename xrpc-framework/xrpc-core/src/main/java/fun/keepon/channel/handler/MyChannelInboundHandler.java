package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
//public class MyChannelInboundHandler extends ChannelInboundHandlerAdapter {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        XRpcBootStrap.PENDING_REQUEST.get(1L).complete(str);
//        super.channelRead(ctx, msg);
//    }
//}

public class MyChannelInboundHandler extends SimpleChannelInboundHandler<XRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XRpcResponse msg) throws Exception {
        XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(msg.getReturnVal());
    }
}

package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import fun.keepon.constant.ResponseStatus;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
@Slf4j
public class MyChannelInboundHandler extends SimpleChannelInboundHandler<XRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XRpcResponse msg) {
        byte code = msg.getCode();
        if (code == ResponseStatus.CURRENT_LIMITING_REJECTION.getId()) {
            XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(null);
//            log.error("The request {} is too fast and is blocked by the limiter, error: {}", msg.getRequestId(), ResponseStatus.CURRENT_LIMITING_REJECTION.getDesc());
            log.error("The request {} is too fast and is blocked by the limiter", msg.getRequestId());
        }
        XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(msg.getReturnVal());
    }
}

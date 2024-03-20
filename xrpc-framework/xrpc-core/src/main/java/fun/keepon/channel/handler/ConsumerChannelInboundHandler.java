package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import fun.keepon.constant.ResponseStatus;
import fun.keepon.exceptions.ResponseException;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/4
 */
@Slf4j
public class ConsumerChannelInboundHandler extends SimpleChannelInboundHandler<XRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XRpcResponse msg) {
        byte code = msg.getCode();
        if (code == ResponseStatus.CURRENT_LIMITING_REJECTION.getId()) {
            XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(null);
            log.error("The request {} is too fast and is blocked by the limiter", msg.getRequestId());
        } else if (code == ResponseStatus.PREPARE_SHUTDOWN.getId()) {
            XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(null);
            log.error("The server is ready to shutdown, denying the request {}", msg.getRequestId());

            // 修正负载均衡器
            // 从健康列表移除
            XRpcBootStrap.CHANNEL_CACHE.remove(ctx.channel().remoteAddress());

            // TODO 找到负载均衡器进行reloadBalance 重新进行负载均衡
            LoadBalancer loadBalancer = XRpcBootStrap.getInstance().getConfiguration().getLoadBalancer();

            throw new ResponseException(ResponseStatus.PREPARE_SHUTDOWN.getDesc());
        }else if (code == ResponseStatus.SUCCESS.getId()){
            log.debug("Request successful -- {}", msg.getRequestId());
            XRpcBootStrap.PENDING_REQUEST.get(msg.getRequestId()).complete(msg.getReturnVal());
        }
    }
}

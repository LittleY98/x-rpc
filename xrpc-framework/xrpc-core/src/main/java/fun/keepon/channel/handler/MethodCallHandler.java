package fun.keepon.channel.handler;

import fun.keepon.config.Configuration;
import fun.keepon.config.ServiceConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.constant.RequestType;
import fun.keepon.constant.ResponseStatus;
import fun.keepon.protect.RateLimiter;
import fun.keepon.protect.impl.SlidingWindowRateLimiter;
import fun.keepon.serialize.SerializerFactory;
import fun.keepon.shutdown.ShutDownAssist;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/5
 */
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<XRpcRequest> {
    Configuration conf = XRpcBootStrap.getInstance().getConfiguration();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XRpcRequest msg) throws Exception {
        log.debug("MethodCallHandler: {}", msg);

        // 先构造基础的报文
        XRpcResponse response = buildingBaseResponse(msg);

        // 检查挡板
        if (ShutDownAssist.BAFFLE.get()) {
            log.warn("System is about to shut down. Request denied...");
            response.setCode(ResponseStatus.PREPARE_SHUTDOWN.getId());
            ctx.channel().writeAndFlush(response);
            return;
        }

        // 增加任务计数器
        ShutDownAssist.EXECUTE_COUNTER.increment();

        // 限流器验证
        boolean rateLimiterCheck = rateLimiterCheck(ctx.channel());

        if (!rateLimiterCheck){
            response.setCode(ResponseStatus.CURRENT_LIMITING_REJECTION.getId());
        }else if (msg.getRequestType() == RequestType.REQUEST.getId()) {
            RequestPayLoad payLoad = msg.getRequestPayLoad();
            Object ret = callTargetMethod(payLoad);
            response.setCode(ResponseStatus.SUCCESS.getId());
            response.setReturnVal(ret);
        }
        ShutDownAssist.EXECUTE_COUNTER.decrement();
        // 发出响应
        ctx.channel().writeAndFlush(response);
    }

    /**
     * 构建基础的报文
     * @param msg 请求消息体
     * @return XRpcResponse
     */
    private XRpcResponse buildingBaseResponse(XRpcRequest msg) {
        XRpcResponse response = new XRpcResponse();
        response.setRequestId(msg.getRequestId());
        response.setCompressType(CompressorFactory.getCompressorByName(conf.getCompress()).getCode());
        response.setSerializeType(SerializerFactory.getSerializerByName(conf.getSerializer()).getCode());
        response.setRequestType(msg.getRequestType());

        return response;
    }

    /**
     * 限流器验证
     * @param channel Channel
     * @return 是否允许通过
     */
    private boolean rateLimiterCheck(Channel channel) {
        SocketAddress socketAddress = channel.remoteAddress();
        Map<SocketAddress, RateLimiter> cache = conf.getRateLimiterForIpCache();
        if (!cache.containsKey(socketAddress)) {
            cache.put(socketAddress, new SlidingWindowRateLimiter());
        }

        return cache.get(socketAddress).allowRequest();
    }

    private Object callTargetMethod(RequestPayLoad payLoad) {
        String methodName = payLoad.getMethodName();
        Object[] parameters = payLoad.getParameters();
        Class<?>[] parameterTypes = payLoad.getParameterTypes();
        String serviceName = payLoad.getServiceName();

        ServiceConfig<?> serviceConfig = XRpcBootStrap.SERVERS_MAP.get(serviceName);

        Object ref = serviceConfig.getRef();

        Object invoke;
        try {
            Method method = ref.getClass().getMethod(methodName, parameterTypes);
            invoke = method.invoke(ref, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("method call failed, request payload: {}", payLoad);
            throw new RuntimeException(e);
        }

        return invoke;
    }
}

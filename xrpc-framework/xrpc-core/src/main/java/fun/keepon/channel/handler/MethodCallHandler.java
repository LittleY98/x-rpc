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
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.transport.message.XRpcResponse;
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
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XRpcRequest msg) throws Exception {
        log.debug("MethodCallHandler: {}", msg);
        RequestPayLoad payLoad = msg.getRequestPayLoad();
        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();


        // 限流器验证
        // TODO 待完善
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        Map<SocketAddress, RateLimiter> cache =
                conf.getRateLimiterForIpCache();
        if (!cache.containsKey(socketAddress)) {
            cache.put(socketAddress, new SlidingWindowRateLimiter(200, 10));
        }
        if (!cache.get(socketAddress).allowRequest()) {
            log.info("ip: {} The request is too fast and is blocked by the limiter", socketAddress);

            // 封装响应报文
            XRpcResponse response = new XRpcResponse();
            response.setRequestId(msg.getRequestId());
            response.setRequestType(msg.getRequestType());
            response.setCode(ResponseStatus.CURRENT_LIMITING_REJECTION.getId());
            response.setCompressType(CompressorFactory.getCompressorByName(conf.getCompress()).getCode());
            response.setSerializeType(SerializerFactory.getSerializerByName(conf.getSerializer()).getCode());
            // 发出响应
            ctx.channel().writeAndFlush(response);
            return;
        }

        // 调用方法拿到结果
        Object ret = null;
        if (!(msg.getRequestType() == RequestType.HEART_BEAT.getId())) {
            ret = callTargetMethod(payLoad);
        }
        // 封装响应报文
        XRpcResponse response = new XRpcResponse();
        response.setCode(ResponseStatus.SUCCESS.getId());
        response.setRequestId(msg.getRequestId());
        response.setCompressType(CompressorFactory.getCompressorByName(conf.getCompress()).getCode());
        response.setSerializeType(SerializerFactory.getSerializerByName(conf.getSerializer()).getCode());
        response.setRequestType(msg.getRequestType());
        response.setReturnVal(ret);

        // 发出响应
        ctx.channel().writeAndFlush(response);
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

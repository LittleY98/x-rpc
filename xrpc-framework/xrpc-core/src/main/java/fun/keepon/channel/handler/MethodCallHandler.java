package fun.keepon.channel.handler;

import fun.keepon.ServiceConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.constant.RequestType;
import fun.keepon.constant.ResponseStatus;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        // 调用方法拿到结果
        Object ret = null;
        if (!(msg.getRequestType() == RequestType.HEART_BEAT.getId())) {
            ret = callTargetMethod(payLoad);
        }
        // 封装响应报文
        XRpcResponse response = new XRpcResponse();
        response.setCode(ResponseStatus.SUCCESS.getId());
        response.setRequestId(msg.getRequestId());
        response.setCompressType(CompressorFactory.getCompressorByName(XRpcBootStrap.getInstance().getConfiguration().getCompress()).getCode());
        response.setSerializeType(msg.getSerializeType());
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

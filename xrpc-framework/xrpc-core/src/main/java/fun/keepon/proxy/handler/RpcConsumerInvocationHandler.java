package fun.keepon.proxy.handler;

import fun.keepon.NettyBootStrapInitializer;
import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.config.Configuration;
import fun.keepon.constant.RequestType;
import fun.keepon.discovery.Registry;
import fun.keepon.exceptions.NetWorkException;
import fun.keepon.loadbalance.RoundRobinLoadBalancer;
import fun.keepon.serialize.SerializerFactory;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.utils.SnowflakeIDGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LittleY
 * @description 封装了客户端通信的基础逻辑
 * @date 2024/2/4
 */
@Slf4j
public class RpcConsumerInvocationHandler<T> implements InvocationHandler {

    /**
     * 注册中心
     */
    @Setter
    private Registry registry;

    /**
     * 代理接口
     */
    private Class<T> interfaceRef;

    public RpcConsumerInvocationHandler(Registry registry, Class<T> interfaceRef) {
        this.registry = registry;
        this.interfaceRef = interfaceRef;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();

        // 2.2 封装报文
        CompletableFuture<Object> retFuture = new CompletableFuture<>();
        // 请求标识符
        long reqId = conf.getSnowflakeIdGenerator().nextId();
        XRpcBootStrap.PENDING_REQUEST.put(reqId, retFuture);

        RequestPayLoad payLoad = RequestPayLoad.builder()
                .serviceName(interfaceRef.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .methodName(method.getName())
                .returnType(method.getReturnType())
                .build();

        XRpcRequest request = XRpcRequest.builder()
                .requestId(reqId)
                .compressType(CompressorFactory.getCompressorByName(conf.getCompress()).getCode())
                .requestType(RequestType.REQUEST.getId())
                .serializeType(SerializerFactory.getSerializerByName(conf.getSerializer()).getCode())
                .requestPayLoad(payLoad)
                .build();

        XRpcBootStrap.REQUEST_THREAD_LOCAL.set(request);


        // 1 拿到服务节点的地址
        InetSocketAddress addr = conf.getLoadBalancer().selectServiceAddr(interfaceRef.getName());

        // 2 向服务端发起请求，获取结果
        // 2.1 获取Channel
        Channel ch = getChannel(addr);


        log.debug("request obj: {}", request);


        // 2.3 发起请求
        ch.writeAndFlush(request).addListener(promise -> {
            if (!promise.isSuccess()){
                retFuture.completeExceptionally(promise.cause());
            }
        });

        // 2.4 获取结果
        return retFuture.get(3, TimeUnit.SECONDS);
    }

    /**
     * 根据地址获取Channel
     *
     * @param addr InetSocketAddress
     * @return Channel
     */
    private static Channel getChannel(InetSocketAddress addr) {
        // 获取Channel
        Channel ch = XRpcBootStrap.CHANNEL_CACHE.get(addr);
        if (ch == null) {
            CompletableFuture<Channel> channelCompletableFuture = new CompletableFuture<>();
            NettyBootStrapInitializer.getBootstrap().connect(addr).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.debug("与 {} 成功建立连接", addr);
                    channelCompletableFuture.completeExceptionally(channelFuture.cause());
                } else {
                    channelCompletableFuture.complete(channelFuture.channel());
                }
            });

            try {
                ch = channelCompletableFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new NetWorkException("获取与" + addr + "的通道时发生异常");
            }
        }

        if (ch == null) {
            log.error("获取与 {} 的通道时发生异常", addr);
            throw new NetWorkException("获取与" + addr + "的通道时发生异常");
        }

        XRpcBootStrap.CHANNEL_CACHE.put(addr, ch);
        return ch;
    }
}

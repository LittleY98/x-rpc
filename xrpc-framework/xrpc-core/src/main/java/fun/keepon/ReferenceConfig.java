package fun.keepon;

import fun.keepon.discovery.Registry;
import fun.keepon.exceptions.NetWorkException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:12
 */
@Slf4j
@NoArgsConstructor
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

    @Setter
    private Registry registry;

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 生成一个api接口的代理对象
     *
     * @return 代理对象
     */
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {

            // 拿到服务节点的地址
            InetSocketAddress addr = registry.lookUp(interfaceRef.getName());
            log.debug("获取到服务节点的地址: {}", addr);

            // 向服务端发起请求，获取结果

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

                ch = channelCompletableFuture.get(3, TimeUnit.SECONDS);
            }
            if (ch == null) {
                log.error("获取与 {} 的通道时发生异常", addr);
                throw new NetWorkException("获取与" + addr + "的通道时发生异常");
            }
            XRpcBootStrap.CHANNEL_CACHE.put(addr, ch);


            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
            buffer.writeBytes("hello world".getBytes());

            CompletableFuture<Object> retFuture = new CompletableFuture<>();
            ch.writeAndFlush(buffer).addListener(promise -> {
                if (promise.isSuccess()) {
                    retFuture.complete(promise.getNow());
                } else {
                    retFuture.completeExceptionally(promise.cause());
                }
            });

            retFuture.get(3, TimeUnit.SECONDS);

            return null;
        });

        return (T) helloProxy;
    }
}

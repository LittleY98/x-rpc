package fun.keepon.heatbeat;

import fun.keepon.NettyBootStrapInitializer;
import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.constant.RequestType;
import fun.keepon.serialize.SerializerFactory;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LittleY
 * @date 2024/3/8
 * @description 心跳检测器
 */
@Slf4j
public class HeartBeatDetector {
    public static void detectHeartbeat(String serviceName){
        List<InetSocketAddress> serviceNodeList = XRpcBootStrap.getInstance().getRegistry().lookUp(serviceName);

        for (InetSocketAddress inetSocketAddress : serviceNodeList) {
            if (!XRpcBootStrap.CHANNEL_CACHE.containsKey(inetSocketAddress)) {
                try {
                    Channel channel = null;
                    channel = NettyBootStrapInitializer.getBootstrap().connect(inetSocketAddress).sync().channel();
                    XRpcBootStrap.CHANNEL_CACHE.put(inetSocketAddress, channel);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        new Timer().scheduleAtFixedRate(new HeatBeatTask() ,3000, 3000);
    }

    private static class HeatBeatTask extends TimerTask{
        @Override
        public void run() {
            Map<InetSocketAddress, Channel> cache = XRpcBootStrap.CHANNEL_CACHE;

            for (Map.Entry<InetSocketAddress, Channel> entry : cache.entrySet()) {
                // 请求标识符
                long reqId = XRpcBootStrap.snowflakeIdGenerator.nextId();

                CompletableFuture<Object> retFuture = new CompletableFuture<>();
                XRpcBootStrap.PENDING_REQUEST.put(reqId, retFuture);

                XRpcRequest request = XRpcRequest.builder()
                        .requestId(reqId)
                        .compressType(CompressorFactory.getCompressorByName(XRpcBootStrap.compress).getCode())
                        .requestType(RequestType.HEART_BEAT.getId())
                        .serializeType(SerializerFactory.getSerializerByName(XRpcBootStrap.serializer).getCode())
                        .build();

                XRpcBootStrap.REQUEST_THREAD_LOCAL.set(request);

                log.debug("request obj: {}", request);

                Channel ch = entry.getValue();

                long start = System.currentTimeMillis();
                // 发起请求
                ch.writeAndFlush(request).addListener(promise -> {
                    if (!promise.isSuccess()){
                        retFuture.completeExceptionally(promise.cause());
                    }
                });

                try {
                    retFuture.get(1, TimeUnit.SECONDS);
                    long answerTime = System.currentTimeMillis() - start;
                    log.debug("the node: {} heat beat response time is {} ms", entry.getKey(), answerTime);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("An error occurred connecting to node: {}", entry.getKey());
                    throw new RuntimeException(e);
                }

            }
        }
    }
}

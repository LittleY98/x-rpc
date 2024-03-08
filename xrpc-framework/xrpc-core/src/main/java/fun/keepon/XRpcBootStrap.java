package fun.keepon;

import fun.keepon.channel.handler.MethodCallHandler;
import fun.keepon.channel.handler.XRpcRequestDecoderHandler;
import fun.keepon.channel.handler.XRpcResponseEncoderHandler;
import fun.keepon.discovery.Registry;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.loadbalance.ConsistentHashLoadBalancer;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.loadbalance.RoundRobinLoadBalancer;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.utils.SnowflakeIDGenerator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @description 单例 XRpcBootStrap
 * @date 2024/2/1 20:39
 */
@Slf4j
public class XRpcBootStrap {
    private static final XRpcBootStrap xRpcBootStrap = new XRpcBootStrap();

    public static final Map<String ,ServiceConfig<?>> SERVERS_MAP = new HashMap<>();

    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>();

    public static final Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>();

    public static final SnowflakeIDGenerator snowflakeIdGenerator = new SnowflakeIDGenerator(1L, 1L);

    @Getter
    public static final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    public static final ThreadLocal<XRpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();

    public static final int PORT = 8092;


    // 定义相关的基础配置
    private String applicationName = "Default";

    private ProtocolConfig protocolConfig;

    /**
     * 序列化器，默认使用JDK序列化
     */
    public static String serializer = "jdk";

    /**
     * 压缩器，默认使用zlib
     */
    public static String compress = "zlib";

    @Getter
    private Registry registry;

    private XRpcBootStrap() {
    }

    /**
     * 获取实例
     * @return this
     */
    public static XRpcBootStrap getInstance(){
        return xRpcBootStrap;
    }

    /**
     * 定义当前应用名称
     * @param appName 名称
     * @return this
     */
    public XRpcBootStrap application(String appName){
        this.applicationName = appName;
        return this;
    }

    /**
     * 配置注册中心
     * @return this
     */
    public XRpcBootStrap registry(RegistryConfig registryConfig){
        registry = registryConfig.getRegistry();
        return this;
    }

    /**
     * 配置当前暴露的服务使用的协议
     * @param protocolConfig 协议配置的封装
     * @return this
     */
    public XRpcBootStrap protocol(ProtocolConfig protocolConfig){
        log.debug("当前工具使用了 {} 协议进行序列化", protocolConfig);
        this.protocolConfig = protocolConfig;
        return this;
    }

    public XRpcBootStrap serializeType(String  serializeTypeName){
        log.debug("当前工具使用了 {} 协议进行序列化", serializeTypeName);
        serializer = serializeTypeName;
        return this;
    }

    public XRpcBootStrap compressorType(String  compressorTypeName){
        log.debug("当前工具使用了 {} 协议进行压缩", compressorTypeName);
        compress = compressorTypeName;
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start(){
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                        //入站处理器
                        ch.pipeline().addLast("报文解码", new XRpcRequestDecoderHandler());
                        ch.pipeline().addLast("方法调用", new MethodCallHandler());

                        //出站处理器
                        ch.pipeline().addLast(new XRpcResponseEncoderHandler());
                    }
                }).bind(PORT);
    }


    //===================================服务提供方API===================================

    /**
     * 发布服务
     * @param service 需要发布的服务
     * @return this
     */
    public XRpcBootStrap publish(ServiceConfig<?> service){
        registry.register(service);
        SERVERS_MAP.put(service.getInterface().getName(), service);
        log.debug("服务： {}， 已经被注册", service);
        return this;
    }

    /**
     * 批量发布
     * @param services 需要发布的服务
     * @return this
     */
    public XRpcBootStrap publish(List<ServiceConfig<?>> services){
        for (ServiceConfig<?> service : services) {
            publish(service);
        }
        return this;
    }


    //===================================服务调用方API===================================
    /**
     *
     * @param reference ReferenceConfig<?>
     * @return XRpcBootStrap
     */
    public XRpcBootStrap reference(ReferenceConfig<?> reference){
        reference.setRegistry(registry);
        return this;
    }
}

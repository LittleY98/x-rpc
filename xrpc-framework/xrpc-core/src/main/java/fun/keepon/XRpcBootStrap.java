package fun.keepon;

import com.alibaba.fastjson2.JSON;
import fun.keepon.annotation.XRpcApi;
import fun.keepon.channel.handler.MethodCallHandler;
import fun.keepon.channel.handler.XRpcRequestDecoderHandler;
import fun.keepon.channel.handler.XRpcResponseEncoderHandler;
import fun.keepon.discovery.Registry;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.heatbeat.HeartBeatDetector;
import fun.keepon.loadbalance.ConsistentHashLoadBalancer;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.loadbalance.MinResponseTimeLoadBalancer;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public static final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    public static final ThreadLocal<XRpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();

    public static final TreeMap<Long, Channel> ANSWER_TIME_CHANNEL_CACHE = new TreeMap<>();

    public static final int PORT = 8093;


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
        log.debug("服务： {}， 已经被注册", JSON.toJSONString(service));
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

    public XRpcBootStrap scan(String packageName){
        // 获取包下的所有类
        List<Class> classList = getClassList(packageName);

        // 筛选出classList中有XRpcApi注解的类,并返回list
        List<Class> collect = classList.stream().filter(c -> c.isAnnotationPresent(XRpcApi.class)).collect(Collectors.toList());

        for (Class serviceImpl : collect) {

            Class[] interfaces = serviceImpl.getInterfaces();

            Object instance = null;
            try {
                instance = serviceImpl.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                log.error("实例化失败");
                throw new RuntimeException(e);
            }

            ArrayList<ServiceConfig<?>> serviceConfigs = new ArrayList<>();
            for (Class inter : interfaces) {
                ServiceConfig<?> s = new ServiceConfig<>();
                s.setInterface(inter);
                s.setRef(instance);
                serviceConfigs.add(s);
            }

            publish(serviceConfigs);
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
//        HeartBeatDetector.detectHeartbeat(reference.getInterface().getName());
        return this;
    }

    private static List<Class> getClassList(String packageName) {
        List<Class> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static List<Class> findClasses(File directory, String packageName) {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}

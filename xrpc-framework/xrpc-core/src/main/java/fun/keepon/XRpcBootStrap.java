package fun.keepon;

import fun.keepon.discovery.Registry;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.discovery.impl.ZookeeperRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author LittleY
 * @description 单例 XRpcBootStrap
 * @date 2024/2/1 20:39
 */
@Slf4j
public class XRpcBootStrap {
    private static final XRpcBootStrap xRpcBootStrap = new XRpcBootStrap();

    // 定义相关的基础配置
    private String applicationName = "Default";

    private RegistryConfig registryConfig;

    private ProtocolConfig protocolConfig;


    // TODO 待处理
    private Registry registry = new ZookeeperRegistry();

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
    public XRpcBootStrap registry(){
        return this;
    }

    public XRpcBootStrap registry(RegistryConfig registryConfig){
        this.registryConfig =registryConfig;

        registry = registryConfig.getRegistry();

        return this;
    }

    public XRpcBootStrap protocol(){
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

    /**
     * 启动netty服务
     */
    public void start(){
    }


    //===================================服务提供方API===================================

    /**
     * 发布服务
     * @param service 需要发布的服务
     * @return this
     */
    public XRpcBootStrap publish(ServiceConfig<?> service){
        registry.register(service);
        return this;
    }

    /**
     * 批量发布
     * @param services 需要发布的服务
     * @return this
     */
    public XRpcBootStrap publish(List<ServiceConfig<?>> services){
        for (ServiceConfig<?> service : services) {
            registry.register(service);
            log.debug("服务： {}， 已经被注册", service);
        }
        return this;
    }


    //===================================服务调用方API===================================
    /**
     *
     * @param reference
     * @return
     */
    public XRpcBootStrap reference(ReferenceConfig<?> reference){
         return this;
    }
}

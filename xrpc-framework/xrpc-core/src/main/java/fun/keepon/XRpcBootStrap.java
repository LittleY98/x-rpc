package fun.keepon;

import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.utils.NetUtils;
import fun.keepon.utils.zk.ZkNode;
import fun.keepon.utils.zk.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Scanner;

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

    private CuratorFramework zookeeperClient;

    private int port;

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
        zookeeperClient = ZookeeperUtil.getClient();
        this.port = 8080;

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
        String serviceNamePath = ZooKeeperConstant.BASE_PROVIDERS_PATH + "/" + service.getInterface().getName();
        ZookeeperUtil.createNode(zookeeperClient, new ZkNode(serviceNamePath, null));

        String nodePath = serviceNamePath + "/" + NetUtils.getLocalIP() + ":" + this.port;
        ZookeeperUtil.createNode(zookeeperClient, new ZkNode(nodePath, null), CreateMode.EPHEMERAL);

        log.debug("服务： {}， 已经被注册", service.getInterface().getName());
        return this;
    }

    /**
     * 批量发布
     * @param services 需要发布的服务
     * @return this
     */
    public XRpcBootStrap publish(List<ServiceConfig<?>> services){
        log.debug("服务： {}， 已经被注册", services);
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

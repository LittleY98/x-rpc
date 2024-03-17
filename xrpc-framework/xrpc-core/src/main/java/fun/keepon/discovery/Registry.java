package fun.keepon.discovery;

import fun.keepon.config.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author LittleY
 * @description 注册中心
 * @date 2024/2/3
 */
public interface Registry {

    /**
     * 注册服务
     * @param serviceConfig 注册服务配置
     */
    void register(ServiceConfig<?> serviceConfig);

    /**
     * 从注册中心中获取一个可用的服务
     * @param name 服务名称
     * @return InetSocketAddress
     */
    List<InetSocketAddress> lookUp(String name);

}

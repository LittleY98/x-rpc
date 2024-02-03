package fun.keepon.discovery;

import fun.keepon.ServiceConfig;

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

}

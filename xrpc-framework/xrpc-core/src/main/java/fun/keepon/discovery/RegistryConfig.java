package fun.keepon.discovery;

import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.discovery.Registry;
import fun.keepon.discovery.impl.ZookeeperRegistry;
import fun.keepon.exceptions.DiscoveryException;
import lombok.Data;

import java.util.concurrent.RecursiveTask;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 20:52
 */
@Data
public class RegistryConfig {

    // 连接URL 如：zookeeper://127.0.0.1:2181
    private String connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    public Registry getRegistry() {
        // 获取注册中心类型
        String registryType = getRegistryType(connectString);
        if (registryType.equals("zookeeper")) {
            String registryUrl = getRegistryUrl(connectString);
            return new ZookeeperRegistry(registryUrl, ZooKeeperConstant.DEFAULT_ZK_SESSION_TIMEOUT);
        }

        throw new DiscoveryException("注册中心类型不支持");
    }

    private String[] validateUrl(String connectStr){
        String[] typeAndHost = connectStr.split("://");

        if (typeAndHost.length != 2) {
            throw new RuntimeException("注册中心的URL不合法");
        }

        return typeAndHost;
    }

    private String getRegistryType(String connectStr){
        String[] res = validateUrl(connectStr);
        return res[0].toLowerCase().trim();
    }

    private String getRegistryUrl(String connectStr){
        String[] res = validateUrl(connectStr);
        return res[1].toLowerCase().trim();
    }

}

package fun.keepon.config;

import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.discovery.Registry;
import fun.keepon.discovery.impl.ZookeeperRegistry;
import fun.keepon.exceptions.DiscoveryException;
import lombok.Data;


/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 20:52
 */
@Data
public class RegistryConfig {

    public static final String ZOOKEEPER = "zookeeper";
    // 连接URL 如：zookeeper://127.0.0.1:2181
    private String connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 获取注册中心
     * @return
     */
    public Registry getRegistry() {
        // 获取注册中心类型
        String registryType = getRegistryType(connectString);
        if (ZOOKEEPER.equals(registryType)) {
            String registryUrl = getRegistryUrl(connectString);
            return new ZookeeperRegistry(registryUrl, ZooKeeperConstant.DEFAULT_ZK_SESSION_TIMEOUT);
        }

        throw new DiscoveryException("注册中心类型不支持");
    }

    /**
     * 判断和分割URL
     * TODO 还要考虑未来的集群的配置，后续需修改
     * @param connectStr 连接URL
     * @return String[0] 类型; String[1] 连接地址
     */
    private String[] validateUrl(String connectStr){
        String[] typeAndHost = connectStr.split("://");

        if (typeAndHost.length != 2) {
            throw new RuntimeException("注册中心的URL不合法");
        }

        return typeAndHost;
    }

    /**
     * 获取注册中心类型
     * @param connectStr String
     * @return String
     */
    private String getRegistryType(String connectStr){
        String[] res = validateUrl(connectStr);
        return res[0].toLowerCase().trim();
    }

    /**
     * 获取注册中心地址
     * @param connectStr String
     * @return String
     */
    private String getRegistryUrl(String connectStr){
        String[] res = validateUrl(connectStr);
        return res[1].toLowerCase().trim();
    }

}

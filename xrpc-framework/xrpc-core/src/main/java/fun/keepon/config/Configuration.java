package fun.keepon.config;

import com.alibaba.fastjson2.JSON;
import fun.keepon.discovery.Registry;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.loadbalance.RoundRobinLoadBalancer;
import fun.keepon.utils.SnowflakeIDGenerator;
import fun.keepon.utils.yml.YamlReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LittleY
 * @date 2024/3/14
 * @description 配置类
 */
@Data
@Slf4j
public class Configuration {

    private int port = 8093;

    private String applicationName = "Default";

    private RegistryConfig registryConfig =new RegistryConfig("zookeeper://127.0.0.1:2181");

    private Registry registry;

    private ProtocolConfig protocolConfig;

    private SnowflakeIDGenerator snowflakeIdGenerator = new SnowflakeIDGenerator(1L, 1L);

    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    /**
     * 序列化器，默认使用JDK序列化
     */
    private String serializer = "jdk";

    /**
     * 压缩器，默认使用zlib
     */
    private String compress = "zlib";


    public Configuration() {
        SpiResolver.loadFromSpi(this);

        YamlResolver.loadFromYaml(this);
    }
}

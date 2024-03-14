package fun.keepon.config;

import fun.keepon.ProtocolConfig;
import fun.keepon.discovery.Registry;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.loadbalance.RoundRobinLoadBalancer;
import fun.keepon.utils.SnowflakeIDGenerator;
import lombok.Data;
import lombok.Getter;

/**
 * @author LittleY
 * @date 2024/3/14
 * @description 配置类
 */
@Data
public class Configuration {

    private int port = 8093;

    private String applicationName = "Default";

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

    private Registry registry;




}

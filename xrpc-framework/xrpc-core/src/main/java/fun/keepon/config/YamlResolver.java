package fun.keepon.config;

import com.alibaba.fastjson2.JSON;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.utils.SnowflakeIDGenerator;
import fun.keepon.utils.yml.YamlReader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description Yaml解析器
 */
@Slf4j
public class YamlResolver {
    public static void loadFromYaml(Configuration configuration) {
        YamlReader reader = YamlReader.instance;
        Object port = reader.getValueByKey("xrpc.port");
        Object appName = reader.getValueByKey("xrpc.applicationName");
        Object serializerName = reader.getValueByKey("xrpc.serializer");
        Object compressorName = reader.getValueByKey("xrpc.compress");
        Object loadBalancerPath = reader.getValueByKey("xrpc.loadBalancer");
        Object workerId = reader.getValueByKey("xrpc.snowflakeIdGenerator.workerId");
        Object dataCenterId = reader.getValueByKey("xrpc.snowflakeIdGenerator.dataCenterId");
        Object registryAddress = reader.getValueByKey("xrpc.registry.address");

        if (port != null) {
            configuration.setPort((int) port);
        }

        if (appName != null) {
            configuration.setApplicationName((String) appName);
        }

        if (serializerName != null) {
            configuration.setSerializer((String) serializerName);
        }

        if (compressorName != null) {
            configuration.setCompress((String) compressorName);
        }

        if (loadBalancerPath != null) {
            try {
                configuration.setLoadBalancer((LoadBalancer) Class.forName((String) loadBalancerPath).getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.error("LoadBalancer init failed, use default RoundRobinLoadBalancer", e);
            }
        }

        if (workerId != null && dataCenterId != null) {
            long workedIdL = Long.valueOf((int)workerId);
            long dataCenterIdL = Long.valueOf((int)dataCenterId);
            configuration.setSnowflakeIdGenerator(new SnowflakeIDGenerator(workedIdL, dataCenterIdL));
        }

        if (registryAddress != null) {
            RegistryConfig registryConfig = new RegistryConfig(((String) registryAddress));
            configuration.setRegistryConfig(registryConfig);
            configuration.setRegistry(registryConfig.getRegistry());
        }

        log.debug("Configuration init from [xrpc-conf.yml] completed, {}", configuration);

    }
}

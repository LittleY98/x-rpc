package fun.keepon.config;

import fun.keepon.loadbalance.LoadBalancer;

import java.util.ServiceLoader;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description SPI解析器
 */
public class SpiResolver {
    public static void loadFromSpi(Configuration configuration) {
        ServiceLoader<LoadBalancer> loadBalancers = ServiceLoader.load(LoadBalancer.class);

        for (LoadBalancer loadBalancer : loadBalancers) {
            configuration.setLoadBalancer(loadBalancer);
            break;
        }
    }
}

package fun.keepon.loadbalance;

import fun.keepon.XRpcBootStrap;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description 负载均衡模版
 */
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer{

    private final Map<String, LoadBalanceSelector> SERVICE_SELECTOR_CACHE = new ConcurrentHashMap<>();

    @Override
    public InetSocketAddress selectServiceAddr(String serviceName) {

        LoadBalanceSelector selector = SERVICE_SELECTOR_CACHE.get(serviceName);
        if (selector == null) {
            List<InetSocketAddress> inetSocketAddresses = XRpcBootStrap.getInstance().getRegistry().lookUp(serviceName);
            selector = getLoadBalanceSelector(inetSocketAddresses);
            SERVICE_SELECTOR_CACHE.put(serviceName, selector);
        }

        return selector.getNext();
    }

    @Override
    public synchronized void reBalance(String serviceName, List<InetSocketAddress> addresses) {
        SERVICE_SELECTOR_CACHE.put(serviceName, getLoadBalanceSelector(addresses));
        log.info("Updated load balancing selector {} for service {}", addresses, serviceName);
    }

    protected abstract LoadBalanceSelector getLoadBalanceSelector(List<InetSocketAddress> inetSocketAddresses);
}

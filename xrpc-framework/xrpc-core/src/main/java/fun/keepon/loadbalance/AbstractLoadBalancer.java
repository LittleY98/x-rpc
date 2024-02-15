package fun.keepon.loadbalance;

import fun.keepon.XRpcBootStrap;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description 负载均衡模版
 */
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

    protected abstract LoadBalanceSelector getLoadBalanceSelector(List<InetSocketAddress> inetSocketAddresses);
}

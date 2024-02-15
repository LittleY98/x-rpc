package fun.keepon.loadbalance;

import fun.keepon.exceptions.LoadBalancerException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description 轮询负载均衡
 */
@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer{
    @Override
    protected LoadBalanceSelector getLoadBalanceSelector(List<InetSocketAddress> inetSocketAddresses) {
        return new RoundRobinSelector(inetSocketAddresses);
    }

    private static class RoundRobinSelector implements LoadBalanceSelector{
        private final List<InetSocketAddress> serviceList;
        private final AtomicInteger idx;

        public RoundRobinSelector(List<InetSocketAddress> serviceList) {
            this.serviceList = serviceList;
            this.idx = new AtomicInteger(0);
        }

        @Override
        public InetSocketAddress getNext() {
            if(serviceList == null || serviceList.isEmpty()){
                log.error("进行负载均衡选取节点时发现服务列表为空");
                throw new LoadBalancerException();
            }

            InetSocketAddress inetSocketAddress = serviceList.get(idx.get());
            if(idx.get() ==serviceList.size()-1){
                idx.set(0);
            }else{
                idx.incrementAndGet();
            }
            return inetSocketAddress;
        }

        @Override
        public void reBalance() {

        }
    }
}

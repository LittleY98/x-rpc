package fun.keepon.loadbalance;

import fun.keepon.XRpcBootStrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author LittleY
 * @date 2024/3/9
 * @description TODO 存在问题，待修复
 */
@Slf4j
public class MinResponseTimeLoadBalancer extends AbstractLoadBalancer{

    @Override
    protected LoadBalanceSelector getLoadBalanceSelector(List<InetSocketAddress> inetSocketAddresses) {
        return new MinResponseTimeSelector(inetSocketAddresses);
    }

    private static class MinResponseTimeSelector implements LoadBalanceSelector{
        private List<InetSocketAddress> serviceList;

        public MinResponseTimeSelector(List<InetSocketAddress> serviceList) {
            this.serviceList = serviceList;
        }

        @Override
        public InetSocketAddress getNext() {
            Map.Entry<Long, Channel> entry = XRpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.firstEntry();
            if(entry != null){
                log.debug("A service node {} with a response time of {}ms is selected", entry.getValue().remoteAddress(),entry.getKey());
                return (InetSocketAddress)entry.getValue().remoteAddress();
            }

            Channel channel = (Channel)XRpcBootStrap.CHANNEL_CACHE.values().toArray()[0];

            return (InetSocketAddress) channel.remoteAddress();
        }
    }
}


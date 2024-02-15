package fun.keepon.loadbalance;

import java.net.InetSocketAddress;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description 负载均衡选择器接口
 */
public interface LoadBalanceSelector {

    InetSocketAddress getNext();

    void reBalance();

}

package fun.keepon.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description 负载均衡器接口
 */
public interface LoadBalancer {

    InetSocketAddress selectServiceAddr(String serviceName);

}

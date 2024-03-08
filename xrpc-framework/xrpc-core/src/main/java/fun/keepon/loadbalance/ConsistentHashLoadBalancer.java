package fun.keepon.loadbalance;

import fun.keepon.XRpcBootStrap;
import fun.keepon.exceptions.DiscoveryException;
import fun.keepon.transport.message.XRpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author LittleY
 * @date 2024/2/17
 * @description 一致性哈希负载均衡器
 */
@Slf4j
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer{


    @Override
    protected LoadBalanceSelector getLoadBalanceSelector(List<InetSocketAddress> inetSocketAddresses) {
        return new ConsistentHashSelector(inetSocketAddresses);
    }

    private static class ConsistentHashSelector implements LoadBalanceSelector {

        //哈希
        private final SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();

        // 虚拟节点，默认512个
        private Integer virtualNodesNum = 512;

        public ConsistentHashSelector( List<InetSocketAddress> serverList, Integer virtualNodesNum) {
            this.virtualNodesNum = virtualNodesNum;
            for (InetSocketAddress inetSocketAddress : serverList) {
                addServerNodesToCircle(inetSocketAddress);
            }
        }
        public ConsistentHashSelector( List<InetSocketAddress> serverList) {
            for (InetSocketAddress inetSocketAddress : serverList) {
                addServerNodesToCircle(inetSocketAddress);
            }
        }


        @Override
        public InetSocketAddress getNext() {
            XRpcRequest request = XRpcBootStrap.REQUEST_THREAD_LOCAL.get();

            if (request == null) {
                log.error("The current Request was not obtained");
                throw  new DiscoveryException("The current Request was not obtained");
            }

            int hashCode = hash(request.getRequestId() + "");

            if (!circle.containsKey(hashCode)) {
                SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hashCode);
                hashCode = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }

            log.debug("request id {}, get the node {}", request.getRequestId(), circle.get(hashCode));
            return circle.get(hashCode);
        }

        @Override
        public void reBalance() {

        }

        /**
         * 向hash环中挂载服务节点
         * @param inetSocketAddress InetSocketAddress
         */
        private void addServerNodesToCircle(InetSocketAddress inetSocketAddress) {
            for (Integer i = 0; i < virtualNodesNum; i++) {
                int hashCode = hash(inetSocketAddress + "-" + i);
                circle.put(hashCode, inetSocketAddress);
                log.debug("Service Node: [{}] has be mounted to circle, hashCode:{}", inetSocketAddress, hashCode);
            }
        }

        /**
         * 从hash环中删除服务节点
         * @param inetSocketAddress InetSocketAddress
         */
        private void removeServiceNodesFromCircle(InetSocketAddress inetSocketAddress){
            for (Integer i = 0; i < virtualNodesNum; i++) {
                int hashCode = hash(inetSocketAddress.getAddress() + "-" + i);
                circle.remove(hashCode);
                log.debug("Service Node: [{}] has be removed from circle, hashCode:{}", inetSocketAddress, hashCode);
            }
        }

        /**
         * 哈希算法
         * TODO 后续待完善
         * @param s String
         * @return int
         */
        private int hash(String s) {
            try {
                // 创建一个MessageDigest实例，并初始化为MD5算法对象
                MessageDigest digest = MessageDigest.getInstance("MD5");

                // 对输入字符串进行hash处理
                byte[] hashedBytes = digest.digest(s.getBytes());

                // 将得到的字节数据转换为int类型
                int hash = 0;
                for (int i = 0; i < 4; i++) {
                    hash <<= 8; // 左移8位，相当于hash *= 256
                    hash |= (hashedBytes[i] & 0xFF); // 将字节转换为int，并累加到hash上
                }

                return hash;
            } catch (NoSuchAlgorithmException e) {
                // NoSuchAlgorithmException是一个异常，可能会在MessageDigest.getInstance()调用时抛出
                // 这意味着你请求的加密算法可能不被当前环境支持
                throw new RuntimeException("MD5 algorithm not found", e);
            }
        }

    }
}

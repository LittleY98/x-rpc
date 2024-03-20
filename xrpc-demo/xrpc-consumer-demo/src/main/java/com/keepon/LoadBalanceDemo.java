package com.keepon;

import fun.keepon.exceptions.LoadBalancerException;
import fun.keepon.loadbalance.LoadBalanceSelector;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.loadbalance.RoundRobinLoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description TODO
 */
@Slf4j
public class LoadBalanceDemo implements LoadBalancer {

    @Override
    public InetSocketAddress selectServiceAddr(String serviceName) {
        log.error("测试负载均衡");
        return new InetSocketAddress("127.0.0.1",8848);
    }

    @Override
    public void reBalance(String serviceName, List<InetSocketAddress> addresses) {
        log.error("进行负载均衡选取节点时发现服务列表为空");
    }
}

package fun.keepon.discovery.impl;

import fun.keepon.ServiceConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.discovery.AbstractRegistry;
import fun.keepon.discovery.Registry;
import fun.keepon.exceptions.DiscoveryException;
import fun.keepon.utils.NetUtils;
import fun.keepon.utils.zk.ZkNode;
import fun.keepon.utils.zk.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/3
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry implements Registry {

    private CuratorFramework zookeeperClient;

    public ZookeeperRegistry() {
        this.zookeeperClient = ZookeeperUtil.getClient();
    }

    public ZookeeperRegistry(String conn, int timeout) {
        this.zookeeperClient = ZookeeperUtil.getClient(conn, timeout, ZooKeeperConstant.DEFAULT_ZK_NAMESPACE);
    }

    @Override
    public void register(ServiceConfig<?> serviceConfig) {
        String serviceNamePath = ZooKeeperConstant.BASE_PROVIDERS_PATH + "/" + serviceConfig.getInterface().getName();
        ZookeeperUtil.createNode(zookeeperClient, new ZkNode(serviceNamePath, null));

        String nodePath = serviceNamePath + "/" + NetUtils.getLocalIP() + ":" + XRpcBootStrap.PORT;
        ZookeeperUtil.createNode(zookeeperClient, new ZkNode(nodePath, null), CreateMode.EPHEMERAL);

        log.debug("服务： {}， 已经被注册", serviceConfig.getInterface().getName());
    }

    @Override
    public List<InetSocketAddress> lookUp(String name) {

        List<String> children = ZookeeperUtil.getChildren(zookeeperClient, ZooKeeperConstant.BASE_PROVIDERS_PATH + "/" + name);

        log.info("children: {}", children);

        List<InetSocketAddress> collect = children.stream().map(s -> {
            String[] split = s.split(":");
            return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
        }).toList();

        if (collect.isEmpty()) {
            throw new DiscoveryException("未找到服务");
        }

        return collect;
    }
}

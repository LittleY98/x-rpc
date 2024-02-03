package fun.keepon.discovery.impl;

import fun.keepon.ServiceConfig;
import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.discovery.AbstractRegistry;
import fun.keepon.discovery.Registry;
import fun.keepon.utils.NetUtils;
import fun.keepon.utils.zk.ZkNode;
import fun.keepon.utils.zk.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

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

        String nodePath = serviceNamePath + "/" + NetUtils.getLocalIP() + ":" + 8088;
        ZookeeperUtil.createNode(zookeeperClient, new ZkNode(nodePath, null), CreateMode.EPHEMERAL);

        log.debug("服务： {}， 已经被注册", serviceConfig.getInterface().getName());
    }
}

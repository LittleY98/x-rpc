package fun.keepon;

import fun.keepon.utils.ZkNode;
import fun.keepon.utils.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/2 16:35
 */
@Slf4j
public class Application {
    public static void main(String[] args) {
        CuratorFramework client = ZookeeperUtil.getClient();

        String providerPath = "/providers";
        String consumersPath = "/consumers";
        ZkNode providersNode = new ZkNode(providerPath, null);
        ZkNode consumersNode = new ZkNode(consumersPath, null);

        List.of(providersNode, consumersNode).
                forEach(zkNode -> ZookeeperUtil.createNode(client, zkNode));

        ZookeeperUtil.closeClient(client);
    }

}

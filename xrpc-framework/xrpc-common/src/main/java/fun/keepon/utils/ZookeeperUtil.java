package fun.keepon.utils;

import fun.keepon.constant.ZooKeeperConstant;
import fun.keepon.exceptions.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author LittleY
 * @description Zookeeper的工具类
 * @date 2024/2/3 15:20
 */
@Slf4j
public class ZookeeperUtil {

    /**
     * 获取Curator客户端
     * @return CuratorFramework
     */
    public static CuratorFramework getClient(){
        return getClient(ZooKeeperConstant.DEFAULT_ZK_CONNECT, ZooKeeperConstant.DEFAULT_ZK_SESSION_TIMEOUT
                , ZooKeeperConstant.DEFAULT_ZK_NAMESPACE);
    }

    public static CuratorFramework getClient(String conn, int timeout, String namespace){
        CuratorFramework client = CuratorFrameworkFactory.builder()
                // ip端口号
                .connectString(conn)
                // 会话超时
                .sessionTimeoutMs(timeout)
                // 重试机制，这里是超时后1000毫秒重试一次
                .retryPolicy(new RetryOneTime(1000))
                // 名称空间，在操作节点的时候，会以这个为父节点
                .namespace(namespace)
                .build();

        client.start();
        return client;
    }

    /**
     * 关闭客户端
     * @param client CuratorFramework
     */
    public static void closeClient(CuratorFramework client){
        client.close();
    }

    /**
     * 创建节点
     * @param client CuratorFramework
     * @param node ZkNode
     * @param createMode CreateMode
     * @return Boolean
     */
    public static Boolean createNode(CuratorFramework client, ZkNode node, CreateMode createMode){
        try {
            if (exists(client, node.getPath())) {
                log.warn("该路径节点已存在，无法再次创建: {}", node.getPath());
                return false;
            }
            client.create()
                    .withMode(createMode)
                    .forPath(node.getPath(), node.getData());
            log.debug("成功创建节点: {}", node);
            return true;
        } catch (Exception e) {
            log.error("创建节点出错: {}", node);
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * 默认持久节点
     * @param client CuratorFramework
     * @param node ZkNode
     * @return Boolean
     */
    public static Boolean createNode(CuratorFramework client, ZkNode node){
        return createNode(client, node, CreateMode.PERSISTENT);
    }

    public static boolean exists(CuratorFramework client, String path){
        try {
            Stat stat = client.checkExists().forPath(path);

            return stat != null;
        } catch (Exception e) {
            log.error("检查是否存在节点 异常: {}", path);
            throw new ZookeeperException(e.getCause());
        }
    }

    /**
     * 获取子节点
     * @param client CuratorFramework
     * @param path String
     * @return List<String>
     */
    public static List<String> getChildren(CuratorFramework client, String path){
        try {
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("获取子节点异常: {}", path);
            throw new ZookeeperException(e.getCause());
        }

    }


}

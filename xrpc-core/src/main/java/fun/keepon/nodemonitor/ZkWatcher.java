package fun.keepon.nodemonitor;

import fun.keepon.NettyBootStrapInitializer;
import fun.keepon.XRpcBootStrap;
import fun.keepon.config.Configuration;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author LittleY
 * @date 2024/3/12
 * @description TODO
 */
@Slf4j
public class ZkWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {

        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();

        if (watchedEvent.getPath().isBlank() || watchedEvent.getPath() == null) {
            return;
        }

        log.warn("Find that the child of {} has changed", watchedEvent.getPath());

        if (Event.EventType.NodeChildrenChanged.equals(watchedEvent.getType())) {
            String[] split = watchedEvent.getPath().split("/");
            String serviceName = split[split.length - 1];

            log.info("The service [{}] node will be re-pulled", serviceName);

//            XRpcBootStrap.CHANNEL_CACHE.clear();
//            XRpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.clear();

            // 重新获取对应服务的节点地址
            List<InetSocketAddress> inetSocketAddresses = conf.getRegistry().lookUp(serviceName);

            // 将新增的，CHANNEL_CACHE中不存在的节点，创建channel并缓存
            for (InetSocketAddress inetSocketAddress : inetSocketAddresses) {
                if (!XRpcBootStrap.CHANNEL_CACHE.containsKey(inetSocketAddress)) {
                    try {
                        // TODO 后续待解决：有新节点上线时，注册到zookeeper之后立马开始获取channel，但此时新结点的netty服务还没启动
                        Thread.sleep(800);
                        Channel channel = NettyBootStrapInitializer.getBootstrap().connect(inetSocketAddress).sync().channel();
                        XRpcBootStrap.CHANNEL_CACHE.put(inetSocketAddress, channel);
                        log.debug("Added a new channel with the new node {}", inetSocketAddress);
                    } catch (InterruptedException e) {
                        log.error("Failed to create channel for node {}", inetSocketAddress);
                    }
                }
            }

            // 将减少的，CHANNEL中存在，但已经下线的节点，从缓存中删除
            for (Map.Entry<InetSocketAddress, Channel> entry : XRpcBootStrap.CHANNEL_CACHE.entrySet()) {
                // 重新获取的节点地址中不存在以前缓存的，则将其删除
                if (!inetSocketAddresses.contains(entry.getKey())) {
                    XRpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                    log.debug("Removed the lower bound node {}", inetSocketAddresses);
                }
            }

            // ReBalance
            conf.getLoadBalancer().reBalance(serviceName, inetSocketAddresses);
        }
    }
}

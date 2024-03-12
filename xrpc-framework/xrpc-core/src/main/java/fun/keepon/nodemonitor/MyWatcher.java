package fun.keepon.nodemonitor;

import com.alibaba.fastjson2.JSON;
import fun.keepon.XRpcBootStrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/12
 * @description TODO
 */
@Slf4j
public class MyWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        log.warn("Find that the child of {} has changed", watchedEvent.getPath());

        if (Event.EventType.NodeChildrenChanged.equals(watchedEvent.getType())) {
            String[] split = watchedEvent.getPath().split("/");
            String serviceName = split[split.length - 1];

            log.info("The service {} node will be re-pulled", serviceName);

            // TODO 待优化：即 重新下拉后，删除 CHANNEL_CACHE中和ANSWER_TIME_CHANNEL_CACHE中存在的地址，但重新下拉后不存在的地址
            XRpcBootStrap.CHANNEL_CACHE.clear();
            XRpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.clear();

            // reBalance
            List<InetSocketAddress> inetSocketAddresses = XRpcBootStrap.getInstance().getRegistry().lookUp(serviceName);
            XRpcBootStrap.getLoadBalancer().reBalance(serviceName, inetSocketAddresses);
        }
    }
}

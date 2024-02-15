package fun.keepon.constant;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/2 16:00
 */
public class ZooKeeperConstant {
    // Zookeeper 默认连接地址
    public static final String DEFAULT_ZK_CONNECT = "192.168.1.66:2181,192.168.1.66:2182,192.168.1.66:2183";

    // 默认超时时间
    public static final int DEFAULT_ZK_SESSION_TIMEOUT = 10000;

    public static final String DEFAULT_ZK_NAMESPACE = "xrpc-meta";

    public static final String BASE_PROVIDERS_PATH = "/providers";

    public static final String BASE_CONSUMERS_PATH = "/consumers";



}

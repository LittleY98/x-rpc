package fun.keepon;

import fun.keepon.discovery.Registry;
import fun.keepon.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.drivers.AdvancedTracerDriver;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:12
 */
@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

    private Registry registry;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Class<T> getInterfaceRef() {
        return interfaceRef;
    }

    public void setInterfaceRef(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 生成一个api接口的代理对象
     * @return 代理对象
     */
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {
            log.debug("proxy: {}, method: {}, args: {}", proxy, method, args);

            InetSocketAddress addr = registry.lookUp(interfaceRef.getName());

            log.debug("获取到服务节点的地址: {} : {}", addr.getAddress(), addr.getPort());

            return null;
        });

        return (T)helloProxy;
    }
}

package fun.keepon;

import fun.keepon.discovery.Registry;
import fun.keepon.proxy.handler.RpcConsumerInvocationHandler;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:12
 */
@Slf4j
@NoArgsConstructor
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

    @Setter
    private Registry registry;

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 生成一个api接口的代理对象
     *
     * @return 代理对象
     */
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes
                , new RpcConsumerInvocationHandler<T>(registry, interfaceRef));

        return (T) helloProxy;
    }
}

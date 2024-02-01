package fun.keepon;

import java.lang.reflect.Proxy;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:12
 */
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {
            System.out.println("Hello proxy");
            return null;
        });

        return (T)helloProxy;
    }
}

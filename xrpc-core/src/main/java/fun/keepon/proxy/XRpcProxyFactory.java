package fun.keepon.proxy;

import fun.keepon.XRpcBootStrap;
import fun.keepon.config.ReferenceConfig;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description 服务代理工厂类
 */
public class XRpcProxyFactory {
    private static final ConcurrentHashMap<Class<?>, Object> cache = new ConcurrentHashMap<>();

    public static <T> T get(Class<T> clazz){
        Object obj = cache.get(clazz);

        if (obj != null) {
            return (T)obj;
        }

        ReferenceConfig<T> ref = new ReferenceConfig<>();
        ref.setInterface(clazz);

        XRpcBootStrap.getInstance().reference(ref);
        T res = ref.get();

        cache.put(clazz, res);

        return res;
    }

}

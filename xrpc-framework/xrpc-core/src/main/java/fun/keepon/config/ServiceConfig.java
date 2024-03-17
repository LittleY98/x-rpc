package fun.keepon.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:03
 */
public class ServiceConfig<T> {
    private Class<T> interfaceProvider;

    private Object ref;

    public Class<T> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Class<T> getInterfaceProvider() {
        return interfaceProvider;
    }

    public void setInterfaceProvider(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}

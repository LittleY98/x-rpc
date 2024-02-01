package fun.keepon;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 22:03
 */
public class ServiceConfig<T> {
    private Class<T> interfaceProvider;
    @Setter
    @Getter
    private Object ref;

    public Class<T> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

}

package fun.keepon.springboot.xrpcspringbootconsumer.proxy;

import fun.keepon.annotation.XRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import fun.keepon.proxy.XRpcProxyFactory;
import org.springframework.stereotype.Component;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@Component
@Slf4j
public class XRpcProxyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            XRpcService annotation = field.getAnnotation(XRpcService.class);
            if (annotation != null) {
                Class<?> type = field.getType();
                Object proxy = XRpcProxyFactory.get(type);
                field.setAccessible(true);
                try {
                    field.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    log.error("Failed to inject proxy object into bean", e);
                }
            }
        }

        return bean;
    }
}

package fun.keepon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LittleY
 * @date 2024/3/14
 * @description XRpcApi 用于标记要发布的服务
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRpcApi {

}

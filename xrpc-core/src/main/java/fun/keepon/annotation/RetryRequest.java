package fun.keepon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LittleY
 * @date 2024/3/18
 * @description 请求重试
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryRequest {
        int retryTimes() default 3;

        long sleepTime() default 1000;

        long timeout() default 3000;
}

package fun.keepon.protect;

/**
 * @author LittleY
 * @date 2024/3/18
 * @description 限流器接口
 */
public interface RateLimiter {

    /**
     * 是否允许新的请求进入
     * @return true 可以进入 false 拦截进入
     */
    boolean allowRequest();
}

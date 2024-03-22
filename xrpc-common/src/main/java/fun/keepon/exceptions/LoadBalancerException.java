package fun.keepon.exceptions;

/**
 * @author LittleY
 * @date 2024/2/15
 * @description TODO
 */
public class LoadBalancerException extends RuntimeException{
    public LoadBalancerException() {
    }

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadBalancerException(Throwable cause) {
        super(cause);
    }

    public LoadBalancerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

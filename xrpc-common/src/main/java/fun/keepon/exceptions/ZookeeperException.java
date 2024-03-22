package fun.keepon.exceptions;

/**
 * @author LittleY
 * @description Zookeeper 异常类
 * @date 2024/2/3 15:42
 */
public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }

    public ZookeeperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

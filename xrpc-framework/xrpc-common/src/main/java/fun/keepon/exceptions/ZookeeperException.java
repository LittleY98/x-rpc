package fun.keepon.exceptions;

/**
 * @author LittleY
 * @description Zookeeper 异常类
 * @date 2024/2/3 15:42
 */
public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }
}

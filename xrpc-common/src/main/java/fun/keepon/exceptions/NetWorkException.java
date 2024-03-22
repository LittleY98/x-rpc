package fun.keepon.exceptions;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/3 17:08
 */
public class NetWorkException extends RuntimeException{
    public NetWorkException() {
    }

    public NetWorkException(String message) {
        super(message);
    }

    public NetWorkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetWorkException(Throwable cause) {
        super(cause);
    }

    public NetWorkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

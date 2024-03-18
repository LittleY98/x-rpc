package fun.keepon.api;

import fun.keepon.annotation.RetryRequest;

import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/14
 * @description TODO
 */
public interface OrderApi {
//    @RetryRequest(retryTimes = 2, sleepTime = 900, timeout = 2000)
    @RetryRequest
    List<String> listOrder();
}

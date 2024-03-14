package fun.keepon.impl;

import fun.keepon.annotation.XRpcApi;
import fun.keepon.api.OrderApi;

import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/14
 * @description TODO
 */
@XRpcApi
public class OrderApiImpl implements OrderApi {
    @Override
    public List<String> listOrder() {
        return List.of("order1", "order2", "order3");
    }
}

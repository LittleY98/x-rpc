package fun.keepon.springboot.xrpcspringbootprovider.serviceImpl;

import fun.keepon.XRpcBootStrap;
import fun.keepon.annotation.XRpcApi;
import fun.keepon.api.OrderApi;

import java.util.Arrays;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/21
 * @description TODO
 */
@XRpcApi
public class OrderApiImpl implements OrderApi {
    @Override
    public List<String> listOrder() {
        String appName = XRpcBootStrap.getInstance().getConfiguration().getApplicationName();

        return Arrays.asList(appName, "Order1", "Order2");
    }
}

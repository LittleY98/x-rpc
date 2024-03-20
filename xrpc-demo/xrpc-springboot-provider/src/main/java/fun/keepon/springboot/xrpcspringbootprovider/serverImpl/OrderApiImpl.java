package fun.keepon.springboot.xrpcspringbootprovider.serverImpl;

import fun.keepon.XRpcBootStrap;
import fun.keepon.annotation.XRpcApi;
import fun.keepon.api.OrderApi;
import fun.keepon.config.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@XRpcApi
public class OrderApiImpl implements OrderApi {
    @Override
    public List<String> listOrder() {
        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();

        return Arrays.asList(conf.getApplicationName(), "order1", "order2", "order3");
    }
}

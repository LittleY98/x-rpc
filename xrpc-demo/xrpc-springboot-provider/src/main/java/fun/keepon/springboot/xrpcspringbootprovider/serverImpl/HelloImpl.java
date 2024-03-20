package fun.keepon.springboot.xrpcspringbootprovider.serverImpl;

import fun.keepon.XRpcBootStrap;
import fun.keepon.annotation.XRpcApi;
import fun.keepon.annotation.XRpcService;
import fun.keepon.api.HelloXRpc;
import fun.keepon.config.Configuration;

import java.util.Date;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@XRpcApi
public class HelloImpl implements HelloXRpc {
    @Override
    public String sayHi(String msg) {
        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();
        return "FROM ["  + conf.getApplicationName() +  "]: "+ msg;
    }

    @Override
    public Date whatNow() {
        return new Date();
    }
}

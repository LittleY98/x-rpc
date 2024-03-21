package fun.keepon.springboot.xrpcspringbootprovider.serviceImpl;

import fun.keepon.XRpcBootStrap;
import fun.keepon.annotation.XRpcApi;
import fun.keepon.api.HelloXRpc;
import fun.keepon.config.Configuration;

import java.util.Date;

/**
 * @author LittleY
 * @date 2024/3/21
 * @description HelloXRpc服务实现
 */
@XRpcApi
public class HelloImpl implements HelloXRpc {
    @Override
    public String sayHi(String msg) {
        String appName = XRpcBootStrap.getInstance().getConfiguration().getApplicationName();

        return "FROM [" + appName + "] : 你发送的是" + msg;
    }

    @Override
    public Date whatNow() {
        return new Date();
    }
}

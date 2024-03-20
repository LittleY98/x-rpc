package fun.keepon.springboot.xrpcspringbootprovider.serverImpl;

import fun.keepon.annotation.XRpcApi;
import fun.keepon.api.HelloXRpc;

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
        return "你好: " + msg;
    }

    @Override
    public Date whatNow() {
        return new Date();
    }
}

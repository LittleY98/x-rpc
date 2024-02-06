package com.keepon;

import fun.keepon.ProtocolConfig;
import fun.keepon.ReferenceConfig;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.api.HelloXRpc;
import fun.keepon.serialize.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 21:09
 */
@Slf4j
public class Application {
    public static void main(String[] args) {
        ReferenceConfig<HelloXRpc> ref = new ReferenceConfig<>();
        ref.setInterface(HelloXRpc.class);

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .protocol(new ProtocolConfig("jdk"))
                .serializeType("json")
                .reference(ref);

        HelloXRpc helloXRpc = ref.get();
//        String res = helloXRpc.sayHi("yangxun");
        Date res = helloXRpc.whatNow();
        log.error("res : {}", res);

    }
}

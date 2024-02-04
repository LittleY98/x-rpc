package com.keepon;

import fun.keepon.ProtocolConfig;
import fun.keepon.ReferenceConfig;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.api.HelloXRpc;
import lombok.extern.slf4j.Slf4j;

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
                .protocol(new ProtocolConfig("jdk"))
                .reference(ref);

        HelloXRpc helloXRpc = ref.get();
        String res = helloXRpc.sayHi("啦啦啦");
        log.error("res : {}", res);

    }
}

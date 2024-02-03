package com.keepon;

import fun.keepon.ReferenceConfig;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.api.HelloXRpc;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 21:09
 */
public class Application {
    public static void main(String[] args) {
        ReferenceConfig<HelloXRpc> ref = new ReferenceConfig<>();
        ref.setInterface(HelloXRpc.class);

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(ref);

        for (int i = 0; i < 20; i++) {
            HelloXRpc helloXRpc = ref.get();
            helloXRpc.sayHi("啦啦啦");
        }

    }
}

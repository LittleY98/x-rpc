package com.keepon;

import fun.keepon.ReferenceConfig;
import fun.keepon.api.HelloXRpc;
import fun.keepon.bean.Student;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
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
//                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .registry(new RegistryConfig("zookeeper://192.168.1.66:2181"))
//                .protocol(new ProtocolConfig("jdk"))
                .serializeType("hessian")
                .compressorType("zlib")
                .reference(ref);

        HelloXRpc helloXRpc = ref.get();

        for (int i = 0; i < 10; i++) {
            String res = helloXRpc.sayHi("yangxun");
//          Date res = helloXRpc.whatNow();
            log.error("res : {}", res);
        }


    }
}

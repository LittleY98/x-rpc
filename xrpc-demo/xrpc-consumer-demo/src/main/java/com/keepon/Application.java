package com.keepon;

import fun.keepon.ReferenceConfig;
import fun.keepon.api.Hello;
import fun.keepon.bean.Student;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.api.HelloXRpc;
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
//        ReferenceConfig<HelloXRpc> ref = new ReferenceConfig<>();
//        ref.setInterface(HelloXRpc.class);

        ReferenceConfig<Hello> helloRef = new ReferenceConfig<>();
        helloRef.setInterface(Hello.class);

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .protocol(new ProtocolConfig("jdk"))
                .serializeType("hessian")
                .reference(helloRef);

//        HelloXRpc helloXRpc = ref.get();
//        String res = helloXRpc.sayHi("yangxun");
//        Date res = helloXRpc.whatNow();
        Hello hello = helloRef.get();
        Student res = hello.generateStudent(666L, 18);
        log.error("res : {}", res);

    }
}

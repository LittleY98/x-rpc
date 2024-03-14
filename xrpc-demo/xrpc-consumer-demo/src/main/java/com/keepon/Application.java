package com.keepon;

import com.alibaba.fastjson2.JSON;
import fun.keepon.ReferenceConfig;
import fun.keepon.api.HelloXRpc;
import fun.keepon.api.OrderApi;
import fun.keepon.bean.Student;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.XRpcBootStrap;
import fun.keepon.heatbeat.HeartBeatDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;


/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 21:09
 */
@Slf4j
public class Application {
    public static void main(String[] args) throws InterruptedException {
        ReferenceConfig<HelloXRpc> ref = new ReferenceConfig<>();
        ref.setInterface(HelloXRpc.class);

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .registry(new RegistryConfig("zookeeper://192.168.1.66:2181"))
//                .protocol(new ProtocolConfig("jdk"))
                .serializeType("jdk")
                .compressorType("zlib")
                .reference(ref);

        HelloXRpc helloXRpc = ref.get();

        ReferenceConfig<OrderApi> orderApiCfg = new ReferenceConfig<>();
        orderApiCfg.setInterface(OrderApi.class);

        OrderApi orderApi = orderApiCfg.get();

        new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                try {
//                    res = helloXRpc.sayHi("littleY");
                    List<String> res = orderApi.listOrder();
                    log.info("res: {}", JSON.toJSONString(res));

                } catch (Exception e) {
                    log.error("请求出错");
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
}

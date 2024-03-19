package com.keepon;

import com.alibaba.fastjson2.JSON;
import fun.keepon.api.DemoApi;
import fun.keepon.config.ReferenceConfig;
import fun.keepon.api.HelloXRpc;
import fun.keepon.api.OrderApi;
import fun.keepon.XRpcBootStrap;
import lombok.extern.slf4j.Slf4j;

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
        HelloXRpc helloXRpc = ref.get();

        ReferenceConfig<OrderApi> orderApiCfg = new ReferenceConfig<>();
        orderApiCfg.setInterface(OrderApi.class);
        OrderApi orderApi = orderApiCfg.get();

        ReferenceConfig<DemoApi> demoApiCfg = new ReferenceConfig<>();
        demoApiCfg.setInterface(DemoApi.class);
        DemoApi demoApi = demoApiCfg.get();


        XRpcBootStrap.getInstance()
                .application("consumer")
//                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .registry(new RegistryConfig("zookeeper://192.168.1.66:2181"))
//                .protocol(new ProtocolConfig("jdk"))
//                .serializeType("jdk")
//                .compressorType("zlib")
                .reference(ref);




        new Thread(()->{
            for (int i = 0; i < 5000; i++) {
                try {
//                    res = helloXRpc.sayHi("littleY");
                    List<String> res = orderApi.listOrder();
                    log.info("res: {}", JSON.toJSONString(demoApi.hello()));

                } catch (Exception e) {
                    log.error("请求出错");
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
}

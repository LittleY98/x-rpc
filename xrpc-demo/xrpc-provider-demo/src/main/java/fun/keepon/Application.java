package fun.keepon;

import fun.keepon.api.HelloXRpc;
import fun.keepon.config.ServiceConfig;
import fun.keepon.config.RegistryConfig;
//import fun.keepon.impl.DemoApiImpl;
import fun.keepon.impl.HelloXRpcImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {

        ServiceConfig<HelloXRpc> service = new ServiceConfig<>();
        service.setInterface(HelloXRpc.class);
        service.setRef(new HelloXRpcImpl());

//        ServiceConfig<DemoApi> demoService = new ServiceConfig<>();
//        demoService.setInterface(DemoApi.class);
//        demoService.setRef(new DemoApiImpl());

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .registry(new RegistryConfig("zookeeper://debian:2181"))
//                .serializeType("hessian")
//                .compressorType("zlib")
//                .publish(service)
//                .publish(demoService)
                .scan("fun.keepon.impl")
                .start();
    }

}

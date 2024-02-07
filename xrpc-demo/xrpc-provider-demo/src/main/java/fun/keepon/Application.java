package fun.keepon;

import fun.keepon.api.Hello;
import fun.keepon.api.HelloXRpc;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.impl.HelloImpl;
import fun.keepon.impl.HelloXRpcImpl;

import fun.keepon.serialize.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {

        ServiceConfig<HelloXRpc> service = new ServiceConfig<>();
        ServiceConfig<Hello> helloService = new ServiceConfig<>();

        service.setInterface(HelloXRpc.class);
        service.setRef(new HelloXRpcImpl());

        helloService.setInterface(Hello.class);
        helloService.setRef(new HelloImpl());

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .protocol(new ProtocolConfig("JDK"))
                .serializeType("jdk")
                .compressorType("zlib")
                .publish(service)
                .publish(helloService)
                .start();
    }

}

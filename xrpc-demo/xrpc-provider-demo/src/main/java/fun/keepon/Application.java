package fun.keepon;

import fun.keepon.api.HelloXRpc;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.impl.HelloXRpcImpl;

import fun.keepon.serialize.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {

        ServiceConfig<HelloXRpc> service = new ServiceConfig<>();

        service.setInterface(HelloXRpc.class);
        service.setRef(new HelloXRpcImpl());

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
//                .registry(new RegistryConfig("zookeeper://192.168.1.66:2181"))
//                .protocol(new ProtocolConfig("JDK"))
                .serializeType("hessian")
                .compressorType("zlib")
                .publish(service)
                .start();
    }

}

package fun.keepon;

import fun.keepon.api.HelloXRpc;
import fun.keepon.impl.HelloXRpcImpl;
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
                .protocol(new ProtocolConfig("JDK"))
                .publish(service)
                .start();
    }

}

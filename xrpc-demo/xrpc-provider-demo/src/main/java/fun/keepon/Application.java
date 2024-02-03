package fun.keepon;

import fun.keepon.api.HelloXRpc;
import fun.keepon.discovery.RegistryConfig;
import fun.keepon.impl.HelloXRpcImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class Application {
    public static void main(String[] args) {

        ServiceConfig<HelloXRpc> service = new ServiceConfig<>();
        service.setInterface(HelloXRpc.class);
        service.setRef(new HelloXRpcImpl());

        ServiceConfig<Date> dateServiceConfig = new ServiceConfig<>();
        dateServiceConfig.setInterface(Date.class);
        dateServiceConfig.setRef(new HelloXRpcImpl());

        ArrayList<ServiceConfig<?>> serviceConfigs = new ArrayList<>();
        serviceConfigs.add(service);
        serviceConfigs.add(dateServiceConfig);

        XRpcBootStrap.getInstance()
                .application("consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig("JDK"))
                .publish(serviceConfigs)
                .start();
    }

}

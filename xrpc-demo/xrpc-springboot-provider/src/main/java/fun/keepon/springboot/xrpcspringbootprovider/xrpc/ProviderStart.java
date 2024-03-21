package fun.keepon.springboot.xrpcspringbootprovider.xrpc;

import fun.keepon.XRpcBootStrap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @author LittleY
 * @date 2024/3/21
 * @description TODO
 */
@Configuration
public class ProviderStart implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        XRpcBootStrap.getInstance()
                .scan("fun.keepon.springboot.xrpcspringbootprovider.serviceImpl")
                .start();
    }
}

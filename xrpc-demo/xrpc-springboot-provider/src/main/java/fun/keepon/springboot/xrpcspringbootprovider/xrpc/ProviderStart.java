package fun.keepon.springboot.xrpcspringbootprovider.xrpc;

import fun.keepon.XRpcBootStrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@Component
public class ProviderStart implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("即将启动XRPC。。。。。。");
        XRpcBootStrap.getInstance()
                .scan("fun.keepon.springboot.xrpcspringbootprovider.serverImpl")
                .start();
    }
}

package fun.keepon.springboot.xrpcspringbootconsumer.controller;

import com.alibaba.fastjson2.JSON;
import fun.keepon.annotation.XRpcService;
import fun.keepon.api.HelloXRpc;
import fun.keepon.api.OrderApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@RestController
public class DemoController {
    @XRpcService
    private HelloXRpc rmHello;

    @XRpcService
    private OrderApi orderApi;

    @GetMapping("/test1/{msg}")
    public String test1(@PathVariable("msg") String msg){
        return rmHello.sayHi(msg);
//        return msg;
    }
    @GetMapping("/test2")
    public String time(){
        return rmHello.whatNow().toString();
    }

    @GetMapping("/test3")
    public String test3(){
        return JSON.toJSONString(orderApi.listOrder());
    }
}

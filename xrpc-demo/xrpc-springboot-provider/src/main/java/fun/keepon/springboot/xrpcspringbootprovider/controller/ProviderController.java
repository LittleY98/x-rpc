package fun.keepon.springboot.xrpcspringbootprovider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LittleY
 * @date 2024/3/20
 * @description TODO
 */
@RestController
public class ProviderController {

    @GetMapping("/hello/{msg}")
    public String hello(@PathVariable("msg") String msg){
        return msg;
    }

}

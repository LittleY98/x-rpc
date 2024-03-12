package fun.keepon.impl;

import fun.keepon.api.DemoApi;

/**
 * @author LittleY
 * @date 2024/3/9
 * @description TODO
 */
public class DemoApiImpl implements DemoApi {
    @Override
    public String hello() {
        return "恭喜发财";
    }
}

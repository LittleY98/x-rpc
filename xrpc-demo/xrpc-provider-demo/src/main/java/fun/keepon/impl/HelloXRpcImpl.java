package fun.keepon.impl;

import fun.keepon.api.HelloXRpc;

import java.util.Date;

public class HelloXRpcImpl implements HelloXRpc {
    @Override
    public String sayHi(String msg) {
        return "Hi, " + msg;
    }

    @Override
    public Date whatNow() {
        return new Date();
    }
}

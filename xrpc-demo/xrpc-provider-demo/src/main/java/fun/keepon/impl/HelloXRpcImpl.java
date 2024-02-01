package fun.keepon.impl;

import fun.keepon.api.HelloXRpc;

public class HelloXRpcImpl implements HelloXRpc {
    @Override
    public String sayHi(String msg) {
        return "Hi, " + msg;
    }
}

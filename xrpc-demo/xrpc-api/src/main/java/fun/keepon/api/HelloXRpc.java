package fun.keepon.api;

public interface HelloXRpc {

    /**
     * 通用接口，Server和Client都需要依赖
     * @param msg 需要发送的具体的消息
     * @return 返回的结果
     */
    String sayHi(String msg);

}

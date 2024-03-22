# xRPC

## 介绍
xRPC 是一个开源项目，旨在创建一个轻量级和高性能的 Java RPC 框架。

## 安装教程

1. 下载源码
    ```shell
    git clone https://gitee.com/LittleY98/x-rpc.git
    ```
2. 使用maven打包
    ```shell
    cd x-rpc
    mvn clean install
    ```


## 使用说明

#### Quick Start

1. 服务提供者
    ```java
         public class Application {
            public static void main(String[] args) {
        
                ServiceConfig<HelloXRpc> service = new ServiceConfig<>();
                service.setInterface(HelloXRpc.class);
                service.setRef(new HelloXRpcImpl());
        
                ServiceConfig<DemoApi> demoService = new ServiceConfig<>();
                demoService.setInterface(DemoApi.class);
                demoService.setRef(new DemoApiImpl());
        
                XRpcBootStrap.getInstance()
                        .application("consumer")
                        .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                        .serializeType("hessian")
                        .compressorType("zlib")
                        .publish(service)
                        .publish(demoService)
                        .scan("your.api.package")
                        .port(8848)
                        .start();
            }
        
        }
   ```

2. 服务消费者
    ```java
    @Slf4j
    public class Application {
        public static void main(String[] args) throws InterruptedException {
            ReferenceConfig<HelloXRpc> ref = new ReferenceConfig<>();
            ref.setInterface(HelloXRpc.class);
            HelloXRpc helloXRpc = ref.get();
            
            XRpcBootStrap.getInstance()
                        .application("consumer")
                        .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                        .registry(new RegistryConfig("zookeeper://192.168.1.66:2181"))
                        .serializeType("jdk")
                        .compressorType("zlib")
                        .reference(ref);
               
               
            for (int i = 0; i < 5000; i++) {
                try {
                    String res = helloXRpc.sayHi("littleY");
                    log.info("res: {}", JSON.toJSONString(res));
                } catch (Exception e) {
                    log.error("请求出错");
                }
            }
        }
   }
    ```
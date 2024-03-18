package fun.keepon.config;

import fun.keepon.compress.Compressor;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.loadbalance.LoadBalancer;
import fun.keepon.serialize.ObjectWrapper;
import fun.keepon.serialize.Serializer;
import fun.keepon.serialize.SerializerFactory;

import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description SPI解析器
 */
public class SpiResolver {
    public static void loadFromSpi(Configuration configuration) {

        // 加载负载均衡策略
        List<ObjectWrapper<LoadBalancer>> loadBalancerList = SpiLoader.load(LoadBalancer.class);
        if (!loadBalancerList.isEmpty()) {
            configuration.setLoadBalancer(loadBalancerList.getFirst().getObj());
        }

        // 加载序列化器
        List<ObjectWrapper<Serializer>> serializerList = SpiLoader.load(Serializer.class);
        for (ObjectWrapper<Serializer> s : serializerList) {
            SerializerFactory.addWrapper(s);
        }

        // 加载压缩器
        List<ObjectWrapper<Compressor>> compressorList = SpiLoader.load(Compressor.class);
        for (ObjectWrapper<Compressor> c : compressorList) {
            CompressorFactory.addWrapper(c);
        }
    }
}

package fun.keepon.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import fun.keepon.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author LittleY
 * @description JSON序列化器
 * @date 2024/2/6
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        String jsonString = JSON.toJSONString(obj);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T t = JSON.parseObject(bytes, clazz, JSONReader.Feature.SupportClassForName);

        return t;
    }
}

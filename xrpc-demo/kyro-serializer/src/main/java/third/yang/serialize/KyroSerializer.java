package third.yang.serialize;

import com.alibaba.fastjson2.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import fun.keepon.serialize.Serializer;

import java.io.*;

/**
 * @author LittleY
 * @date 2024/3/21
 * @description 模拟第三方序列化器
 */
public class KyroSerializer implements Serializer {
    // 线程本地变量，为每个线程独立创建一个Kryo实例
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 配置Kryo实例
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = kryoThreadLocal.get();
        kryo.writeClassAndObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = kryoThreadLocal.get();
        // 这里使用了readClassAndObject，因此不需要传递具体的类信息
        @SuppressWarnings("unchecked")
        T obj = (T) kryo.readClassAndObject(input);
        input.close();
        return obj;
    }
}

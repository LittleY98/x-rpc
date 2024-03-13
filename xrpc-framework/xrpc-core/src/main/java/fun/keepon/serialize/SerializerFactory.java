package fun.keepon.serialize;

import fun.keepon.serialize.impl.HessianSerializer;
import fun.keepon.serialize.impl.JdkSerializer;
import fun.keepon.serialize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @description 序列化器工厂
 * @date 2024/2/6
 */
@Slf4j
public class SerializerFactory {

    public static final String JDK_SERIALIZER_NAME = "jdk";
    public static final Byte JDK_SERIALIZER_CODE = (byte)1;

    public static final String JSON_SERIALIZER_NAME = "json";
    public static final Byte JSON_SERIALIZER_CODE = (byte)2;

    public static final String HESSIAN_SERIALIZER_NAME = "hessian";
    public static final Byte HESSIAN_SERIALIZER_CODE = (byte)3;

    private static final Map<String ,ObjectWrapper<Serializer>> SERIALIZER_NAME_CACHE = new ConcurrentHashMap<>(8);
    private static final Map<Byte ,ObjectWrapper<Serializer>> SERIALIZER_CODE_CACHE = new ConcurrentHashMap<>(8);

    static {
        ObjectWrapper<Serializer> jdk = new ObjectWrapper<>(JDK_SERIALIZER_NAME, JDK_SERIALIZER_CODE, new JdkSerializer());
        ObjectWrapper<Serializer> json = new ObjectWrapper<>(JSON_SERIALIZER_NAME, JSON_SERIALIZER_CODE, new JsonSerializer());
        ObjectWrapper<Serializer> hessian = new ObjectWrapper<>(HESSIAN_SERIALIZER_NAME, HESSIAN_SERIALIZER_CODE, new HessianSerializer());

        SERIALIZER_NAME_CACHE.put(jdk.getName(), jdk);
        SERIALIZER_CODE_CACHE.put(jdk.getCode(), jdk);

        SERIALIZER_NAME_CACHE.put(json.getName(), json);
        SERIALIZER_CODE_CACHE.put(json.getCode(), json);

        SERIALIZER_NAME_CACHE.put(hessian.getName(), hessian);
        SERIALIZER_CODE_CACHE.put(hessian.getCode(), hessian);
    }

    public static ObjectWrapper<Serializer> getSerializerByName(String serializeTypeName) {
        ObjectWrapper<Serializer> serializerWrapper = SERIALIZER_NAME_CACHE.get(serializeTypeName);
        if (serializerWrapper == null) {
            log.error("there is no serializer named {}", serializeTypeName);
            log.error("tips: there are serializers: {}", SERIALIZER_NAME_CACHE.keySet());
            throw new RuntimeException("there is no serializer named " + serializeTypeName);
        }

        return serializerWrapper;
    }

    public static ObjectWrapper<Serializer> getSerializerByCode(Byte code) {
        ObjectWrapper<Serializer> serializerWrapper = SERIALIZER_CODE_CACHE.get(code);
        if (serializerWrapper == null) {
            log.error("there is no serializer, code: {}", code);
            log.error("tips: there are serializers: {}", SERIALIZER_CODE_CACHE.keySet());
            throw new RuntimeException("there is no serializer, code: " + code);
        }

        return serializerWrapper;
    }
}

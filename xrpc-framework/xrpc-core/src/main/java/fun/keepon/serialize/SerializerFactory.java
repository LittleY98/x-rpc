package fun.keepon.serialize;

import fun.keepon.serialize.impl.JdkSerializer;
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

    private static final Map<String ,ObjectWrapper<Serializer>> SERIALIZER_NAME_CACHE = new ConcurrentHashMap<>(8);
    private static final Map<Byte ,ObjectWrapper<Serializer>> SERIALIZER_CODE_CACHE = new ConcurrentHashMap<>(8);

    static {
        ObjectWrapper<Serializer> jdk = new ObjectWrapper<>(JDK_SERIALIZER_NAME, JDK_SERIALIZER_CODE, new JdkSerializer());

        SERIALIZER_NAME_CACHE.put(jdk.getName(), jdk);
        SERIALIZER_CODE_CACHE.put(jdk.getCode(), jdk);
    }

    public static Serializer getSerializerByName(String serializeTypeName) {
        ObjectWrapper<Serializer> serializerWrapper = SERIALIZER_NAME_CACHE.get(serializeTypeName);
        if (serializerWrapper == null) {
            log.error("there is no serializer named {}", serializeTypeName);
            log.error("tips: there are serializers: {}", SERIALIZER_NAME_CACHE.keySet());
            throw new RuntimeException("there is no serializer named " + serializeTypeName);
        }

        return serializerWrapper.getObj();
    }

    public static Serializer getSerializerByCode(Byte code) {
        ObjectWrapper<Serializer> serializerWrapper = SERIALIZER_CODE_CACHE.get(code);
        if (serializerWrapper == null) {
            log.error("there is no serializer, code: {}", code);
            log.error("tips: there are serializers: {}", SERIALIZER_CODE_CACHE.keySet());
            throw new RuntimeException("there is no serializer, code: " + code);
        }

        return serializerWrapper.getObj();
    }
}

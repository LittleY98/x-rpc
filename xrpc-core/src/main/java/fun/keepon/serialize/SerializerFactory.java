package fun.keepon.serialize;

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

    private static final Map<String ,ObjectWrapper<Serializer>> SERIALIZER_NAME_CACHE = new ConcurrentHashMap<>(8);
    private static final Map<Byte ,ObjectWrapper<Serializer>> SERIALIZER_CODE_CACHE = new ConcurrentHashMap<>(8);


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

    public static void addWrapper(ObjectWrapper<Serializer> objectWrapper) {

        byte code = objectWrapper.getCode();
        String name = objectWrapper.getName();

        if (SERIALIZER_CODE_CACHE.containsKey(code)) {
            throw new RuntimeException("serializer code " + code + " already exists");
        }

        if (SERIALIZER_NAME_CACHE.containsKey(name)) {
            throw new RuntimeException("serializer name " + name + " already exists");
        }

        SERIALIZER_NAME_CACHE.put(name, objectWrapper);
        SERIALIZER_CODE_CACHE.put(code, objectWrapper);
    }
}

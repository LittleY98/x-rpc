package fun.keepon.serialize;

import java.io.IOException;

/**
 * @author LittleY
 * @description 序列化器接口
 * @date 2024/2/6
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj 带序列化对象
     * @return byte[]
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes byte[]
     * @return T
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}

package fun.keepon.serialize.impl;

import fun.keepon.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author LittleY
 * @description JDK序列化器
 * @date 2024/2/6
 */
@Slf4j
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[]{};
        }

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("serialize failed, obj: {}", obj);
            throw new RuntimeException("serialize failed", e);
        }
    }


    public <T> T deserialize(byte[] bytes, Class<T> clazz){
        
        if (bytes == null || bytes.length == 0) {
            log.error("deserialize parameter invalid");
            throw new RuntimeException("deserialize parameter invalid");
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis)){
            return (T)ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            log.error("deserialize failed");
            throw new RuntimeException(e);
        }
    }
}

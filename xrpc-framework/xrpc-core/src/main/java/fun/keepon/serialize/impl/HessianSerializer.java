package fun.keepon.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import fun.keepon.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Date;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/6
 */
@Slf4j
public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[]{};
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            HessianOutput out = new HessianOutput(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz){

        if (bytes == null || bytes.length == 0) {
            log.error("deserialize parameter invalid");
            throw new RuntimeException("deserialize parameter invalid");
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
            HessianInput in = new HessianInput(bis);
            return (T) in.readObject(clazz);
        } catch (Exception e) {
            log.error("deserialize failed");
            throw new RuntimeException(e);
        }
    }
}

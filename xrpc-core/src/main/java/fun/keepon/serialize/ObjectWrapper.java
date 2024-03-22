package fun.keepon.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LittleY
 * @description 对象包装类
 * @date 2024/2/6
 */
@Data
@AllArgsConstructor
public class ObjectWrapper <T>{

    private String name;

    private byte code;

    private T obj;

}

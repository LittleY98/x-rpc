package fun.keepon.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LittleY
 * @description 描述调用发请求方法数据
 * @date 2024/2/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPayLoad {
    /**
     * 服务名称
     * 如：fun.keepon.api.HelloXRpc
     */
    private String serviceName;

    /**
     * 方法名称
     * 如： sayHi
     */
    private String methodName;

    /**
     * 参数类型
     * 如：[class java.lang.String]
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求参数
     * 如：[啦啦啦]
     */
    private Object[] parameters;

    /**
     * 返回值类型
     * 如：class java.lang.String
     */
    private Class<?> returnType;
}

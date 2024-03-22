package fun.keepon.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LittleY
 * @description 服务提供方响应报文
 * @date 2024/2/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XRpcResponse implements Serializable {

    /**
     * 请求ID
     */
    private long requestId;

    /**
     * 请求类型
     */
    private byte requestType;

    /**
     * 响应码
     */
    private byte code;

    /**
     * 压缩类型
     */
    private byte compressType;

    /**
     * 序列化类型
     */
    private byte serializeType;

    /**
     * 响应数据
     */
    private Object returnVal;

}

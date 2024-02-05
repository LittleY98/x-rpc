package fun.keepon.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LittleY
 * @description 服务调用方发起请求报文
 * @date 2024/2/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XRpcRequest {

    /**
     * 请求ID
     */
    private long requestId;

    /**
     * 请求类型
     */
    private byte requestType;

    /**
     * 压缩类型
     */
    private byte compressType;

    /**
     * 序列化类型
     */
    private byte serializeType;

    /**
     * 请求负载数据
     */
    private RequestPayLoad requestPayLoad;

}

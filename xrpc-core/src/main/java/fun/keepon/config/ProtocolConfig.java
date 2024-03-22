package fun.keepon.config;

import lombok.Data;

/**
 * @author LittleY
 * @description TODO 模拟 dubbo 的协议配置，暂时无用
 * @date 2024/2/1 20:54
 */
@Data
public class ProtocolConfig {
    private String protocolName;

    public ProtocolConfig(String protocolName) {
        this.protocolName = protocolName;
    }
}

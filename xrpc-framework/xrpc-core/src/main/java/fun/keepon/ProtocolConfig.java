package fun.keepon;

import lombok.Data;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 20:54
 */
@Data
public class ProtocolConfig {
    private String protocolName;

    public ProtocolConfig(String protocolName) {
        this.protocolName = protocolName;
    }
}

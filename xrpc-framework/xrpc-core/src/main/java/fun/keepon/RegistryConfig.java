package fun.keepon;

import lombok.Data;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/1 20:52
 */
@Data
public class RegistryConfig {
    private String connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }
}

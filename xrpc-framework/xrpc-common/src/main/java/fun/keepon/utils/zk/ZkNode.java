package fun.keepon.utils.zk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/3 15:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZkNode {
    private String path;

    private byte[] data;
}

package fun.keepon.constant;

import lombok.Getter;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/6
 */
@Getter
public enum ResponseStatus {
    SUCCESS((byte) 0, "成功"),
    FAIL((byte) 1, "失败"),
    CURRENT_LIMITING_REJECTION((byte)3, "被限流拒绝"),
    ERROR((byte)4, "服务端出错");

    private byte id;

    private String desc;


    ResponseStatus(byte id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}

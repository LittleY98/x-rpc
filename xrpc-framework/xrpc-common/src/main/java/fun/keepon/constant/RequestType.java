package fun.keepon.constant;

import lombok.Data;
import lombok.Getter;

/**
 * @author LittleY
 * @description 请求类型枚举类
 * @date 2024/2/5
 */
@Getter
public enum RequestType {
    REQUEST((byte) 1, "request"),

    HEART_BEAT((byte)2, "heart_beat");

    private byte id;

    private String type;

    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }
}

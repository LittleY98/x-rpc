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
    /**
     * 请求
     */
    REQUEST((byte) 1, "request"),

    /**
     * 心跳
     */
    HEART_BEAT((byte)2, "heart_beat");

    private final byte id;

    private final String type;

    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }
}

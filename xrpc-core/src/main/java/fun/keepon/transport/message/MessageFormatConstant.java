package fun.keepon.transport.message;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/5
 */
public class MessageFormatConstant {
    public static final byte[] MAGIC_NUMBER = "yang".getBytes();

    public static final byte VERSION = 1;

    public static final short HEAD_LENGTH = (short) (MAGIC_NUMBER.length + 1 + 2 + 4 + 1 + 1 + 1 + 1 + 8);

    public static final int MAX_FRAME_LENGTH = 1024 * 1024;
}

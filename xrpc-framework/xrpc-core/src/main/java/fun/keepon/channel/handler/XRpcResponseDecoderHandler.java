package fun.keepon.channel.handler;

import fun.keepon.constant.RequestType;
import fun.keepon.transport.message.MessageFormatConstant;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/6
 */
@Slf4j
public class XRpcResponseDecoderHandler extends LengthFieldBasedFrameDecoder {
    public XRpcResponseDecoderHandler() {
        super(MessageFormatConstant.MAX_FRAME_LENGTH,
                MessageFormatConstant.MAGIC_NUMBER.length + 1 + 2
                , 4
                , -(MessageFormatConstant.MAGIC_NUMBER.length + 1 + 2)
                , 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return decodeFrame(in);
    }

    private Object decodeFrame(ByteBuf bytebuf) throws IOException {
        byte[] magic = new byte[MessageFormatConstant.MAGIC_NUMBER.length];
        bytebuf.readBytes(magic);

        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != MessageFormatConstant.MAGIC_NUMBER[i]) {
                throw new RuntimeException("magic number is illegal");
            }
        }

        byte version = bytebuf.readByte();
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("version is illegal");
        }

        short headLength = bytebuf.readShort();
        int totalLength = bytebuf.readInt();
        byte code = bytebuf.readByte();
        byte serializeType = bytebuf.readByte();
        byte compressType = bytebuf.readByte();
        long requestId = bytebuf.readLong();
        log.debug("version: {}, headLength: {}, totalLength: {}, code: {}, serializeType: {}, compressType: {}, requestId: {}",
                version, headLength, totalLength, code, serializeType, compressType, requestId);

        XRpcResponse xRpcResponse = new XRpcResponse();
        xRpcResponse.setRequestId(requestId);
        xRpcResponse.setCode(code);
        xRpcResponse.setSerializeType(serializeType);
        xRpcResponse.setCompressType(compressType);

//        // 如果是心跳包，直接返回
//        if (requestType == RequestType.HEART_BEAT.getId()){
//            return  xRpcResponse;
//        }

        byte[] returnVal = new byte[totalLength - MessageFormatConstant.HEAD_LENGTH];
        bytebuf.readBytes(returnVal);
        Object responseVal;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(returnVal);
             ObjectInputStream ois = new ObjectInputStream(bis)){

            responseVal = ois.readObject();
        } catch (ClassNotFoundException e) {
            log.error("deserialize failed, RequestID = {}", requestId);
            throw new RuntimeException(e);
        }

        xRpcResponse.setReturnVal(responseVal);

        return xRpcResponse;
    }
}

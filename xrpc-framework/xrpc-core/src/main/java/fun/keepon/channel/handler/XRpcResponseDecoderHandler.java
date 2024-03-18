package fun.keepon.channel.handler;

import com.alibaba.fastjson2.JSON;
import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.Compressor;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.constant.RequestType;
import fun.keepon.constant.ResponseStatus;
import fun.keepon.serialize.Serializer;
import fun.keepon.serialize.SerializerFactory;
import fun.keepon.serialize.impl.JdkSerializer;
import fun.keepon.transport.message.MessageFormatConstant;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import fun.keepon.transport.message.XRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

        log.warn("********** {}", ByteBufUtil.prettyHexDump(bytebuf));

        byte[] magic = new byte[MessageFormatConstant.MAGIC_NUMBER.length];
        bytebuf.readBytes(magic);

        //  校验魔数
        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != MessageFormatConstant.MAGIC_NUMBER[i]) {
//                throw new RuntimeException("magic number is illegal");
                log.error("magic number is illegal: [{}]", JSON.toJSONString(magic));
                return null;
            }
        }

        // 版本号
        byte version = bytebuf.readByte();
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("version is illegal");
        }

        // 头长度
        short headLength = bytebuf.readShort();
        // 总长度
        int totalLength = bytebuf.readInt();
        byte requestType = bytebuf.readByte();
        byte serializeType = bytebuf.readByte();
        byte compressType = bytebuf.readByte();
        byte code = bytebuf.readByte();
        long requestId = bytebuf.readLong();
        log.debug("version: {}, headLength: {}, totalLength: {}, code: {}, serializeType: {}, compressType: {}, requestId: {}",
                version, headLength, totalLength, code, serializeType, compressType, requestId);

        XRpcResponse xRpcResponse = new XRpcResponse();
        xRpcResponse.setRequestId(requestId);
        xRpcResponse.setCode(code);
        xRpcResponse.setSerializeType(serializeType);
        xRpcResponse.setCompressType(compressType);

        // 如果是心跳包，直接返回
        if (requestType == RequestType.HEART_BEAT.getId()){
            return  xRpcResponse;
        }

        if (code == ResponseStatus.CURRENT_LIMITING_REJECTION.getId()) {
            return xRpcResponse;
        }

        byte[] returnVal = new byte[totalLength - MessageFormatConstant.HEAD_LENGTH];
        bytebuf.readBytes(returnVal);

        Serializer serializer = SerializerFactory.getSerializerByCode(serializeType).getObj();
        Compressor compressor = CompressorFactory.getCompressorByCode(compressType).getObj();

        byte[] decompress = compressor.decompress(returnVal);
        Object responseVal = serializer.deserialize(decompress, Object.class);

        xRpcResponse.setReturnVal(responseVal);

        return xRpcResponse;
    }
}

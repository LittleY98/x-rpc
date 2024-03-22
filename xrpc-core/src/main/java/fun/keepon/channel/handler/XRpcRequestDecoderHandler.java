package fun.keepon.channel.handler;

import fun.keepon.compress.Compressor;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.constant.RequestType;
import fun.keepon.serialize.Serializer;
import fun.keepon.serialize.SerializerFactory;
import fun.keepon.transport.message.MessageFormatConstant;
import fun.keepon.transport.message.RequestPayLoad;
import fun.keepon.transport.message.XRpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author LittleY
 * @description 请求解码器
 * @date 2024/2/5
 */
@Slf4j
public class XRpcRequestDecoderHandler extends LengthFieldBasedFrameDecoder {
    public XRpcRequestDecoderHandler() {
        super(MessageFormatConstant.MAX_FRAME_LENGTH,
                MessageFormatConstant.MAGIC_NUMBER.length + 1 + 2
                , 4
                , -(MessageFormatConstant.MAGIC_NUMBER.length + 1 + 2 + 4)
                , 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        try {
            return decodeFrame(frame);
        } finally {
            frame.release();
        }
    }

    private Object decodeFrame(ByteBuf bytebuf) {
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
        byte requestType = bytebuf.readByte();
        byte serializeType = bytebuf.readByte();
        byte compressType = bytebuf.readByte();
        // 占位符，无意义
        bytebuf.readByte();
        long requestId = bytebuf.readLong();
        log.debug("version: {}, headLength: {}, totalLength: {}, requestType: {}, serializeType: {}, compressType: {}, requestId: {}",
                version, headLength, totalLength, requestType, serializeType, compressType, requestId);

        XRpcRequest xRpcRequest = new XRpcRequest();
        xRpcRequest.setRequestId(requestId);
        xRpcRequest.setRequestType(requestType);
        xRpcRequest.setSerializeType(serializeType);
        xRpcRequest.setCompressType(compressType);

        // 如果是心跳包，直接返回
        if (requestType == RequestType.HEART_BEAT.getId()){
            return  xRpcRequest;
        }

        byte[] payload = new byte[totalLength - MessageFormatConstant.HEAD_LENGTH];
        bytebuf.readBytes(payload);

        // 解压缩
        Compressor compressor = CompressorFactory.getCompressorByCode(compressType).getObj();
        byte[] decompress = compressor.decompress(payload);

        //序列化
        Serializer serializer = SerializerFactory.getSerializerByCode(serializeType).getObj();
        RequestPayLoad requestPayLoad = serializer.deserialize(decompress, RequestPayLoad.class);


        xRpcRequest.setRequestPayLoad(requestPayLoad);

        return xRpcRequest;
    }
}

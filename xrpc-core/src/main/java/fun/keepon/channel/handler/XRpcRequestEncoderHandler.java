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
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/5
 */
@Slf4j
public class XRpcRequestEncoderHandler extends MessageToByteEncoder<XRpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, XRpcRequest msg, ByteBuf out) throws Exception {

        // 魔数
        out.writeBytes(MessageFormatConstant.MAGIC_NUMBER);

        // 版本号
        out.writeByte(MessageFormatConstant.VERSION);

        // 头部长度
        out.writeShort(MessageFormatConstant.HEAD_LENGTH);

        // 对payloads序列化和压缩
        Serializer serializer = SerializerFactory.getSerializerByCode(msg.getSerializeType()).getObj();
        Compressor compressor = CompressorFactory.getCompressorByCode(msg.getCompressType()).getObj();

        byte[] serialize = serializer.serialize(msg.getRequestPayLoad());
        byte[] payloadBytes = compressor.compress(serialize);

        // 总长度
        out.writeInt(payloadBytes.length + MessageFormatConstant.HEAD_LENGTH);

        //请求类型
        out.writeByte(msg.getRequestType());

        // 序列化类型
        out.writeByte(msg.getSerializeType());

        //压缩类型
        out.writeByte(msg.getCompressType());

        // 占位符
        out.writeByte((byte)0);

        //请求ID
        out.writeLong(msg.getRequestId());

        //请求负载数据
        if (msg.getRequestType() != RequestType.HEART_BEAT.getId()){
            out.writeBytes(payloadBytes);
        }
    }

    private byte[] getByteArray(RequestPayLoad requestPayLoad){
        if (requestPayLoad == null) {
            return new byte[]{};
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(requestPayLoad);
        } catch (IOException e) {
            log.error("序列化失败");
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }
}

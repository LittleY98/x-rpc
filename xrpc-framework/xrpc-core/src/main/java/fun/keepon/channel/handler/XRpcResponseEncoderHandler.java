package fun.keepon.channel.handler;

import fun.keepon.XRpcBootStrap;
import fun.keepon.compress.Compressor;
import fun.keepon.compress.CompressorFactory;
import fun.keepon.config.Configuration;
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
public class XRpcResponseEncoderHandler extends MessageToByteEncoder<XRpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, XRpcResponse msg, ByteBuf out) throws Exception {

        Configuration conf = XRpcBootStrap.getInstance().getConfiguration();

        // 魔数
        out.writeBytes(MessageFormatConstant.MAGIC_NUMBER);

        // 版本号
        out.writeByte(MessageFormatConstant.VERSION);

        // 头部长度
        out.writeShort(MessageFormatConstant.HEAD_LENGTH);

        // 总长度
        Serializer serializer = SerializerFactory.getSerializerByName(conf.getSerializer()).getObj();
        Compressor compressor = CompressorFactory.getCompressorByName(conf.getCompress()).getObj();


        int payLoadLen = 0;
        byte[] payloadBytes = new byte[1];
        if (msg.getCode() != ResponseStatus.CURRENT_LIMITING_REJECTION.getId()){
            byte[] serialize = serializer.serialize(msg.getReturnVal());
            payloadBytes = compressor.compress(serialize);
            payLoadLen = payloadBytes.length;
        }
        out.writeInt(payLoadLen + MessageFormatConstant.HEAD_LENGTH);

//        TODO 请求类型
        out.writeByte(msg.getRequestType());

        // 序列化类型
        out.writeByte(SerializerFactory.getSerializerByName(conf.getSerializer()).getCode());

        //压缩类型
        out.writeByte(msg.getCompressType());

        out.writeByte(msg.getCode());

        //请求ID
        out.writeLong(msg.getRequestId());

        //请求负载数据
        if (msg.getRequestType() != RequestType.HEART_BEAT.getId() && msg.getCode() != ResponseStatus.CURRENT_LIMITING_REJECTION.getId()){
            out.writeBytes(payloadBytes);
        }
    }

    private byte[] getByteArray(Object requestPayLoad){
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

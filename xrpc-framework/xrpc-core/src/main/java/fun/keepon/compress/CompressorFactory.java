package fun.keepon.compress;

import fun.keepon.compress.impl.NoCompressor;
import fun.keepon.compress.impl.ZlibCompressor;
import fun.keepon.serialize.ObjectWrapper;
import fun.keepon.serialize.Serializer;
import fun.keepon.serialize.impl.HessianSerializer;
import fun.keepon.serialize.impl.JdkSerializer;
import fun.keepon.serialize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LittleY
 * @description 压缩器工厂
 * @date 2024/2/7
 */
@Slf4j
public class CompressorFactory {

    private static final Map<String , ObjectWrapper<Compressor>> COMPRESSOR_NAME_CACHE = new ConcurrentHashMap<>(8);
    private static final Map<Byte ,ObjectWrapper<Compressor>> COMPRESSOR_CODE_CACHE = new ConcurrentHashMap<>(8);

    private static final String  ZLIB_COMPRESSOR_NAME = "zlib";
    private static final byte  ZLIB_COMPRESSOR_CODE = 1;

    private static final String  NO_COMPRESSOR_NAME = "nocompress";
    private static final byte  NO_COMPRESSOR_CODE = 0;

    static {
        ObjectWrapper<Compressor> zlib = new ObjectWrapper<>(ZLIB_COMPRESSOR_NAME, ZLIB_COMPRESSOR_CODE, new ZlibCompressor());
        ObjectWrapper<Compressor> nocompress = new ObjectWrapper<>(NO_COMPRESSOR_NAME, NO_COMPRESSOR_CODE, new NoCompressor());

        COMPRESSOR_NAME_CACHE.put(zlib.getName(), zlib);
        COMPRESSOR_CODE_CACHE.put(zlib.getCode(), zlib);


        COMPRESSOR_NAME_CACHE.put(nocompress.getName(), nocompress);
        COMPRESSOR_CODE_CACHE.put(nocompress.getCode(), nocompress);
    }

    public static ObjectWrapper<Compressor> getCompressorByName(String compressorTypeName) {
        ObjectWrapper<Compressor> compressorWrapper = COMPRESSOR_NAME_CACHE.get(compressorTypeName);
        if (compressorWrapper == null) {
            log.error("there is no compressor named {}", compressorTypeName);
            log.error("tips: there are compressors: {}", COMPRESSOR_NAME_CACHE.keySet());
            throw new RuntimeException("there is no compressor named " + compressorTypeName);
        }

        return compressorWrapper;
    }

    public static ObjectWrapper<Compressor> getCompressorByCode(Byte code) {
        ObjectWrapper<Compressor> serializerWrapper = COMPRESSOR_CODE_CACHE.get(code);
        if (serializerWrapper == null) {
            log.error("there is no compressor, code: {}", code);
            log.error("tips: there are compressors: {}", COMPRESSOR_CODE_CACHE.keySet());
            throw new RuntimeException("there is no compressor, code: " + code);
        }

        return serializerWrapper;
    }
}

package fun.keepon.compress;

import fun.keepon.serialize.ObjectWrapper;
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

    public static void addWrapper(ObjectWrapper<Compressor> compressorWrapper) {
        byte code = compressorWrapper.getCode();
        String name = compressorWrapper.getName();

        if (COMPRESSOR_CODE_CACHE.containsKey(code)) {
            log.error("compressor code {} is already exists, name: {}", code, name);
            throw new RuntimeException("compressor code " + code + " is already exists, name: " + name);
        }

        if (COMPRESSOR_NAME_CACHE.containsKey(name)) {
            log.error("compressor name {} is already exists, code: {}", name, code);
            throw new RuntimeException("compressor name " + name + " is already exists, code: " + code);
        }


        COMPRESSOR_NAME_CACHE.put(name, compressorWrapper);
        COMPRESSOR_CODE_CACHE.put(code, compressorWrapper);
    }
}

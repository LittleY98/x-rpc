package fun.keepon.compress.impl;

import fun.keepon.compress.Compressor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author LittleY
 * @description Zlib压缩器
 * @date 2024/2/7
 */
@Slf4j
public class ZlibCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        if (data.length == 0) {
            return output;
        }

        Deflater compressor = new Deflater();

        compressor.reset();
        compressor.setInput(data);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int i = compressor.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            log.error("Zlib compress error", e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("bos.close failed", e);
            }
        }
        compressor.end();
        return output;
    }

    @Override
    public byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompressed = new Inflater();
        decompressed.reset();
        decompressed.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompressed.finished()) {
                int i = decompressed.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            log.error("Zlib decompress error", e);
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                log.error("o.close failed", e);
            }
        }

        decompressed.end();
        return output;
    }
}

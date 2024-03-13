package fun.keepon.compress.impl;

import fun.keepon.compress.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author LittleY
 * @description Zlib压缩器
 * @date 2024/2/7
 */
public class NoCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }
}

package fun.keepon.compress;

/**
 * @author LittleY
 * @description 压缩器接口
 * @date 2024/2/7
 */
public interface Compressor {

    /**
     * 压缩
     * @param data byte[]
     * @return byte[]
     */
    byte[] compress(byte[] data);

    /**
     * 解压
     * @param data byte[]
     * @return byte[]
     */
    byte[] decompress(byte[] data);

}

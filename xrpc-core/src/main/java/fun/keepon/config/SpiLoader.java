package fun.keepon.config;

import fun.keepon.serialize.ObjectWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description SPI加载工具类
 */
@Slf4j
public class SpiLoader {

    /**
     * 如：
     * <p>0-nocompress-fun.keepon.compress.impl.NoCompressor</p>
     * <p>1-zlib-fun.keepon.compress.impl.ZlibCompressor</p>
     */
    private static final String XRPC_SPI_DIR = "META-INF/xrpc-services/";
    public static final String SPLIT = "-";

    public static <T> List<ObjectWrapper<T>> load(Class<T> service) {
        List<ObjectWrapper<T>> services = new ArrayList<>();
        String serviceName = service.getName();
        try {
            Enumeration<URL> configs = ClassLoader.getSystemResources(XRPC_SPI_DIR + serviceName);
            while (configs.hasMoreElements()) {
                URL url = configs.nextElement();
                InputStream is = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(SPLIT);
                    if (parts.length == 3) {
                        int code = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String implClass = parts[2];
                        // 根据需要处理priority和name
                        T serviceInstance = (T) Class.forName(implClass).getDeclaredConstructor().newInstance();

                        ObjectWrapper<T> wrapper = new ObjectWrapper<>(name, (byte) code, serviceInstance);
                        services.add(wrapper);
                    }
                }
                reader.close();
                is.close();
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
            log.error("load spi [{}] error", service.getName());
            throw new RuntimeException("load spi [" + service.getName() + "] error", e);
        }
        return services;
    }
}

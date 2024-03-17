package fun.keepon.config;

import fun.keepon.serialize.ObjectWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author LittleY
 * @date 2024/3/17
 * @description TODO
 */
public class SpiUtil {
    private static final String XRPC_SPI_DIR = "META-INF/xrpc-services/";

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
                    String[] parts = line.split("-");
                    if (parts.length == 3) {
                        int code = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String implClass = parts[2];
                        // 根据需要处理priority和name
                        T serviceInstance = (T) Class.forName(implClass).newInstance();

                        ObjectWrapper<T> wrapper = new ObjectWrapper<>(name, (byte) code, serviceInstance);
                        services.add(wrapper);
                    }
                }
                reader.close();
                is.close();
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
        return services;
    }
}

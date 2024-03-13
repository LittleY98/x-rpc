package fun.keepon.utils;

import fun.keepon.exceptions.NetWorkException;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author LittleY
 * @description 网络工具类
 * @date 2024/2/3 16:54
 */
@Slf4j
public class NetUtils {

    /**
     * 获取本机在局域网中的ip
     * @return String
     */
    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            String localIP = "";
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();

                if (network.isLoopback() || network.isVirtual() || network.isPointToPoint()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = network.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    ip = inetAddresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip instanceof Inet4Address) {
                        localIP = ip.getHostAddress();
                    }
                }
            }
            return localIP;
        } catch (SocketException e) {
            throw new NetWorkException("获取本机IP失败", e.getCause());
        }
    }
}

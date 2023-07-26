package com.ksyun.trade.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author ksc
 */
@Slf4j
public class IpUtils {

    private static final String OS_X_ETH0 = "en0";
    private static final String LINUX_ETH0 = "eth0";

    private static final String CLIENT_IP = "CLIENT_IP";

    public static String getRealIp(HttpServletRequest req) {
        String ip = (String) req.getAttribute(CLIENT_IP);
        if (ip == null) {
            ip = getFirstNonBlankHeader(req, "X-Real-IP", "x-real-ip",
                    "X-Forwarded-For", "x-forwarded-for");
            if (ip == null) {
                ip = req.getRemoteAddr();
                if (ip == null) {
                    ip = "";
                }
            }
            int idx = ip.indexOf(',');
            ip = (idx != -1) ? ip.substring(0, idx).trim() : ip;
            req.setAttribute(CLIENT_IP, ip);
        }
        return ip;
    }

    public static String getEthernet0Ip() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (LINUX_ETH0.equals(networkInterface.getName()) || OS_X_ETH0.equals(networkInterface.getName())) {
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress address = interfaceAddress.getAddress();
                        if (address instanceof Inet4Address) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("get network interface failed", e);
        }
        return StringUtils.EMPTY;
    }

    private static String getFirstNonBlankHeader(HttpServletRequest req,
                                                 String... headerNames) {
        if (req == null) {
            return null;
        }
        for (String name : headerNames) {
            String value = req.getHeader(name);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}

package com.goal.utils;

import org.springframework.util.StringUtils;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class CommonUtil {

    /**
     * 获取 ip 地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ipAddress) || "unkonwn".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ipAddress) || "unkonwn".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ipAddress) || "unkonwn".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if ("127.0.0.1".equals(ipAddress)) {
                    // 根据网卡获取本机配置的IP
                    InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inetAddress.getHostAddress();
                }
            }

            // 通过多个代理的情况：第一个ip为真实ip
            if (ipAddress != null && ipAddress.length() > 15) {
                int index = ipAddress.indexOf(",");
                if (index > 0) {
                    ipAddress = ipAddress.substring(0, index);
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        return ipAddress;
    }


    /**
     * MD5加密
     * @param data 加密数据
     * @return 加密数据
     */
    public static String MD5(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] bytes = md5.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte item : bytes) {
                builder.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return builder.toString().toUpperCase();
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 生成随机验证码
     * @param length 验证码长度
     * @return
     */
    public static String getRandomCode(int length) {

        String sources = "0123456789";

        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        int up = sources.length() - 1;
        for (int i = 0; i < length; i++) {
            builder.append(sources.charAt(random.nextInt(up)));
        }

        return builder.toString();
    }

}

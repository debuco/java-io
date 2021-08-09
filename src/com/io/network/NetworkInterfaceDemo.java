package com.io.network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author bxwang
 * @date 2021/8/9 16:19
 */
public class NetworkInterfaceDemo {
    /**
     * 未提供public的构造器
     * 1. 每个NetworkInterface有多个InterfaceAddress对象
     * 2. 每个InterfaceAddress对象有一个InetAddress对象
     */
    private static Enumeration<NetworkInterface> networkInterfaces = getDefaultNetWorkInterface();

    public static void main(String[] args) {
        if (networkInterfaces != null) {
            while (networkInterfaces.hasMoreElements()) {
                try {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    System.out.println("name:" + networkInterface.getDisplayName() + "\n"
                        + "MTU:" + networkInterface.getMTU() + "\n"
                        + "UP:" + networkInterface.isUp() + "\n"
                        + "LookBack:" + networkInterface.isLoopback() + "\n"
                        + "MAC:" + networkInterface.getHardwareAddress());

                    System.out.println("--------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Enumeration<NetworkInterface> getDefaultNetWorkInterface() {
        try {
           return NetworkInterface.getNetworkInterfaces();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<InetAddress> getInetAddress() {
        List<InetAddress> inetAddresses = new ArrayList<>();
        if (networkInterfaces != null) {
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    inetAddresses.add(inetAddressEnumeration.nextElement());
                }
            }
        }
        return inetAddresses;
    }

    public static List<InterfaceAddress> getIterfaceAddress() {
        List<InterfaceAddress> interfaceAddresses = new ArrayList<>();
        if (networkInterfaces != null) {
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
                interfaceAddresses.addAll(addresses);
            }
        }
        return interfaceAddresses;
    }
}

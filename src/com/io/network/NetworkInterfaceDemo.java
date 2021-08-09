package com.io.network;

import sun.nio.ch.Net;

import java.net.InetAddress;
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
}

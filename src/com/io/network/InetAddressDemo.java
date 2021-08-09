package com.io.network;

import java.net.InetAddress;
import java.util.List;

/**
 * @author bxwang
 * @date 2021/8/9 17:09
 */
public class InetAddressDemo {
    private static List<InetAddress> inetAddresses = NetworkInterfaceDemo.getInetAddress();

    public static void main(String[] args) {
        inetAddresses.forEach( inetAddress -> {
            System.out.println(inetAddress.getCanonicalHostName());
            System.out.println(inetAddress.getHostAddress());
            System.out.println(inetAddress.getHostName());
            System.out.println("----------------------------------");

        });
    }

}

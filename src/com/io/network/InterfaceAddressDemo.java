package com.io.network;

import java.net.InterfaceAddress;
import java.util.List;

/**
 * @author bxwang
 * @date 2021/8/9 17:45
 */
public class InterfaceAddressDemo {
    private static List<InterfaceAddress> interfaceAddresses = NetworkInterfaceDemo.getIterfaceAddress();

    public static void main(String[] args) {
        interfaceAddresses.forEach(interfaceAddress -> {

            System.out.println(interfaceAddress.getAddress());
            System.out.println(interfaceAddress.getNetworkPrefixLength());
            System.out.println(interfaceAddress.getBroadcast());
            System.out.println("------------------------------------------");
        });
    }
}

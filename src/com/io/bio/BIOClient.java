package com.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 * BIO 客户端
 * Socket : 		向服务端发送连接
 * PrintWriter : 	向服务端传递参数
 * BufferedReader : 从服务端接收参数
 */
public class BIOClient {
    private static final int CLIENT_NUM = 1;

    private static Integer PORT = 8888;

    private static String IP_ADDRESS = "127.0.0.1";

    public static void main(String[] args) {
        // 模拟10个客户端发送请求  
        for (int i = 0; i < CLIENT_NUM; i++) {
            clientReq(i);
        }
    }

    private static void clientReq(int i) {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            // Socket 发起连接操作。连接成功后，双方通过输入和输出流进行同步阻塞式通信
            socket = new Socket(IP_ADDRESS, PORT);

            // 获取返回内容
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer = new PrintWriter(socket.getOutputStream(), true);

//            System.out.println("客户端开始读取数据......");
//            String readline = reader.readLine();
//            System.out.println("客户端读取数据" + readline);

            String[] operators = {"+", "-", "*", "/"};

            Random random = new Random(System.currentTimeMillis());

            String expression = random.nextInt(10)
                + operators[random.nextInt(4)]
                + (random.nextInt(10) + 1);

            // 向服务器端发送数据
            writer.println(expression);

            System.out.println(i + " 客户端打印返回数据 : " + reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
                if (null != socket) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

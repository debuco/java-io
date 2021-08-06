package com.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * server 任务处理类
 */
public class BIOServerHandler implements Runnable {

    private Socket socket;
    private static final String DEFAULT_TEXT = "hello, world!";

    public BIOServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(this.socket.getOutputStream(), true);
//        System.out.println("打印客户端传来的数据 : " + reader.readLine());  加了这行代码，会导致程序无法进行 ，细节
            String body = null;
//            while (true) {
                // 若客户端用的是 writer.print() 传值，那readerLine() 是不能获取值，细节
//                body = reader.readLine();
//                if (null == body) {
//                    break;
//                }

//                System.out.println("server服务端接收参数 : " + body);

                this.socket.getOutputStream().write(
                    ("HTTP/1.1 200 OK\n\nWorkerRunnable: "
                    + DEFAULT_TEXT + " - " + System.currentTimeMillis() + "").getBytes());
//                writer.println(body + " = " + CalculatorUtil.cal(body).toString());
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
            try {
                if (null != reader) {
                    reader.close();
                }
                if (null != this.socket) {
                    this.socket.close();
                    this.socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

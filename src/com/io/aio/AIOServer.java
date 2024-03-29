package com.io.aio;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO, 也叫 NIO2.0 是一种异步非阻塞的通信方式
 * NIO 通过多路复用器轮询注册的通道，从而实现非阻塞效果，同时也说明了NIO是同步的。既然AIO是非阻塞的，哪它又是如何实现异步非阻塞的呢？
 * AIO 引入了异步通道的概念 AsynchronousServerSocketChannel 和 AsynchronousSocketChannel
 */
public class AIOServer {

    // 服务器通道
    public AsynchronousServerSocketChannel asynServerSocketChannel;
    // 线程池
    private ExecutorService executorService;
    // 通道组
    private AsynchronousChannelGroup threadGroup;

    public static void main(String[] args) {
        AIOServer server = new AIOServer();
        server.start(8888);
    }

    public void start(Integer port) {
        try {
            // 1.创建一个缓存池
            executorService = Executors.newCachedThreadPool();
            // 2.创建通道组
            threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            // 3.创建服务器通道
            asynServerSocketChannel = AsynchronousServerSocketChannel.open(threadGroup);
            // 4.进行绑定
            asynServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("server start , port : " + port);
            // 5.等待客户端请求
            asynServerSocketChannel.accept(this, new AIOServerHandler());
            // 一直阻塞 不让服务器停止，真实环境是在tomcat下运行，所以不需要这行代码
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

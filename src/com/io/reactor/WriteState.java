package com.io.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class WriteState implements HandlerState {

    public WriteState() {
    }

    @Override
    public void changeState(TCPHandler h) {
        // TODO Auto-generated method stub
        h.setState(new ReadState());
    }

    @Override
    public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // send()
        // get message from message queue

        String str = "Your message has sent to "
            + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        // wrap自动把buf的position设为0，所以不需要再flip()
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

        while (buf.hasRemaining()) {
            // 回传给client响应字符串，发送buf的position位置 到limit位置为止之间的內容
            sc.write(buf);
        }

        // 改变状态(SENDING->READING)
        h.setState(new ReadState());
        // 通过key改变通道注册的事件
        sk.interestOps(SelectionKey.OP_READ);
        // 使一个阻塞住的selector操作立即返回
        sk.selector().wakeup();
    }
}
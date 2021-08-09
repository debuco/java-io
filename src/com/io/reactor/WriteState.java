package nio.reactor;

import nio.HandlerState;

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
        // wrap自動把buf的position設為0，所以不需要再flip()
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

        while (buf.hasRemaining()) {
            // 回傳給client回應字符串，發送buf的position位置 到limit位置為止之間的內容
            sc.write(buf);
        }

        // 改變狀態(SENDING->READING)
        h.setState(new ReadState());
        // 通過key改變通道註冊的事件
        sk.interestOps(SelectionKey.OP_READ);
        // 使一個阻塞住的selector操作立即返回
        sk.selector().wakeup();
    }
}
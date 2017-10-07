package Netty.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/10/7.
 * telnet按Ctrl+】进入send  。。。 模式
 * NIO服务端
 * NIO中的ServerSocketChannel对应传统IO的ServerSocket
 * SocketChannel对应Socket
 * Selector用来监听ServerSocketChannel、SocketChannel,NIO核心，可以实现单线程为多个客户端服务。
 *
 *
 */
public class NIO1_2 {
    // 通道管理器
    private Selector selector;

    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     *
     */
    public void initServer(int port) throws IOException {
        // 获得一个ServerSocket通道
        ServerSocketChannel serverchannel=ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverchannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到port端口
        serverchannel.socket().bind(new InetSocketAddress(port));
        // 获得一个通道管理器
        this.selector=Selector.open();
        // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
        // 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
        serverchannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     */
    public void listen() throws IOException {
        System.out.println("服务端启动成功");
        // 轮询访问selector
        while (true){
            // 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
            selector.select();
            // 获得selector中选中的项的迭代器，选中的项为注册的事件
            Iterator<SelectionKey> ite=this.selector.selectedKeys().iterator();
            while (ite.hasNext()){
                SelectionKey key=ite.next();
                // 删除已选的key,以防重复处理
                ite.remove();
                handler(key);
            }
        }
    }

    /**
     * 处理请求
     */
    public void handler(SelectionKey key) throws IOException {
        // 客户端请求连接事件
        if(key.isAcceptable()){
            handleraccept(key);
            // 获得了可读的事件
        }else if(key.isReadable()){
            handlerread(key);
        }
    }

    /**
     * 处理连接请求
     */
    public void handleraccept(SelectionKey key) throws IOException {
        ServerSocketChannel server= (ServerSocketChannel) key.channel();
        // 获得和客户端连接的通道
        SocketChannel channel=server.accept();
        // 设置成非阻塞
        channel.configureBlocking(false);
        // 在这里可以给客户端发送信息哦
        System.out.println("新的客户端连接成功");
        // 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
        channel.register(this.selector,SelectionKey.OP_READ);
    }

    /**
     * 处理读的事件
     */
    public void handlerread(SelectionKey key) throws IOException {
        // 服务器可读取消息:得到事件发生的Socket通道
        SocketChannel channel= (SocketChannel) key.channel();
        // 创建读取的缓冲区
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        int read=channel.read(buffer);
        //客户端关闭的时候会抛出异常，死循环,解决方案read>0,加入大于0则读取。否则取消。
        if(read > 0) {
            byte[] data = buffer.array();
            String msg = new String(data).trim();
            System.out.println("服务端收到信息：" + msg);

            //回写数据
            ByteBuffer outBuffer = ByteBuffer.wrap("ok".getBytes());
            channel.write(outBuffer);// 将消息回送给客户端
        }else {
            System.out.println("客户端关闭");
            key.cancel();
        }
    }

    /**
     * 启动服务端测试
     */
    public static void main(String[] args) throws IOException {
        NIO1_2 server = new NIO1_2();
        server.initServer(8000);
        server.listen();
    }

}

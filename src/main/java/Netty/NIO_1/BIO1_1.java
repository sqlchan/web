package Netty.NIO_1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/10/7.
 * 传统socket服务端
 * 用客户端Telnet进行模拟，打开Telnet客户端  telnet 127.0.0.1 10101
 * 传统的IOsocket有两个阻塞点，`Server_2.accept();``inputStream.read(bytes);`
 * 单线程情况下只能有一个客户端,用线程池可以有多个客户端连接，但是非常消耗性能
 */
public class BIO1_1 {
    public static void main(String[] args) throws IOException {
        ExecutorService newcachedthreadpool= Executors.newCachedThreadPool();
        //创建socket服务,监听10101端口
        ServerSocket server=new ServerSocket(10101);
        System.out.println("服务端启动");
        while (true){
            //获取一个套接字（阻塞）,等待一个客户端连接进来，返回一个socket,进行处理
            //acept是一个阻塞点
            final Socket socket=server.accept();
            System.out.println("来了一个新客户端");
            newcachedthreadpool.execute(new Runnable() {
                @Override
                public void run() {
                    //业务处理
                    handler(socket);
                }
            });
        }

    }

    /**
     * 读取数据
     * 读取是一个while(true)循环，其他线程不能进来。阻塞在while中，不能返回到accept中，处理第二个客户端
     * **/
    public static void handler(Socket socket){

        try {
            byte[] bytes=new byte[1024];
            //获取socket的输入流
            InputStream inputStream=socket.getInputStream();
            while (true){
                //读取数据（阻塞）,另一个阻塞点
                int read=inputStream.read(bytes);
                if(read!=-1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("socket 关闭");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

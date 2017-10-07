package Netty.Server_2.com.server;

import org.jboss.netty.channel.*;

/**
 * 消息接受处理类
 */
public class HelloHandler extends SimpleChannelHandler{
    //接收消息
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        //网络传输的是字节流，netty用channelbuffer包装起来，字节流转换为string，才能打印
        //ChannelBuffer message= (ChannelBuffer) e.getMessage();
        String s = (String) e.getMessage();
        System.out.println(s);

        //回写数据
        ctx.getChannel().write("hi");
        super.messageReceived(ctx, e);
    }

    //捕获异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, e);
    }

    //新连接
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected");
        super.channelConnected(ctx, e);
    }

    //必须是链接已经建立，关闭通道的时候才会触发
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    //channel关闭的时候触发
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed");
        super.channelClosed(ctx, e);
    }
}

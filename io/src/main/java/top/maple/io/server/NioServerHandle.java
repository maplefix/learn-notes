package top.maple.io.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author : ThinkPad
 * @Description : 服务端业务操作
 * @Date : Created in 2019/5/20 17:06
 * @Modified By:
 */
public class NioServerHandle implements Runnable{

    /**
     * 多路复用器 Selector会对注册在其上面的channel进行；轮询，当某个channel发生读写操作时，就会处于相应的就绪状态，通过SelectionKey的值急性IO 操作
     */
    private Selector selector;
    private ServerSocketChannel channel;
    private volatile boolean stop;
    /**
     * @param port
     * 构造函数
     */
    public NioServerHandle(int port) {
        try {
            //打开多路复用器
            selector = Selector.open();
            //打开socketChannel
            channel = ServerSocketChannel.open();
            //配置通道为非阻塞的状态
            channel.configureBlocking(false);
            //通道socket绑定地址和端口
            channel.socket().bind(new InetSocketAddress(port), 1024);
            //将通道channel在多路复用器selector上注册为接收操作
            channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NIO 服务启动 端口： "+ port);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void stop(){
        this.stop=true;
    }

    @Override
    public void run() {//线程的Runnable程序
        System.out.println("NIO 服务  run()");
        while(!stop){
            try {
                //非阻塞模式：无论是否有读写时间发生，selector每隔1s被唤醒一次
                selector.select(1000);
                //阻塞模式：只有当至少一个注册的事件发生的时候才会继续
                //selector.select();
                //获取多路复用器的事件值SelectionKey，并存放在迭代器中
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                SelectionKey key = null;
                while(iterator.hasNext()){
                    System.out.println("NIO 服务  iterator.hasNext()");
                    key = iterator.next();
                    //获取后冲迭代器中删除此值
                    iterator.remove();
                    try {
                        //根据SelectionKey的值进行相应的读写操作
                        handleInput(key);
                    } catch (Exception e) {
                        if(key!=null){
                            key.cancel();
                            if(key.channel()!= null)
                                key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("NIO 服务  run  catch IOException");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * @param key
     * @throws IOException
     * 根据SelectionKey的值进行相应的读写操作
     */
    private void handleInput(SelectionKey key) throws IOException {
        System.out.println("NIO 服务  handleInput");
        //判断所传的SelectionKey值是否可用
        if(key.isValid()){
            //在构造函数中注册的key值为OP_ACCEPT,，在判断是否为接收操作
            if(key.isAcceptable()){
                //获取key值所对应的channel
                ServerSocketChannel  ssc = (ServerSocketChannel)key.channel();
                //设置为接收非阻塞通道
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                //并把这个通道注册为OP_READ
                sc.register(selector, SelectionKey.OP_READ);
            }

            //判断所传的SelectionKey值是否为OP_READ,通过上面的注册后，经过轮询后就会是此操作
            if(key.isReadable()){
                //获取key对应的channel
                SocketChannel sc = (SocketChannel)key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //从channel中读取byte数据并存放readbuf
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();//检测时候为完整的内容,若不是则返回完整的
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    //把读取的数据转换成string
                    String string = new String(bytes, "UTF-8");
                    System.out.println("服务器接受到命令 :"+ string);
                    //"查询时间"就是读取的命令，此字符串要与客户端发送的一致，才能获取当前时间，否则就是bad order
                    String currentTime = "查询时间".equalsIgnoreCase(string) ? new java.util.Date(System.currentTimeMillis()).toString() : "bad order";
                    //获取到当前时间后，就需要把当前时间的字符串发送出去
                    dowrite(sc,currentTime);
                }else if (readBytes < 0){
                    key.cancel();
                    sc.close();
                }else{}
            }
        }
    }
    /**
     * @param sc
     * @param currenttime
     * @throws IOException
     * 服务器的业务操作，将当前时间写到通道内
     */
    private void dowrite(SocketChannel sc, String currenttime) throws IOException {
        System.out.println("服务器 dowrite  currenttime"+  currenttime);
        if(currenttime !=null && currenttime.trim().length()>0){
            //将当前时间序列化
            byte[] bytes = currenttime.getBytes();
            ByteBuffer writebuf = ByteBuffer.allocate(bytes.length);
            //将序列化的内容写入分配的内存
            writebuf.put(bytes);
            writebuf.flip();
            //将此内容写入通道
            sc.write(writebuf);
        }
    }

}

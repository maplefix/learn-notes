package top.maple.io.client;

/**
 * @Author : ThinkPad
 * @Description : Nio客户端
 * @Date : Created in 2019/5/20 17:07
 * @Modified By:
 */
public class NioClient {
    private static String DEFAULT_HOST = "127.0.0.1";
    private static int DEFAULT_PORT = 8888;

    public static void start(){
        start(DEFAULT_HOST,DEFAULT_PORT);
    }

    public static synchronized void start(String ip,int port){
        //创建客户端线程
        new Thread(new NioClientHandle("127.0.0.1",port),"client").start();
    }

    /**
     * 客户端启动程序
     * @param args
     */
    public static void main(String[] args) {
       start();
    }

}

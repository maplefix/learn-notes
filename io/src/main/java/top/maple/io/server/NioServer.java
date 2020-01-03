package top.maple.io.server;

/**
 * @Author : ThinkPad
 * @Description : Nio服务端
 * @Date : Created in 2019/5/20 17:06
 * @Modified By:
 */
public class NioServer {
    private static int DEFAULT_PORT = 8888;

    public static void start(){
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port){
        //创建服务器线程
        NioServerHandle nioServerWork = new NioServerHandle(port);
        new Thread(nioServerWork, "server").start();
    }

    /**
     * 服务端启动程序
     * @param args
     */
    public static void main(String[] args) {
       start();
    }
}

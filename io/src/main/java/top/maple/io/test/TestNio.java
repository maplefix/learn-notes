package top.maple.io.test;

import top.maple.io.client.NioClient;
import top.maple.io.server.NioServer;

/**
 * @Author : Maple
 * @Description :
 * @Date : Created in 2019/5/20 16:35
 * @Modified By:
 */
public class TestNio {

    public static void main(String[] args) throws Exception{

        //运行服务端
        NioServer.start();
        //避免客户端先于服务端启动
        Thread.sleep(1000);
        //运行客户端
        NioClient.start();

    }
}

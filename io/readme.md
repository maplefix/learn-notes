1.NIO是非阻塞IO，其核心组件就是多路复用器Selector和channel，所有的channel都要在Selector上去注册，来实现非阻塞的过程；

2.Selector提供选择已经就绪的任务的能力：Selector会不断轮询注册在其上的Channel，如果某个Channel上面发生读或者写事件，这个Channel就处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可以获取就绪Channel的集合，进行后续的I/O操作。

3.一个Selector可以同时轮询多个Channel，因为JDK使用了epoll()代替传统的select实现，所以没有最大连接句柄1024/2048的限制。所以，只需要一个线程负责Selector的轮询，就可以接入成千上万的客户端。


## 创建NIO服务端的主要步骤如下
####    1.打开ServerSocketChannel，监听客户端连接
####    2.绑定监听端口，设置连接为非阻塞模式
####    3.创建Reactor线程，创建多路复用器并启动线程
####    4.将ServerSocketChannel注册到Reactor线程中的Selector上，监听ACCEPT事件
####    5.Selector轮询准备就绪的key
####    6.Selector监听到新的客户端接入，处理新的接入请求，完成TCP三次握手，简历物理链路
####    7.设置客户端链路为非阻塞模式
####    8.将新接入的客户端连接注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
####    9.异步读取客户端消息到缓冲区
####    10.对Buffer编解码，处理半包消息，将解码成功的消息封装成Task
####    11.将应答消息编码为Buffer，调用SocketChannel的write将消息异步发送给客户端

    因为应答消息的发送，SocketChannel也是异步非阻塞的，所以不能保证一次能吧需要发送的数据发送完，此时就会出现写半包的问题。
    我们需要注册写操作，不断轮询Selector将没有发送完的消息发送完毕，然后通过Buffer的hasRemain()方法判断消息是否发送完成。
## 第三章 Netty的组件和设计

1. 从高层次的角度来看， Netty 解决了两个相应的关注领域，我们可将其大致标记为技术的和体系结构的。首先，它的基于 Java NIO 的异步的和事件驱动的实现，保证了高负载下应用程序性能的最大化和可伸缩性。其次， Netty 也包含了一组设计模式，将应用程序逻辑从网络层解耦，简化了开发过程， 同时也最大限度地提高了可测试性、模块化以及代码的可重用性。  
2. 基本的 I/O 操作（ bind()、 connect()、 read()和 write()）依赖于底层网络传输所提供的原语。在基于 Java 的网络编程中，其基本的构造是 class Socket。 Netty 的 Channel 接口所提供的 API，大大地降低了直接使用 Socket 类的复杂性。
3. EventLoop 定义了 Netty 的核心抽象， 用于处理连接的生命周期中所发生的事件。
4. Channel、 EventLoop、 Thread 以及 EventLoopGroup 之间的关系 :
   - 一个 EventLoopGroup 包含一个或者多个 EventLoop  
   - 一个 EventLoop 在它的生命周期内只和一个 Thread 绑定  
   - 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理  
   - 一个 Channel 在它的生命周期内只注册于一个 EventLoop  
   - 一个 EventLoop 可能会被分配给一个或多个 Channel  
5. 因为一个操作可能不会立即返回，所以我们需要一种用于在之后的某个时间点确定其结果的方法。为此， Netty 提供了ChannelFuture 接口，其 addListener()方法注册了一个 ChannelFutureListener，以便在某个操作完成时（无论是否成功）得到通知。  所有属于同一个 Channel 的操作都被保证其将以它们被调用的顺序
   被执行。  
6. 从应用程序开发人员的角度来看， Netty 的主要组件是 ChannelHandler， 它充当了所有处理入站和出站数据的应用程序逻辑的容器。这是可行的，因为 ChannelHandler 的方法是由网络事件（其中术语“事件” 的使用非常广泛）触发的。  
7. ChannelInboundHandler 是一个你将会经常实现的子接口。这种类型的ChannelHandler 接收入站事件和数据，这些数据随后将会被你的应用程序的业务逻辑所处理。当你要给连接的客户端发送响应时，也可以从 ChannelInboundHandler 冲刷数据。你的应用程序的业务逻辑通常驻留在一个或者多个 ChannelInboundHandler 中。  
8. ChannelPipeline 提供了 ChannelHandler 链的容器，并定义了用于在该链上传播入站和出站事件流的 API。当 Channel 被创建时， 它会被自动地分配到它专属的 ChannelPipeline。  
9. 使得事件流经 ChannelPipeline 是 ChannelHandler 的工作， 它们是在应用程序的初始化或者引导阶段被安装的。这些对象接收事件、执行它们所实现的处理逻辑， 并将数据传递给链中的下一个 ChannelHandler。它们的执行顺序是由它们被添加的顺序所决定的。实际上，被我们称为 ChannelPipeline 的是这些 ChannelHandler 的编排顺序。  
10. 入站和出站 ChannelHandler 可以被安装到同一个 ChannelPipeline中。  如果一个消息或者任何其他的入站事件被读取， 那么它会从 ChannelPipeline 的头部开始流动，并被传递给第一个 ChannelInboundHandler。这个 ChannelHandler 不一定会实际地修改数据， 具体取决于它的具体功能，在这之后，数据将会被传递给链中的下一个ChannelInboundHandler。最终，数据将会到达 ChannelPipeline 的尾端， 届时，所有处理就都结束了。  
11. 数据的出站运动（即正在被写的数据）在概念上也是一样的。在这种情况下，数据将从ChannelOutboundHandler 链的尾端开始流动，直到它到达链的头部为止。在这之后，出站数据将会到达网络传输层，这里显示为 Socket。通常情况下，这将触发一个写操作。
12. 虽然 ChannelInboundHandle 和 ChannelOutboundHandle 都扩展自 ChannelHandler ，但是 Netty 能区分ChannelInboundHandler 实现和 ChannelOutboundHandler 实现， 并确保数据只会在具有相同定 向类型的个 ChannelHandler 之间传递。
13. 在 Netty 中，有两种发送消息的方式。你可以直接写到 Channel 中，也可以 写到和 ChannelHandler 相关联的ChannelHandlerContext 对象中。前一种方式将会导致消息从 ChannelPipeline 的尾端开始流动，而后者将致消息从 ChannelPipeline 中的下一个 ChannelHandler 开始流动。
14. 当你通过 Netty 发送或者接收一个消息的时候，就将会发生一次数据转换。入站消息会被解 码；也就是说，从字节转换为另一种格式，通常是一个 Java 对象。如果是出站消息，则会发生 相反方向的转换：它将从它的当前格式被编码为字节。这两种方向的转换的原因很简单：网络数 据总是一系列的字节。
15. 你将会发现对于入站数据来说， channelRead 方法/事件已经被重写了。对于每个从入站 Channel 读取的息，这个方法都将会被调用。随后，它将调用由预置解码器所提供的 decode() 方法，并将已解码的字节转发给 ChannelPipeline 中的下一个 ChannelInboundHandler 。
16. 最常见的情况是，你的应用程序会利用一个 ChannelHandler 来接收解码消息，并对该数据应用业务逻辑。创建一个这样的 ChannelHandler ，你只需要扩展基类 SimpleChannelInboundHandler<T> ，其中 T 是你要处的消息的 Java 类型 。
17. Netty 的引导类为应用程序的网络层配置提供了容器，这涉及将一个进程绑定到某个指定的 端口，或者将个进程连接到另一个运行在某个指定主机的指定端口上的进程。
18. 服务器需要两组不同的 Channel 。第一组将只包含一个 ServerChannel ，代表服务 器自身的已绑定到某个地端口的正在监听的套接字。而第二组将包含所有已创建的用来处理传 入客户端连接（对于每个服务器已经接受的连接都有一个）的 Channel 。
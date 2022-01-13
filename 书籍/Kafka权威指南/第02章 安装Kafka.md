## 第02章 安装Kafka

1. Kafka使用Zookeeper保存Broker的元数据。

2. Zookeeper集群被称为群组。Zookeeper使用的是一致性协议，所以建议每个群组里应该包含奇数个节点，因为只有当群组里的大多数节点处于可用状态，Zookeeper才能处理外部的请求。

3. initlimit表示用于在从节点与主节点之间建立初始化连接的时间上限，syncLimit表示允许从节点与主节点处于不同状态的时间上限。这两个值都是tickTime的倍数。

4. 服务器地址遵循server.X=hostname:peerPort:leaderPort格式：

   1. X：服务器的ID，它必须是一个整数，不过不一定要从0开始，而不要求是连续的。
   2. hostname：服务器的机器名或IP地址。
   3. peerPort：用于节点间通信的TCP端口。
   4. leaderPort：用于首领选举的TCP端口。

5. 客户端只需要通过clientPort就能连接到群组，而群组节点间的通信则需要同时用到这三个端口。

6. 除了公共的配置文件外，每个服务器都必须在dataDir目录中创建一个叫做myid的文件，文件里要包含服务器ID，这个ID要与配置文件里的ID保持一致。

7. broker配置：

   1. broker.id：每个broker都需要有一个标识符，使用broker.id来表示。它的默认值是0，也可以被设置成其他任意整数。这个值在整个Kafka集群里必须是唯一的。

   2. port：如果使用了配置样本来启动Kafka，它会监听9092端口。修改port配置参数可以把它设置成其他任意可用的端口。

   3. zookeeper.connect：用于保存broker元数据的Zookeeper地址是通过zookeeper.connect来指定的。该配置参数是用逗号分隔的一组hostname:port/path，每一部分含义如下：

      1. hostname是Zookeeper服务器的机器名或IP地址。

      2. port是Zookeeper的客户端连接端口。

      3. /path是可选的Zookeeper路径，作为Kafka集群的chroot环境。如果指定的chroot路径不存在，borker会在启动的时候创建它。

      4. log.dirs：Kafka会把所有消息都保存在磁盘上，存放这些日志片段的目录是通过log.dirs指定的。它是一组用逗号分隔的本地文件系统路径。如果指定了多个路径，那么broker会根据“最少使用”的原则，把同一个分区的日志片段保存到同一个路径下。要注意，broker会往拥有最少数目分区的路径新增分区，而不是往拥有最小磁盘空间的路径新增分区。

      5. num.recovery.threads.per.data.dir：对于如下3种情况，Kafka会使用可配置的线程池来处理日志片段：

         1. 服务器正常启动，用于打开每个分区的日志片段。
         2. 服务器崩溃后重启，用于检查和截断每个分区的日志片段。
         3. 服务器正常关闭，用于关闭日志片段。

         默认情况下，每个日志目录只使用一个线程。因为这些线程只是在服务器启动和关闭时会用到，完全可以设置大量的线程来大佬并行操作的目的。所配置的数字对应的是log.dirs指定的单个日志目录。

      6. auto.create.topics.enable：默认情况下，Kafka会在如下几种情况下自动创建主题：

         1. 当一个生产者开始往主题写入消息时。
         2. 当一个消费者开始从主题读取消息时。
         3. 当任意一个客户端向主题发送元数据请求时。

   4. 主题的默认配置：

      1. num.partitions：指定了新创建的主题将包含多少个分区。如果启用了主题自动创建功能（该功能是默认启用的），主题分区的个数就是该参数指定的值。默认值是1。如果要让一个主题的分区个数少于num.partitions指定的值，需要手动创建该主题。

      2. log.retention.ms：Kafka通常根据时间来决定数据可以被保留多久。默认使用log.retention.hours，默认值为168小时，也就是一周。除此之外，还有其他两个参数log.retention.minutes和log.retention.ms。这3个参数的作用是一样的，如果指定了不止一个参数，Kafka会优先使用最小值的那个数。（根据时间保留数据是通过检查磁盘上日志片段文件的最后修改时间来实现的）

      3. log.retention.bytes：保留的消息字节数来判断消息是否过期。作用在每一个分区上。如果同时指定了log.retention.bytes和log.retention.ms，只要任意一个条件得到满足，消息就会被删除。

      4. log.segment.bytes：当日志片段大小到log.segment.bytes指定的上限（默认是1GB）时，当前日志片段就会被关闭，一个新的日志片段被打开。如果一个日志片段被关闭，就开始等待过期。这个参数的值越小，就会越频繁地关闭和分配新的文件，从而降低磁盘写入的整体效率。在日志片段被关闭之前消息是不会过期的。

      5. log.segment.ms：指定了多长时间之后日志片段会被关闭。与上一个参数可以同时生效，看哪个条件先得到满足。默认情况下没有值，只根据大小来关闭日志。（在使用基于时间的片段时，要着重考虑并行关闭多个日志片段对磁盘性能的影响）

      6. message.max.bytes：限制单个消息的大小，默认是1MB。

         （消费者客户端设置的fetch.message.max.bytes必须与服务器端设置的消息大小进行协调。

   5. 生产者客户端的性能直接受到服务器端磁盘吞吐量的影响。

   6. 要把一个broker加入到集群里，只需要修改两个配置参数。首先，所有broker都必须配置相同的zookeeper.connect，该参数指定了用于保存元数据的Zookeeper群组和路径。其次，每个broker都必须为broker.id设置唯一的值。

   7. 内存页和磁盘之间的交换对Kafka各方面的性能都有重大影响。内存交换不是必须的，进行内存交换可以防止操作系统由于内存不足突然终止进程。建议把vm.swappiness参数的值设置得小一点。

   8. vm.dirty_background_ratio设置后台刷写脏页启动的系统内存百分比。

   9. vm.dirty_ratio参数可以增加被内核进程刷新到磁盘之前的脏页的数量，可以将它设置为大于20的值（百分比，60～80是一个合理的区间）如果该参数设置了较高的值，建议启用Kafka的复制功能，避免因系统崩溃造成数据丢失。

   10. 可以在/proc/vmstat文件里查看当前脏页的数量。

   11. 元文件数据包含3个时间戳：创建时间，最后修改时间以及最后访问时间。默认情况下，每个文件被读取后都会更新atime。

   12. socket读写缓冲区对应的参数分别是net.core.wmem_default和net.core.rmem_default，合理的值是128KB。读写缓冲区最大对应的参数分别是net.core.wmem_max和net.core.rmem_max，合理值是2MB。

   13. TCP socket的读写缓冲区，参数分别是net.ipv4.tcp_wmem和net.ipv4.tcp_mem。这些参数的值由3个整数组成，它们使用空格分隔，分别表示最小值、默认值和最大值。最大值不能大于net.core.wmem_max和net.core.rmem_max指定的大小。

   14. net.ipv4.tcp_window_scaliing设为1，启用TCP时间窗扩展，可以提升客户端传输数据的效率，传输的数据可以在服务器端进行缓冲。

   15. net.ipv4.tcp_max_syn_backlog设为比默认值1024更大的值，可以接受更多的并发连接。把net.core.netdev_max_backlog设为比默认值1000更大的值，有助于网络流量的爆发。

   16. G1的两个调整参数：

       1. MaxGCPauseMillis：指定每次垃圾回收默认的停顿时间。默认值是200ms。
       2. InitiatingHeapOccupancyPercent：指定了在G1启动新一轮垃圾回收之前可以使用的堆内存百分比，默认值是45。

   
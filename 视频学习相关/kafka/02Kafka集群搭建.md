## Kafka集群搭建

1. 环境搭建：Zookeeper -> Kafka(Kafka依赖zookeeper做分布式协调)

2. kafka-topics.sh 实例：

   kafka-topics.sh --zookeeper 192.168.85.11:2181,192.168.85.12:2181,192.168.85.13:2181/kafka --creat --topic wyz --partitions 2 --replication-factor 2     

3. 默认情况下，生产者轮询topic下的partition，每个partition发一次。

4. 当有新的消费者加入集群时，会触发再均衡，重新分配消费者和partition的关系。

5. 为了保证消息的有序性，需要确保只有一个生产者生产此类消息并发送到固定分区上。

6. 拉取 and 推送：

   1. 推送指server主动去推送消息，拉去指消费者主动请求消息
   2. 推送的话broker就存在状态（记录消费者的消费状况），需要提供反馈机制，也无法保证实时性
   3. 拉取consumer自主、按需去获取server的数据，比推送更优，拉取粒度：一批，多条。

7. 顺序消息的处理：

   1. 单线程，按顺序，单条处理，offset自然递增存储，无论对db，offset频率，成本有点高，CUP，网卡资源浪费，但是进度控制精确。
   2. 多线程，流式计算，当作一个事务，可以并行的部分尽量并行，要么全部成功，要么全部失败。offset为批次的头或者尾

8. 什么情况下多线程的优势可以发挥极致：任务具备隔离性。
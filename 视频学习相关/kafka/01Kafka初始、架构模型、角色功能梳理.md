## 01、Kafka初始、架构模型、角色功能梳理

1. kafka是什么？ 消息中间件、数据流平台。
2. 技术需要面对的两个通用问题：
   1. 单点问题
   2. 性能问题
3. Kafka的AKF模型实现,分散无关的数据，聚合相关的数据（比如存储时存到同一个分区下的同一个partition）以追求并发并行处理。
4. Kafka只允许在主分区上进行读写，从分区上只是做一个备份使用。
5. Kafka只能保证在分区内部有序，分区间无法保证顺序性。
6. Kafka依赖zookeeper选出controller-borker。（zookeeper应当只做分布式协调角色，而不应该是store）。
7. admin-api需要通过zookeeper找到当前的controller-borker。
8. Kafka的producer旧版本需要从zookeeper种获取broker信息，新版本直接从brokerlist中的某一台获取所有broker元数据信息。
9. Kafka可以保证消息按到达顺序排序消费，但如何使消息按期望顺序到达由程序员自己把控。
10. 一个consumer可以同时消费多个partition，一个partition最多被一个消费者消费。其中，“一个consumer”只的是一组做同样业务的consumer，不同组之间隔离。
11. 老版本中Kafka的偏移量维护在zookeeper中，新版本中kafka自己建立了topic维护offset（默认50个分区）
12. 更新偏移量的两个问题：1、消息丢失。2、重复消费。
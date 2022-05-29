## 第03章 部署Kubernetes集群

1. [Kubernetes部署教程](https://blog.csdn.net/qq_39135287/article/details/104575561)

2. Kubernetes的官方客户端是kubectl，该命令行工具能够与Kubernetes API进行交互。（Kubernetes工具能够向前向后兼容Kubernetes API的不同版本，只要你使用的工具和集群都没有超出两个次要版本就没问题）
3. 诊断集群：kubectl get componentstatuses
4. controller-manager负责运行各种控制器，这些控制器的作用就是调节集群的行为。
5. scheduler负责将不同的Pod放置到集群的不同节点上。
6. etcd服务器是集群的存储，负责保存所有API对象。
7. 在Kubernetes中，节点分为master节点和work节点，master节点包含了API服务器、调度器等管理集群的容器；worker节点内运行的则是用户的容器。通常Kubernetes不会将工作安排到master节点上，目的是为了确保用户工作负责不会影响集群的整体运行。
8. 石头kubectl describe命令还获取有关特定节点的信息。
9. Kubernetes会跟踪机器上运行的每个Pod的资源请求与约束。
10. Kubernetes Proxy负责将网络流量路由到Kubernetes集群的负载均衡服务器上。为此，集群中的每个节点都必须运行代理。
11. Kubernetes还运行了一个DNS服务器，其主要作用是为集群中定义的服务提供命名和发现。
12. 最后一个Kubernetes组件是GUI，用户见面，现在需要自己主动部署。
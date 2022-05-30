## 第08章 Ingress的HTTP负载均衡

1. 在Kubernetes中，基于HTTP的负载均衡系统名叫Ingress。
2. Kubernetes通过三项措施简化了在动态环境中负载均衡器的配置：
   1. 标准化配置。
   2. 将虚拟主机的概念变成标准的Kubernetes对象。
   3. 将多个Ingress对象合并到负载均衡器的一个配置中。
3. Ingress控制器是一个软件系统，通过type:LoadBalancer服务公开到集群外部。它可以代理发给“上游”服务器的请求。而代理的配置则通过读取和监视Ingress对象获得。
4. Ingress分为两部分：一个通用的资源规范，以及一个控制器的实现。Kubernetes没有内置的“标准”Ingress控制器，因此用户必须从多种实现中选择一种安装。
5. 安装Contour： kubectl apply -f https://j.hept.io/contour-deployment-rbac, 它会创建一个名为projectcontour的命名空间，还会再命名空间内创建一个部署（具有两个副本）和一个面向外部的type:LoadBalancer服务。
6. 为了保证Ingress正常工作，需要为负载均衡器的外部地址设置DNS条目。
7. 在Ingress系统中，如果同一个主机下列出了多个路径，则采用最长前缀匹配的方式。
8. 在一个集群上运行多个和Ingress控制器时，使用kubernetes.io/ingress.class注释指定哪个Ingress控制器使用哪个Ingress对象。这个值应该是一个字符串，指定操作该对象的Ingress控制器应该是哪个。ingress控制器本身也要使用同样的字符串进行配置，而且只能使用带有相应注释的Ingress对象。
9. Ingress对象只能引用同一个命名空间中的上游服务。
10. 通过命令式的方式创建secret: kubectl create secret tls <机密名称> --cert <证书的PEM文件> --key <私钥的PEM文件>
11. NGINX ingress控制器：读取Ingress对象，将其合并成一个NGINX配置文件。然后，向NGINX进程发出信号，请求按照新配置重启（同时负责正在进行的连接）
12. Ingress中的许多功能都没有充分定义。实现可以通过各种不同的方式来提供这些功能，因此各个实现之间的配置的可移植性很低。
13. 跨命名空间合并Ingress的方式破坏了命名空间隔离的思想。
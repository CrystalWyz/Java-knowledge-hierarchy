## 第04章 常用的kubectl命令

1. Kubernetes使用命名空间来组织集群中的对象。默认情况下，kubectl命令行工具交互的命名空间是default。如果你想使用其他的命名空间，则可以在kubectl中指定--namespace标志。
2. 如果想用就地改变默认命名空间，则可以使用上下文。上下午记录在kubectl配置文件中，一般位于$HOME/.kube/config中。
3. 创建一个拥有不同默认命名空间的上下文：kubectl config set-context [xxx] --namespace=xxx 使用: kubectl config use-context xxx
4. 通过kubectl查看对象的基本命令是: kubectl get <资源名称> <对象名称> 获取更多信息加入-o wide 如果想查看完整对象,则可以通过-o json或者-o yaml分别输出JSON或者YAML格式的对象,如果指定--no-headers标志,则kubectl不会显示方便人类阅读的表头 kubect使用JSONPath查询语言来选择返回对象的字段
5. 获取对象的详细信息: kubectl describe <资源名称> <对象名称>
6. Kubernetes API中的对象以JSON或YAML文件的形式表示。在Kubernetes中创建和修改对象：kubectl apply -f xxx.yaml 当你想确保集群的状态与文件系统的状态相匹配时，就可以反复使用apply来协调状态。
7. 如果你只想看看apply命令会执行哪些操作，而不想真的改变对象，则可以使用--try-run标志将对象输出到终端。
8. 如果你想通过交互式的方式修改对象，而不是编辑本地文件，则可以使用edit命令，这个命令会下载最新的对象状态，然后启动编辑器，并在其中显示对象的定义：kubectl edit <资源名称> <对象名称> 保存文件后，该命令会自动将对象上传回到Kubernetes集群。
9. apply命令还会将以前的配置历史记录保存到对象的注释中。你可以通过edit-last-applied、set-last-applied和view-last-applied命令处理这些记录。
10. 删除对象：kubectl delete -f xxx.yaml OR kubectl delete <资源名称> <对象名称>
11. kubectl label 给对象添加标签，默认情况下，label和annotate不允许覆盖已有的标签。如果想覆盖已有的标签，则需要添加--overwrite标志。
12. 移除标签：使用<标签名称>-语法
13. 查看正在运行的容器日志：kubectl logs <Pod名称> 如果Pod中有多个容器，则可以使用-c标志选择要查看的容器。 默认情况下，kubectl logs会列出当前日志并退出。如果你想将日志流不间断地输出到终端，则可以在命令行中添加-f标志。
14. 在运行的容器中执行命令：kubectl exec -it <Pod名称> -- bash 如果容器中没有安装bash或其他终端，可以通过attach命令，将终端附着到正在运行的进程之上。
15. 使用kubectl cp 源路径 目标路径 进行文件复制。
16. kubectl port-forward <Pod名称> 主机端口:容器端口 将主机接收到的网络流量转发到Pod中
17. 你也可以使用port-forward命令转发服务，只需将<Pod 名称>换成 services/<服务名称>。但请注意，如果你转发服务的端口，则请求只会被转发到该服务的某个Pod上，而不会经过服务的负载均衡器。
18. 如果你对集群中资源的使用情况感兴趣，则可以使用top命令查看节点或Pod正在使用的资源列表
    1. kubectl top nodes：显示节点上CPU与内存的使用情况，而且会以绝对单位以及可占用资源百分比的两种方式显示数值。
    2. kubectl top pods：显示所有Pod及其上的资源使用情况。默认情况下，该命令仅显示当前命名空间的Pod，单杀加上--all-namespaces标志以后就可以查看集群中所有Pod的资源使用情况。
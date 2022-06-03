## 第13章 ConfigMap与Secret

1. 尽可能提高容器镜像的可重用性是一种良好的实践。
2. ConfigMap负责提供工作负载的配置信息，其内容可以是非常详细的信息（短字符串），也可以是以文件形式提供的多个值。Secret与ConfigMap相似，但Secret侧重于为工作负载提供敏感信息，他们可用于提供凭证或TLS证书等。
3. 可以认为ConfigMap是一种定义了一个小型文件系统的kubernetes对象。可以把ConfigMap视为一组变量，可以在定义容器的环境中使用，也可以在命令行中使用。
4. ConfigMap的使用方法主要有三种：
   1. 文件系统：你可以将ConfigMap挂载到Pod中。系统会为每个键值对创建一个文件，文件名就是键，文件的内容就是相应的值。
   2. 环境变量：可以使用ConfigMap动态设置环境变量的值。
   3. 命令行参数：kubernetes可以根据ConfigMap的值，动态地为容器创建命令行。
5. 文件系统方法：在Pod内创建一个新的卷，并指向要挂载的ConfigMap。必须使用volumeMount指定将ConfigMap挂载到容器的什么位置。
6. 如果要采用环境变量的方法，则需要使用特殊的valueFrom成员。valueFrom只想需要使用的ConfigMap以及ConfigMap中的键
7. 命令行参数方法建立在环境变量方法之上。kubernetes将使用特殊的语法$(<环境变量名>)来完成相应的替换。
8. Secret可以通过Pod清单中的显示声明和kubernetes API暴露给Pod
9. 默认情况下，kubernetes Secret以纯文本的形式存储在集群的etcd存储中。
10. 机密卷由kubelet管理，并在Pod创建时创建。Secret存储在tmpfs卷（又名RAM磁盘）上，因此不会写入节点的磁盘。
11. Secret的每个数据元素都存储在单独的文件中，位于挂载卷时指定的目标挂载点下。
12. 镜像拉取Secret能够利用Secret API来自动分发私有仓库的凭据。镜像拉取Secret的存储方式与普通Secret一样，只不过需要通过Pod规范中的字段spec.imagePullSecrets来消费。
13. 使用create secret docker-registry命令来创建镜像拉取Secret。如果你需要反复从同一个仓库中拉取镜像，则可以将这些Secret添加到与每个Pod相关联的默认服务账号，这样就不必在每个Pod中指定Secret。
14. Secret与ConfigMap内部的数据项定义了键名，可以映射成有效的环境变量名。
15. ConfigMap数据值时清单中直接指定的UTF-8文本。从Kubernetes1.6开始，ConfigMap不能再存储二进制数据。
16. Secret数据值可以包含base64编码的任意数据。由于使用base64编码，因此机密数据可以存储二进制数据。
17. ConfigMap与Secret的最大上限为1MB。
18. 使用kubectl get secrets命令列出当前命名空间中所有的Secret，使用kubectl get configmaps命令列出当前命名空间中所有的ConfigMap。
19. 创建Secret或ConfigMap的方法是，运行kubectl create secret generic或kubectl create configmap，结合以下参数：
    1. --from-file=<文件名>：从文件中加载机密数据，文件名即为键名
    2. --from-file=<键>=<文件名>：从文件中加载机密数据，并指定键名
    3. --from-file=<文件夹>：从文件夹中加载所有名称为合法键名的文件
    4. --from-literal=<键>=<值>：直接使用指定的键值对
20. 如果ConfigMap或Secret有清单，则可以直接编辑清单，然后使用kubectl replace -f <文件名>推送新版本。如果创建资源的时候使用了kubectl apply，那么也可以使用kubectl apply -f <文件名>。
21. 在使用API更新ConfigMap与Secret之后，它会被自动推送到使用该ConfigMap或Secret的所有卷中。这个过程可能需要几秒钟，但是这些文件列表以及文件内容都会被更新成最新值。
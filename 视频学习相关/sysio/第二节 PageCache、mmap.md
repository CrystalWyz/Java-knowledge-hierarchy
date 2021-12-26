## 第二节 PageCache、mmap

1. 一个进程的组成(C)：代码段、数据段、栈、堆。
1. 操作系统中，程序并不是全部加载入内存，而是只加载部分页。
1. pagecache：内核维护、中间层 、懒分配、淘汰机制、刷写磁盘。
1. vm.dirty_background_ratio：脏页达到可用内存的百分比后刷写到磁盘。
1. vm.dirty_ratio：程序对分配pagecache达到阈值后阻塞。
1. 脏页不能直接被淘汰掉，必须先写入到磁盘。
1. 使用buffer快的原因：减少了系统调用。数据一批一批的写入（8KB）。
1. 只有文件的channel才有map(内存映射)，map以后产生的MappedByteBuffer直接映射到内核对应的pagecache的内存地址空间。
1. 堆：
   1. 堆内：JVM堆内的字节数组
   1. 堆外：JVM管理内存之外，Java进程里的字节数组
1. <font color=red>OS 没有绝对的数据可靠性。</font>
1. 为什么设计pagecache？ 减少硬件I/O的调用，提速，优先使用内存。即便想要可靠性，调成最高级别，但无法避免单点故障。


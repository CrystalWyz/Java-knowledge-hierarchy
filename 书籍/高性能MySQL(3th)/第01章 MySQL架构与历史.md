## 第一章 MySQL架构与历史

1. 和其他数据库系统相比，MySQL有点与众不同，它的架构可以在多种不同的场景中应用并发挥好的作用，但同时也会带来一些选择上的困难。

2. MySQL最重要、最与众不同的特性是它的存储引擎架构，这种架构的设计将查询处理及其他系统任务和数据的存储/提取相分离

3. MySQL架构图：

   ![](images/MySQL架构.png)

4. 最上层的服务并不是MySQL独有的，大多数基于网络的客户端/服务器的工具或者服务都有类似的架构。第二层架构大多数MySQL的核心服务功能都在这一层，所有跨存储引擎的功能都在这一层实现。第三层包含存储引擎。存储引擎负责MySQL中数据的存储和提取。和GNU/Linux下的各种文件系统一样，每个存储引擎都有它的优势和劣势。

5. 服务器通过API与存储引擎进行通信。这些接口屏蔽了不同存储引擎之间的差异，使得这些差异对上层的查询过程透明。

6. 每个客户端连接都会在服务器进程中拥有一个线程，这个连接的查询只会再这个单独的线程中执行，该线程只能轮流在某个CPU核心或者CPU中运行。服务器会负责缓存线程，因此不需要为每一个新建的连接创建或销毁线程。

7. 当客户端（应用）连接到MySQL服务器时，服务器需要对其进行认证。认证基于用户名、原始主机信息和密码、如果使用了安全套接字的方式连接，还可以使用X.509证书认证.一旦客户端连接成功，服务器会继续验证客户端是否具有执行某个特定查询的权限。

8. MySQL会解析查询，并创建内部数据结构（解析树），然后对其进行各种优化，包括重写查询、决定表的读取顺序，以及选择合适的索引等。

9. 优化器并不关心使用的是什么存储引擎，但存储引擎对于优化查询是又影响的。优化器会请求存储引擎提供容量或某个具体操作的开销信息

10. 对于SELECT语句，在解析查询之前，服务器会先检查查询缓存，如果能够在其中找到对应的查询，服务器就不必在执行查询解析、优化和执行的整个过程。而是直接返回查询缓存中的结果集。

11. MySQL在两个层面处理并发控制：服务器层和存储引擎层

12. 读锁是共享的，或者说是相互不阻塞的。多个客户在同一时刻可以同时读取同一个资源，而互不干扰。写锁则是排他的，也就是一个写锁会阻塞其他的写锁和读锁。MySQL的存储引擎都可以实现自己的锁策略和锁粒度。

13. 表锁是MySQL中最基本的锁策略，并且是开销最小的策略。行级锁可以最大程度的支持并发处理。

14. 写锁比读锁又更高的优先级，一个写锁请求可能会被插入到读锁队列的前面。

15. 服务器会为诸如ALTER、TABLE之类的语句使用表锁。而忽略存储引擎的锁机制。

16. 事物就是一组原子性的SQL查询，或者说是一个独立的工作单元。如果数据库引擎能够成功地对数据库应用该组查询的全部语句，那么久执行该组查询。如果其中有任何一条语句应为崩溃或其他他原因无法执行，那么所有的语句都不会执行。 也就是说，要么全部执行成功，要么全部执行失败。

17. 可以用START TRANSACTION语句开始一个事务，然后要么使用COMMIT提交事物将修改的数据持久保留，要么使用ROLLBACK撤销所有的修改

18. 事务的ACID特性：原子性、一致性、隔离性和持久性

    1. 原子性：一个事物必须被视为一个不可分割的最小工作单元，整个事务中的所有操作要么全部成功，要么全部失败回滚，对于一个事务来说，不可能只执行其中的一部分操作，这就是事务的原子性
    2. 一致性：数据库总是从一个一致性的状态转换到另外一个一致性的状态。
    3. 隔离性：一个事务所做的修改再最终提交以前，对其他事务是不可见的。
    4. 持久性：一旦事务提交，则其所做的修改就会永久保存到数据库中。

19. 较低级别的隔离通常可以执行更高的并发，系统的开销也更低。

20. 隔离级别：

    1. 读未提交：事务中的修改，即使没有提交，对其他事务也都是可见的。事务可以读取未提交的数据，这也被称为脏读。
    2. 读已提交：大多数数据库系统的默认隔离级别（MySQL除外）。一个事务开始时，只能看见已经提交的事务所做的修改。换句话说，一个事务从开始直到提交之前，所做的任何修改对其他事务都是不可见的。也叫不可重复读。
    3. 可重复读是MySQL的默认隔离级别，解决了脏读的问题。保证了在同一个事务中多次读取同样记录的结果是一致的。
    4. 幻读：当某个事物在读取某个范围内的记录时，另外一个事务又在该范围内插入了新的记录，当之前的事务再次读取该范围的记录时，会产生幻行。
    5. SERIALIZABLE是最高的隔离级别。通过强制事务串行执行，避免了幻读问题。SERIALIZABLE会在读取的每一行数据上都加锁。

21. MySQL提供了两种事务型的存储引擎：InnoDB和NDB Cluster。

22. MySQL默认采用自动提交模式。也就是说，如果不是显示地开始一个事务，则每个查询都被当作一个事务执行提交操作。还有一些命令，再执行之前会强制执行COMMIT提交当前的活动事务。

23. MySQL可以通过执行SET TRANSACTION ISOLATION LEVEL命令来设置隔离级别。

24. MySQL服务器层不管理事务，事务是由下层的存储引擎实现的。所以在同一个事务中，使用多种存储引擎是不可靠的。

25. nnoDB采用的是两阶段锁定协议。在事务执行的过程中，随时都可以执行锁定，锁只有在执行COMMIT或者ROLLBACK的时候才会释放，并且所有的锁是在同一时刻被释放。前面描述的锁定都是隐式锁定。

26. InnoDB也支持通过特定的语句进行显示锁定：LOCK IN SHARE MODE 与FOR UPDATE

27. MySQL也支持LOCK TABLES 和 UNLOCK TABLES语句，这是在服务器层实现的，和存储引擎无关。

28. MySQL的大多数事务型存储引擎实现的都不是简单的行级锁。基于提升并发性能的考虑，它们一般都同时实现了多版本并发控制（MVCC）

29. MVCC可以认为是行级锁的一个变种，在很多情况下都避免了加锁操作，开销更低，大都实现了非阻塞的读操作，写操作也只锁定必要的行。MVCC使用过保存数据在某个时间点的快照来实现的。有乐观并发控制和悲观并发控制。

30. InnoDB的MVCC，是通过在每行记录后面保存两个隐藏列来实现的。一个保持了行的创建时间，一个保持行的过期时间（或删除时间）存储的并不是实际的时间值，而是系统版本号。每开始一个新的事物，系统版本号都会自动递增。事务开始时刻的系统版本号会作为事务的版本号，用来和查询到的每行记录的版本号进行比较。

31. 在REPEATABLE READ隔离级别下，MVCC具体操作：

    1. SELECT：InnoDB会根据以下两个条件检查每行记录：
       1. InnoDB只查找版本早于当前事务版本的数据行（也就是，行的系统版本号小于或等于事务的系统版本号），这样可以确保事务读取的行，要么是在事务开始前已经存在，要么是事务自身插入或者修改过的。
       2. 行的删除版本要么未定义，要么大于当前事务的版本号。这可以确保事务读取到的行，在事务开始之前未被删除。
    2. INSERT：InnoDB为新插入的每一行保存当前系统版本号作为版本行号
    3. DELETE：InnoDB为删除的每一行保存当前系统版本号作为行删除标识。
    4. UPDATE：InnoDB为插入一行新纪录，保存当前系统版本号作为版本行号，同时保存当前系统版本号到原来的行作为行删除标识。

32. MVCC只在REPEATABLE READ和READ COMMITTED两个隔离级别下工作，其他两个隔离级别都和MVCC不兼容。

33. 在文件系统中，MySQL将每个数据库（也可以称之为schema）保存为数据目录下的一个子目录。创建表时，MySQL会在数据库子目录下创建一个和表同名的.frm文件保存表的定义。

34. 不同的存储引擎保存数据和索引的方式时不同的，但表的定义则是在MySQL服务层统一处理的。可以使用SHOW TABLE STATUS命令（在MySQL5.0以后的版本中，也可以查询INFORMATION_SCHEMA中对于的表）显示表的相关信息。

35. 表信息中：

    1. Engine：表的存储引擎类型。在旧版本中该列的名字为Type。
    2. Row_format:行的格式。对于MyISAM表，可选的值为Dynamic、Fixed或者Compressed. Dynamic的行长度是可变的，一般包含可变长度的字段，如VARCHAR或BLOB。Fixed的长度则是固定的，只包含固定长度的列，如CHAR和INTEGER。Compressed的行则只在压缩表中存在。
    3. Rows：表中的行数。对于MyISAM和其他一些存储引擎，该值是精确的，但对于InnoDB，该值是估计值。
    4. Data_free对于MyISAM表，表示已经分配但目前没有使用的空间。这部分空间也包括了之前删除的行，以及后续可以被INSERT利用到的空间。
    5. Collation：表的默认字符集和字符排序规则。
    6. checksum：如果启用，保存的是整个表的实时校验和。
    7. Comment：该列包含了一些其他的额外信息。对于MyISAM表，保存的是表在创建时带的注释。对于InnoDB表，保存至的是InnoDB表空间的剩余空间信息。如果是一个视图，则该列包含“VIEW”的文本字样。

36. InnoDB是MySQL的默认事务型引擎，也是最重要、使用最广泛的存储引擎。它被设计用来处理大量的短期事务，短期事务大部分情况是正常提交的，很少会回滚。nnoDB的性能和自动崩溃恢复特性，使得它在非事务型存储的需求中也很流行。

37. InnoDB的数据存储在表空间中，表空间是由InnoDB管理的一个黑盒子，有一系列的数据文件组成。

38. nnoDB采用MVCC来支持高并发，并且实现了四个标准的隔离级别。其默认级别是REPEATABLE READ，并且通过间隙锁策略防止幻读的出现。

39. InnoDB表是基于聚簇索引建立的。

40. InnoDB的存储格式是平台独立的。

41. InnoDB内部做了很多优化，包括从磁盘读取数据时采用的可预测性预读，能够自动在内存中创建hash索引以加速读操作的自适应哈希索引，已经能够加速插入操作的插入缓冲区等。

42. InnoDB通过一些机制和工具支持真正的热备份。

43. 在MySQL5.1及之前的版本，MyISAM是默认的存储引擎。MyISAM提供了大量的特性，包括全文索引、压缩、空间函数等，但MyISAM不支持事务和行级锁，而且有一个毫无疑问的缺陷就是崩溃后无法安全恢复。

44. MyISAM会将表存储在两个文件中：数据文件和索引文件，分别以.MYD和.MYI为扩展名。

45. MyISAM表可以包含动态或者静态行。MySQL会根据表的定义来决定采用何种格式。

46. MyISAM表可以存储的行记录数，一般受限于可用的磁盘空间，或者操作系统中单个文件的最大尺寸。

47. 要改变MyISAM表只在的长度，可以通过修改表的MAX_ROWS和AVG_ROW_LENGTH表指针的长度，两者相乘就是表可能达到的最大大小。修改这两个参数会导致重建整个表和表的所有索引

48. MyISAM特性：

    1. MyISAM对整张表加锁，而不是针对行。读取时会对需要读到的所有表加共享锁，写入时则对表加排他锁。但是在表有读取查询的同时，也可以往表中插入新的记录（并发插入）
    2. 对于MyISAM表，MySQL可以手工或者自动执行行检查和修复操作。可以通过CHECK TABLE检查表的错误，如果有错误可以通过执行REPAIR TABLE进行修复。如果MySQL服务器已经关闭，也可以通过myisamchk命令行工具进行检查和修复操作。
    3. 对于MyISAM表，即使是BLOB和TEXT等长字段，也可以基于前500个字符串创建索引。MyISAM也支持全文索引，这是一种基于分词创建的索引，可以支持复杂的查询。
    4. 创建MyISAM表的时候，如果指定了DELAY_KEY_WRITE选项，在每次修改执行完成时，不会立刻将修改的索引数据写入磁盘，而是会写到内存缓冲区。只有在清理键缓冲区或者关闭表的时候才会将对应的索引块写入到磁盘。这种方式可以极大地提升写入性能，但是在数据库或主机崩溃时会造成索引损坏，需要执行修复操作。延迟更新索引键的特性，可以在全局设置，也可以为单个表设置。

49. 如果表在创建并导入数据以后，不会再进行修改操作，那么这样的表或许合适采用MyISAM压缩表。可以使用myisampack对MyISAM表进行压缩（也叫打包pack）。压缩表是不能进行修改的（除非先将表解除压缩，修改数据，然后再次压缩）。压缩表可以极大地减少磁盘空间占用，因此也可以减少磁盘I/O，从而提升查询性能。压缩表也支持索引，但索引也是只读的。

50. MyISAM引擎设计简单，数据以紧密格式存储。有一些服务器级别的性能扩展限制，比如对索引键缓冲区的Mutex锁，MariaDB基于字段的索引键缓冲机制来避免该问题。

51. Archive存储引擎只支持INSERT和SELECT操作，在MySQL5.1之前也不支持索引。Archive引擎会缓存所有的写并利用zlib对插入的行进行压缩，所以比MyISAM表的磁盘I/O更少。但每次SELECT查询都需要执行全盘扫描。索引Archive表适合日志和数据采集类应用。

52. Archive引擎支持行级锁和专用的缓冲区，所以可以实现高并发的插入。在一个查询开始直到返回表中存在的所有行数之前，Archive引擎会阻止其他的SELECT执行，以实现一致性读。也实现了批量插入在完成之前对读操作是不可见的。但Archive引擎不是一个事务型的引擎，而是一个针对告诉插入和压缩做优化的简单引擎。

53. Blackhole引擎没有实现任何的存储机制，他会丢弃所有插入的数据，不做任何保存。但是服务器会记录Blackhole表的日志，所以可以用于复制数据到备库，或者只是单纯地记录到日志。

54. CSV引擎可以将普通的CSV文件（逗号分割值的文件）作为MySQL的表来处理，但这种表不支持索引。CSV引擎可以在数据库运行时拷入或者拷出文件。可以将Excel等电子表格软件中的数据存储为CSV文件，然后复制到MySQL数据目录下，就能在MySQL中打开使用。同样，如果将数据写入到一个CSV引擎表，其他的外部程序也能立即从表的数据文件中读取CSV格式的数据。因此CSV引擎可以作为一种数据交换的机制。

55. Federated引擎是访问其他MySQL服务器的一个代理，它会创建一个到远程MySQL服务器的客户端连接，并将查询传输到远程服务器执行，然后提取或者发送需要的数据。

56. 如果需要快速地访问数据，并且这些数据不会被修改，重启以后丢失也没有关系，那么使用Memory表（以前也叫做HEAP表）是非常有用的。Memory表至少比MyISAM表快一个数量级，因为所有的数据都保存在内存中，不需要进行磁盘I/O。Memory表结构在重启以后还会保留，但数据会丢失。

57. Mymory表支持Hash索引，因此查找操作非常快。Memory表是表级锁，因此并发写入的性能较低。它不支持BLOB或TEXT类型的列，并且每行的长度是固定的，所以即使指定了VARCHAR列，实际存储时也会转换成CHAR，这可能导致部分内存浪费。

58. 如果MySQL在执行插叙的过程中需要使用临时表来保存中间结果，内部使用的临时表就是Memory表。如果中间结果太大超出了Memory表的限制，或者含有BLOB或TEXT字段，则临时表会转换成MyISAM表。

59. 临时表只在单个连接中可见，当连接断开时，临时表也将不复存在。

60. Merge引擎是MyISAM引擎的一个变种。MerGE表是由多个MyISAM表合并而来的虚拟表。已经被放弃。

61. MySQL服务器、NDB集群存储引擎，以及分布式的、share-nothing的、容灾的、高可用的NDB数据库的组合，被称为MySQL集群。

62. Percona的XtraDB存储引擎是基于InnoDB引擎的一个改进版本，以及包含在Percona Server和MariaDB中，它的改进点主要集中在性能、可测量性和操作灵活性方面/

63. XtraDB可以作为InnoDB的一个完全的替代产品，甚至可以兼容的读写InnoDB数据文件，并支持InnoDB的所有查询。

64. PBXT支持引擎级别的复制、外键约束，并且以一种比较复杂的架构对固态存储提供了适当的支持，还对较大的值类型如BLOB也做了优化。是一款社区支持的存储引擎。

65. TokuDB引擎使用了一种新的叫做分形树的索引数据结构。该结构是缓存无关的，因此即使其大小超过内存性能也不会下降，也就没有内存生命周期和碎片的问题。TokuDB是一种大数据存储引擎，因为其拥有很高的压缩比，可以在很大的数据量上创建大量索引。

66. PethinkDB最初是为固态存储而设计的，比较特别的地方在于采用了一种只能追加的写时复制B树作为索引的数据结构。

67. MySQL默认是面向行的，每一行的数据时仪器存储的，服务器的查询也是以行为单位处理的。

68. Infobright是最有名的面向列的存储引擎。在非常大的数据量时，该引擎工作良好。Infobright时为数据分析和数据仓库应用而设计的。数据高度压缩，按照块进行排序，每一块都对应一组元数据。在处理查询时，访问元数据可以决定跳过该块，甚至可能只需要元数据即可满足查询的需求。不支持索引。

69. Aria，之前的名字是Maria，是MySQL创建者计划用来替代MyISAM的一款引擎。可以说Aria就是解决了崩溃安全恢复问题的MyISAM。

70. Groonga是一款全文索引引擎，号称可以提供准确而高效的全文索引。

71. OQGraph由Open Query研发，支持图操作。

72. Q4M在MySQL内部实现了队列操作。

73. SphinxSE为Sphinx全文索引搜索服务器提供了SQL接口。

74. Spider可以将数据切分成不同的分区，比较高透明地实行哪里分片，并且可以针对分片执行并行查询。

75. VPForMySQL支持垂直分区，通过一系列的代理存储引擎实现。垂直分区指的是可以将表粉刺不同列的组合，并且单独存储。

76. 如果要用到全文索引，建议优先考虑InnoDB加上Sphinx的组合，而不是使用支持全文索引的MyISAM。

77. 除非万不得已，否则建议不要混合使用多种存储引擎。

78. 如果应用需要事物支持，那么InnoDB（或者XtraDB）是目前最稳定并且经过验证的选择。如果不需要事务，并且主要是SELECT和INSERT操作，那么MyISAM是不错的选择。

79. 如果需要在线热备份，那么选择InnoDB就是基本的要求。

80. 系统崩溃后如何快速地恢复是一个需要考虑的问题。MyISAM奔溃后发生损坏的概率比InnoDB要高得多。

81. MySQL只有MyISAM支持地理空间搜索。

82. 对于日志型应用，MyISAM或者Archive存储引擎对这类应用比较合适。

83. 只读或者大部分情况下只读，如果不介意MyISAM对崩溃恢复问题，选用MyISAM引擎是合适的。建议采用InnoDB，MySIAM引擎在一开始可能没有任何问题，但随着应用压力的上升，则可能迅速恶化。各种锁争用，崩溃后的数据丢失等问题都会随之而来。

84. InnoDB是订单处理类应用的最佳选择。

85. 如果要发布一个基于CD-ROM或者DVD-ROM并且使用了MySQL数据文件的应用，可以考虑使用MyISAM表或者MyISAM压缩表，这样表之间可以隔离并且可以在不同介质上相互拷贝。

86. 大数据量大情况下，Infobright时MySQl数据仓库最成功的解决方案。也有一些大数据库不适合Infobrigt，却适合TokuDB。

87. 将表从一个引擎修改为另一个引擎最简单的办法是使用ALTER TABLE语句，需要执行很长时间。MySQL会按行将数据从原表复制到一张新表中，在复制期间可能会消耗系统所有的I/O能力，同时原表上会加上读锁。如果转换表的存储引擎，将会失去和原引擎相关的所有特性。

88. 为了更好地控制转换的过程，可以使用mysqldump工具将数据导出到文件，然后修改文件中CREATE TABLE语句的存储引擎选项，注意同时修改 表名，因为同一个数据库中不能存在相同的表名，即使他们使用的是不同的存储引擎。

89. 第三种转换的技术综合了第一种方法的高效和第二种方法的安全：先创建一个新的存储引擎的表，然后利用INSERT...SELECT语法来导数据。

90. 版本3.23（2001）：一般认为这个版本的发布是MySQL真正“诞生”的时刻，其开始获得广泛使用。引入MyISAM代替了老旧而去有诸多限制的ISAM引擎。InnoDB引擎也可以使用，但没有包含在默认的二进制发行版中，必需手工编译。引入了全文索引和复制。复制是mySQL成为互联网应用的数据库系统的关键特性。

91. 版本4.0（2003）：支持新的语法，比如UNION和多表DELETE语法。重写了复制，在备库使用了两个线程来实现复制，避免了之前一个线程做所有复制工作的模式下任务切换导致的问题。InnoDB成为标准配备，包括了全部特性：行级锁、外键等。还引入了查询缓存，同时还支持通过SSL进行连接。

92. 版本4.1（2005）：子查询和INSERT ON DUPLICATE KEY UPDATE。开始支持UTF-8字符集。支持新的二进制协议和prepared语句。

93. 版本5.0（2008）：这个版本出现了一些“企业级”特性：视图、触发器、存储过程和存储函数。老的ISAM引擎的代码被彻底移除，同时引入新的Federated等引擎。

94. 版本5.1（2008）：引入了分区、基于行的复制，以及plugin API。也出了BerkeyDB引擎，这是MySQL最早的事务引擎。其他如Federated引擎也将被放弃。

95. 版本5.5（2010）：主要改善集中在性能、扩展性、复制、分区、对微软Windows系统的支持。InnoDB成为默认的存储引擎。增加了PERFORMANCE_SCHEMA库，包含了一些可测量的性能指标的增强。增加了复制、认证和审计API。InnoDB在架构方面也做了较大的改进。

## 第一章 走进Java

1. JDK包含：Java程序设计语言、Java虚拟机、Java类库。

2. JRE包含：Java SE API子集、Java虚拟机。

3. JRE是支持Java程序运行到标准环境。

4. 按技术领域来划分Java技术体系：

   ​	Java Card、Java ME、JavaSE、Java EE

5. 1995年，Oak改名Java，Java诞生。

6. 1996年，JDK1.0发布。

7. 1997年，JDK1.1发布，基础技术支撑点，如JDBC。

8. 1998年，JDK1.2发布，技术拆分：J2ME、J2SE、J2EE。

9. 1999年，Longview Technologies创造HotSpot虚拟机（1997年被Sun收购），随后称为后续JDK的默认虚拟机（JDK1.3）

10. 2000年，JDK1.3发布，主要改进类库。

11. 随后，Sun约每两年发布一个主JDK版本，以动物命名，修正版以昆虫命名。

12. 2002年，JDK1.4发布，标志Java走向成熟。

13. 2004年，JDK1.5发布，放弃“JDK 1.X”命名，提高语法的易用性，JUD。

14. 2006年，JDK6发布。

15. Oracle收购Sun公司后，大幅裁剪了JDK7预定目标，于2011年发布。

16. 2014年，JDK8发布（Oracle使用JEP来定义和管理纳入新版JDK发布范围的功能特性），完成JDK7规划的功能。

17. 2017年，JDK9发布，完成jigsaw模块化功能，Oracle宣布每年3月与9月发布一个大版本，只有LTS版本支持长期维护。

18. 2018年3月，JDK10发布，主要是内部重构。

19. 2018年9月，JDK11发布，LTS版，至此，RedHat替Oracle维护JDK旧版本。

20. 2019年3月，JDK12发布。

21. 世界上第一款商用Java虚拟机——Sun Classic虚拟机。

22. 准确式内存管理指虚拟机可以知道内存中某个位置的数据具体是什么类型（Sun Classic基于句柄查找）

23. Sun Classic虚拟机在1.2及以前为默认虚拟机，在JDK1.4时完全退出。

24. 目前使用范围最广的虚拟机——HotSpot。

25. HotSpot虚拟机的热点代码探测能力可以通过执行计数器找出最具有编译价值的代码，然后通知即时编译器以方法为单位进行编译。

26. EBA JRockit专注于服务端应用，不关注启动速度，故不包含解释器，随着Oracle收购，停留于R28。

27. IBM J9由IBM Ottawa实验室SmallTalk虚拟机项目扩展而来，全平台虚拟机，至今活跃。

28. 专有虚拟机往往能够有更高的性能，但难以维护。

29. Zing虚拟机从HotSpot旧版本代码再加速研发而成。

30. Harmony虚拟机和Dalvik虚拟机（Android）并非Java虚拟机。

31. Graal VM：高性能、多语言虚拟机，通过程序特化（各语言的源码或字节码通过解释器转换能识别的中间表示）

32. HotSpot虚拟机中含有2个即时编译器：（JDK10之前）

    ​	C1:客户端编译器，耗时短质量低。

    ​	C2:服务端编译器，耗时长质量高。

33. JDK10，HotSpot使用Graal编译器代替C2。

34. SubStrate VM是在Graal VM0.20版本里新出现的一个极小型的运行时环境，目标是代替HotSpot来支持提前编译后的程序执行。

35. 开源：源码可阅读、复用，即可进行二次开发。
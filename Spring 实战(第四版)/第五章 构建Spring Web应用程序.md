## 第五章 构建Spring Web应用程序

1. Spring MVC请求处理流程图。
    ![](./images/Spring%20MVC处理流程.png)
2. 请求首先到达DispatcherServlet。与大多数基于Java的Web框架一样，Spring MVC所有的请求都会通过一个前端控制器Servlet（Spring MVC中单例）。
3. DispatcherServlet会查询一个或多个处理器映射器，通过URL来决策将请求发送给哪个Spring MVC控制器（controller）。
4. Spring MVC中，所有请求都由控制器（controller）处理（实际上，设计良好的控制器本身只处理很少甚至不处理工作，而是将业务逻辑委托给一个或多个服务对象进行处理。）
5. 控制器在完成逻辑处理后，通常会产生一些信息，这些信息需要返回给用户并在浏览器上显示。这些信息被称为模型（model）。信息需要发送给一个视图 （view），通常会是 JSP。
6. 控制器所做的最后一件事就是将模型数据打包，并且标示出用于渲染输出的视图名。它接下来会将请求连同模型和视图名发送回 DispatcherServlet 。
7. 然后，dispatcherServlet 将会使用视图解析器（view resolver） 来将逻辑视图名匹配为一个特定的视图实现。
8. 最后，将视图渲染上模型数据，形成最终的视图对象返回给客户端。
9. 扩展 AbstractAnnotationConfigDispatcherServletInitializer 的任意类都会自动地配置 DispatcherServlet 和 Spring 应用上下文，Spring 的应用上下文会位于应用程序的 Servlet 上下文之中。
10. 在 Servlet 3.0 环境中，容器会在类路径中查找实现 javax.servlet.ServletContainerInitializer 接口的类， 如果能发现的话，就会用它来配置 Servlet 容器。Spring 提供了这个接口的实现，名为 SpringServletContainerInitializer，这个类反过来又会查找实现 WebApplicationInitializer 的类并将配置的任务交给它们来完成。Spring 3.2 引入了一个便利的 WebApplicationInitializer 基础实现，也就 是 AbstractAnnotationConfigDispatcherServletInitializer 因为我们的 SpittrWebAppInitializer 扩展了 AbstractAnnotationConfigDispatcherServletInitializer（同时也就实现了 WebApplicationInitializer），因此当部署到 Servlet 3.0 容器中的时候，容器会自动发现它，并用它来配置 Servlet 上下文。
11. AbstractAnnotationConfigDispatcherServletInitializer 会同时创建 DispatcherServlet 和 ContextLoaderListener。GetServletConfigClasses() 方法返回的带有 @Configuration 注解的类将会用来定义 DispatcherServlet 应用上下文中的 bean。getRootConfigClasses() 方法返回的带有 @Configuration 注解的类将会用来配置 ContextLoaderListener 创建的应用上下文中的 bean。
12. 如果使用 XML 进行配置，可以使用 `<mvc:annotation-driven>` 启用注解驱动的 Spring MVC。最简单的 Spring MVC 配置就是一个带有 @EnableWebMvc 注解的类，但有以下问题待解决：
    - 没有配置视图解析器。如果这样的话，Spring 默认会使用 BeanNameViewResolver，这个视图解析器会查找 ID 与视图名称匹配的 bean，并且查找的 bean 要实现 View 接口，它以这样的方式来解析视图。
    - 没有启用组件扫描。这样的结果就是，Spring 只能找到显式声明在配置类中的控制器。
    - 这样配置的话，DispatcherServlet 会映射为应用的默认 Servlet，所以它会处理所有的请求，包括对静态资源的请求，如图片和样式表（在大多数情况下，这可能并不是你想要的效果）。
13. 对配置类扩展WebMvcConfigurerAdapter 并重写其 configureDefaultServletHandling() 方法。通过调用 DefaultServletHandlerConfigurer 的 enable() 方法，要求 DispatcherServlet 将对静态资源的请求转发到 Servlet 容器中默认的 Servlet 上，而不是使用 DispatcherServlet 本身来处理此类请求。
14. 在 Spring MVC 中，控制器只是方法上添加了 @RequestMapping 注解的类，这个注解声明了它们所要处理的请求。
15. @Controller 是一个构造型（stereotype）的注解，它基于 @Component 注解，目的就是辅助实现组件扫描。也可以让其带有 @Component 注解，它所实现的效果是一样的，但是在表意性上可能会差一些。
16. @RequestMapping 注解：它的 value 属性指定了这个方法所要处理的请求路径，value 属性能够接受一个 String 类型的数组。，method 属性细化了它所处理的 HTTP 方法。
17. Spring 现在包含了一种 mock Spring MVC 并针对控制器执行 HTTP 请求的机制。这样的话，在测试控制器的时候，就没有必要再启动 Web 服务器和 Web 浏览器 了。
18. 当控制器在类级别上添加@RequestMapping 注解时，这个注解会应用到控制器的所有处理器方法上。处理器方法上的 @RequestMapping 注解会对类级别上的 @RequestMapping 的声明进行补充。
19. Model 实际上就是一个 Map（也就是 key-value 对的集合），它会传递给视图，这样数据就能渲染到客户端了。当调用 addAttribute() 方法并且不指定 key 的时候，那么 key 会根据值的对象类型推断确定。
20. Spring MVC 允许以多种方式将客户端中的数据传送到控制器的处理器方法中，包括：
    - 查询参数（Query Parameter）。
    - 表单参数（Form Parameter）。
    - 路径变量（Path Variable）。
21. @RequestParam 可以获取请求中的参数并赋值给对应方法的参数。
22. 在理想情况下，要识别的资源应该通过 URL 路径进行标示，而不是通过查询参数。
23. 为了实现路径变量，Spring MVC 允许我们在 @RequestMapping 路径中添加占位符。占位符的名称要用大括号（`{` 和 `}`）括起来。路径中的其他部分要与所处理的请求完全匹配，但是占位符部分可以是任意的值。然后在参数上添加 @Path-Variable("") 注解，如果方法的参数名与占位符的名称相同，去掉 @PathVariable 中的 value 属性。
24. 视图格式中的 `redirect:` 前缀时，它就知道要将其解析为重定向的规则，而不是视图的名称。还能识别`forward:` 前缀。当它发现视图格式中以 `forward:` 作为前缀时，请求将会前往（forward）指定的 URL 路径，而不再是重定向。
25. 添加了 @Valid 注解会告知 Spring，需要确保这个对象满足校验限制。如果有校验出现错误的话，那么这些错误可以通过 Errors 对象进行访问，Errors 对象已作为 方法的参数。（很重要一点需要注意，Errors 参数要紧跟在带有 @Valid 注解的参数后面，@Valid 注解所标注的就是要检验的参数。）


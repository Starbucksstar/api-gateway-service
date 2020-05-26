# api-gateway-service
基于Spring Cloud Gateway + Nacos实现JwtToken校验、路由、负载均衡和熔断功能的gateway服务
![](https://github.com/Starbucksstar/api-gateway-service/blob/master/spring_cloud_gateway_diagram.png)

## Spring Cloud Gateway
- 特性：
  - 基于Spring Framework 5, Project Reactor 和 Spring Boot 2.0 进行构建；
  - 动态路由：能够匹配任何请求属性；
  - 可以对路由指定 Predicate（断言）和 Filter（过滤器）；
  - 集成Hystrix的断路器功能；
  - 集成 Spring Cloud 服务发现功能；
  - 易于编写的 Predicate（断言）和 Filter（过滤器）；
  - 请求限流功能；
  - 支持路径重写。
- Spring Cloud Gateway需要Spring Boot和Spring Webflux提供的Netty运行时。它不能在传统的Servlet容器中或作为WAR构建时使用。

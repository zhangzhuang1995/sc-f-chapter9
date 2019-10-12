package com.forezp;

import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ServiceHiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHiApplication.class, args);
    }

    private static final Logger LOG = Logger.getLogger(ServiceHiApplication.class.getName());


    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * 两种方法：
     * 一、
     * zipkin链路追踪(Spring Cloud Sleuth)
     * 1、启动zipkin-server-2.10.1-exec.jar
     * 2、启动eureka注册中心
     * 3、分别启动hi,miya两个服务，注册到注册中心上
     *
     * 二、
     * 创建server-zipkin，集成依赖，需要降低springcloud版本
     * 也发现一个鬼畜的东西，@RequestMapping("/info")，
     * 后缀是info时，调用接口返回{}，没有数据，猜测info是保留字段，或者有其他用处的字段
     * 高版本的springcloud没有这种情况
     *
     * http://localhost:9411/zipkin/ 查看调用链
     *
     * 缺陷：
     * 缺陷1：zipkin客户端向zipkin-server程序发送数据使用的是http的方式通信，每次发送的时候涉及到连接和发送过程。
     * 缺陷2：当我们的zipkin-server程序关闭或者重启过程中，因为客户端收集信息的发送采用http的方式会被丢失。
     *
     * 改进：
     * 1、通信采用socket或者其他效率更高的通信方式。
     * 2、客户端数据的发送尽量减少业务线程的时间消耗，采用异步等方式发送收集信息。
     * 3、客户端与zipkin-server之间增加缓存类的中间件，例如redis、MQ等，在zipkin-server程序挂掉或重启过程中，
     *    客户端依旧可以正常的发送自己收集的信息。
     *
     *
     *
     *  改进一、将HTTP通信改成MQ异步方式通信
     *  改进二、持久化到mysql或者elasticsearch(推荐)
     *
     *  可以看到如下效果：
     1）请求的耗时时间不会出现突然耗时特长的情况
     2）当ZipkinServer不可用时（比如关闭、网络不通等），追踪信息不会丢失，因为这些信息会保存在Rabbitmq服务器上，直到Zipkin服务器可用时，再从Rabbitmq中取出这段时间的信息

        zipkin客户端-->rabbitmq-->zipkin服务端
     */


    /**
     * http://localhost:8988/hi
     */
    @RequestMapping("/hi")
    public String hi() {
        LOG.log(Level.INFO, "hi is being called");
        return "i'm service-hi";
    }

    /**
     * http://localhost:8988/hiCallMiya
     */
    @RequestMapping("/hiCallMiya")
    public String hiCallMiya() {
        LOG.log(Level.INFO, "calling trace service-miya");
        return restTemplate.getForObject("http://localhost:8989/miya", String.class);
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}

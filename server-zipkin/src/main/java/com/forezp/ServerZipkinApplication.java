package com.forezp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;
import zipkin.server.EnableZipkinServer;

@SpringBootApplication
@EnableEurekaClient
/**
 * 开启ZipkinServer的功能，默认采用http通信方式启动ZipkinServer
 */
//@EnableZipkinServer

/**
 * 采用Stream通信方式启动ZipkinServer，也支持HTTP通信方式。
 * @EnableZipkinStreamServer 包含了 @EnableZipkinServer，同时
 * 还创建了一个rabbit-mq的消息队列监听器，所以也支持原来的HTTP通信方式
 */
@EnableZipkinStreamServer
public class ServerZipkinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerZipkinApplication.class, args);
    }
}

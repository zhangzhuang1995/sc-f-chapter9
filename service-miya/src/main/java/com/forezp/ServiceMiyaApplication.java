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
public class ServiceMiyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMiyaApplication.class, args);
    }

    private static final Logger LOG = Logger.getLogger(ServiceMiyaApplication.class.getName());


    /**
     * http://localhost:8989/miya
     */
    @RequestMapping("/miya")
    public String miya() {
        LOG.log(Level.INFO, "miya is being called");
        return "i'm service-miya";
    }

    /**
     * http://localhost:8989/miyaCallHi
     */
    @RequestMapping("/miyaCallHi")
    public String miyaCallHi() {
        LOG.log(Level.INFO, "calling trace service-hi");
        return restTemplate.getForObject("http://localhost:8988/hi", String.class);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}

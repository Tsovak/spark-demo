package com.tsovak.sparkdemo;

import com.tsovak.sparkdemo.components.SparkRequestMappingAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.tsovak.sparkdemo"})
public class TestConfig {

    @Bean
    public SparkRequestMappingAnnotationBeanPostProcessor postProcessor(){
        return new SparkRequestMappingAnnotationBeanPostProcessor();
    }
}

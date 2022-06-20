package com.example.websocketdemo;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableRabbit
public class WebSocketDemoApplication {

    @Bean
    public TopicExchange notiExcahnge() {
        return ExchangeBuilder.topicExchange("noti")
                .durable(true)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketDemoApplication.class, args);
    }

}

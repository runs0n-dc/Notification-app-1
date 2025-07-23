package com.example.ENS.notificationSystem.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Configuration
public class RabbitMQConfig {
public static final String EMAIL_QUEUE = "queue_email";
public static final String SMS_QUEUE = "queue_sms";
public static final String PUSH_QUEUE = "queue_push";

@Bean
public Queue emailQueue() {
    return new Queue(EMAIL_QUEUE);
}
@Bean
public Queue smsQueue() {
    return new Queue(SMS_QUEUE);
}
@Bean
public Queue pushQueue() {
    return new Queue(PUSH_QUEUE);
}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public Supplier<Double> randomSupplier() {
        return Math::random;
    }

}

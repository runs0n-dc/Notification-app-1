package com.example.ENS.notificationSystem.service;

import com.example.ENS.notificationSystem.Entity.Event;
import com.example.ENS.notificationSystem.EventTypes;
import com.example.ENS.notificationSystem.Service.EventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {EventConsumerServiceTest.Config.class})
public class EventConsumerServiceTest {
    private EventConsumer consumerService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        consumerService = new EventConsumer(restTemplate);
    }
    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplate restTemplate() {
            return mock(RestTemplate.class);
        }

        @Bean
        public EventConsumer eventConsumer(RestTemplate restTemplate) {
            return new EventConsumer(restTemplate);
        }
    }

    @Autowired
    private EventConsumer eventConsumer;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testCallbackOnSuccess() throws InterruptedException {
        Event event = new Event("e1", EventTypes.EMAIL, Map.of("message", "test"), "http://mock-callback.com");

        when(restTemplate.postForObject(eq(event.getCallbackUrl()), any(), eq(Void.class)))
                .thenReturn(null);

        consumerService.handleEmail(event);

        verify(restTemplate, times(1))
                .postForObject(eq(event.getCallbackUrl()), any(), eq(Void.class));
    }
    @Test
    public void testCallbackOnFailureSimulated() throws InterruptedException {
        // Custom consumer with a "failing" random supplier
        Supplier<Double> alwaysFail = () -> 0.01;
        EventConsumer failingConsumer = new EventConsumer(restTemplate, alwaysFail);

        Event event = new Event("e2", EventTypes.PUSH, Map.of("message", "simulate fail"), "http://mock-callback.com");

        when(restTemplate.postForObject(eq(event.getCallbackUrl()), any(), eq(Void.class)))
                .thenReturn(null);

        failingConsumer.handlePush(event);

        verify(restTemplate, times(1))
                .postForObject(eq(event.getCallbackUrl()), any(), eq(Void.class));
    }

    @Test
    public void testShutdownCompletesGracefully() throws InterruptedException {
        eventConsumer.onShutdown();
        assertTrue(true, "Graceful shutdown completed without error");

    }
}

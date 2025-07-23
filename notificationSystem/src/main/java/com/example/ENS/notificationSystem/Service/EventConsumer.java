package com.example.ENS.notificationSystem.Service;

import com.example.ENS.notificationSystem.Configuration.RabbitMQConfig;
import com.example.ENS.notificationSystem.Entity.Event;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class EventConsumer {
private final RestTemplate restTemplate ;
private final Supplier<Double> randomSupplier;
   @Autowired
    public EventConsumer(RestTemplate restTemplate) {
        this(restTemplate, Math::random);
    }
    public EventConsumer(RestTemplate restTemplate, Supplier<Double> randomSupplier) {
        this.restTemplate = restTemplate;
        this.randomSupplier = randomSupplier;
    }
private final ExecutorService shutdownExecutor = Executors.newSingleThreadExecutor();

@RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
public void handleEmail(Event event) throws InterruptedException {
    processEvent(event,5000);
}
@RabbitListener(queues = RabbitMQConfig.PUSH_QUEUE)
public void handlePush(Event event) throws InterruptedException {
    processEvent(event,2000);
}
@RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
public void handleSms(Event event) throws InterruptedException{
    processEvent(event,3000);
}
    private void processEvent(Event event, int delayMs) throws InterruptedException {
        Thread.sleep(delayMs);
        boolean fail = randomSupplier.get() < 0.1;

        Map<String, Object> response = new HashMap<>();
        response.put("eventId", event.getEventId());
        response.put("eventType", event.getEventType());
        response.put("processAt", Instant.now().toString());

        if (fail) {
            response.put("status", "Failed");
            response.put("errorMessage", "Simulated processing failed");
        } else {
            response.put("status", "Completed");
        }

        try {
            restTemplate.postForObject(event.getCallbackUrl(), response, Void.class);
        } catch (Exception e) {
           System.out.println("Exception occured: " + e);
        }
    }

    @PreDestroy
    public void onShutdown() throws InterruptedException {
        shutdownExecutor.shutdown();
        shutdownExecutor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(" Consumers shutdown gracefully.");
    }


}

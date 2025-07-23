package com.example.ENS.notificationSystem.Controllers;

import com.example.ENS.notificationSystem.Configuration.RabbitMQConfig;
import com.example.ENS.notificationSystem.DTOs.EventRequestDto;
import com.example.ENS.notificationSystem.Entity.Event;
import com.example.ENS.notificationSystem.EventRequestDtoValidator;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api")
@Validated
public class EventController {

private final RabbitTemplate rabbitTemplate;
private final AtomicBoolean acceptingEvents = new AtomicBoolean(true);

public EventController(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
}
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new EventRequestDtoValidator());
    }
 @PostMapping("/events")
 public ResponseEntity<?> receiveEvent(@Valid @RequestBody EventRequestDto dto){
     System.out.println(">>> VALIDATION PASSED <<<");
    if (!acceptingEvents.get()) {
         return ResponseEntity.status(503).body(Map.of("message", "System is shutting down. Not accepting new events."));
     }
 String eventId = UUID.randomUUID().toString();
 Event e = Event.builder().eventId(eventId).eventType(dto.getEventType())
           .payload(dto.getPayload()).callbackUrl(dto.getCallbackUrl()).build();
 String queue = switch(dto.getEventType()){
     case SMS -> RabbitMQConfig.SMS_QUEUE;
     case EMAIL -> RabbitMQConfig.EMAIL_QUEUE;
     case PUSH -> RabbitMQConfig.PUSH_QUEUE;
 };
 rabbitTemplate.convertAndSend(queue,e);
 return ResponseEntity.ok(Map.of("eventId",eventId,"message","Event accepted for processing."));

}
@GetMapping("/get")
public ResponseEntity<?> getEvent(){
    return ResponseEntity.ok(("Test"));
}

    @PreDestroy
    public void shutdown() {
        acceptingEvents.set(false);
        System.out.println("Graceful shutdown: No longer accepting new events.");
    }

}

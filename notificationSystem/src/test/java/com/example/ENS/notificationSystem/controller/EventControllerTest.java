package com.example.ENS.notificationSystem.controller;
import com.example.ENS.notificationSystem.Controllers.EventController;
import com.example.ENS.notificationSystem.DTOs.EventRequestDto;
import com.example.ENS.notificationSystem.EventTypes;
import com.example.ENS.notificationSystem.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(GlobalExceptionHandler.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RabbitTemplate rabbitTemplate() {
            return Mockito.mock(RabbitTemplate.class);
        }

        @Bean
        public EventController eventController(RabbitTemplate rabbitTemplate) {
            return new EventController(rabbitTemplate);

        }
    }

    @Test
    public void testValidEmailEvent() throws Exception {
        EventRequestDto request = new EventRequestDto();
        request.setEventType(EventTypes.EMAIL);
        request.setPayload(Map.of("recipient", "user@example.com", "message", "Hello"));
        request.setCallbackUrl("http://localhost:9090/api/event-status");

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").exists())
                .andExpect(jsonPath("$.message").value("Event accepted for processing."));


        Mockito.verify(rabbitTemplate).convertAndSend(Mockito.eq("queue_email"), Mockito.<Object>any());
    }

    @Test
    public void testInvalidEventType() throws Exception {
        String invalidPayload = "{\"eventType\": \"INVALID\", \"payload\": {}, \"callbackUrl\": \"http://localhost:9090/api/event-status\"}";

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingFields() throws Exception {
        String invalidPayload = "{\"eventType\": \"EMAIL\"}";

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(jsonPath("$.payload").value("Payload is required"))
                .andExpect(jsonPath("$.callbackUrl").value("Callback URL is required"));
    }
}

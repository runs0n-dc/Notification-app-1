package com.example.ENS.notificationSystem.DTOs;

import com.example.ENS.notificationSystem.EventTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequestDto {
    @NotNull(message = "Event type is required")
    private EventTypes eventType;

    @NotBlank(message = "Callback URL is required")
    private String callbackUrl;
    @NotNull(message = "Payload is required")
    private Map<String,Object> payload;



}

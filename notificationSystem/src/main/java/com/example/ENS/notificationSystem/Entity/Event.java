package com.example.ENS.notificationSystem.Entity;

import com.example.ENS.notificationSystem.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
public class Event implements Serializable {
    private String eventId;
    private EventTypes eventType;
    private Map<String,Object> payload;
    private String callbackUrl;

}

package com.example.ENS.notificationSystem;

import com.example.ENS.notificationSystem.DTOs.EventRequestDto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EventRequestDtoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventRequestDto dto = (EventRequestDto) target;

        if (dto.getPayload() == null || dto.getPayload().isEmpty()) {
            errors.rejectValue("payload", "payload.required", "Payload is required");
        }

        if (dto.getCallbackUrl() == null || dto.getCallbackUrl().trim().isEmpty()) {
            errors.rejectValue("callbackUrl", "callbackUrl.required", "Callback URL is required");
        }
    }
}

package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeCapsuleCreateDtoConverter implements Converter<String, TimeCapsuleCreateDto> {
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public TimeCapsuleCreateDto convert(String source) {
        return objectMapper.readValue(source, TimeCapsuleCreateDto.class);
    }
}

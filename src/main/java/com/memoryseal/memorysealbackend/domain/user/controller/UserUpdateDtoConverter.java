package com.memoryseal.memorysealbackend.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateDtoConverter implements Converter<String, UserUpdateDto> {
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public UserUpdateDto convert(String source) {
        return objectMapper.readValue(source, UserUpdateDto.class);
    }
}

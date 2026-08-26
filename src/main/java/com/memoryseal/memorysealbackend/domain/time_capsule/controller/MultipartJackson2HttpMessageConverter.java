package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    // 1. Class 타입 검사 시 String 및 MultipartFile 제외
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (clazz.equals(String.class) || MultipartFile.class.isAssignableFrom(clazz)) {
            return false;
        }
        return super.canRead(clazz, mediaType);
    }

    // 2. Generic Type 검사 시 String 제외
    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        if (type.equals(String.class)) {
            return false;
        }
        return super.canRead(type, contextClass, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}

package com.blog.app.util;


import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.blog.app.dto.EncryptedResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice
public class GlobalResponseEncryptAdvice {
//implements ResponseBodyAdvice<Object> {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return true;
//    }

//    @Override
//    public Object beforeBodyWrite(
//            Object body,
//            MethodParameter returnType,
//            MediaType selectedContentType,
//            Class<? extends HttpMessageConverter<?>> selectedConverterType,
//            ServerHttpRequest request,
//            ServerHttpResponse response
//    ) {
//        try {
//            String plainJson = mapper.writeValueAsString(body);
//            String encrypted = EncryptionUtil.encrypt(plainJson);
//            return new EncryptedResponseDTO(encrypted);
//        } catch (Exception e) {
//            throw new RuntimeException("Encryption failed", e);
//        }
//    }
}

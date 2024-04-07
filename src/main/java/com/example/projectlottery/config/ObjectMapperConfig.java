package com.example.projectlottery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        //LocalDateTime 타입을 JSON 으로 직렬화할 수 있도록 모듈 등록
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}

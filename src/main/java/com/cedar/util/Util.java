package com.cedar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class Util {

    private final ObjectMapper objectMapper;

    public String generate(final String type, final String id){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        stringBuilder.append("::\"");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    public <T> T generateMap(String jsonString, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, type);
    }



}

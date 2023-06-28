package com.cedar.util;

import cedarpolicy.value.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JSONUtil {
     private final ObjectMapper objectMapper;

     public Map<String, Value> convertTo(String input, TypeReference<Map<String, Value>> typeReference) throws JsonProcessingException {
         return StringUtils.isEmpty(input) ? null : objectMapper.readValue(input,typeReference);
     }
}

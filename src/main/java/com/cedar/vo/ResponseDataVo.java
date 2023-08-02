package com.cedar.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Builder
@Data
public class ResponseDataVo {

    private boolean allow;
    private String reason;
    private Map<String,Object> headers;
    private Optional<String> data;
    private Integer status_code;
}

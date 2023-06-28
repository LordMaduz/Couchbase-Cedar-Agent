package com.cedar.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ResponseDataVo {

    private boolean allow;
    private String reason;
    private Map<String,Object> headers;

    private Integer status_code;
}

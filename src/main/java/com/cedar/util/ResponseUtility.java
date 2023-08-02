package com.cedar.util;


import com.cedar.vo.ResponseDataVo;
import com.cedar.vo.ResponseVo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ResponseUtility {

    public ResponseVo response(final boolean allow, final String reason, final Integer statusCode, final Map<String, Object> headers, final Optional<String> data) {
        final ResponseDataVo responseDataVo = ResponseDataVo.builder()
                .allow(allow)
                .headers(headers)
                .reason(reason)
                .status_code(statusCode)
                .data(data)
                .build();

        return ResponseVo.builder()
                .result(responseDataVo)
                .build();
    }
}

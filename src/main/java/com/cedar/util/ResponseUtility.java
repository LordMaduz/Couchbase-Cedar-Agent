package com.cedar.util;


import com.cedar.vo.ResponseDataVo;
import com.cedar.vo.ResponseVo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseUtility {

    public ResponseVo response(final boolean allow, final String reason, final Integer statusCode, final Map<String, Object> headers) {
        final ResponseDataVo responseDataVo = ResponseDataVo.builder()
                .allow(allow)
                .headers(headers)
                .reason(reason)
                .status_code(statusCode)
                .build();

        return ResponseVo.builder()
                .result(responseDataVo)
                .build();
    }
}

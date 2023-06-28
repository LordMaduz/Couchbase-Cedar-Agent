package com.cedar.vo;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ResponseVo {
    private ResponseDataVo result;
}

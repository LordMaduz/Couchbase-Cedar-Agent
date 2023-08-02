package com.cedar.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;


@Builder
@Data
public class ResponseVo {
    private ResponseDataVo result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseVo that = (ResponseVo) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}

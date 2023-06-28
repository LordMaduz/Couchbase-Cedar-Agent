package com.cedar.util;

import org.springframework.stereotype.Component;


@Component
public class Util {

    public String generate(final String type, final String id){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        stringBuilder.append("::\"");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

}

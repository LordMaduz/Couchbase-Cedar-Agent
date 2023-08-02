package com.cedar.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OperationMapping {


    private String operation;
    private List<String> columns;
}

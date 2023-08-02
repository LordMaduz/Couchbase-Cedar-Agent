package com.cedar.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ObjectMapping {

    private String objectId;
    private List<OperationMapping> operationMappingList;
}

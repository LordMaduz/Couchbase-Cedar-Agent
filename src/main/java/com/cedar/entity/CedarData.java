package com.cedar.entity;

import com.cedar.vo.CedarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CedarData {

    @Id
    private String id;
    private String serviceId;
    private List<CedarEntity> input;
}

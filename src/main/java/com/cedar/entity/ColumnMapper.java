package com.cedar.entity;

import com.cedar.entity.model.ObjectMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;

import java.util.List;

@Collection("ColumnMapping")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document()
public class ColumnMapper {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;
    private String securityGroup;
    private List<ObjectMapping> objectMappingList;

}

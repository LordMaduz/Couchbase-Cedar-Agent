package com.cedar.service;

import com.cedar.entity.ColumnMapper;
import com.cedar.entity.model.ObjectMapping;
import com.cedar.entity.model.OperationMapping;
import com.cedar.repo.ColumnMappingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColumnMappingService {

    private final ColumnMappingRepository columnMappingRepository;


    public Mono<ColumnMapper> addNewMapper(final ColumnMapper columnMapper){
        return columnMappingRepository.save(columnMapper);
    }

    public Mono<ColumnMapper> addNewObject(final ObjectMapping objectMapping, final String id){
        return columnMappingRepository.findById(id).flatMap(c-> {
            if(ObjectUtils.isEmpty(c.getObjectMappingList())){
                c.setObjectMappingList(new ArrayList<>());
            }
            c.getObjectMappingList().add(objectMapping);

            return columnMappingRepository.save(c);
        });
    }

    public Mono<ColumnMapper> addNewOperationMapping(final OperationMapping operationMapping, final String id, final String objectId) {
        return columnMappingRepository.findById(id).flatMap(c-> {

            Optional<ObjectMapping> objectMapping = c.getObjectMappingList().stream().filter(o-> o.getObjectId().equals(objectId)).findAny();

            if(!objectMapping.isEmpty()){
                if(ObjectUtils.isEmpty(objectMapping.get().getOperationMappingList())){
                    objectMapping.get().setOperationMappingList(new ArrayList<>());
                }
                objectMapping.get().getOperationMappingList().add(operationMapping);
            }

            return columnMappingRepository.save(c);
        });
    }

    public Flux<ColumnMapper> getAll(){
        return columnMappingRepository.findAll();
    }

    public Mono<ColumnMapper> getAll(String id){
        return columnMappingRepository.findBySecurityGroup(id);
    }
}

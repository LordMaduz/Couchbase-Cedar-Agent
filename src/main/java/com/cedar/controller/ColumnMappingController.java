package com.cedar.controller;

import com.cedar.entity.ColumnMapper;
import com.cedar.entity.model.ObjectMapping;
import com.cedar.entity.model.OperationMapping;
import com.cedar.service.ColumnMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/column")
@RequiredArgsConstructor
public class ColumnMappingController {

    private final ColumnMappingService columnMappingService;


    @PostMapping
    public Mono<ColumnMapper> addNewColumnMapper(@RequestBody final ColumnMapper columnMapper) {
        return columnMappingService.addNewMapper(columnMapper);
    }


    @PostMapping("/{id}")
    public Mono<ColumnMapper> addNewObjectMapping(@RequestBody final ObjectMapping objectMapping, @PathVariable final String id) {
        return columnMappingService.addNewObject(objectMapping, id);
    }

    @PostMapping("/{id}/{objectId}")
    public Mono<ColumnMapper> addNewOperationMapping(@RequestBody final OperationMapping operationMapping,
                                                     @PathVariable final String id,
                                                     @PathVariable final String objectId) {
        return columnMappingService.addNewOperationMapping(operationMapping, id, objectId);
    }

    @GetMapping()
    public Flux<ColumnMapper> getAll(){
        return columnMappingService.getAll();
    }


    @GetMapping("/{id}")
    public Mono<ColumnMapper> getById(@PathVariable String id){
        return columnMappingService.getAll(id);
    }
}

package com.cedar.controller;

import com.cedar.entity.CedarData;
import com.cedar.service.DataService;
import com.cedar.vo.CedarEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @PostMapping("/{id}")
    public Mono<CedarData> createCedarEntity(@RequestBody final Mono<List<CedarEntity>> cedarEntityList, @PathVariable final String id) {
        return cedarEntityList.flatMap(data -> dataService.createCedarEntity(data, id));
    }

    @PutMapping("/{id}")
    public Mono<CedarData> updateCedarEntity(@RequestBody final Mono<List<CedarEntity>> cedarEntityList,
                                             @PathVariable final String id) {
        return cedarEntityList.flatMap(data -> dataService.updateCedarEntity(data, id));
    }

    @GetMapping
    Flux<CedarData> getAllData(){
        return dataService.getAllData();
    }

    @GetMapping("/{id}")
    Mono<CedarData> getById(@PathVariable final String id){
        return dataService.getById(id);
    }
}

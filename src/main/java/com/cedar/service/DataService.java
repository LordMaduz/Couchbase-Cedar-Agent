package com.cedar.service;

import com.cedar.entity.CedarData;
import com.cedar.repo.DataRepository;
import com.cedar.vo.CedarEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;

    public Mono<CedarData> createCedarEntity(final List<CedarEntity> cedarEntityList, final String id) {

        final CedarData cedarData = CedarData.builder()
                .id(id)
                .input(cedarEntityList)
                .build();

        return dataRepository.save(cedarData);
    }

    public Mono<CedarData> updateCedarEntity(final List<CedarEntity> cedarEntityList, final String id) {

        return dataRepository.findById(id).flatMap(cedarData -> {

            cedarData.setInput(cedarEntityList);
            return dataRepository.save(cedarData);
        });
    }

    public Flux<CedarData> getAllData() {
        return dataRepository.findAll();
    }

    public Mono<CedarData> getById(final String id) {
        return dataRepository.findById(id);
    }


}

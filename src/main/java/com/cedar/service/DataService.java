package com.cedar.service;

import cedarpolicy.model.slice.Entity;
import com.cedar.entity.CedarData;
import com.cedar.repo.DataRepository;
import com.cedar.util.Util;
import com.cedar.vo.CedarEntity;
import com.cedar.vo.CedarUid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;
    private final Util util;

    public Mono<CedarData> createCedarEntity(final List<CedarEntity> cedarEntityList, final String id) {

        final Set<Entity> entitySet = generateEntitySet(cedarEntityList);
        final CedarData cedarData = CedarData.builder()
                .id(id)
                .cedarData(entitySet)
                .input(cedarEntityList)
                .build();

        return dataRepository.save(cedarData);
    }

    public Mono<CedarData> updateCedarEntity(final List<CedarEntity> cedarEntityList, final String id) {

        return dataRepository.findById(id).flatMap(cedarData -> {

            final Set<Entity> entitySet = generateEntitySet(cedarEntityList);
            cedarData.setInput(cedarEntityList);
            cedarData.setCedarData(entitySet);
            return dataRepository.save(cedarData);
        });
    }

    public Flux<CedarData> getAllData() {
        return dataRepository.findAll();
    }

    public Mono<CedarData> getById(final String id) {
        return dataRepository.findById(id);
    }

    private Set<Entity> generateEntitySet(final List<CedarEntity> cedarEntityList) {
        final Set<Entity> entitySet = new HashSet<>();
        cedarEntityList.forEach(cedar -> {
            List<CedarUid> parentsList = cedar.getParents();
            if (parentsList.isEmpty()) {
                entitySet.add(new Entity(generateUid(cedar.getUid().getType(), cedar.getUid().getId()), cedar.getAttrs(), new HashSet<>()));
            } else {
                generateParentEntitySet(entitySet, parentsList, cedar);
            }
        });

        return entitySet;
    }

    public void generateParentEntitySet(final Set<Entity> entitySet, final List<CedarUid> parentCedar, final CedarEntity cedar) {
        final Set<String> parents = new HashSet<>();
        parentCedar.forEach(input -> {
            parents.add(generateUid(input.getType(), input.getId()));
            entitySet.add(new Entity(generateUid(cedar.getUid().getType(), cedar.getUid().getId()), cedar.getAttrs(), parents));
        });
    }

    private String generateUid(final String type, final String id) {
        return util.generate(type, id);
    }
}

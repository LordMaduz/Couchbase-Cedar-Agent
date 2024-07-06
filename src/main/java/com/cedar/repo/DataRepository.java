package com.cedar.repo;

import com.cedar.entity.CedarData;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface DataRepository extends ReactiveMongoRepository<CedarData, String> {

    Mono<CedarData> findByServiceId(final String serviceId);
}

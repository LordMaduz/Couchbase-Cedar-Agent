package com.cedar.repo;

import com.cedar.entity.CedarPolicy;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PolicyRepository extends ReactiveMongoRepository<CedarPolicy, String> {

    Flux<CedarPolicy> findByServiceId(String serviceId);
}

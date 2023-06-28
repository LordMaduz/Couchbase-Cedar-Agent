package com.cedar.repo;

import com.cedar.entity.CedarPolicy;
import org.springframework.data.couchbase.repository.DynamicProxyable;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PolicyRepository extends ReactiveCouchbaseRepository<CedarPolicy, String>, DynamicProxyable<PolicyRepository> {

    Flux<CedarPolicy> findByServiceId(String serviceId);
}

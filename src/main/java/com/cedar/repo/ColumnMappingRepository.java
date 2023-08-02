package com.cedar.repo;

import com.cedar.entity.ColumnMapper;
import org.springframework.data.couchbase.repository.DynamicProxyable;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ColumnMappingRepository extends ReactiveCouchbaseRepository<ColumnMapper, String>, DynamicProxyable<ColumnMappingRepository> {

    Mono<ColumnMapper> findBySecurityGroup(final String securityGroup);
}

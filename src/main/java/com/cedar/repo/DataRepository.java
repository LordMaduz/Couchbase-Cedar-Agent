package com.cedar.repo;

import com.cedar.entity.CedarData;
import org.springframework.data.couchbase.repository.DynamicProxyable;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends ReactiveCouchbaseRepository<CedarData, String>, DynamicProxyable<DataRepository> {
}

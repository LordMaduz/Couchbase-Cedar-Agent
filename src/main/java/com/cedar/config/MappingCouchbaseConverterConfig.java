package com.cedar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.util.TypeInformation;
@Primary
@Configuration
public class MappingCouchbaseConverterConfig extends MappingCouchbaseConverter {

    @Override
    @SuppressWarnings("unchecked")
    protected <R> R read(final TypeInformation<R> type, final CouchbaseDocument source, final Object parent) {
        if (type == null)
            return (R) source.export();
        if (Object.class == typeMapper.readType(source, type).getType()) {
            return (R) source.export();
        } else {
            return super.read(type, source, parent);
        }
    }
}

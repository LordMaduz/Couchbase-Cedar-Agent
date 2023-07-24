
package com.cedar.service;

import cedarpolicy.AuthorizationEngine;
import cedarpolicy.WrapperAuthorizationEngine;
import cedarpolicy.model.AuthorizationQuery;
import cedarpolicy.model.AuthorizationResult;
import cedarpolicy.model.exception.AuthException;
import cedarpolicy.model.schema.Schema;
import cedarpolicy.model.slice.BasicSlice;
import cedarpolicy.model.slice.Entity;
import cedarpolicy.model.slice.Policy;
import cedarpolicy.value.*;
import com.cedar.exception.ProcessException;
import com.cedar.repo.DataRepository;
import com.cedar.repo.PolicyRepository;
import com.cedar.util.Util;
import com.cedar.vo.CedarEntity;
import com.cedar.vo.CedarUid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthorizationEngine authorizationEngine = new WrapperAuthorizationEngine();
    private final Util util;
    private final PolicyRepository policyRepository;
    private final DataRepository dataRepository;

    private static final String PRINCIPLE_UID_TYPE = "SecurityGroup";
    private static final String ACTION_UID_TYPE = "Action";
    private static final String RESOURCE_UID_TYPE = "Collection";
    private static final String GENERIC_UID_PHRASE = "ALL";


    public Mono<AuthorizationResult> authorize(final String securityGroup, final String serviceId,
                                               final String actionId, final String resourceId, Map<String, Value> context) {

        return dataRepository.findById(serviceId).flatMap(data -> {
            AtomicReference<AuthorizationResult> authorize = new AtomicReference<>();
            return policyRepository.findByServiceId(serviceId).collectList().flatMap(policyList -> {


                final List<CedarEntity> entityList = data.getInput();
                final Set<Entity> entitySet = generateEntitySet(entityList);

                Set<Policy> policySet = new HashSet<>();
                policyList.forEach(policy -> {
                    final String cedarPolicyPayload = policy.getPayload();
                    policySet.add(new Policy(cedarPolicyPayload, policy.getId()));
                });

                BasicSlice basicSlice = new BasicSlice(policySet, entitySet);

                return Mono.fromCallable(() -> {
                    AuthorizationQuery query = generateAuthorizationQuery(securityGroup, actionId, resourceId, getContext(context), Optional.empty());
                    try {
                        authorize.set(authorizationEngine.isAuthorized(query, basicSlice));
                    } catch (AuthException e) {
                        throw new ProcessException(e.getMessage());
                    }
                    return authorize.get();
                });
            });
        });
    }

    private AuthorizationQuery generateAuthorizationQuery(final String principleId,
                                                          final String actionId,
                                                          final String resourceId,
                                                          final Map<String, Value> context,
                                                          final Optional<Schema> schema) {

        return new AuthorizationQuery(Optional.of(util.generate(PRINCIPLE_UID_TYPE, generateValue(principleId))),
                util.generate(ACTION_UID_TYPE, actionId),
                Optional.of(util.generate(generateValue(RESOURCE_UID_TYPE), resourceId)), context, schema);

    }

    private String generateValue(final String value) {
        return StringUtils.isEmpty(value) ? GENERIC_UID_PHRASE : value;
    }

    private Map<String, Value> getContext(Map<String, Value> input) {
        return input == null ? new HashMap<>() : input;
    }

    private Set<Entity> generateEntitySet(final List<CedarEntity> cedarEntityList) {
        final Set<Entity> entitySet = new HashSet<>();
        cedarEntityList.forEach(cedar -> {
            List<CedarUid> parentsList = cedar.getParents();
            if (parentsList.isEmpty()) {
                entitySet.add(new Entity(generateUid(cedar.getUid().getType(), cedar.getUid().getId()), getAttributes(cedar.getAttrs()), new HashSet<>()));
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
            entitySet.add(new Entity(generateUid(cedar.getUid().getType(), cedar.getUid().getId()), getAttributes(cedar.getAttrs()), parents));
        });
    }

    public Map<String, Value> getAttributes(Map<String, Object> attributeMap) {
        Map<String, Value> map = new HashMap<>();
        attributeMap.forEach((k, v) -> {
            if (v instanceof List<?>) {
                List<Value> valueList = new ArrayList<>();
                ((List<Object>) v).forEach(e -> {
                    valueList.add(populateValue(e));
                });
                map.put(k, new CedarList(valueList));
            } else {
                map.put(k, populateValue(v));
            }
        });
        return map;
    }

    public Value populateValue(Object value) {
        if (value instanceof String) {
            return new PrimString(value.toString());
        } else if (value instanceof Number) {
            return new PrimLong(((Number) value).longValue());
        } else if (value instanceof Boolean) {
            return new PrimBool(((Boolean) value).booleanValue());
        } else {
            return null;
        }
    }

    private String generateUid(final String type, final String id) {
        return util.generate(type, id);
    }
}


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
import cedarpolicy.value.Value;
import com.cedar.exception.ProcessException;
import com.cedar.repo.DataRepository;
import com.cedar.repo.PolicyRepository;
import com.cedar.util.Util;
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
    private static final String RESOURCE_UID_TYPE = "Resource";
    private static final String GENERIC_UID_PHRASE = "ALL";


    public Mono<AuthorizationResult> authorize(final String securityGroup, final String serviceId,
                                               final String actionId, final String resourceId, Map<String, Value> context) {

        return dataRepository.findById(serviceId).flatMap(data -> {
            AtomicReference<AuthorizationResult> authorize = new AtomicReference<>();
            return policyRepository.findByServiceId(serviceId).collectList().flatMap(policyList -> {

                final Set<Entity> entitySet = data.getCedarData();

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
}

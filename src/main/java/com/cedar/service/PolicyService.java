package com.cedar.service;

import com.cedar.entity.CedarPolicy;
import com.cedar.repo.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public Mono<CedarPolicy> createPolicy(final String policy, final String serviceId) {
        final CedarPolicy cedarPolicy = CedarPolicy.builder()
                .serviceId(serviceId)
                .payload(policy)
                .build();

        return policyRepository.save(cedarPolicy);
    }

    public Flux<CedarPolicy> getPoliciesByServiceId(String serviceId){
        return policyRepository.findByServiceId(serviceId);
    }

    public Flux<CedarPolicy> getAllPolicies(){
        return policyRepository.findAll();
    }

    public Mono<CedarPolicy> getPolicyById(String id){
        return policyRepository.findById(id);
    }
}

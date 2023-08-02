package com.cedar.controller;


import com.cedar.entity.CedarPolicy;
import com.cedar.service.PolicyService;
import com.cedar.vo.PolicyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public Mono<CedarPolicy> createPolicy(final Mono<PolicyVo> policyMono) {
        return policyMono.flatMap(policyVo -> policyService.createPolicy(policyVo.getPolicy(), policyVo.getServiceId()));
    }

//    @GetMapping("/{serviceId}")
//    Flux<CedarPolicy> getPolicies(@PathVariable final String serviceId){
//        return policyService.getPoliciesByServiceId(serviceId);
//    }

    @GetMapping("/{id}")
    Mono<CedarPolicy> getPolicyById(@PathVariable final String id){
        return policyService.getPolicyById(id);
    }

    @GetMapping()
    Flux<CedarPolicy> getAllPolicies(){
        return policyService.getAllPolicies();
    }
}

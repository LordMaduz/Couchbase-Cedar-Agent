package com.cedar.controller;

import cedarpolicy.value.Value;
import com.cedar.service.AuthorizationService;
import com.cedar.util.JSONUtil;
import com.cedar.util.ResponseUtility;
import com.cedar.vo.ResponseVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;


@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final ResponseUtility responseUtility;

    private final JSONUtil jsonUtil;

    @PostMapping()
    public Mono<ResponseVo> authorize(@RequestHeader(name = "service-id") final String serviceId,
                                      @RequestHeader(name = "security-group") final String securityGroup,
                                      @RequestHeader(name = "action-id", required = false) final String actionId,
                                      @RequestHeader(name = "resource-id", required = false) final String resourceId,
                                      @RequestHeader(name = "context", required = false) final String contextString
    ) {

        try {
            final Map<String, Value> context = jsonUtil.convertTo(contextString, new TypeReference<>() {});

            return authorizationService.authorize(securityGroup, serviceId, actionId, resourceId, context).flatMap(authorize ->
                    Mono.just(responseUtility.response(authorize.isAllowed(), null, null, null))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

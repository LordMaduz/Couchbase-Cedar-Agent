package com.cedar.controller;

import com.cedar.service.AuthorizationService;
import com.cedar.vo.ResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping()
    public Mono<ResponseVo> authorize(@RequestHeader(name = "service-id") final String serviceId,
                                      @RequestHeader(name = "security-group") final String securityGroup,
                                      @RequestHeader(name = "action-id", required = false) final String actionId,
                                      @RequestHeader(name = "resource-id", required = false) final String resourceId,
                                      @RequestHeader(name = "context", required = false) final String contextString,
                                      @RequestHeader(name = "data", required = false) final String dataObject,
                                      @RequestHeader(name = "isColumnLevelValidation", required = false) final boolean isColumnLevelValidation
    ) {
        try {
            return authorizationService.authorize(securityGroup, serviceId, actionId, resourceId, contextString, dataObject, isColumnLevelValidation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}